package com.msc.app.cook.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.msc.app.cook.R;
import com.msc.app.cook.models.ImageModel;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;
    private static OnItemClickListener onItemClickListener;
    private final Context context;
    private final ArrayList<ImageModel> imageList;

    public ImageAdapter(Context context, ArrayList<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == IMAGE_LIST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list, parent, false);
            return new ImageListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_picker_list, parent, false);
            return new ImagePickerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < 2 ? IMAGE_PICKER : IMAGE_LIST;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == IMAGE_LIST) {
            final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.spinner_load);
            requestOptions.error(R.drawable.ic_baseline_error_24);
            requestOptions.centerCrop();

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageList.get(position).getImage())
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.image);

            if (imageList.get(position).isSelected()) {
                viewHolder.checkBox.setChecked(true);
            } else {

                viewHolder.checkBox.setChecked(false);
            }
        } else {
            ImagePickerViewHolder viewHolder = (ImagePickerViewHolder) holder;
            viewHolder.image.setImageResource(imageList.get(position).getResImg());
            viewHolder.title.setText(imageList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CheckBox checkBox;

        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.circle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public class ImagePickerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ImagePickerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

}