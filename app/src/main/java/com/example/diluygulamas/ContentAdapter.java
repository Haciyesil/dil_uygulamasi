package com.example.diluygulamas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diluygulamas.model.ContentItem;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private List<ContentItem> contentItems;
    private boolean isColorCategory;

    public ContentAdapter(List<ContentItem> contentItems, boolean isColorCategory) {
        this.contentItems = contentItems;
        this.isColorCategory = isColorCategory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentItem item = contentItems.get(position);
        holder.bind(item, isColorCategory);
    }

    @Override
    public int getItemCount() {
        return contentItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView originalTextView;
        private TextView translatedTextView;
        private TextView pronunciationTextView;
        private View colorPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            originalTextView = itemView.findViewById(R.id.originalText);
            translatedTextView = itemView.findViewById(R.id.translatedText);
            pronunciationTextView = itemView.findViewById(R.id.pronunciationText);
            colorPreview = itemView.findViewById(R.id.colorPreview);
        }

        public void bind(ContentItem item, boolean isColorCategory) {
            originalTextView.setText(item.getOriginalText());
            translatedTextView.setText(item.getTranslatedText());

            if (item.getPronunciation() != null && !item.getPronunciation().isEmpty()) {
                pronunciationTextView.setText("/" + item.getPronunciation() + "/");
                pronunciationTextView.setVisibility(View.VISIBLE);
            } else {
                pronunciationTextView.setVisibility(View.GONE);
            }

            // Renk önizlemeyi sadece renkler kategorisinde göster
            if (isColorCategory) {
                int red = item.getRed();
                int green = item.getGreen();
                int blue = item.getBlue();
                int color = android.graphics.Color.rgb(red, green, blue);
                colorPreview.setBackgroundColor(color);
                colorPreview.setVisibility(View.VISIBLE);
            } else {
                colorPreview.setVisibility(View.GONE);
            }
        }
    }
}