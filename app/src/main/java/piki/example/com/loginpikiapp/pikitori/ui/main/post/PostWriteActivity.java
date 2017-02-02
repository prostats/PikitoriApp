package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PictureService;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.core.ImageUtils;
import piki.example.com.loginpikiapp.pikitori.core.TagParse;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.TagVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;


public class PostWriteActivity extends AppCompatActivity implements  View.OnClickListener{

    public File cachedir;
    public File folder;

    private PostService postService = new PostService();
    private PictureService pictureService = new PictureService();
    private ImageUtils imageUtils = new ImageUtils();

    private int timeLimit;
    private List<String> selectedImage;
    private List<PictureVo> pictureList = new ArrayList<>();
    private List<TagVo> tagList= new ArrayList<TagVo>();
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private List<File> fileList = new ArrayList<File>();

    private Timer timer = new Timer();

    private ImageView sampleVideo;
    private EditText editTextPost;
    private SeekBar seekBar;
    private TextView videoSpeedSet;
    private String[] videoSpeedValue={"0.5초","1초","1.5초","2초"};
    private RadioGroup radioGroup;
    private Button btn_play;

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();

    private boolean isplay = true;
    private Long ispublic = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        sampleVideo=(ImageView) findViewById(R.id.sampleVideo);
        editTextPost=(EditText) findViewById(R.id.editTextPost);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        videoSpeedSet = (TextView) findViewById(R.id.videoSpeedSet);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        sampleVideo.setOnClickListener(this);
        btn_play.setVisibility(View.INVISIBLE);

        selectedImage = getIntent().getStringArrayListExtra("selectedImage");

        //1. 비트맵 사진 사이즈를 변경한다.
        bitmapList = imageUtils.renderImage(getApplicationContext(), selectedImage);

        //2. 비트맵 저장
        fileList = imageUtils.saveFiles(bitmapList, selectedImage);

        timeLimit = selectedImage.size();
        timer = new Timer();
        timer.schedule(new SampleImageTimerTask(),1000,1000);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radioPublic:
                        ispublic=1L;
                        break;
                    case R.id.radioPrivate:
                        ispublic=2L;
                        break;
                }
            }
        });

        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videoSpeedSet.setText(videoSpeedValue[i]);

                timer.cancel();
                timer= new Timer();

                if(videoSpeedValue[i]=="0.5초") {
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(),1000,500);
                } else if (videoSpeedValue[i]=="1초") {
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(),1000,1000);
                } else if (videoSpeedValue[i]=="1.5초"){
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(),1000,1500);
                } else if (videoSpeedValue[i]=="2초") {
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(),1000,2000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //samplevideo play button
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.sampleVideo:{
                timer.cancel();
                timer.purge();
                timer = null;
                isplay = false;
                btn_play.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.btn_play: {
                timer = new Timer();
                timer.schedule(new SampleImageTimerTask(),1000,1000);
                isplay = true;
                btn_play.setVisibility(View.INVISIBLE);
            }
            break;
        }
    }


    private class SampleImageTimerTask extends TimerTask {
        private int seconds = 0;
        @Override
        public void run() {

            seconds++;
            if(timeLimit <= seconds){
                seconds=0;
            }

            //ui변경
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateImage(seconds);
                }
            });
        }
    }

    private void updateImage(int seconds) {
        sampleVideo.setImageDrawable(new BitmapDrawable(getResources(), bitmapList.get(seconds)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.next:{
                timer.cancel();
                PostVo post = new PostVo();

                if(!editTextPost.getText().toString().equals(null)) {
                    post.setPost_content(editTextPost.getText().toString());
                }
                post.setPost_ispublic(ispublic);
                post.setUser_no(((UserVo)Utils.getUserPreferences(this,"PikiUser")).getUser_no());

//               2. 태그

                List<String> parseList = new TagParse().exportHashTag(editTextPost.getText().toString());
                TagVo[] tag = new TagVo[parseList.size()];
                for(int i = 0; i<parseList.size(); i++){
                    tag[i] = new TagVo();
                    tag[i].setTag_name(parseList.get(i));
                    tagList.add(tag[i]);
                }

//               3. 사진
                PictureVo[] picture = new PictureVo[selectedImage.size()];
                File[] file = new File[selectedImage.size()];

                for(int i = 0; i < selectedImage.size(); i++){
                    picture[i] = new PictureVo();

                    try{
                        ExifInterface exif = new ExifInterface(selectedImage.get(i));

                        Double latitude = null;
                        Double longitude = null;

                        if((exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)!= null)) {
                            latitude = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
                            picture[i].setPicture_lat(latitude);
                            System.out.println(latitude);
                        }
                        if((exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)!= null)) {
                            longitude = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
                            picture[i].setPicture_lng(longitude);
                            System.out.println(longitude);
                        }

                        if(latitude!=null && longitude!=null) {
                            String address = findAddress(latitude, longitude);
                            System.out.println(address);
                            picture[i].setPicture_location(address);
                        }

                        picture[i].setUser_no(((UserVo)Utils.getUserPreferences(this,"PikiUser")).getUser_no());
                        picture[i].setPicture_local_url(selectedImage.get(i));
                        pictureList.add(picture[i]);


                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }

                post.setTagList(tagList);
                post.setPictureList(pictureList);


                new AddPostAsyncTask(post).execute();


                //Intent intent = new Intent(this, pikiMainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();
            }
            break;
        }
        return false;
    }

    private class AddPostAsyncTask extends SafeAsyncTask<PostVo> {

        PostVo post;

        AddPostAsyncTask(PostVo post) {
            this.post=post;
        }

        @Override
        public PostVo call() throws Exception {
            return postService.addPost(getApplicationContext(),post);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------------Error AddPost");
        }

        @Override
        protected void onSuccess(PostVo post) throws Exception {

            System.out.println(post);


            System.out.println("------------------------------Success AddPost");
            new ImageFileAsyncTask(fileList,post).execute();
        }
    }

    private class ImageFileAsyncTask extends SafeAsyncTask{

        List<File> fileList;
        PostVo post;

        ImageFileAsyncTask(List<File> fileList,PostVo post){
            this.fileList = fileList;
            this.post = post;
        }

        @Override
        public Object call() throws Exception {
            pictureService.makeMovie(fileList,post);
            System.out.println("-------------------0");
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------------Error AddImageFile");
        }

        @Override
        protected void onSuccess(Object o) throws Exception {
            System.out.println("----------------Success AddImageFile");
//            Intent intent = new Intent(, pikiMainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            imageUtils.deleteBitmapCache(getApplicationContext());
            finish();

        }
    }

    private Double convertToDegree(String stringDMS, String direction) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;



        //if(direction == "N" || direction == "E"){
            result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));
       // }else{
       //     result = 0 - new Double(FloatD + (FloatM / 60) + (FloatS / 3600));
       // }
        return result;
    }

    private String findAddress(double lat, double lng) {
        String currentLocationAddress = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                address = geocoder.getFromLocation(lat, lng, 1);
                // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                if (address != null && address.size() > 0) {
                    // 주소
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();

                    // 전송할 주소 데이터 (위도/경도 포함 편집)

                }
            }

        } catch (IOException e) {
            Toast.makeText(this, "주소취득 실패" ,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return currentLocationAddress;
    }
}
