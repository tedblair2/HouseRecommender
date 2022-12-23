package com.example.reaste.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.reaste.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder>{
    private Context context;
    private List<String> imagelinks;

    public EditAdapter(Context context, List<String> imagelinks) {
        this.context = context;
        this.imagelinks = imagelinks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.edit_items,parent,false);
        return new EditAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(imagelinks.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imagelinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image_edit);
        }
    }
}
