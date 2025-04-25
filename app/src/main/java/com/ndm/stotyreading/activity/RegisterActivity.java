package com.ndm.stotyreading.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.user.LoginResponse;
import com.ndm.stotyreading.enitities.user.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText edtUsername, edtPassword, edtFullName, edtAge;
    private Button btnRegister;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init Retrofit
        api = RetrofitClient.getClient().create(ApiService.class);

        // init view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtFullName = findViewById(R.id.edtFullName);
        edtAge      = findViewById(R.id.edtAge);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String u = edtUsername.getText().toString().trim();
        String p = edtPassword.getText().toString().trim();
        String f = edtFullName.getText().toString().trim();
        String ageStr = edtAge.getText().toString().trim();

        if (u.isEmpty() || p.isEmpty() || f.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Age phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest req = new RegisterRequest(u, p, f, age);
        api.register(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResp = response.body();
                    if (loginResp != null) {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        finish(); // quay về LoginActivity
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thất bại: Phản hồi rỗng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Đăng ký thất bại: " + response.message();
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
