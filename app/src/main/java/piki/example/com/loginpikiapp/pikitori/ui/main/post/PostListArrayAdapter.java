package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.reply.ReplyListActivity;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.displayImageOption;


public class PostListArrayAdapter extends ArrayAdapter<PostVo> {

    private ImageView profileImageView;
    private LayoutInflater layoutInflater;
    private VideoView videoView;
    //private static final String MOVIE_URL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
    private Context context;
    private ProgressDialog pDialog;


    public PostListArrayAdapter(Context context){
        super(context, R.layout.fragment_post_list);
        layoutInflater = LayoutInflater.from(context);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PostVo post = getItem(position);
        View view = convertView;
        UserVo user=new UserVo();

        if(view == null){
            view = (LinearLayout)layoutInflater.inflate(R.layout.post_item,parent,false);
        }

        Button button=(Button)view.findViewById(R.id.btn_reply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ReplyListActivity.class);
                intent.putExtra("user_no",post.getUser_no());

                context.startActivity(intent);

            }
        });

        //내부 ArrayList에서 해당 포지션의 User 객체를 받아옴


        videoView = (VideoView)view.findViewById(R.id.videoView);

        profileImageView = (ImageView)view.findViewById(R.id.profilepic);

        System.out.println(post);

        try{
            MediaController mediaController = new MediaController(context);

            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse(post.getPost_movie());
            videoView.setVideoURI(video);
        }catch (Exception e){
            Log.e("Error",e.getMessage());
            e.printStackTrace();
        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //pDialog.dismiss();
                videoView.start();
            }
        });




        //프로필
        ImageLoader.getInstance().displayImage( post.getUser_profile_url(), profileImageView, displayImageOption );


        //이름 세팅팅
        TextView textview = (TextView)view.findViewById(R.id.name);
        textview.setText(post.getUser_name());

        TextView textView2=(TextView)view.findViewById(R.id.user_name);
        TextView textView3=(TextView)view.findViewById(R.id.post_content);

        textView2.setText(post.getUser_name());
        textView3.setText(post.getPost_content());

        videoView.start();
        return view;
    }

    public void add(List<PostVo> postList){

        if(postList == null){
            return;
        }

        for(PostVo post :postList){
            add(post);
        }
    }
}
