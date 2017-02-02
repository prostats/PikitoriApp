package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.FollowVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.saveSession;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;

/**
 * Created by joohan on 2017-01-31.
 */

public class FollowService {
    private static final String TAG = "FollowService";
    Gson gson = new Gson();

    public String followCheck(FollowVo follow){
    String url = BasicInfo.domain+"/mecavo/follow/followcheck?user_no=" + follow.getUser_no() + "&user_no_friend=" +follow.getUser_no_friend();
    HttpRequest httpRequest = HttpRequest.get(url);
    httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
    httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
    httpRequest.connectTimeout(3000);
    httpRequest.readTimeout(3000);

    int responseCode = httpRequest.code();
    if(responseCode != HttpURLConnection.HTTP_OK) {
        Log.e("UserService", "fetchUserList() error : Not 200 OK");
    }

        JSONResult jsonResultFollowCheck = fromJSON(httpRequest, JSONResultString.class,TAG);
    return (String)jsonResultFollowCheck.getData();
}

    public String following(Context context){

        String url = BasicInfo.domain+"/mecavo/follow/following" ;

        FollowVo follow = new FollowVo();
        follow.setUser_no( ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no() );
        follow.setUser_no_friend(250L);

        HttpRequest request = sendDataPost(url,gson.toJson(follow) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,TAG);

        System.out.println("following " + jsonResult.getData());

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (String)jsonResult.getData();
    }

    public FollowVo unFollowing(FollowVo followVo){
        String url = BasicInfo.domain+"/mecavo/follow/unfollowing?user_no=" +followVo.getUser_no() + "&user_no_friend=" + followVo.getUser_no_friend();
        HttpRequest httpRequest = HttpRequest.get(url);
        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        int responseCode = httpRequest.code();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            Log.e("UserService", "fetchUserList() error : Not 200 OK");
        }
        JSONResultFollowing jsonResultFollowing = fromJSON(httpRequest, JSONResultFollowing.class,TAG);
        return jsonResultFollowing.getData();
    }

    public List<FollowVo> fetchFollowList(Context context,Long no) {

        String url = BasicInfo.domain+"/mecavo/follow/followlist";

        UserVo user = new UserVo();
        user.setUser_no(no);

        HttpRequest request = sendDataPost(url,gson.toJson(user) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUserList.class,TAG);

        for(FollowVo v: (List<FollowVo>)jsonResult.getData()){
            Log.d(TAG,"success: " + v);
        }
        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<FollowVo>)jsonResult.getData();
    }

    public List<FollowVo> fetchFollowerList(Context context, Long no) {
        //HTTP는 서버와 통신하는데 가장 정통한 방식.

        String url = BasicInfo.domain+"/mecavo/follow/followerlist";

        UserVo user = new UserVo();
        user.setUser_no(no);

        HttpRequest request = sendDataPost(url,gson.toJson(user) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUserList.class,TAG);

        System.out.println("fetchFollowList " + jsonResult.getResult());
        for(FollowVo v: (List<FollowVo>)jsonResult.getData()){
            Log.d(TAG,"success: " + v);
        }
        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<FollowVo>)jsonResult.getData();
    }

    private class JSONResultUserList extends JSONResult<List<FollowVo>> {};
    private class JSONResultString extends JSONResult<String>{}
    private class JSONResultFollowing extends JSONResult<FollowVo>{};

}
