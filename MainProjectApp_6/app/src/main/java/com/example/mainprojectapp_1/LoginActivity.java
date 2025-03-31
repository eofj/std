package com.example.mainprojectapp_1;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mainprojectapp_1.api.APIService;
import com.example.mainprojectapp_1.api.RetrofitClient;
import com.example.mainprojectapp_1.models.LoginRequest;
import com.example.mainprojectapp_1.models.LoginResponse;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.security.MessageDigest;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "로그인";

    private EditText editID, editPW;
    private Button btnLogin;
    private ImageView kakaoLogin;
    private String fromPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editID = findViewById(R.id.editID);
        editPW = findViewById(R.id.editPW);
        btnLogin = findViewById(R.id.btn_login);
        kakaoLogin = findViewById(R.id.loginBtn_kakao);

        fromPage = getIntent().getStringExtra("from");

        printKeyHash();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithKakao();
            }
        });
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
            Signature[] signatures = info.signingInfo.getApkContentsSigners();
            for (Signature signature : signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.d("KeyHash", "🔑 keyHash: " + keyHash);
            }
        } catch (Exception e) {
            Log.e("KeyHash", "키 해시를 가져오는 중 오류", e);
        }
    }

    private void loginUser() {
        String id = editID.getText().toString();
        String pw = editPW.getText().toString();

        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        LoginRequest request = new LoginRequest(id, pw);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    navigateAfterLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패. 아이디 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginWithKakao() {
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {
                    Log.i(TAG, "카카오 로그인 성공: " + oAuthToken.getAccessToken());
                    updateKakaoLoginUi();
                }
                if (throwable != null) {
                    Log.w(TAG, "카카오 로그인 실패: " + throwable.getLocalizedMessage());
                }
                return null;
            }
        };

        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
            UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
        }
    }

    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    Log.i(TAG, "카카오 로그인 사용자: " + user.getKakaoAccount().getProfile().getNickname());
                    Toast.makeText(LoginActivity.this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show();
                    navigateAfterLogin();
                }
                if (throwable != null) {
                    Log.w(TAG, "카카오 로그인 정보 불러오기 실패: " + throwable.getLocalizedMessage());
                }
                return null;
            }
        });
    }

    private void navigateAfterLogin() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        if ("mypage".equals(fromPage)) {
            intent.putExtra("goTo", "mypage");
        }
        startActivity(intent);
        finish();
    }

    public void onSignupClick(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    private void navigateToMyPage() {
        Fragment myPageFragment = new MyPageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, myPageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}