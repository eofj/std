package com.example.mainprojectapp_1;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

public class MyPageFragment extends Fragment {

    private static final String TAG = "MyPageFragment";

    private TextView nicknameText;
    private ImageView profileImage;
    private Button btnEditInfo;
    private Button logoutButton;
    private Button loginButton;
    private TextView withdrawText;
    private LinearLayout layoutLoggedIn, layoutLoggedOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 로그인/비로그인 레이아웃 연결
        layoutLoggedIn = view.findViewById(R.id.layout_logged_in);
        layoutLoggedOut = view.findViewById(R.id.layout_logged_out);

        // 로그인 상태 UI 요소 연결
        nicknameText = view.findViewById(R.id.nickname);
        profileImage = view.findViewById(R.id.profile_image);
        btnEditInfo = view.findViewById(R.id.btn_edit_info);
        logoutButton = view.findViewById(R.id.logout);
        withdrawText = view.findViewById(R.id.text_withdraw);

        // 비로그인 상태 UI 요소 연결
        loginButton = view.findViewById(R.id.btn_login);

        // 로그인 여부 확인
        UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
            if (error != null || tokenInfo == null) {
                layoutLoggedIn.setVisibility(View.GONE);
                layoutLoggedOut.setVisibility(View.VISIBLE);
            } else {
                layoutLoggedIn.setVisibility(View.VISIBLE);
                layoutLoggedOut.setVisibility(View.GONE);
                loadUserProfile();
            }
            return null;
        });

        // 비로그인 상태 → 로그인 버튼 클릭
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra("from", "mypage");
            startActivity(intent);
        });

        // 회원정보 수정 페이지 이동
        btnEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // 회원탈퇴 텍스트뷰 설정
        withdrawText.setPaintFlags(withdrawText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        withdrawText.setOnClickListener(v -> {
            UserApiClient.getInstance().unlink(error -> {
                if (error != null) {
                    Log.e(TAG, "회원탈퇴 실패", error);
                    Toast.makeText(getContext(), "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "회원탈퇴 성공. SDK에서 연결 해제 및 토큰 삭제 완료");
                    Toast.makeText(getContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    // ✅ MainActivity로 이동 + goTo: mypage → 비로그인 UI
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("goTo", "mypage");
                    startActivity(intent);
                }
                return null;
            });
        });


        // 로그아웃 버튼 클릭 처리
        logoutButton.setOnClickListener(v -> {
            UserApiClient.getInstance().logout(error -> {
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 폐기됨", error);
                } else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 폐기됨");
                    Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("from", "mypage");
                    startActivity(intent);
                }
                return null;
            });
        });

        return view;
    }

    // 카카오 프로필 가져오기
    private void loadUserProfile() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (throwable != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", throwable);
                Toast.makeText(getActivity(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (user != null && user.getKakaoAccount() != null && user.getKakaoAccount().getProfile() != null) {
                Log.d("KakaoLogin", "사용자 정보 요청 성공");

                String nickname = user.getKakaoAccount().getProfile().getNickname();
                nicknameText.setText(nickname != null ? nickname : "사용자");

                String profileUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();
                Glide.with(this)
                        .load(profileUrl)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .circleCrop()
                        .into(profileImage);
            } else {
                Log.e("KakaoLogin", "프로필 정보가 없습니다.");
                Toast.makeText(getActivity(), "프로필 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }
}