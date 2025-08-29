package com.curosoft.splashify.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curosoft.splashify.R;
import com.curosoft.splashify.model.WallpaperItem;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.VH> {
    private final List<WallpaperItem> items;

    public WallpaperAdapter(List<WallpaperItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.image.setImageResource(items.get(position).imageResId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgWallpaper);
        }
    }
}
