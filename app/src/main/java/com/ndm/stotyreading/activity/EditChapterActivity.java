// Activity hiển thị chi tiết chapter
package com.ndm.stotyreading.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.ChapterImageAdapter;
import com.ndm.stotyreading.adapter.CommentAdapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Chapter;
import com.ndm.stotyreading.enitities.story.ChapterDetailResponse;
import com.ndm.stotyreading.utils.PathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditChapterActivity extends AppCompatActivity {
    private EditText edtTitle, edtNumber, edtDate;
    private Button btnSave, btnDeleteComments, btnChooseImages;
    private LinearLayout imageContainer;
    private RecyclerView rvChapterImages, rvComments;

    private Chapter chapter;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ChapterImageAdapter imageAdapter;
    private CommentAdapter commentAdapter;
    private ProgressBar progressBar; // progress loading


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        edtTitle = findViewById(R.id.edtChapterTitle);
        edtNumber = findViewById(R.id.edtChapterNumber);
        edtDate = findViewById(R.id.edtChapterDate);
        btnSave = findViewById(R.id.btnSaveChapter);
        btnDeleteComments = findViewById(R.id.btnDeleteComments);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        imageContainer = findViewById(R.id.imageContainer);
        rvChapterImages = findViewById(R.id.rvChapterImages);
        rvComments = findViewById(R.id.rvComments);

        rvChapterImages.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        chapter = getIntent().getParcelableExtra("chapter");
        if (chapter != null) {
            loadChapterDetail(chapter.getId());
        }

        btnChooseImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // ✅ cho phép chọn nhiều
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh chương"), 100);
        });


        btnDeleteComments.setOnClickListener(v -> Toast.makeText(this, "Xóa comment (gọi API delete)", Toast.LENGTH_SHORT).show());

        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String numberStr = edtNumber.getText().toString().trim();
            String date = edtDate.getText().toString().trim();
            Log.d("CHAPTER_UPDATE", "Tiêu đề: " + title);
            Log.d("CHAPTER_UPDATE", "Số chương: " + numberStr);
            Log.d("CHAPTER_UPDATE", "Ngày phát hành: " + date);
            if (title.isEmpty() || numberStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int number;
            try {
                number = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số chương không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            updateChapterWithImagesAndText( title, number, date);
        });

    }

    private void updateChapterWithImagesAndText(String title, int number, String date) {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            String path = PathUtil.getPath(this, uri); // bạn cần tạo class PathUtil.java để lấy đường dẫn thực
            if (path != null) {
                File file = new File(path);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                imageParts.add(part);
            }
        }

        RequestBody rbTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody rbNumber = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(number));
        RequestBody rbDate = RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody rbViews = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chapter.getViews()));

        api.updateChapterWithImages(chapter.getId(), rbTitle, rbNumber, rbDate, rbViews, imageParts)
                .enqueue(new Callback<Chapter>() {
                    @Override
                    public void onResponse(Call<Chapter> call, Response<Chapter> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(EditChapterActivity.this, "✅ Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            finish();


                        } else {

                            Toast.makeText(EditChapterActivity.this, "❌ Lỗi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Chapter> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);

                        Toast.makeText(EditChapterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void loadChapterDetail(String chapterId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getChapterDetail(chapterId).enqueue(new Callback<ChapterDetailResponse>() {
            @Override
            public void onResponse(Call<ChapterDetailResponse> call, Response<ChapterDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChapterDetailResponse detail = response.body();

                    edtTitle.setText(detail.getChapter().getTitle());
                    edtNumber.setText(String.valueOf(detail.getChapter().getChapterNumber()));
                    edtDate.setText(detail.getChapter().getReleaseDate());

                    imageAdapter = new ChapterImageAdapter(detail.getImages());
                    rvChapterImages.setLayoutManager(new LinearLayoutManager(EditChapterActivity.this)); // ✅ FIXED
                    rvChapterImages.setAdapter(imageAdapter);

                    commentAdapter = new CommentAdapter(detail.getComments(), commentId -> showDeleteCommentDialog(commentId));
                    rvComments.setAdapter(commentAdapter);

                } else {
                    Toast.makeText(EditChapterActivity.this, "Lỗi khi tải chi tiết chương", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChapterDetailResponse> call, Throwable t) {
                Toast.makeText(EditChapterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    addImageToContainer(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                addImageToContainer(imageUri);
            }
        }
    }

    private void addImageToContainer(Uri imageUri) {
        selectedImageUris.add(imageUri);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        imageView.setPadding(8, 8, 8, 8);
        Glide.with(this).load(imageUri).into(imageView);
        imageContainer.addView(imageView);
    }


    private void showDeleteCommentDialog(String commentId) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bình luận")
                .setMessage("Bạn có chắc chắn muốn xóa bình luận này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteComment(commentId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteComment(String commentId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.deleteComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditChapterActivity.this, "Đã xóa bình luận", Toast.LENGTH_SHORT).show();
                    loadChapterDetail(chapter.getId()); // Load lại dữ liệu
                } else {
                    Toast.makeText(EditChapterActivity.this, "Lỗi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditChapterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}