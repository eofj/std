package com.example.mainprojectapp_1;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mainprojectapp_1.adapter.AdditionalImagesAdapter;
import com.example.mainprojectapp_1.api.DiaryApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryEditFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 100;
    private static final String TAG = "DiaryEdit";

    private ImageView diaryImage;
    private RatingBar ratingBar;
    private EditText editFoodName, editLocation, editContent, editRestaurantName;
    private Button btnSave, btnAddImage;
    private RecyclerView recyclerView;
    private AdditionalImagesAdapter imagesAdapter;
    private List<String> additionalImages = new ArrayList<>();

    private DiaryApiService diaryApiService;
    private String baseImageUrl = null;
    private long restaurantIdLong = -1;
    private long diaryId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_edit_fragment, container, false);

        diaryImage = view.findViewById(R.id.diary_image);
        ratingBar = view.findViewById(R.id.rating_bar);
        editRestaurantName = view.findViewById(R.id.edit_restaurantname);
        editLocation = view.findViewById(R.id.edit_location);
        editContent = view.findViewById(R.id.edit_content);
        btnSave = view.findViewById(R.id.btn_save);
        btnAddImage = view.findViewById(R.id.btn_add_image);
        recyclerView = view.findViewById(R.id.recycler_additional_images);
        editFoodName = view.findViewById(R.id.edit_foodname);

        imagesAdapter = new AdditionalImagesAdapter(additionalImages, uri -> removeAdditionalImage(uri));
        recyclerView.setAdapter(imagesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        diaryApiService = RetrofitClient.getDiaryApiService();

        btnSave.setOnClickListener(v -> saveDiaryEntry());
        btnAddImage.setOnClickListener(v -> openGallery());

        Bundle bundle = getArguments();
        if (bundle != null) {
            diaryId = bundle.getLong("diaryId", -1);
            restaurantIdLong = bundle.getLong("restaurant_id", -1);
            String restaurantName = bundle.getString("restaurant_name", "");
            String restaurantAddress = bundle.getString("restaurant_address", "");
            String restaurantImage = bundle.getString("restaurant_image", "");
            String foodName = bundle.getString("foodName", "");
            String diaryContent = bundle.getString("content", "");
            float rating = bundle.getFloat("rating", 0f);

            Log.d(TAG, "📥 번들 데이터 수신:");
            Log.d(TAG, "diaryId = " + diaryId);
            Log.d(TAG, "restaurantId = " + restaurantIdLong);
            Log.d(TAG, "restaurantName = " + restaurantName);
            Log.d(TAG, "restaurantAddress = " + restaurantAddress);
            Log.d(TAG, "imageUrl = " + restaurantImage);
            Log.d(TAG, "foodName = " + foodName);
            Log.d(TAG, "diaryContent = " + diaryContent);
            Log.d(TAG, "rating = " + rating);

            editRestaurantName.setText(restaurantName);
            editLocation.setText(restaurantAddress);
            editFoodName.setText(foodName);
            editContent.setText(diaryContent);
            ratingBar.setRating(rating);
            baseImageUrl = restaurantImage;

            if (restaurantImage != null && !restaurantImage.isEmpty()) {
                String fullImageUrl = restaurantImage.startsWith("http")
                        ? restaurantImage
                        : "http://13.125.46.254:8080" + restaurantImage;

                Glide.with(this)
                        .load(fullImageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(diaryImage);
            }
        }

        return view;
    }

    private void removeAdditionalImage(String uri) {
        additionalImages.remove(uri);
        imagesAdapter.notifyDataSetChanged();
    }

    private void openGallery() {
        if (additionalImages.size() >= 5) {
            Toast.makeText(getContext(), "최대 5장까지 추가할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                additionalImages.add(selectedImageUri.toString());
                imagesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void saveDiaryEntry() {
        Log.d(TAG, "📋 saveDiaryEntry() 실행됨");

        String restaurantName = editRestaurantName.getText().toString();
        String location = editLocation.getText().toString();
        String content = editContent.getText().toString();
        float ratingValue = ratingBar.getRating();
        String menuNameString = editFoodName.getText().toString();

        Log.d(TAG, "입력값 확인 → restaurantName: " + restaurantName + ", location: " + location + ", menuName: " + menuNameString);
        Log.d(TAG, "내용: " + content + ", 별점: " + ratingValue);

        RequestBody restaurantId = RequestBody.create(String.valueOf(restaurantIdLong), MediaType.parse("text/plain"));
        RequestBody diaryText = RequestBody.create(content, MediaType.parse("text/plain"));
        RequestBody rating = RequestBody.create(String.valueOf(ratingValue), MediaType.parse("text/plain"));
        RequestBody menuName = RequestBody.create(menuNameString, MediaType.parse("text/plain"));

        if (!additionalImages.isEmpty()) {
            Uri imageUri = Uri.parse(additionalImages.get(0));
            String filePath = com.example.mainprojectapp_1.utils.FileUtil.getPath(getContext(), imageUri);
            if (filePath != null) {
                File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
                MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

                Log.d(TAG, "✅ 앨범 이미지 선택됨: " + file.getAbsolutePath());
                if (diaryId != -1) {
                    uploadUpdatedDiaryToServer(photoPart, restaurantId, diaryText, rating, menuName);
                } else {
                    uploadDiaryToServer(photoPart, restaurantId, diaryText, rating, menuName);
                }
                return;
            }
        } else if (baseImageUrl != null && !baseImageUrl.isEmpty()) {
            String fullImageUrl = baseImageUrl.startsWith("http")
                    ? baseImageUrl
                    : "http://13.125.46.254:8080" + baseImageUrl;

            Log.d(TAG, "📸 실제 다운로드할 이미지 URL: " + fullImageUrl);

            downloadImageAndUpload(fullImageUrl, file -> {
                Log.d(TAG, "✅ 이미지 다운로드 완료: " + file.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
                MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

                if (diaryId != -1) {
                    uploadUpdatedDiaryToServer(photoPart, restaurantId, diaryText, rating, menuName);
                } else {
                    uploadDiaryToServer(photoPart, restaurantId, diaryText, rating, menuName);
                }
            });
            return;
        }

        Log.d(TAG, "⚠️ 이미지 없이 저장 시도");
        if (diaryId != -1) {
            uploadUpdatedDiaryToServer(null, restaurantId, diaryText, rating, menuName);
        } else {
            uploadDiaryToServer(null, restaurantId, diaryText, rating, menuName);
        }
    }

    private void uploadDiaryToServer(MultipartBody.Part photoPart, RequestBody restaurantId,
                                     RequestBody diaryText, RequestBody rating, RequestBody menuName) {
        Log.d(TAG, "📝 새 일기 작성 요청:");
        Log.d(TAG, "restaurantId = " + bodyToString(restaurantId));
        Log.d(TAG, "diaryText = " + bodyToString(diaryText));
        Log.d(TAG, "rating = " + bodyToString(rating));
        Log.d(TAG, "menuName = " + bodyToString(menuName));
        Log.d(TAG, "photoPart = " + (photoPart != null ? "있음" : "없음"));

        diaryApiService.createDiaryWithImages(restaurantId, diaryText, rating, menuName, photoPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "일기가 저장되었습니다!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Log.e(TAG, "❌ 저장 실패 - 코드: " + response.code());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "에러 내용: " + response.errorBody().string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(), "저장 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "❌ 저장 실패", t);
                        Toast.makeText(getContext(), "저장 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadUpdatedDiaryToServer(MultipartBody.Part photoPart, RequestBody restaurantId,
                                            RequestBody diaryText, RequestBody rating, RequestBody menuName) {
        Log.d(TAG, "✏️ 일기 수정 요청 - diaryId: " + diaryId);
        Log.d(TAG, "restaurantId = " + bodyToString(restaurantId));
        Log.d(TAG, "diaryText = " + bodyToString(diaryText));
        Log.d(TAG, "rating = " + bodyToString(rating));
        Log.d(TAG, "menuName = " + bodyToString(menuName));
        Log.d(TAG, "photoPart = " + (photoPart != null ? "있음" : "없음"));

        diaryApiService.updateDiaryWithImages(diaryId, restaurantId, diaryText, rating, menuName, photoPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "일기가 수정되었습니다!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();  // 👈 이 줄만 있었음
                        }
                        else {
                            Log.e(TAG, "❌ 수정 실패 - 코드: " + response.code());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "에러 내용: " + response.errorBody().string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(), "수정 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "❌ 수정 실패", t);
                        Toast.makeText(getContext(), "수정 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String bodyToString(RequestBody body) {
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "IOException 발생";
        }
    }

    private void downloadImageAndUpload(String imageUrl, UploadCallback callback) {
        Glide.with(this)
                .asFile()
                .load(imageUrl)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        callback.onDownloaded(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    interface UploadCallback {
        void onDownloaded(File file);
    }
}