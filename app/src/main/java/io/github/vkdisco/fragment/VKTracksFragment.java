package io.github.vkdisco.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.TrackAdapter;
import io.github.vkdisco.adapter.interfaces.OnTrackClickListener;
import io.github.vkdisco.fragment.interfaces.OnTrackSelectedListener;
import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.VKTrack;

/**
 * Created by tkaczenko on 20.11.16.
 */

public class VKTracksFragment extends Fragment {
    private RecyclerView mRecyclerView;

    private List<Track> mData;
    private TrackAdapter mAdapter;
    private OnTrackSelectedListener mListener;

    private int userID;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnTrackSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + "must implement OnVKTrackSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getInt("user_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvTracks);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        mData = new ArrayList<>();

        loadListOfVKTracks();

        return v;
    }

    private void loadListOfVKTracks() {
        VKRequest vkRequest = VKApi.audio().get(VKParameters.from(VKApiConst.USER_ID,
                (userID == 0) ? VKAccessToken.USER_ID : userID));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList<VKApiAudio> list = (VKList) response.parsedModel;

                for (VKApiAudio audio : list) {
                    mData.add(
                            new VKTrack(audio)
                    );
                }

                mAdapter = new TrackAdapter(mData, new OnTrackClickListener() {
                    @Override
                    public void onTrackClick(Track track) {
                        mListener.onTrackSelected(track);
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }
}
