package piki.example.com.loginpikiapp.pikitori.Service;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.domain.CommentVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;

public class CommentService {
    private static final String TAG = "CommentService";

    public void addReplyInfoTask(CommentVo comment){

        String url = BasicInfo.domain + "mecavo/comment/addreply";
        HttpRequest request = HttpRequest.post( url );
        Map<String,Object> data=new HashMap<String,Object>();
        data.put("comment_content", comment.getComment_content());
        data.put("user_no",comment.getUser_no());
        data.put("post_no",comment.getPost_no());
        if( request.form(data).created()) {
            System.out.println("Ok");
        }
    }

    public List<CommentVo> showReplyInfo(CommentVo comment){
        String url = BasicInfo.domain + "mecavo/comment/showreplyList";
        HttpRequest request = HttpRequest.post( url );

        Map<String,Object> data=new HashMap<String,Object>();
        data.put("comment_content", comment.getComment_content());
        data.put("user_no",comment.getUser_no());
        data.put("post_no",comment.getPost_no());

        if( request.form(data).created()) {
            System.out.println("Ok");
        }

        JSONResultUserReplyList jsonResult= fromJSON(request,JSONResultUserReplyList.class,TAG);
        return jsonResult.getData();
    }

    private class JSONResultUserReplyList extends JSONResult<List<CommentVo>> {};
}

