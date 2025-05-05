package com.ndm.stotyreading.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.StoryAdapter;
import com.ndm.stotyreading.enitities.story.Story;

import java.util.List;

public class StoryListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo UI
//        recyclerView = findViewById(R.id.recyclerViewStories);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu từ Intent
        String categoryId = getIntent().getStringExtra("category_id");
        String categoryName = getIntent().getStringExtra("category_name");
        storyList = (List<Story>) getIntent().getSerializableExtra("stories");

        // Kiểm tra dữ liệu
//        if (storyList == null) {
//            storyList = new ArrayList<>();
//            Toast.makeText(this, "Không có truyện nào trong thể loại này", Toast.LENGTH_SHORT).show();
//        }

        // Thiết lập tiêu đề
        setTitle(categoryName);

        // Thiết lập adapter
        storyAdapter = new StoryAdapter(this, storyList);
        recyclerView.setAdapter(storyAdapter);
    }
}
