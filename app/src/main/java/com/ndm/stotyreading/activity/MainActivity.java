package com.ndm.stotyreading.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.StoryAdapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Category;
import com.ndm.stotyreading.enitities.story.Story;
import com.ndm.stotyreading.enitities.story.StoryBasic;
import com.ndm.stotyreading.enitities.story.StoryResponse;

import java.io.IOException;
import java.io.Serializable;
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
    private RecyclerView rcvListItem;
    private EditText edtSearch;
    private Button btnSearch;
    private ImageView imgSearch;
    private FrameLayout frameGioHang;

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
        loadCategories();
    }

    private void initUI() {
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        toolbar = findViewById(R.id.toolbar);
        viewFlipper = findViewById(R.id.viewlipper);
        navigationView = findViewById(R.id.navigation_view);
        mDrawerLayout = findViewById(R.id.main);
        rcvListItem = findViewById(R.id.rcv_list_item);
        imgSearch = findViewById(R.id.imgsearch);
        frameGioHang = findViewById(R.id.framegiohang);

        rcvListItem.setLayoutManager(new LinearLayoutManager(this));
        storyAdapter = new StoryAdapter(this, filteredList);
        rcvListItem.setAdapter(storyAdapter);
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
        imgSearch.setOnClickListener(v -> performSearch());
        btnSearch.setOnClickListener(v -> performSearch());
        frameGioHang.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng giỏ hàng chưa được triển khai", Toast.LENGTH_SHORT).show();
        });
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
        String token = getToken();

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getStories("Bearer " + token).enqueue(new Callback<StoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<StoryResponse> call, @NonNull Response<StoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullStoryList = response.body().getData();
                    filteredList.clear();
                    filteredList.addAll(fullStoryList);
                    storyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải danh sách truyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoryResponse> call, @NonNull Throwable t) {
                Log.e("API Error", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addCategoriesToMenu(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi tải danh mục: " + t.getMessage());
            }
        });
    }

    private void addCategoriesToMenu(List<Category> categories) {
        Menu menu = navigationView.getMenu();
        SubMenu categorySubMenu = menu.addSubMenu("Thể loại truyện");

        for (Category category : categories) {
            categorySubMenu.add(category.getName()).setOnMenuItemClickListener(menuItem -> {
                openCategoryStories(category.getId(), category.getName());
                return true;
            });
        }

        navigationView.invalidate();
    }

    private void openCategoryStories(String categoryId, String categoryName) {
        if (categoryId == null || categoryName == null) {
            Toast.makeText(this, "Dữ liệu thể loại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<StoryResponse> call = apiService.getStoriesByCategory(categoryId);

        call.enqueue(new Callback<StoryResponse>() {
            @Override
            public void onResponse(Call<StoryResponse> call, Response<StoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullStoryList = response.body().getData();
                    filteredList.clear();
                    filteredList.addAll(fullStoryList);
                    storyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải danh sách truyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StoryResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi tải danh sách truyện: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFavoriteStories() {
        String token = getToken();

        if (token == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getFavoriteStories("Bearer " + token).enqueue(new Callback<StoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<StoryResponse> call, @NonNull Response<StoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    filteredList.clear();
                    filteredList.addAll(response.body().getData());
                    storyAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Đã tải danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoryResponse> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String error = response.errorBody().string();
                Log.e("API Error", "Chi tiết lỗi: " + error);
            }
        } catch (IOException e) {
            Log.e("ErrorBody", "Lỗi khi đọc error body", e);
        }
        Toast.makeText(this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getString("token", null);
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
        } else if (id == R.id.nav_like) {
            fetchFavoriteStories(); // Gọi API để lấy truyện yêu thích
        } else if (id == R.id.nav_logo_out) {
            clearToken();
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
