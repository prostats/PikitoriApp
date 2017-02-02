package piki.example.com.loginpikiapp.pikitori.core;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 2017-01-17.
 */

public class Utils {

    private static final String TAG = "Utils";

    //올바른 이메일 형식을 체크하는 부분
    public static boolean isValidEmail(String email) {

        String mail = "^[_a-zA-Z0-9-\\.]+@[\\.a-zAZ0-9-]+\\.[a-zA-Z]+$";

        Pattern p = Pattern.compile(mail);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    //올바른 패스워드 형식을 체크하는 부분
    public static boolean isValidPassword(String password) {
        if (password.length() > 6 && password.length() < 16) {
            return true;
        }
        return false;
    }

    //닉네임 크기 체크하는 부분
    public static boolean isLengthNickname(String user_name) {
        if (user_name.length() > 2 && user_name.length() < 20) {
            return true;
        }
        return false;
    }

    public static boolean isLengthProfileMsg(String statusMsg) {
        if (statusMsg.length() <= 100) {
            return true;
        }
        return false;
    }

    //json형태의 데이터를 gson을 통해 파싱
    public static <V> V fromJSON(HttpRequest request, Class<V> target,String tag) {
        V v = null;
        try {
            Gson gson = new GsonBuilder().create();

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);
            reader.close();
        } catch (Exception ex) {
            Log.e( tag , "fromJSON error : " + ex);
            throw new RuntimeException(ex);
        }
        return v;
    }

    //    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            //READ_EXTERNAL_STORAGE( 사용권한이 없을 경우  -1)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 사용자가 임의로 권한을 취소 시킨경우, 권한 재요청
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    //최초로 권한을 요청하는경우 (첫실행)
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    return false;
                }
            }
            //WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    return false;
                }
            }
            //CAMERA
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, BasicInfo.MY_PERMISSIONS_REQUEST_CAMERA);
                    return false;
                }
            }
            return true;
        } else {
            //사용 권한이 있음을 확인한 경우.
            return true;
        }
    }

    public static void setStringPreferences(Context context, String key, String value) {
        SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBooleanPreferences(Context context, String key, boolean value) {
        SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        editor.putBoolean(key, true);
        editor.commit();
    }

    public static String getStringPreferences(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
        p = context.getSharedPreferences("setting", MODE_PRIVATE);
        return p.getString(key, "");
    }

    public static boolean getBooleanPreferences(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
        p = context.getSharedPreferences("setting", MODE_PRIVATE);
        return p.getBoolean(key, false);
    }

    public static void setUserPreferences(Context context, String key, UserVo value) {
        Gson gson = new Gson();
        SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        editor.putString(key, gson.toJson(value));
        editor.commit();
    }

    public static UserVo getUserPreferences(Context context, String key) {
        Gson gson = new Gson();
        SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
        p = context.getSharedPreferences("setting", MODE_PRIVATE);
        Log.d(TAG, ""+ gson.fromJson(p.getString(key, ""), UserVo.class));
        return gson.fromJson(p.getString(key, ""), UserVo.class);
    }

    public static boolean removePreferences(Context context,String key){
            SharedPreferences p = context.getSharedPreferences("setting", MODE_PRIVATE);
            SharedPreferences.Editor editor = p.edit();
            editor.remove(key);
            editor.commit();
        return true;
    }

    //    public static HttpRequest sendDataPost( String url, Map<String ,Object> data) {
//
//        HttpRequest request = HttpRequest.post(url);
//        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
//        request.accept( HttpRequest.CONTENT_TYPE_JSON );
//        request.connectTimeout( 2000 );
//        request.readTimeout( 30000 );
//
//        boolean result = request.form(data).created();
//        return request;
//    }
//    public static HttpRequest sendDataPost( String url, Map<String ,Object> data, String session) {
//
//        HttpRequest request = HttpRequest.post(url);
//        request.header("Cookie", "JSESSIONID=" + session);
//        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
//        request.accept( HttpRequest.CONTENT_TYPE_JSON );
//        request.connectTimeout( 2000 );
//        request.readTimeout( 30000 );
//
//        boolean result = request.form(data).created();
//        return request;
//    }

    public static HttpRequest sendDataPost(String url, String json) {

        HttpRequest request = HttpRequest.post(url);
        request.contentType(HttpRequest.CONTENT_TYPE_JSON);
        request.accept(HttpRequest.CONTENT_TYPE_JSON);
        request.connectTimeout(2000);
        request.readTimeout(30000);
        request.send(json).code();

        return request;
    }

    public static HttpRequest sendDataPost(String url, String json, String session) {

        Log.d(TAG,""+json);
        HttpRequest request = HttpRequest.post(url);
        request.header("Cookie", "JSESSIONID=" + session);
        request.contentType(HttpRequest.CONTENT_TYPE_JSON);
        request.accept(HttpRequest.CONTENT_TYPE_JSON);
        request.connectTimeout(2000);
        request.readTimeout(30000);
        request.send(json).code();

        return request;
    }

    public static HttpRequest sendGet(String tag, String url) {
        HttpRequest request = HttpRequest.get(url);
        request.contentType(HttpRequest.CONTENT_TYPE_JSON);
        request.accept(HttpRequest.CONTENT_TYPE_JSON);
        request.connectTimeout(2000);
        request.readTimeout(30000);

        int responseCode = request.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Log.e(tag, "fetchUserList() error : Not 200 OK");
        }
        return request;
    }

    public static void saveSession(JSONResult jsonResult, HttpRequest request, Context context) {
        if ("success".equals(jsonResult.getResult())) {
             /* Session 저장하기 */
            Map<String, List<String>> headers = request.headers();
            if (headers.get("Set-Cookie") != null) {
                List<String> cookie = headers.get("Set-Cookie");
                String session = cookie.get(0);
                session = session.substring(session.indexOf("=") + 1, session.indexOf(";"));
                Utils.setStringPreferences(context, "sessionID", session);
                System.out.println(" 세션 저장 :  " + Utils.getStringPreferences(context, "sessionID"));
            }
        }
    }

}
