package com.ndm.stotyreading.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.enitities.story.Chapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<Chapter> chapterList;
    private OnChapterClickListener onChapterClickListener;
    private String lastViewedChapterId;

    public void setLastViewedChapterId(String id) {
        this.lastViewedChapterId = id;
        notifyDataSetChanged();
    }


    // Define the listener interface
    public interface OnChapterClickListener {
        void onChapterClick(String chapterId, List<Chapter> chapterList);
    }


    public ChapterAdapter(List<Chapter> chapterList, OnChapterClickListener listener) {
        this.chapterList = chapterList;
        this.onChapterClickListener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvChapterNumber.setText("Chương " + chapter.getChapterNumber());
        holder.tvChapterTitle.setText(chapter.getTitle());
        holder.tvReleaseDate.setText(chapter.getReleaseDate());
        Log.d("ChapterID", "chapterId: " + chapter.getId() + ", lastViewedId: " + lastViewedChapterId);

        Context context = holder.itemView.getContext();

        if (chapter.isViewed()) {
            if (chapter.getId().equals(lastViewedChapterId)) {
                holder.cardChapter.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow));
            } else {
                holder.cardChapter.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_green));
            }
        } else {
            holder.cardChapter.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onChapterClickListener != null) {
                onChapterClickListener.onChapterClick(chapter.getId(), chapterList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList != null ? chapterList.size() : 0;
    }

    // ViewHolder class
    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterNumber, tvChapterTitle, tvReleaseDate;
        CardView cardChapter;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterNumber = itemView.findViewById(R.id.tvChapterNumber);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
            tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
            cardChapter = itemView.findViewById(R.id.cardChapter);
        }
    }
}