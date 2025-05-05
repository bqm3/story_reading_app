package com.ndm.stotyreading.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
;

import com.bumptech.glide.Glide;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.ChapterAdapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Chapter;
import com.ndm.stotyreading.enitities.story.FavoriteRequest;
import com.ndm.stotyreading.enitities.story.FavoriteResponse;
import com.ndm.stotyreading.enitities.story.Story;
import com.ndm.stotyreading.enitities.story.StoryBasic;
import com.ndm.stotyreading.enitities.story.StoryChapterRespone;
import com.ndm.stotyreading.fragment.ChapterDetailFragment;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityStoryChapter extends AppCompatActivity {

    private static final String ARG_STORY_ID = "story_id";
    private String storyId;
    private TextView tvTitle, tvAuthor, tvStoryId, tvGenreId, tvDescription, tvStatus, tvViews, tvRating;
    private ImageView ivCoverImage;
    private RecyclerView rvChapters;
    private Button btnFavorite;
    private boolean isFavorited = false;
    private ChapterAdapter chapterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_chapter);
        storyId = getIntent().getStringExtra(ARG_STORY_ID);


        // Ánh xạ View
        ivCoverImage = findViewById(R.id.ivCoverImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvGenreId = findViewById(R.id.tvGenreId);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        tvStoryId = findViewById(R.id.tvStoryId);
        tvViews = findViewById(R.id.tvViews);
        tvRating = findViewById(R.id.tvRating);
        rvChapters = findViewById(R.id.rvChapters);
        btnFavorite = findViewById(R.id.btnFavorite);
        // Setup RecyclerView
        rvChapters.setLayoutManager(new LinearLayoutManager(this));

        // Gọi API lấy thông tin truyện
        loadStoryDetails();
        btnFavorite.setOnClickListener(v -> handleFavoriteToggle());
//        isWebViewAvailable();
        
    }

    private void handleFavoriteToggle() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            Log.d("Message ERR", token);
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Create the FavoriteRequest object to toggle the favorite status
        FavoriteRequest request = new FavoriteRequest(storyId);

        // Send the API request to toggle favorite status
        apiService.toggleChapterFavorite("Bearer " + token, request).enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean newStatus = response.body().isFavorited();
                    isFavorited = newStatus;
                    btnFavorite.setText(newStatus ? "Đã yêu thích" : "Yêu thích");
                    Toast.makeText(ActivityStoryChapter.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("API_ERROR", "Code: " + response.code() + ", Body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ActivityStoryChapter.this, "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                Toast.makeText(ActivityStoryChapter.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String findLastViewedChapterId(List<Chapter> chapterList) {
        String lastViewedId = null;
        ZonedDateTime latestViewedAt = null;

        for (Chapter chapter : chapterList) {
            String viewedAt = chapter.getViewedAt();
            Log.d("Chapter", "chapterId: " + chapter.getId() + ", viewedAt: " + viewedAt);

            if (viewedAt != null) {
                ZonedDateTime viewedAtDateTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    viewedAtDateTime = ZonedDateTime.parse(viewedAt, DateTimeFormatter.ISO_DATE_TIME);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (latestViewedAt == null || viewedAtDateTime.isAfter(latestViewedAt)) {
                        latestViewedAt = viewedAtDateTime;
                        lastViewedId = chapter.getId();
                        Log.d("Last Viewed", "Last viewed chapter ID: " + lastViewedId);
                    }
                }
            }
        }

        return lastViewedId;
    }


    private void loadStoryDetails() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getStoryChapters("Bearer " + token, storyId).enqueue(new Callback<StoryChapterRespone>() {
            @Override
            public void onResponse(@NonNull Call<StoryChapterRespone> call, @NonNull Response<StoryChapterRespone> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StoryChapterRespone storyChapterRespone = response.body();

                    if (storyChapterRespone.isSuccess()) {
                        StoryBasic story = storyChapterRespone.getStory();
                        Log.d("Story ID", "ID: " + story.getId());
                        Log.d("Story Title", "Title: " + story.getTitle());
                        Log.d("Story Author", "Author: " + story.getAuthor());
                        Log.d("Story Genre", "Genre ID: " + story.getGenreId());
                        Log.d("Story Description", "Description: " + story.getDescription());
                        Log.d("Story Status", "Status: " + story.getStatus());
                        Log.d("Story Cover Image", "Cover Image: " + story.getCoverImage());
                        Log.d("Story isLiked", "Is Liked: " + story.isLiked());


                        // Set the liked status from API response
                        // Check if the story is liked
                        boolean isLiked = story.isLiked();

// Set the liked status from API response
                        story.setLiked(isLiked);

// Update the button text and color based on isLiked status
                        if (isLiked) {
                            // If liked, set the text to "Hủy yêu thích" and change the color to indicate 'liked'
                            btnFavorite.setText("Bỏ yêu thích");
                            btnFavorite.setBackgroundColor(ContextCompat.getColor(ActivityStoryChapter.this, R.color.orange3)); // Change color to your liked color
                            btnFavorite.setTextColor(ContextCompat.getColor(ActivityStoryChapter.this, R.color.white)); // Optionally set text color to white
                        } else {
                            // If not liked, set the text to "Yêu thích" and change the color to the default color
                            btnFavorite.setText("Yêu thích");
                            btnFavorite.setBackgroundColor(ContextCompat.getColor(ActivityStoryChapter.this, R.color.light_green)); // Default color for button
                            btnFavorite.setTextColor(ContextCompat.getColor(ActivityStoryChapter.this, R.color.black)); // Default text color
                        }

// Glide for loading cover image and other code continues...


                        // Hiển thị thông tin truyện
                        tvStoryId.setText("ID: " + story.getId());
                        tvTitle.setText(story.getTitle());
                        tvAuthor.setText("Tác giả: " + story.getAuthor());
                        tvGenreId.setText("Thể loại ID: " + story.getGenreId());
                        tvDescription.setText("Mô tả: " + story.getDescription());
                        tvStatus.setText("Trạng thái: " + story.getStatus());

                        // Tải ảnh bìa bằng Glide
                        Glide.with(ActivityStoryChapter.this)
                                .load(story.getCoverImage())
                                .into(ivCoverImage);

                        // Hiển thị danh sách chương
                        List<Chapter> chapters = storyChapterRespone.getChapters();
                        String lastViewedId = findLastViewedChapterId(chapters);
                        chapterAdapter = new ChapterAdapter(chapters, ActivityStoryChapter.this::onChapterClick);
                        chapterAdapter.setLastViewedChapterId(lastViewedId);
                        rvChapters.setAdapter(chapterAdapter);

                    } else {
                        Toast.makeText(ActivityStoryChapter.this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityStoryChapter.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoryChapterRespone> call, @NonNull Throwable t) {
                Toast.makeText(ActivityStoryChapter.this, "Lỗi khi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Lỗi khi tải dữ liệu:",  t.getMessage());
            }
        });
    }


    public void onChapterClick(String chapterID, List<Chapter> chapterList) {
        // Show the fragment container and hide the main content
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.mainContentScrollView).setVisibility(View.GONE);

        // Replace the FrameLayout with ChapterDetailFragment
        Fragment chapterDetailFragment = ChapterDetailFragment.newInstance(chapterID, chapterList);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, chapterDetailFragment)
                .addToBackStack(null) // Add to back stack to allow back navigation
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.fragmentContainer).getVisibility() == View.VISIBLE) {
            // If the fragment container is visible, hide it and show the main content
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            findViewById(R.id.mainContentScrollView).setVisibility(View.VISIBLE);
            super.onBackPressed(); // This will pop the fragment from the back stack
        } else {
            // Normal back button behavior
            super.onBackPressed();
        }
    }

}