package com.example.mainprojectapp_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainprojectapp_1.api.APIService;
import com.example.mainprojectapp_1.api.RetrofitClient;
import com.example.mainprojectapp_1.models.SignUpRequest;
import com.example.mainprojectapp_1.models.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText editName, editID, editPW, editPW2;
    private Button btnSignUp, checkPWBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editName = findViewById(R.id.editName);
        editID = findViewById(R.id.editID);
        editPW = findViewById(R.id.editPW);
        editPW2 = findViewById(R.id.editPW2);
        btnSignUp = findViewById(R.id.signupBtn_aver);
        checkPWBtn = findViewById(R.id.checkPWBtn);

        btnSignUp.setOnClickListener(v -> registerUser());
        checkPWBtn.setOnClickListener(v -> checkPasswordMatch());
    }

    private void registerUser() {
        String name = editName.getText().toString();
        String id = editID.getText().toString();
        String pw = editPW.getText().toString();
        String pw2 = editPW2.getText().toString();

        if (!pw.equals(pw2)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        SignUpRequest request = new SignUpRequest(name, id, pw);

        apiService.register(request).enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignUpActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPasswordMatch() {
        String pw = editPW.getText().toString();
        String pw2 = editPW2.getText().toString();

        if (pw.equals(pw2)) {
            Toast.makeText(this, "비밀번호가 일치합니다", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackClick(View view) {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }
}