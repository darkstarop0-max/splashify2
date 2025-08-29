package com.curosoft.splashify.ui.home;

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
    private final List<Wallpaper> items = new ArrayList<>();

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
        String url = items.get(position).url;
        Glide.with(holder.image.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.image);
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
