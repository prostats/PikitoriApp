package piki.example.com.loginpikiapp.pikitori.Service;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import java.net.HttpURLConnection;
import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.domain.TagVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;

/**
 * Created by joohan on 2017-01-31.
 */

public class TagService {

    private static final String TAG = "TagService";

    public List<TagVo> FetchTagList(String str){
        String url = BasicInfo.domain+"tag/searchtag?str=" + str;
        HttpRequest request = HttpRequest.get( url );
        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
        request.accept( HttpRequest.CONTENT_TYPE_JSON );
        request.connectTimeout( 1000 );
        request.readTimeout( 30000 );
        int responseCode = request.code();//200이 저장되어있다.
        //200이면 서버와 제대로 통신 할 준비가 되어있다.
        if( responseCode != HttpURLConnection.HTTP_OK ) {
            Log.e( "UserService", "fetchUserList() error : Not 200 OK" );
            return null;
        }
        JSONResultTagList jsonResult = fromJSON( request, JSONResultTagList.class,TAG );
        return jsonResult.getData();
    }

    private class JSONResultTagList extends JSONResult<List<TagVo>> {};

}
