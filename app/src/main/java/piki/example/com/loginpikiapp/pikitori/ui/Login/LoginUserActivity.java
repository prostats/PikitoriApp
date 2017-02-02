package piki.example.com.loginpikiapp.pikitori.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import org.json.JSONObject;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.isValidEmail;

public class LoginUserActivity extends SNSBase implements View.OnClickListener {
    private static final String TAG = "LoginUserActivity";
    private UserService userService = new UserService();

    private TextView warningWindow;
    private EditText editText_email;
    private EditText editText_password;
    private SignInButton btn_google_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        getSupportActionBar().setTitle("로그인");

        warningWindow = (TextView) findViewById(R.id.warningWindow);
        editText_email = (EditText)findViewById(R.id.editText_email);
        editText_email.addTextChangedListener(new ValidationWatcher(editText_email));
        editText_password = (EditText)findViewById(R.id.editText_password);
        editText_password.addTextChangedListener(new ValidationWatcher(editText_password));

        Button btn_login_connect = (Button) findViewById(R.id.btn_login_connect);
        btn_login_connect.setOnClickListener(this);
        TextView btn_create_account = (TextView) findViewById(R.id.btn_create_account);
        btn_create_account.setOnClickListener(this);

        /*Google*/
        btn_google_login = (SignInButton) findViewById(R.id.btn_google_login);
        btn_google_login.setOnClickListener(this);
        /*FACEBOOK*/
        LoginButton facebook_login = (LoginButton) findViewById(R.id.btn_facebook_login);
        facebook_login.setReadPermissions("public_profile");
        facebook_login.setReadPermissions("email");
        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "registerCallback onSuccess/" + loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject f_user, GraphResponse response) {

                        Log.d(TAG,f_user.toString());
                        Log.d(TAG, response.toString());
                        UserVo user = new UserVo();
                        user.setUser_social_id(f_user.optString("id"));
                        user.setUser_social_index(BasicInfo.FACEUSER);
                        user.setUser_id(f_user.optString("email"));
                        user.setUser_name(f_user.optString("name"));
                        user.setUser_profile_url(f_user.optString("url"));
                        Log.d(TAG, user.toString());
                        new SNSBase.LoginFaceTask(user).execute();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, link, email, picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "registerCallback onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "registerCallback onError: " + error);
            }
        });

//        if(develope_test){
//            editText_email.setText("hoban123@naver.com");
//            editText_password.setText("1234567");
//            UserVo user = new UserVo();
//            user.setUser_id(editText_email.getText().toString());
//            user.setUser_password(editText_password.getText().toString());
//            new LoginTask(user).execute();
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        warningWindow.setVisibility(View.INVISIBLE);

        if (Utils.getBooleanPreferences(this, "user_login_status")) {
            Log.d(TAG,"로그인 가능?" + (UserVo)Utils.getUserPreferences(this,"PikiUser"));
            new LoginTask((UserVo)Utils.getUserPreferences(this,"PikiUser")).execute();
        }

        if( !TextUtils.isEmpty( getIntent().getStringExtra("joinresult") ) ){
            if(getIntent().getStringExtra("joinresult").equals("success")){
                Toast.makeText(getApplicationContext(),"회원가입이 되었습니다. 로그인하여 주세요.",Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult" + requestCode +"/"+ resultCode);
    }

    @Override
    public void onClick(View view) {
        if (!super.mGoogleApiClient.isConnecting()) {
            switch (view.getId()) {
                case R.id.btn_google_login:
                    super.onSignIn();
                    break;
                case R.id.btn_create_account:
                    Intent i = new Intent(getApplicationContext(), JoinUserActivity.class);
                    startActivityForResult(i, BasicInfo.RESULT_CODE_JOIN_USER);
                    break;

                case R.id.btn_login_connect:{
                    if(check_email && check_password) {
                        UserVo user = new UserVo();
                        user.setUser_id(editText_email.getText().toString());
                        user.setUser_password(editText_password.getText().toString());
                        new LoginTask(user).execute();
                    }else{
                        warningWindow.setText("형식이 맞지 않아 로그인 할수 없습니다.");
                        warningWindow.setVisibility(View.VISIBLE);
                    }

                }
                break;
            }
        }

    }

    @Override
    protected void activity_finish() {
        finish();
    }

    @Override
    protected void warning(String message) {
        super.warning(message);
        warningWindow.setText(message);
        warningWindow.setVisibility(View.VISIBLE);
    }

    private boolean check_email = false;
    private boolean check_password = false;
    private class ValidationWatcher implements TextWatcher {

        private View target_view;
        public ValidationWatcher(View view) {
            target_view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            switch (target_view.getId()){
                case R.id.editText_email:{
                    EditText editText_email = (EditText)target_view;

                    if(charSequence.length() >=1){
                        if(!isValidEmail(charSequence.toString())){
                            editText_email.setError("올바른 이메일 형식을 입력해주세요 ");
                            check_email = false;
                        }else{
                            editText_email.setError(null);
                            check_email = true;
                        }
                    }
                }
                break;
                case R.id.editText_password:{

                    EditText editText_password = (EditText)target_view;
                    if(charSequence.length() >=1){
                        if (!Utils.isValidPassword(charSequence.toString())) {
                            editText_password.setError("7자리 이상 비밀번호를 입력해주세요 ");
                            check_password = false;
                        } else {
                            editText_password.setError(null);
                            check_password = true;
                        }
                    }

                }break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class LoginTask extends SafeAsyncTask<JSONResult> {

        UserVo userVo;

        LoginTask(UserVo userVo) {
            this.userVo = userVo;
        }

        @Override
        public JSONResult call() throws Exception {
            Log.d(TAG,"LoginTask");
            return userService.Login(getApplicationContext(),userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.d(TAG, "onException: " + e);
            warning(e.getMessage());
        }

        @Override
        protected void onSuccess(JSONResult result) throws Exception {
            super.onSuccess(result);

            if( "fail".equals( result.getResult()) ){
                Log.d(TAG, "fail in spring");
                String error = result.getMessage();
                warningWindow.setText(error);
                warningWindow.setVisibility(View.VISIBLE);
                return;
            }

            UserVo user = (UserVo) result.getData();
            if(user==null){
                warning(null);
            }

            Log.d(TAG, "login success" + user);

            //1. 로그인 여부 저장
            Utils.setBooleanPreferences(getApplicationContext(),"user_login_status",BasicInfo.PIKIUSER_SIGNED_IN);
            //2. 로그인 타입 저장
            Utils.setStringPreferences(getApplicationContext(),"user_type", String.valueOf(BasicInfo.PIKIUSER));
            //3. 로그인 유저 정보 저장
            Utils.setUserPreferences(getApplicationContext(),"PikiUser", (UserVo)result.getData());

            Intent i = new Intent(getApplicationContext(),pikiMainActivity.class);
            startActivityForResult(i,BasicInfo.RESULT_CODE_PIKIMAIN);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

