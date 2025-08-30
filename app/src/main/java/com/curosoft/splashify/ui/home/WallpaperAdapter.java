package com.curosoft.splashify.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.curosoft.splashify.R;
import com.curosoft.splashify.model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.VH> {
    public interface Listener {
        void onToggleFavorite(Wallpaper item);
        boolean isFavorite(String id);
    }
    private final List<Wallpaper> items = new ArrayList<>();
    private Listener listener;

    public WallpaperAdapter setListener(Listener l) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Wallpaper wp = items.get(position);
        String url = wp.url;
        Glide.with(holder.image.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.image);

        // favorite icon
        if (listener != null) {
            boolean fav = listener.isFavorite(wp.id);
            holder.btnFavorite.setImageResource(fav ? R.drawable.ic_favorite_24 : R.drawable.ic_favorite_border_24);
            holder.btnFavorite.setOnClickListener(v -> {
                listener.onToggleFavorite(wp);
                boolean nowFav = listener.isFavorite(wp.id);
                holder.btnFavorite.setImageResource(nowFav ? R.drawable.ic_favorite_24 : R.drawable.ic_favorite_border_24);
            });
        }

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
        ImageView btnFavorite;
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgWallpaper);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
