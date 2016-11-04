package io.github.vkdisco.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.models.Audio;

/**
 * Created by tkaczenko on 30.10.16.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.PopularViewHolder> {
    private List<Audio> mData;
    private OnItemClickListener mListener;

    public AudioAdapter(List<Audio> items, OnItemClickListener listener) {
        this.mData = items;
        this.mListener = listener;
    }

    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PopularViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(PopularViewHolder holder, final int position) {
        holder.bind(mData.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Audio item);
    }

    public class PopularViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, time, curPos;

        public PopularViewHolder(View item) {
            super(item);
            title = (TextView) item.findViewById(R.id.tv_title);
            artist = (TextView) item.findViewById(R.id.tv_artist);
            time = (TextView) item.findViewById(R.id.tv_time);
            curPos = (TextView) item.findViewById(R.id.time);
        }

        public void bind(final Audio audio, final OnItemClickListener listener) {
            title.setText(audio.getTitle());
            artist.setText(audio.getArtist());
            time.setText(audio.getTime());
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(audio);
                }
            });
        }
    }
}
