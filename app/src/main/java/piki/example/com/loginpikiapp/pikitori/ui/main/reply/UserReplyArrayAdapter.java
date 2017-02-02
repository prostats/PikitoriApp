package piki.example.com.loginpikiapp.pikitori.ui.main.reply;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.domain.CommentVo;

public class UserReplyArrayAdapter extends ArrayAdapter<CommentVo>{
    private ImageView profileImageView;
    private LayoutInflater layoutInflater;

    public UserReplyArrayAdapter(Context context){
        super(context,R.layout.reply_item);
        layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;

        if(view==null){
            view=layoutInflater.inflate(R.layout.reply_item,parent,false);
        }
        CommentVo comment =getItem(position);
        System.out.println(comment);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(comment.getComment_content());

        ImageLoader.getInstance().displayImage( comment.getUser_profile_url(), (ImageView)view.findViewById( R.id.profilepic ), BasicInfo.displayImageOption );
        return view;
    }

    public void add(List<CommentVo> list){
        if(list==null){
            return ;
        }
        for(CommentVo comment :list){
            add(comment);
        }
    }
}
