package com.ndm.stotyreading.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.StoryAdapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Story;
import com.ndm.stotyreading.enitities.story.StoryResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button btnAddStory;
    private Toolbar toolbar;
    private RecyclerView rvStoryList;
    private StoryAdapter storyAdapter;
    private List<Story> storyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        rvStoryList = findViewById(R.id.rvStoryList);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý truyện");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        storyAdapter = new StoryAdapter(this, storyList);
        rvStoryList.setLayoutManager(new LinearLayoutManager(this));
        rvStoryList.setAdapter(storyAdapter);

        btnAddStory = findViewById(R.id.btnAddStory);
        btnAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddStoryActivity.class);
            startActivity(intent);
        });

        fetchStoryList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStoryList(); // 🔥 gọi lại mỗi khi quay lại màn hình
    }

    private void fetchStoryList() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getStories("Bearer " + token).enqueue(new Callback<StoryResponse>() {
            @Override
            public void onResponse(Call<StoryResponse> call, Response<StoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    storyList.clear();
                    storyList.addAll(response.body().getData());
                    storyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminActivity.this, "Không lấy được danh sách truyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StoryResponse> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_story) {
            startActivity(new Intent(this, AddStoryActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawers();
        return true;
    }
}

