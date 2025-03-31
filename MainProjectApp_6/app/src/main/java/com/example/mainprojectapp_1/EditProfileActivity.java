package com.example.mainprojectapp_1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private Button btnSaveChanges;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnSaveChanges = findViewById(R.id.btn_save_changes);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // 저장된 정보 불러오기
        loadProfile();

        btnSaveChanges.setOnClickListener(v -> {
            saveProfile(); // 입력한 정보 저장
            Toast.makeText(this, "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 현재 액티비티 종료
        });
    }

    // 회원 정보 저장하기
    private void saveProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", editName.getText().toString());
        editor.putString("email", editEmail.getText().toString());
        editor.putString("password", editPassword.getText().toString());
        editor.apply(); // 비동기 저장
    }

    // 저장된 회원 정보 불러오기
    private void loadProfile() {
        editName.setText(sharedPreferences.getString("name", ""));
        editEmail.setText(sharedPreferences.getString("email", ""));
        editPassword.setText(sharedPreferences.getString("password", ""));
    }
}

