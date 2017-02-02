package piki.example.com.loginpikiapp.pikitori.ui;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.ui.Login.JoinUserActivity;
import piki.example.com.loginpikiapp.pikitori.ui.Login.LoginUserActivity;

import static piki.example.com.loginpikiapp.R.id.btn_start;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_CAMERA;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SplashActivity";

    static ImageView backgroundOne = null;
    static ImageView backgroundTwo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        TextView btn_login = (TextView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        //animation
        backgroundOne = (ImageView) findViewById(R.id.background_one);
        backgroundTwo = (ImageView) findViewById(R.id.background_two);

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.reverse();

        Utils.checkPermission(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "자동로그인 가능? "+ Utils.getBooleanPreferences(this, "user_login_status"));
        switch (view.getId()) {
            case R.id.btn_login: {
                Intent i = new Intent(this, LoginUserActivity.class);
                startActivityForResult(i, BasicInfo.RESULT_CODE_LOGIN_USER);
            }
            break;
            case btn_start: {
                if (Utils.getBooleanPreferences(this, "user_login_status")) {
                    Toast.makeText(getApplicationContext(), "로그인을 한적이 있습니다.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this, LoginUserActivity.class);
                    startActivityForResult(i, BasicInfo.RESULT_CODE_LOGIN_USER);
                    break;
                }
                    Intent i = new Intent(this, JoinUserActivity.class);
                    startActivityForResult(i, BasicInfo.RESULT_CODE_JOIN_USER);
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, grantResults.toString());
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "외부저장장치 읽기 권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "외부저장장치 쓰기 권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "카메라 권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BasicInfo.RESULT_CODE_JOIN_USER:
                if (requestCode != BasicInfo.RESPONSE_OK) {

                }
                break;
            case BasicInfo.RESULT_CODE_LOGIN_USER:
                if (requestCode != BasicInfo.RESPONSE_OK) {

                }
                break;

        }

    }
}
