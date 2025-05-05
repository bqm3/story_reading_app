package com.ndm.stotyreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Category;
import com.ndm.stotyreading.enitities.story.Story;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStoryActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtDescription;
    private Spinner spnCategory, spnStatus;
    private Button btnChooseImage, btnSave;
    private ImageView imgPreview;
    private Uri imageUri;
    private List<Category> categories = new ArrayList<>();
    private String selectedCategoryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtDescription = findViewById(R.id.edtDescription);
        spnCategory = findViewById(R.id.spnCategory);
        spnStatus = findViewById(R.id.spnStatus);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        imgPreview = findViewById(R.id.imgPreview);

        loadCategories();
        loadStatusSpinner();

        btnChooseImage.setOnClickListener(v -> chooseImage());

        btnSave.setOnClickListener(v -> saveStory());
    }

//    private void loadCategories() {
//        ApiService api = RetrofitClient.getClient().create(ApiService.class);
//        api.getCategories().enqueue(new Callback<List<Category>>() {
//            @Override
//            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
//                if (response.isSuccessful()) {
//                    categories = response.body();
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                            AddStoryActivity.this,
//                            android.R.layout.simple_spinner_dropdown_item,
//                            categories.stream().map(Category::getName).collect(Collectors.toList())
//                    );
//                    spnCategory.setAdapter(adapter);
//
//                    spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            selectedCategoryId = categories.get(position).getId();
//                        }
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {}
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Category>> call, Throwable t) {
//                Toast.makeText(AddStoryActivity.this, "Không tải được thể loại", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }



    private void loadCategories() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categories = response.body();
                    if (categories != null && !categories.isEmpty()) {
                        // Create adapter with category names
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AddStoryActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                categories.stream().map(Category::getName).collect(Collectors.toList())
                        );
                        spnCategory.setAdapter(adapter);

                        // Set default selection and update selectedCategoryId
                        spnCategory.setSelection(0); // Select first item by default
                        selectedCategoryId = String.valueOf(categories.get(0).getId()); // Set default category ID

                        // Handle category selection
                        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedCategoryId = String.valueOf(categories.get(position).getId());
                                Log.d("CategorySelection", "Selected Category ID: " + selectedCategoryId); // Debug log
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedCategoryId = ""; // Reset if nothing selected
                            }
                        });
                    } else {
                        Toast.makeText(AddStoryActivity.this, "Danh sách thể loại rỗng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AddStoryActivity.this, "Không tải được thể loại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStatusSpinner() {
        // Assuming you have a list of statuses (e.g., predefined or fetched)
        String[] statuses = {"Ongoing", "Completed", "Hiatus"}; // Example statuses
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statuses
        );
        spnStatus.setAdapter(adapter);

        // Optional: Set default status
        spnStatus.setSelection(0); // Select first status by default

        // Optional: Add listener to track status selection
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("StatusSelection", "Selected Status: " + statuses[position]); // Debug log
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void saveStory() {
        String title = edtTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String status = spnStatus.getSelectedItem() != null ? spnStatus.getSelectedItem().toString() : "";

        // Validate inputs
        if (title.isEmpty() || author.isEmpty() || selectedCategoryId.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug selected values
        Log.d("SaveStory", "Title: " + title + ", Author: " + author + ", Category ID: " + selectedCategoryId + ", Status: " + status);

        try {
            RequestBody titleBody = RequestBody.create(title, MediaType.parse("text/plain"));
            RequestBody authorBody = RequestBody.create(author, MediaType.parse("text/plain"));
            RequestBody genreIdBody = RequestBody.create(selectedCategoryId, MediaType.parse("text/plain"));
            RequestBody descBody = RequestBody.create(description, MediaType.parse("text/plain"));
            RequestBody statusBody = RequestBody.create(status, MediaType.parse("text/plain"));

            MultipartBody.Part imagePart = null;

            if (imageUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] imageData = new byte[inputStream.available()];
                inputStream.read(imageData);
                inputStream.close(); // Close the stream

                RequestBody imageRequest = RequestBody.create(imageData, MediaType.parse("image/*"));
                imagePart = MultipartBody.Part.createFormData("cover_image", "cover.jpg", imageRequest);
            }

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            Call<Story> call;

            if (imagePart != null) {
                call = api.addStory(titleBody, authorBody, genreIdBody, descBody, statusBody, imagePart);
            } else {
                call = api.addStory(titleBody, authorBody, genreIdBody, descBody, statusBody, null);
            }

            call.enqueue(new Callback<Story>() {
                @Override
                public void onResponse(Call<Story> call, Response<Story> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddStoryActivity.this, "Thêm truyện thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddStoryActivity.this, "Thêm thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Story> call, Throwable t) {
                    Toast.makeText(AddStoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }
}
