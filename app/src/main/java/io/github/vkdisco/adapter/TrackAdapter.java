package io.github.vkdisco.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.interfaces.OnTrackClickListener;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;

/**
 * Created by tkaczenko on 20.11.16.
 */

public class TrackAdapter extends
        RecyclerView.Adapter<TrackAdapter.TrackViewHolder> implements Filterable {
    private List<Track> mList;
    private List<Track> mOriginalValues;
    private OnTrackClickListener mListener;

    public TrackAdapter(List<Track> list, OnTrackClickListener listener) {
        this.mList = list;
        this.mListener = listener;
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
        holder.bind(mList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<Track> filteredList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(mList);
                }

                if (charSequence == null || charSequence.length() == 0) {
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    String constraint = charSequence.toString().toLowerCase();
                    for (Track track : mOriginalValues) {
                        TrackMetaData metaData = track.getMetaData();
                        String title = metaData.getTitle();
                        String artist = metaData.getArtist();
                        String album = metaData.getAlbum();
                        String year = metaData.getYear();
                        if (title != null && title.toLowerCase().contains(constraint)) {
                            filteredList.add(track);
                        } else if (artist != null && artist.toLowerCase().contains(constraint)) {
                            filteredList.add(track);
                        } else if (album != null && album.toLowerCase().contains(constraint)) {
                            filteredList.add(track);
                        } else if (year != null && year.toLowerCase().contains(constraint)) {
                            filteredList.add(track);
                        }
                    }
                    results.count = filteredList.size();
                    results.values = filteredList;
                }
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mList = (List<Track>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView artist, title, duration;
        private ImageButton button;

        TrackViewHolder(View itemView) {
            super(itemView);
            artist = (TextView) itemView.findViewById(R.id.tvArtist);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            duration = (TextView) itemView.findViewById(R.id.tvDuration);
            button = (ImageButton) itemView.findViewById(R.id.imgBtnMore);
            button.setVisibility(View.INVISIBLE);
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
