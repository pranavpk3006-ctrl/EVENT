package com.example.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private Context context;
    private List<ImageModel> imageList;

    public GalleryAdapter(Context context, List<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        ImageModel model = imageList.get(position);
        
        Glide.with(context)
                .load(model.getImageUrl())
                // .placeholder(R.drawable.placeholder_image) // Un-comment if you have a placeholder
                .into(holder.imageViewGallery);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewGallery;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Resource ID needs to be matching item_gallery_image.xml
            // Update R import based on your real package name
            imageViewGallery = itemView.findViewById(R.id.imageViewGallery);
        }
    }
}
