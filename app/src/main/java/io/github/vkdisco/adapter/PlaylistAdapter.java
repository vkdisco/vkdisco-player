package io.github.vkdisco.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import io.github.vkdisco.R;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.TrackMetaData;
import io.github.vkdisco.player.Playlist;

/**
 * PlaylistAdapter
 * Adapter for a RecyclerView, which adapts playlist
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistItemHolder> {
    private Playlist mPlaylist;

    public PlaylistAdapter(Playlist playlist) {
        this.mPlaylist = playlist;
    }

    @Override
    public PlaylistItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View playlistItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_item, parent, false);
        return new PlaylistItemHolder(playlistItemView);
    }

    @Override
    public void onBindViewHolder(PlaylistItemHolder holder, int position) {
        holder.bind(mPlaylist.getTrack(position));
    }

    @Override
    public int getItemCount() {
        return mPlaylist.count();
    }


    public static class PlaylistItemHolder extends RecyclerView.ViewHolder {
        private TextView tvArtist;
        private TextView tvTitle;
        private TextView tvDuration;
        private ImageButton imgBtnMore;
        public PlaylistItemHolder(View itemView) {
            super(itemView);
            tvArtist = ((TextView) itemView.findViewById(R.id.tvArtist));
            tvTitle = ((TextView) itemView.findViewById(R.id.tvTitle));
            tvDuration = ((TextView) itemView.findViewById(R.id.tvDuration));
            imgBtnMore = ((ImageButton) itemView.findViewById(R.id.imgBtnMore));
        }

        public void bind(final Track track) {
            TrackMetaData metaData = track.getMetaData();
            if (metaData == null) {
                tvArtist.setText(R.string.text_label_no_metadata);
                tvTitle.setText(R.string.text_label_no_metadata);
                tvDuration.setText("--:--");
                return;
            }
            tvArtist.setText(metaData.getArtist());
            tvTitle.setText(metaData.getTitle());
            int seconds = (int) (metaData.getDuration() / 1000); // Conversion from ms to seconds
            int minutes = seconds / 60;
            seconds %= 60;
            tvDuration.setText(String.format(Locale.getDefault(), "%2d:%2d", minutes, seconds));
        }
    }
}
