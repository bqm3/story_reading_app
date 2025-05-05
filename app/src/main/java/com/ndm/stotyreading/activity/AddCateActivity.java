package com.ndm.stotyreading.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Category;
import com.ndm.stotyreading.enitities.story.CategoryRequest;
import com.ndm.stotyreading.enitities.story.Story;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCateActivity extends AppCompatActivity {
    private EditText editCate, editDesc;
    private Button  btnSave;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cate);

        editCate = findViewById(R.id.edtTitle);
        editDesc = findViewById(R.id.edtDesc);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveCate());
    }

    private void saveCate() {
        String title = editCate.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Tạo đối tượng JSON body
            CategoryRequest request = new CategoryRequest(title, desc);

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            Call<Category> call = api.addCate(request);

            call.enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddCateActivity.this, "Thêm category thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddCateActivity.this, "Thêm thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Category> call, Throwable t) {
                    Toast.makeText(AddCateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }
}
