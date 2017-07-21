package com.autumnbytes.porukakaoslika;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<PozadineFirebase> pozadine = new ArrayList<>();

    public GalleryAdapter(Context context, ArrayList<PozadineFirebase> pozadine ) {
        this.context = context;
        this.pozadine = pozadine;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_cell_layout, parent, false);
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Glide.with(context)
                .load(pozadine.get(position).getUrl())
                .override(100, 250)
                .dontTransform()
                .crossFade()
                .into(((MyItemHolder) holder).cellImageView);
    }

    @Override
    public int getItemCount() {
        return pozadine.size();
    }

    public class MyItemHolder extends RecyclerView.ViewHolder {
        private ImageView cellImageView;

        public MyItemHolder(View itemView) {
            super(itemView);
            cellImageView = (ImageView) itemView.findViewById (R.id.cell_image_view);
        }
    }
}