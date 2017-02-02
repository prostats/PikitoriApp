package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.saveSession;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;

/**
 * Created by joohan on 2017-01-19.
 */

public class PostService {

    private static final String TAG = "PostService";
    Gson gson = new Gson();

    public PostVo addPost(Context context, PostVo post){

        String url = BasicInfo.domain+"mecavo/post/add";

        HttpRequest request = sendDataPost(url,gson.toJson(post) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult= fromJSON(request, PostService.JSONResultPost.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (PostVo)jsonResult.getData();
    }

    public List<PostVo> fetchPostInfo(Context context){
        String url = BasicInfo.domain+"mecavo/post/showpostList";

        UserVo user = new UserVo();
        user.setUser_no(Long.parseLong(Utils.getStringPreferences(context,"PostUserNo")));

        HttpRequest request = sendDataPost(url,gson.toJson(user),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultPostList.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<PostVo>)jsonResult.getData();
    }

    private class JSONResultPost extends JSONResult<PostVo> {};
    private class JSONResultPostList extends JSONResult<List<PostVo>>{};

}
