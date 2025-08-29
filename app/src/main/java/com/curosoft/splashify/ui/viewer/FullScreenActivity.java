package com.curosoft.splashify.ui.viewer;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.curosoft.splashify.R;
import com.google.android.material.button.MaterialButton;

import com.github.chrisbanes.photoview.PhotoView;
import androidx.core.content.FileProvider;

public class FullScreenActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URL = "extra_image_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fullscreen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Preview");
        }

    Intent intent = getIntent();
    String url = intent != null ? intent.getStringExtra(EXTRA_IMAGE_URL) : null;

        PhotoView photoView = findViewById(R.id.photoView);
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Image URL missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(photoView);

    MaterialButton btnSet = findViewById(R.id.btnSetWallpaper);
    MaterialButton btnDownload = findViewById(R.id.btnDownload);
    MaterialButton btnShare = findViewById(R.id.btnShare);

    btnSet.setOnClickListener(v -> applyAsWallpaper(url));
    btnDownload.setOnClickListener(v -> downloadImage(url));
    btnShare.setOnClickListener(v -> shareImage(url));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyAsWallpaper(String url) {
        if (url == null) return;
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            WallpaperManager wm = WallpaperManager.getInstance(FullScreenActivity.this);
                            wm.setBitmap(resource);
                            Toast.makeText(FullScreenActivity.this, "Wallpaper applied successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(FullScreenActivity.this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }

    private void downloadImage(String url) {
        if (url == null) return;
        try {
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request req = new DownloadManager.Request(uri);
            req.setTitle("Splashify Wallpaper");
            req.setDescription("Downloading...");
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.allowScanningByMediaScanner();
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Splashify/" + System.currentTimeMillis() + ".jpg");
            dm.enqueue(req);
            Toast.makeText(this, "Wallpaper saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage(String url) {
        if (url == null) return;
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            java.io.File cacheDir = new java.io.File(getCacheDir(), "images");
                            if (!cacheDir.exists()) cacheDir.mkdirs();
                            java.io.File file = new java.io.File(cacheDir, "share.jpg");
                            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                            fos.flush();
                            fos.close();
                            Uri contentUri = FileProvider.getUriForFile(FullScreenActivity.this, getPackageName() + ".fileprovider", file);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(shareIntent, "Share wallpaper"));
                        } catch (Exception e) {
                            Toast.makeText(FullScreenActivity.this, "Share failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }
}
