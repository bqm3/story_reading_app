package com.ndm.stotyreading.api;


import com.ndm.stotyreading.enitities.detailChapter.ChapterContentResponse;
import com.ndm.stotyreading.enitities.story.Category;
import com.ndm.stotyreading.enitities.story.Chapter;
import com.ndm.stotyreading.enitities.story.ChapterDetailResponse;
import com.ndm.stotyreading.enitities.story.ChapterImage;
import com.ndm.stotyreading.enitities.story.CommentRequest;
import com.ndm.stotyreading.enitities.story.CommentResponse;
import com.ndm.stotyreading.enitities.user.RegisterRequest;
import com.ndm.stotyreading.enitities.story.Story;
import com.ndm.stotyreading.enitities.story.StoryChapterRespone;
import com.ndm.stotyreading.enitities.story.StoryResponse;
import com.ndm.stotyreading.enitities.user.LoginRequest;
import com.ndm.stotyreading.enitities.user.LoginResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ApiService {

    @GET("auth/profile")
    Call<LoginResponse.User> auth(
            @Header("Authorization") String token
    );

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest body);

    @GET("common/stories")
    Call<StoryResponse> getStories(
            @Header("Authorization") String token
    );

    @GET("common/story/{storyId}/chapters")
    Call<StoryChapterRespone> getStoryChapters(
            @Header("Authorization") String token,
            @Path("storyId") String storyId
    );

    @GET("common/chapter/{chapterId}")
    Call<ChapterContentResponse> getChapterContent(
            @Header("Authorization") String token,
            @Path("chapterId") String chapterId
    );

    @POST("common/chapter/{chapterId}/comment")
    Call<ChapterContentResponse> postCommentChapterContent(
            @Header("Authorization") String token,
            @Path("chapterId") String chapterId,
            @Body CommentRequest commentRequest
    );

    @POST("common/chapter/{chapterId}")
    Call<CommentResponse> markChapterAsRead(
            @Header("Authorization") String token,
            @Path("chapterId") String chapterId
    );

    @GET("common/chapter/{chapterId}/comments")
    Call<CommentResponse> getChapterComments(
            @Header("Authorization") String token,
            @Path("chapterId") String chapterId
    );

    @Multipart
    @POST("stories")
    Call<Story> addStory(
            @Part("title") RequestBody title,
            @Part("author") RequestBody author,
            @Part("genre_id") RequestBody genreId,
            @Part("description") RequestBody description,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part cover_image
    );

    @GET("categories")
    Call<List<Category>> getCategories();


    @Multipart
    @PUT("stories/{id}")
    Call<Story> updateStoryMultipart(
            @Path("id") String storyId,
            @Part("title") RequestBody title,
            @Part("author") RequestBody author,
            @Part("genre_id") RequestBody genreId,
            @Part("description") RequestBody description,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part coverImage // có thể null nếu không chọn ảnh mới
    );



    @Multipart
    @PUT("chapters/{id}")
    Call<Chapter> updateChapter(
            @Path("id") String chapterId,
            @Part("title") RequestBody title,
            @Part("chapter_number") RequestBody chapterNumber,
            @Part("release_date") RequestBody releaseDate,
            @Part("views") RequestBody views,
            @Part List<MultipartBody.Part> images
    );

    @GET("/chapters/{id}/images")
    Call<List<ChapterImage>> getChapterImages(@Path("id") String chapterId);


    @DELETE("chapters/{chapterId}/comments/{commentId}")
    Call<Void> deleteCommentsByChapter(
            @Path("chapterId") String chapterId,
            @Path("commentId") String commentId
    );
    @GET("common/story/{id}")
    Call<StoryChapterRespone> getChaptersByStoryId(@Path("id") String storyId);

    @Multipart
    @POST("chapters")
    Call<Chapter> createChapter(
            @Part("story_id") RequestBody storyId,
            @Part("chapter_number") RequestBody chapterNumber,
            @Part("title") RequestBody title,
            @Part("release_date") RequestBody releaseDate
    );

    @GET("chapters/{id}/detail")
    Call<ChapterDetailResponse> getChapterDetail(@Path("id") String chapterId);

    @Multipart
    @PUT("chapters/{id}")
    Call<Chapter> updateChapterWithImages(
            @Path("id") String chapterId,
            @Part("title") RequestBody title,
            @Part("chapter_number") RequestBody chapterNumber,
            @Part("release_date") RequestBody releaseDate,
            @Part("views") RequestBody views,
            @Part List<MultipartBody.Part> images
    );


    @DELETE("chapters/comments/{id}")
    Call<Void> deleteComment(@Path("id") String commentId);

}
