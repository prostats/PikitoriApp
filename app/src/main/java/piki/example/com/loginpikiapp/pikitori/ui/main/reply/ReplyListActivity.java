package piki.example.com.loginpikiapp.pikitori.ui.main.reply;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.CommentService;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.domain.CommentVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

public class ReplyListActivity extends AppCompatActivity {

    private UserService userService=new UserService();
    private CommentService commentService=new CommentService();
    CommentVo commentVo=new CommentVo();
    ListView listView;
    private UserReplyArrayAdapter userReplyArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_list);

        getSupportActionBar().setTitle("댓글");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText editText=(EditText)findViewById(R.id.EditText02);
        listView=(ListView)findViewById(R.id.reply_list);

        userReplyArrayAdapter=new UserReplyArrayAdapter(getApplicationContext());
        listView.setAdapter(userReplyArrayAdapter);

        ImageView imageView=(ImageView)findViewById(R.id.imageView1);
        editText.requestFocus();
        Intent intent=getIntent();

        Long B=intent.getLongExtra("user_no",0L);
        Long C=intent.getLongExtra("post_no",0L);

        commentVo.setUser_no(B);
        commentVo.setPost_no(C);

        new showreplyList(commentVo).execute();

        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentVo.setComment_content(editText.getText().toString());
                new addReplyInfoTask(commentVo).execute();
                Toast.makeText(getApplicationContext(), "디비에 추가되었습니다", Toast.LENGTH_SHORT).show();

            }
        });







    }
    private class showreplyList extends SafeAsyncTask<List<CommentVo>>{
        CommentVo commentvo;

        public showreplyList(CommentVo commentvo){
            this.commentvo=commentvo;
        }

        @Override
        public List<CommentVo> call() throws Exception {
            return commentService.showReplyInfo(commentvo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<CommentVo> commentVos) throws Exception {
            userReplyArrayAdapter.add(commentVos);
        }
    }

    private class addReplyInfoTask extends  SafeAsyncTask<UserVo>{
        CommentVo commentvo;

        public addReplyInfoTask(CommentVo commentvo){
            this.commentvo=commentvo;
        }

        @Override
        public UserVo call() throws Exception {
            commentService.addReplyInfoTask(commentvo);
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
            super.onSuccess(userVo);
        }
    }
}