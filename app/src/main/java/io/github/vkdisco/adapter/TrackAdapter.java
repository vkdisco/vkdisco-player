package io.github.vkdisco.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.interfaces.OnTrackClickListener;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;

/**
 * Created by tkaczenko on 20.11.16.
 */

public class TrackAdapter extends
        RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private List<Track> mData;
    private OnTrackClickListener mListener;

    public TrackAdapter(List<Track> mData, OnTrackClickListener mListener) {
        this.mData = mData;
        this.mListener = mListener;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrackViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.music_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.bind(mData.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView artist, title, duration;

        TrackViewHolder(View itemView) {
            super(itemView);
            artist = (TextView) itemView.findViewById(R.id.tvArtist);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            duration = (TextView) itemView.findViewById(R.id.tvDuration);
        }

        void bind(final Track track, final OnTrackClickListener listener) {
            TrackMetaData metaData = track.getMetaData();
            if (metaData == null) {
                artist.setText(R.string.text_label_no_metadata);
                title.setText(R.string.text_label_no_metadata);
                duration.setText("--:--");
                return;
            }

            artist.setText(metaData.getArtist());
            title.setText(metaData.getTitle());
            duration.setText(metaData.getTime());
            //// TODO: 20.11.16 Implement three dots action
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onTrackClick(track);
                }
            });
        }
    }
}
