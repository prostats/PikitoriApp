package piki.example.com.loginpikiapp.pikitori.ui.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.FollowService;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.follow.FollowActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.follow.FollowerActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostImageSelectActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.profile.ProfileModifyActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.search.SearchActivity;

public class pikiMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "pikiMainActivity";

    UserService userService = new UserService();

    private MainTabsAdapter mainTabsAdapter;
    private ImageView profileImageView;
    private TextView postCount;
    private TextView followerCount;
    private TextView followingCount;
    private TextView userName;

    private Button btn_profile_modify, btn_follow_add;
    FloatingActionButton fab_button, fab_camera, fab_image, fab_add;
    Animation FabOpen, FabClose, FabRClockwise, FabRantiClockwise ;
    boolean isOpen = false;

    private UserVo user;
    Uri imageFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piki_main);

        getSupportActionBar().setTitle("메인화면");

        LinearLayout container_profile = (LinearLayout) findViewById(R.id.container_profile);
//        container_profile.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                Toast.makeText(getApplicationContext(), "i: "+ i +"i1: " + i1 + "i2: " + i2 + "i3: " +i3,Toast.LENGTH_SHORT).show();
//            }
//        });
        profileImageView = (ImageView) findViewById(R.id.ProfileImageView);
        userName = (TextView) findViewById(R.id.userName);
        postCount = (TextView) findViewById(R.id.postCount);
        followerCount = (TextView) findViewById(R.id.followerCount);
        followingCount = (TextView) findViewById(R.id.followingCount);
        btn_profile_modify = (Button) findViewById(R.id.btn_profile_modify);
        btn_follow_add = (Button) findViewById(R.id.btn_follow_add);

        followerCount.setOnClickListener(this);
        followingCount.setOnClickListener(this);
        btn_profile_modify.setOnClickListener(this);
        btn_follow_add.setOnClickListener(this);


//      1.  TAB
        mainTabsAdapter = new MainTabsAdapter(
                pikiMainActivity.this,
                (TabHost) findViewById(android.R.id.tabhost),
                (ViewPager) findViewById(R.id.pager));

//      2. FloatingButton
        FabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        FabRantiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);
        fab_image = (FloatingActionButton) findViewById(R.id.fab_image);
        fab_image.setOnClickListener(this);
        fab_camera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab_camera.setOnClickListener(this);
        fab_button = (FloatingActionButton) findViewById(R.id.fab_button);
        fab_button.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Long no = getIntent().getLongExtra("view_no",0L);
        if( no.equals(0L)){
            no = ((UserVo)Utils.getUserPreferences(this,"PikiUser")).getUser_no();
        }
        Utils.setStringPreferences(this,"PostUserNo",String.valueOf(no));
        Log.d(TAG, "PostUserNo: " + no);
        update();
    }

    private void update() {
        new UpdateInfoTask(getApplicationContext()).execute();
    }

    private void updateUI(UserVo user) {
        ImageLoader.getInstance().displayImage(user.getUser_profile_url(), profileImageView, BasicInfo.displayImageOption);
        userName.setText(user.getUser_name());
        postCount.setText(Long.toString(user.getUser_post_count()));
        followerCount.setText(Long.toString(user.getUser_follower_count()));
        followingCount.setText(Long.toString(user.getUser_following_count()));
        if( user.getUser_no().equals(  ((UserVo)Utils.getUserPreferences(getApplicationContext(),"PikiUser")).getUser_no()) ) {
            btn_profile_modify.setVisibility(View.VISIBLE);
            btn_follow_add.setVisibility(View.GONE);
            followerCount.setOnClickListener(this);
            followingCount.setOnClickListener(this);
        }else{
            btn_follow_add.setVisibility(View.VISIBLE);
            btn_profile_modify.setVisibility(View.GONE);
            followerCount.setOnClickListener(null);
            followingCount.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_profile_modify: {
                Intent intent = new Intent(this, ProfileModifyActivity.class);
                startActivityForResult(intent,BasicInfo.RESULT_CODE_PROFILE_MODIFY);
            }
            break;
            case R.id.btn_follow_add:{
                new CreateFollow(getApplicationContext()).execute();
            }
            break;
            case R.id.followerCount:{
                Intent i = new Intent(getApplicationContext(),FollowerActivity.class);
                i.putExtra("view_no",user.getUser_no());
                startActivity(i);
            }
            break;
            case R.id.followingCount:{
                Intent i = new Intent(getApplicationContext(),FollowActivity.class);
                i.putExtra("view_no",user.getUser_no());
                startActivity(i);
            }
            break;
            case R.id.fab_button :{
                if ((isOpen)) {
                    fab_camera.startAnimation(FabClose);
                    fab_image.startAnimation(FabClose);
                    fab_add.startAnimation(FabClose);
                    fab_button.startAnimation(FabRantiClockwise);
                    fab_camera.setClickable(false);
                    fab_image.setClickable(false);
                    fab_add.setClickable(false);
                    isOpen = false;
                } else {
                    fab_camera.startAnimation(FabOpen);
                    fab_image.startAnimation(FabOpen);
                    fab_add.startAnimation(FabOpen);
                    fab_button.startAnimation(FabRClockwise);
                    fab_camera.setClickable(true);
                    fab_image.setClickable(true);
                    fab_add.setClickable(true);
                    isOpen = true;
                }
            }
            break;
            case R.id.fab_add:{

            }
            break;
            case R.id.fab_image:{
                Intent i = new Intent(getApplicationContext(),PostImageSelectActivity.class);
                startActivityForResult(i, BasicInfo.RESULT_CODE_IMAGE_GALLERY );
            }
            break;
            case R.id.fab_camera:{
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(intent, BasicInfo.RESULT_CAMERA );
            }
            break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Intent i = new Intent(this, PostImageListActivity.class);
//        i.putExtra("uri", imageFileUri);
//        startActivityForResult(i, BasicInfo.RESULT_CODE_IMAGE_SELECT_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Toast.makeText(getApplicationContext(),newText,Toast.LENGTH_SHORT).show();
            return false;
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:{
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.logout:{
                Utils.removePreferences(this,"user_login_status");
                Utils.removePreferences(this,"user_type");
                Utils.removePreferences(this,"PikiUser");
                Utils.removePreferences(this,"sessionID");
                NavUtils.navigateUpFromSameTask(this);
            }
        }
        return true;
    }

    private class UpdateInfoTask extends SafeAsyncTask<JSONResult> {
        Context context;
        public UpdateInfoTask(Context context) {
            this.context = context;
        }

        @Override
        public JSONResult call() throws Exception {
            return userService.getUserInfo(context);
        }

        @Override
        protected void onSuccess(JSONResult jsonResult) throws Exception {
            if("fail".equals(jsonResult.getResult())){

            }else{
                user = (UserVo)jsonResult.getData();
                updateUI(user);
                Log.d(TAG,"UpdateTask" + (UserVo)jsonResult.getData());
            }
        }
    }

    private class CreateFollow extends SafeAsyncTask<String> {
        Context context;
        FollowService followService = new FollowService();
        public CreateFollow(Context context) {
            this.context = context;
        }

        @Override
        public String call() throws Exception {
            return followService.following(context);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);
            if("create".equals(result)) {
                Toast.makeText(getApplicationContext(), "팔로우를 맺었습니다." + result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
