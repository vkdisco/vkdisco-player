package io.github.vkdisco.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.models.Friend;

/**
 * Created by tkaczenko on 03.11.16.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private Context context;
    private List<Friend> mData;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Friend item);
    }

    public FriendAdapter(Context context, List<Friend> mData, OnItemClickListener mListener) {
        this.context = context;
        this.mData = mData;
        this.mListener = mListener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_friend, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.bind(mData.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView photo, city;
        TextView name;

        public FriendViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            name = (TextView) itemView.findViewById(R.id.tv_name);
        }

        public void bind(final Friend friend, final OnItemClickListener listener) {
            name.setText(friend.getFirstName() + " " + friend.getLastName());
            Picasso.with(context)
                    .load(friend.getPhotoUrl())
                    .into(photo);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(friend);
                }
            });
        }
    }
}
