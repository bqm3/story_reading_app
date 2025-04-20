package com.ndm.stotyreading.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ndm.stotyreading.R;
import com.ndm.stotyreading.adapter.ChapterAdapter;
import com.ndm.stotyreading.api.ApiService;
import com.ndm.stotyreading.api.RetrofitClient;
import com.ndm.stotyreading.enitities.story.Category;
import com.ndm.stotyreading.enitities.story.Chapter;
import com.ndm.stotyreading.enitities.story.Story;
import com.ndm.stotyreading.enitities.story.StoryBasic;
import com.ndm.stotyreading.enitities.story.StoryChapterRespone;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditStoryActivity extends AppCompatActivity {

    private EditText edtTitle, edtAuthor, edtDescription;
    private Spinner spnStatus, spnCategory;
    private Button btnChooseImage, btnUpdate;
    private ImageView imgPreview;
    private Uri imageUri;
    private StoryBasic story;
    private List<Category> categories = new ArrayList<>();
    private String selectedCategoryId = "";
    private RecyclerView rvChapters;
    private ChapterAdapter chapterAdapter;
    private String storyId;

    @Override
    protected void onResume() {
        super.onResume();

        // Ưu tiên giữ lại story đã có → chỉ cần cập nhật danh sách chương
        if (story != null && story.getId() != null) {
            fetchUpdatedChapters();
            fetchStoryFromApi(storyId);
        }
        // Nếu chỉ có storyId thì fetch full từ API
        else if (storyId != null) {
            fetchStoryFromApi(storyId);
        }
        // Nếu không có gì thì thông báo lỗi
        else {
            Toast.makeText(this, "Không thể tải thông tin truyện", Toast.LENGTH_SHORT).show();
        }
    }



    private void fetchStoryFromApi(String id) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getChaptersByStoryId(id).enqueue(new Callback<StoryChapterRespone>() {
            @Override
            public void onResponse(Call<StoryChapterRespone> call, Response<StoryChapterRespone> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    StoryBasic apiStory = response.body().getStory();

                    // Tạo và mapping từ Story sang StoryBasic
                    story = new StoryBasic();
                    story.setId(apiStory.getId());
                    story.setTitle(apiStory.getTitle());
                    story.setAuthor(apiStory.getAuthor());
                    story.setDescription(apiStory.getDescription());
                    story.setCover_image(apiStory.getCoverImage());
                    story.setStatus(apiStory.getStatus());
                    story.setChapters(response.body().getChapters());

                    storyId = story.getId();
                    populateStoryToUI(story);

                } else {
                    Toast.makeText(EditStoryActivity.this, "Không thể tải truyện", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<StoryChapterRespone> call, Throwable t) {
                Toast.makeText(EditStoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void populateStoryToUI(StoryBasic story) {
        edtTitle.setText(story.getTitle());
        edtAuthor.setText(story.getAuthor());
        edtDescription.setText(story.getDescription());

        Glide.with(this)
                .load(story.getCoverImage())
                .error(R.drawable.error_image)
                .into(imgPreview);

        // Spinner
        String[] statuses = getResources().getStringArray(R.array.story_status);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(story.getStatus())) {
                spnStatus.setSelection(i);
                break;
            }
        }

        // RecyclerView
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setNestedScrollingEnabled(false);
        chapterAdapter = new ChapterAdapter(story.getChapters(), (chapterId, chapters) -> {
            Chapter selected = findChapterById(chapterId, chapters);
            if (selected != null) {
                Intent intent = new Intent(this, EditChapterActivity.class);
                intent.putExtra("chapter", selected);
                intent.putExtra("story_id", story.getId());
                editChapterLauncher.launch(intent);
            }
        });
        rvChapters.setAdapter(chapterAdapter);
        chapterAdapter.notifyDataSetChanged();
    }


    private void setupChapterList(StoryBasic story) {
        chapterAdapter = new ChapterAdapter(story.getChapters(), (chapterId, chapters) -> {
            Chapter selected = findChapterById(chapterId, chapters);
            if (selected != null) {
                Intent intent = new Intent(this, EditChapterActivity.class);
                intent.putExtra("chapter", selected);
                intent.putExtra("story_id", storyId);
                editChapterLauncher.launch(intent);
            }
        });

        rvChapters.setAdapter(chapterAdapter);
        chapterAdapter.notifyDataSetChanged();
    }

    private void fetchUpdatedChapters() {
        if (storyId == null) {
            storyId = (story != null) ? story.getId() : null;
        }

        if (storyId == null) {
            Log.e("CHAPTER_FETCH", "Không thể tải chương vì storyId bị null.");
            Toast.makeText(this, "Không thể tải chương vì thiếu ID truyện", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
//        api.getChaptersByStoryId(storyId).enqueue(new Callback<StoryBasic>() {
//            @Override
//            public void onResponse(Call<StoryBasic> call, Response<StoryBasic> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    story = response.body();
//                    Log.e("CHAPTER_FETCH", String.valueOf(story));
//                    populateStoryToUI(story);
//                } else {
//                    Toast.makeText(EditStoryActivity.this, "Không thể tải truyện", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<StoryBasic> call, Throwable t) {
//                Toast.makeText(EditStoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgPreview.setImageURI(imageUri);
                }
            });
    ActivityResultLauncher<Intent> editChapterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Chapter updated = result.getData().getParcelableExtra("updated_chapter");
                    if (updated != null) updateChapterInList(updated);
                }
            }
    );

    private void updateChapterInList(Chapter updatedChapter) {
        for (int i = 0; i < story.getChapters().size(); i++) {
            if (story.getChapters().get(i).getId().equals(updatedChapter.getId())) {
                story.getChapters().set(i, updatedChapter);
                chapterAdapter.notifyItemChanged(i);
                break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        // Khởi tạo view
        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtDescription = findViewById(R.id.edtDescription);
        spnStatus = findViewById(R.id.spnStatus);
        spnCategory = findViewById(R.id.spnCategory);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        imgPreview = findViewById(R.id.imgPreview);
        btnUpdate = findViewById(R.id.btnUpdate);
        rvChapters = findViewById(R.id.rvChapters);
        Button btnAddChapter = findViewById(R.id.btnAddChapter);

        // Lấy dữ liệu từ intent
        storyId = getIntent().getStringExtra("story_id");
        story = getIntent().getParcelableExtra("story_basic");

        if (storyId == null && story != null) {
            storyId = story.getId();
        }

        // Luôn gọi API để lấy thông tin cập nhật mới nhất
        if (storyId != null) {
            fetchStoryFromApi(storyId);
        } else {
            Toast.makeText(this, "Không có ID truyện", Toast.LENGTH_SHORT).show();
            finish();
        }

        loadCategories();

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnUpdate.setOnClickListener(v -> updateStory());
        btnAddChapter.setOnClickListener(v -> showAddChapterDialog());
    }


    private void loadCategories() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categories = response.body();
                    List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditStoryActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                    spnCategory.setAdapter(adapter);

                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getId().equals(story.getGenreId())) {
                            spnCategory.setSelection(i);
                            selectedCategoryId = categories.get(i).getId();
                            break;
                        }
                    }

                    spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategoryId = categories.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(EditStoryActivity.this, "Không tải được thể loại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStory() {
        if (story.getId() == null && storyId != null) {
            story.setId(storyId);
        }

        String title = edtTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        if (title.isEmpty() || author.isEmpty() || selectedCategoryId.isEmpty()) {
            Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            RequestBody titleBody = RequestBody.create(title, MediaType.parse("text/plain"));
            RequestBody authorBody = RequestBody.create(author, MediaType.parse("text/plain"));
            RequestBody genreBody = RequestBody.create(selectedCategoryId, MediaType.parse("text/plain"));
            RequestBody descBody = RequestBody.create(description, MediaType.parse("text/plain"));
            RequestBody statusBody = RequestBody.create(status, MediaType.parse("text/plain"));

            MultipartBody.Part imagePart = null;
            if (imageUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] imageData = new byte[inputStream.available()];
                inputStream.read(imageData);
                RequestBody imageRequest = RequestBody.create(imageData, MediaType.parse("image/*"));
                imagePart = MultipartBody.Part.createFormData("cover_image", "cover.jpg", imageRequest);
            }
            if (story == null || story.getId() == null) {
                Toast.makeText(this, "Không tìm thấy ID truyện để cập nhật", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getClient().create(ApiService.class);
            api.updateStoryMultipart(
                    story.getId(),
                    titleBody,
                    authorBody,
                    genreBody,
                    descBody,
                    statusBody,
                    imagePart
            ).enqueue(new Callback<Story>() {
                @Override
                public void onResponse(Call<Story> call, Response<Story> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditStoryActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditStoryActivity.this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Story> call, Throwable t) {
                    Toast.makeText(EditStoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddChapterDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_chapter, null);
        EditText edtTitle = view.findViewById(R.id.edtNewChapterTitle);
        EditText edtNumber = view.findViewById(R.id.edtNewChapterNumber);
        EditText edtDate = view.findViewById(R.id.edtNewChapterDate);

        // Đảm bảo storyId được cập nhật
        if (storyId == null && story != null) {
            storyId = story.getId();
        }

        new AlertDialog.Builder(this)
                .setTitle("Thêm chương mới")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String title = edtTitle.getText().toString().trim();
                    String numberStr = edtNumber.getText().toString().trim();
                    String date = edtDate.getText().toString().trim();

                    if (title.isEmpty() || numberStr.isEmpty() || date.isEmpty() || storyId == null) {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int chapterNumber = Integer.parseInt(numberStr);
                        createNewChapter(storyId, title, chapterNumber, date);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Số chương không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private Chapter findChapterById(String chapterId, List<Chapter> chapters) {
        for (Chapter chapter : chapters) {
            if (chapter.getId().equals(chapterId)) {
                return chapter;
            }
        }
        return null;
    }

    private void createNewChapter(String storyIdInput, String title, int chapterNumber, String releaseDate) {
        if (storyId == null) {
            // Gán lại từ biến đầu vào hoặc từ story nếu có
            storyId = storyIdInput != null ? storyIdInput : (story != null ? story.getId() : null);
        }

        if (storyId == null) {
            Toast.makeText(this, "Không tìm thấy ID truyện để tạo chương", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("CREATE_CHAPTER", "storyId: " + storyId);
        Log.d("CREATE_CHAPTER", "title: " + title);
        Log.d("CREATE_CHAPTER", "chapterNumber: " + chapterNumber);
        Log.d("CREATE_CHAPTER", "releaseDate: " + releaseDate);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        RequestBody rbStoryId = RequestBody.create(MediaType.parse("text/plain"), storyId);
        RequestBody rbTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody rbNumber = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chapterNumber));
        RequestBody rbDate = RequestBody.create(MediaType.parse("text/plain"), releaseDate);

        api.createChapter(rbStoryId, rbNumber, rbTitle, rbDate)
                .enqueue(new Callback<Chapter>() {
                    @Override
                    public void onResponse(Call<Chapter> call, Response<Chapter> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditStoryActivity.this, "Tạo chương mới thành công", Toast.LENGTH_SHORT).show();
                            fetchUpdatedChapters();  // gọi lại API để refresh
                        } else {
                            Toast.makeText(EditStoryActivity.this, "Tạo chương lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Chapter> call, Throwable t) {
                        Toast.makeText(EditStoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




//    private void deleteCommentsOfChapter(String chapterId, String commentId) {
//        ApiService api = RetrofitClient.getClient().create(ApiService.class);
//        api.deleteCommentsByChapter(chapterId, commentId).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(EditStoryActivity.this, "Đã xóa toàn bộ comment", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(EditStoryActivity.this, "Lỗi khi xóa comment", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(EditStoryActivity.this, "Không thể kết nối", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}
