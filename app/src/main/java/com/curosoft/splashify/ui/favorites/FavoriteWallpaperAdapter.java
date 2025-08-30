package com.curosoft.splashify.ui.favorites;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.curosoft.splashify.R;
import com.curosoft.splashify.model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteWallpaperAdapter extends RecyclerView.Adapter<FavoriteWallpaperAdapter.VH> {
    
    public interface Listener {
        void onToggleFavorite(Wallpaper item);
        boolean isFavorite(String id);
        void onDeleteFavorite(Wallpaper item);
    }
    
    private final List<Wallpaper> items = new ArrayList<>();
    private Listener listener;

    public FavoriteWallpaperAdapter setListener(Listener l) {
        this.listener = l;
        return this;
    }

    public void submitList(List<Wallpaper> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_favorite_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Wallpaper wp = items.get(position);
        String url = wp.url;
        
        // Load image
        Glide.with(holder.image.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.image);

        // Set title if available
        if (holder.title != null && wp.title != null && !wp.title.isEmpty()) {
            holder.title.setText(wp.title);
            holder.title.setVisibility(View.VISIBLE);
        } else if (holder.title != null) {
            holder.title.setVisibility(View.GONE);
        }

        // Setup favorite button
        if (listener != null) {
            boolean fav = listener.isFavorite(wp.id);
            holder.btnFavorite.setImageResource(fav ? R.drawable.ic_favorite_24 : R.drawable.ic_favorite_border_24);
            holder.btnFavorite.setOnClickListener(v -> {
                listener.onToggleFavorite(wp);
                boolean nowFav = listener.isFavorite(wp.id);
                holder.btnFavorite.setImageResource(nowFav ? R.drawable.ic_favorite_24 : R.drawable.ic_favorite_border_24);
            });
        }

        // Setup delete button
        if (listener != null && holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                // Show confirmation dialog
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.delete)
                        .setMessage(R.string.confirm_delete)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            listener.onDeleteFavorite(wp);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_delete_24)
                        .show();
            });
        }

        // Item click opens fullscreen view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), com.curosoft.splashify.ui.viewer.FullScreenActivity.class);
            intent.putExtra(com.curosoft.splashify.ui.viewer.FullScreenActivity.EXTRA_IMAGE_URL, url);
            intent.putExtra(com.curosoft.splashify.ui.viewer.FullScreenActivity.EXTRA_IMAGE_ID, wp.id);
            intent.putExtra(com.curosoft.splashify.ui.viewer.FullScreenActivity.EXTRA_IMAGE_TITLE, wp.title);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton btnFavorite;
        ImageButton btnDelete;
        TextView title;
        
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgWallpaper);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            title = itemView.findViewById(R.id.tvTitle);
        }
    }
}
