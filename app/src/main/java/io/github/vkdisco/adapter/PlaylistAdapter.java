package io.github.vkdisco.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

    private OnPlaylistItemClickListener mListener;

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
        holder.bind(mPlaylist.getTrack(position), position, mListener);
    }

    @Override
    public int getItemCount() {
        return mPlaylist.count();
    }

    public interface OnPlaylistItemClickListener {
        void onPlaylistItemClick(View view, int position);
    }

    public void setListener(OnPlaylistItemClickListener listener) {
        this.mListener = listener;
    }

    public static class PlaylistItemHolder extends RecyclerView.ViewHolder {
        private TextView mTVArtist;
        private TextView mTVTitle;
        private TextView mTVDuration;
        private ImageButton mImgBtnMore;

        private OnPlaylistItemClickListener mListener;

        private int mPosition;

        public PlaylistItemHolder(View itemView) {
            super(itemView);
            mTVArtist = ((TextView) itemView.findViewById(R.id.tvArtist));
            mTVTitle = ((TextView) itemView.findViewById(R.id.tvTitle));
            mTVDuration = ((TextView) itemView.findViewById(R.id.tvDuration));
            mImgBtnMore = ((ImageButton) itemView.findViewById(R.id.imgBtnMore));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onPlaylistItemClick(v, mPosition);
                    }
                }
            });
        }

        public void bind(Track track, int position, OnPlaylistItemClickListener listener) {
            TrackMetaData metaData = track.getMetaData();
            if (metaData == null) {
                mTVArtist.setText(R.string.text_label_no_metadata);
                mTVTitle.setText(R.string.text_label_no_metadata);
                mTVDuration.setText("--:--");
                return;
            }

            mTVArtist.setText(metaData.getArtist());
            mTVTitle.setText(metaData.getTitle());

           /* int seconds = (int) (metaData.getDuration() / 1000); // Conversion from ms to seconds
            int minutes = seconds / 60;
            seconds %= 60;*/
            mTVDuration.setText(metaData.getTime());

            mPosition = position;

            mListener = listener;
        }
    }
}
