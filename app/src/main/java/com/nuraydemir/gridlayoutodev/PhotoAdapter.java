package com.nuraydemir.gridlayoutodev;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuraydemir.gridlayoutodev.databinding.RecycleRowBinding;

import java.sql.Blob;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    ArrayList<Photo> photoArrayList;

    public PhotoAdapter(ArrayList<Photo> photoArrayList){
        this.photoArrayList=photoArrayList;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleRowBinding recycleRowBinding=RecycleRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PhotoHolder(recycleRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        //holder.binding.recyclerTextView.setText(artArrayList.get(position).name);
        holder.binding.recyclerViewImageView.setImageBitmap(photoArrayList.get(position).image);
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), activity_photo.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return photoArrayList.size();//Photoarraylist içinde kaç eleman varsa onu göstereceğim

    }

    public class PhotoHolder extends RecyclerView.ViewHolder{
        private RecycleRowBinding binding;
        public PhotoHolder(RecycleRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }
    }
}
