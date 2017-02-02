package piki.example.com.loginpikiapp.pikitori.ui.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.RC_SIGN_IN;

public class SNSBase extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SNSBase";
    protected GoogleApiClient mGoogleApiClient;
    protected CallbackManager callbackManager;
    private ProgressDialog mProgressDialog;

    private UserService userService = new UserService();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = buildGoogleApiClient();
        callbackManager = CallbackManager.Factory.create();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        setting = getSharedPreferences("setting", MODE_PRIVATE);
//        editor = setting.edit();

    }

    private GoogleApiClient buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //silentSignIn 할 필요가 없음.
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if(opr.isDone()){
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        }else{
////            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
//        mGoogleApiClient.connect();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        GoogleSignInAccount acct = null;
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            UpdateUI(acct, true);
        } else {
            UpdateUI(acct, false);
        }
    }

    /* 구글 회원가입 */
    protected void UpdateUI(GoogleSignInAccount account, boolean success) {
        if (success) {
            UserVo user = new UserVo();
            user.setUser_social_id(account.getId());
            user.setUser_social_index(BasicInfo.GOOGLEUSER);
            user.setUser_name(account.getDisplayName());
            user.setUser_id(account.getEmail());
            user.setUser_profile_url(account.getPhotoUrl().toString());
            Log.d(TAG, user.toString());
            new LoginFaceTask(user).execute();
        } else {
            warning(null);
        }
    }

    protected void warning(String message) {
        if(message == null){
            message = "회원이 존재 하지 않습니다. 회원가입 해주세요.";
        }
    }


    //OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void onSignedOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    protected void onSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
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

    /*   SNS 회원의 회원가입. */
    protected class LoginFaceTask extends SafeAsyncTask<JSONResult> {
        UserVo userVo;

        LoginFaceTask(UserVo userVo) {
            this.userVo = userVo;
        }

        @Override
        public JSONResult call() throws Exception {
            Log.d(TAG, "LoginFaceTask");
            return userService.LoginF_User(getApplicationContext(), userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            warning(null);
        }

        @Override
        protected void onSuccess(JSONResult result) throws Exception {
            super.onSuccess(result);

            if ("fail".equals(result.getResult())) {
                Log.d(TAG, "fail in spring");
                String error = result.getMessage();
                warning(error);
                return;
            }

            UserVo user = (UserVo) result.getData();
            if (user == null) {
                warning(null);
            }

            Log.d("Login Facebook Created ", user.toString());

            //1. 로그인 여부 저장
            Utils.setBooleanPreferences(getApplicationContext(),"user_login_status",BasicInfo.PIKIUSER_SIGNED_IN);
            //2. 로그인 타입 저장 **
            if(user.getUser_social_index() == BasicInfo.FACEUSER){
                Utils.setStringPreferences(getApplicationContext(),"user_type", String.valueOf(BasicInfo.FACEUSER));
            }else if(user.getUser_social_index() == BasicInfo.GOOGLEUSER){
                Utils.setStringPreferences(getApplicationContext(),"user_type", String.valueOf(BasicInfo.GOOGLEUSER));
            }else{
                Toast.makeText(getApplicationContext(),"올바른 SNS 유저가 아닙니다.", Toast.LENGTH_LONG).show();
            }
            //3. 로그인 유저 정보 저장
            Utils.setUserPreferences(getApplicationContext(),"PkiUser", (UserVo)result.getData());

            Intent i = new Intent(getApplicationContext(), pikiMainActivity.class);
            startActivityForResult(i, BasicInfo.RESULT_CODE_PIKIMAIN);
            activity_finish();
        }

    }

    protected void activity_finish() {
        finish();
    }

}
