package com.curosoft.splashify.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curosoft.splashify.R;
import com.curosoft.splashify.model.OnboardingPage;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.PageVH> {
    private final List<OnboardingPage> pages;

    public OnboardingAdapter(List<OnboardingPage> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public PageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_page, parent, false);
        return new PageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageVH holder, int position) {
        OnboardingPage p = pages.get(position);
        holder.image.setImageResource(p.imageRes);
        holder.title.setText(p.title);
        holder.desc.setText(p.description);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class PageVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView desc;

        PageVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.description);
        }
    }
}
