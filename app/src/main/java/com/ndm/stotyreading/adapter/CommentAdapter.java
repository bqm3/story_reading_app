package com.ndm.stotyreading.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.stotyreading.R;
import com.ndm.stotyreading.enitities.story.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private OnCommentClickListener listener;
    public CommentAdapter(List<Comment> commentList, OnCommentClickListener listener) {
        this.commentList = commentList;
        this.listener  = listener ;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        if (comment.getUser() != null) {
            holder.tvUserName.setText(comment.getUser().getUserName());
        } else {
            holder.tvUserName.setText("Unknown");
        }

        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(getTimeAgo(comment.getCreatedAt())); // ✅ Cập nhật thành thời gian trước

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(comment.getId());
            }
        });

    }

    private String getTimeAgo(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(dateString);
            if (date == null) return "Không xác định";

            long time = date.getTime();
            long now = System.currentTimeMillis();
            long diff = now - time;

            if (diff < TimeUnit.MINUTES.toMillis(1)) {
                return (diff / 1000) + " giây trước";
            } else if (diff < TimeUnit.HOURS.toMillis(1)) {
                return (diff / TimeUnit.MINUTES.toMillis(1)) + " phút trước";
            } else if (diff < TimeUnit.DAYS.toMillis(1)) {
                return (diff / TimeUnit.HOURS.toMillis(1)) + " giờ trước";
            } else {
                return (diff / TimeUnit.DAYS.toMillis(1)) + " ngày trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Không xác định";
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvContent, tvTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
    public interface OnCommentClickListener {
        void onCommentClick(String commentId);
    }


}
