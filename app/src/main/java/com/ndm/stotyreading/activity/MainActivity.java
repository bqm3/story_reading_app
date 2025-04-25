package com.ndm.stotyreading.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
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
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ViewFlipper viewFlipper;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private RecyclerView rcv_list_item;
    private EditText edtSearch;
    private Button btnSearch;
    private ImageView imgsearch;
    private FrameLayout framegiohang;

    private StoryAdapter storyAdapter;
    private List<Story> fullStoryList = new ArrayList<>();
    private List<Story> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        setSupportActionBar(toolbar);

        setupDrawer();
        setupViewFlipper();
        setupListeners();
        fetchStoriesFromApi();
    }

    private void initUI() {
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        toolbar = findViewById(R.id.toolbar);
        viewFlipper = findViewById(R.id.viewlipper);
        navigationView = findViewById(R.id.navigation_view);
        mDrawerLayout = findViewById(R.id.main);
        rcv_list_item = findViewById(R.id.rcv_list_item);
        imgsearch = findViewById(R.id.imgsearch);
        framegiohang = findViewById(R.id.framegiohang);
        progressDialog = new ProgressDialog(this);

        rcv_list_item.setLayoutManager(new LinearLayoutManager(this));
        storyAdapter = new StoryAdapter(this, filteredList);
        rcv_list_item.setAdapter(storyAdapter);
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupViewFlipper() {
        List<Integer> images = Arrays.asList(R.drawable.img_slide_1, R.drawable.img_slide_2, R.drawable.img_slide_3);

        for (int resId : images) {
            ImageView img = new ImageView(this);
            img.setImageResource(resId);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(img);
        }

        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_rigth));
    }

    private void setupListeners() {
        imgsearch.setOnClickListener(v -> {
            // mở hoạt động tìm kiếm
            // startActivity(new Intent(this, SearchActivity.class));
        });

        framegiohang.setOnClickListener(v -> {
            // mở hoạt động giỏ hàng
            // startActivity(new Intent(this, CartActivity.class));
        });

        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String keyword = edtSearch.getText().toString().trim().toLowerCase();
        filteredList.clear();

        if (keyword.isEmpty()) {
            filteredList.addAll(fullStoryList);
        } else {
            for (Story story : fullStoryList) {
                if (story.getTitle() != null && story.getTitle().toLowerCase().contains(keyword)) {
                    filteredList.add(story);
                }
            }
        }

        storyAdapter.notifyDataSetChanged();
    }

    private void fetchStoriesFromApi() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<StoryResponse> call = apiService.getStories("Bearer " + token);

        call.enqueue(new Callback<StoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<StoryResponse> call, @NonNull Response<StoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullStoryList = response.body().getData();
                    filteredList.clear();
                    filteredList.addAll(fullStoryList);
                    storyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoryResponse> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearToken() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("token").apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_logo_out) {
            clearToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}






