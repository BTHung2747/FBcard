package com.example.englishflashcards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private Context context;
    private List<Topic> topicList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Topic topic);
    }

    public TopicAdapter(Context context, List<Topic> topicList, OnItemClickListener listener) {
        this.context = context;
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);

        holder.tvName.setText(topic.getName());

        if (topic.getName() != null && !topic.getName().isEmpty()) {
            char firstLetter = topic.getName().charAt(0);
            holder.tvFirstLetter.setText(String.valueOf(firstLetter));
        }


        // Chỉ hoạt động khi bạn dùng Chuột hoặc Emulator trên máy tính
        holder.itemView.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        // Khi con chuột đi VÀO ô -> Phóng to
                        v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
                        // Đổi màu nền nhẹ để nổi bật (Tuỳ chọn)
                        v.setAlpha(0.9f);
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                        // Khi con chuột đi RA KHỎI ô -> Thu nhỏ về cũ
                        v.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
                        // Trả lại độ sáng cũ
                        v.setAlpha(1.0f);
                        break;
                }
                return false; // Trả về false để không chặn các sự kiện khác
            }
        });

        // --- Giữ lại hiệu ứng khi Bấm (Touch) cho chắc chắn ---
        holder.itemView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // Khi bấm chuột xuống
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:   // Khi thả chuột ra
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        // Sự kiện Click để chuyển trang
        holder.itemView.setOnClickListener(v -> listener.onItemClick(topic));
    }

    @Override
    public int getItemCount() {
        return (topicList != null) ? topicList.size() : 0;
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFirstLetter;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTopicName);
            tvFirstLetter = itemView.findViewById(R.id.tvFirstLetter);
        }
    }
}