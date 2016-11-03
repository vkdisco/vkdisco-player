package io.github.vkdisco.fragments;

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
import io.github.vkdisco.adapters.AudioAdapter;
import io.github.vkdisco.models.Audio;

/**
 * Created by tkaczenko on 30.10.16.
 */

public class AudioFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private AudioAdapter mAdapter;
    private List<Audio> mItems;

    private OnAudioSelectedListener mListener;

    public interface OnAudioSelectedListener {
        void onAudioSelected(String url);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnAudioSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + "must implement OnAudioSelected");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_audio, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvAudio);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        mItems = new ArrayList<>();

        int id = getArguments().getInt("id");
        VKRequest vkRequest = VKApi.audio().get(VKParameters.from(VKApiConst.USER_ID,
                (id == 0) ? VKAccessToken.USER_ID : id));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList<VKApiAudio> list = (VKList) response.parsedModel;

                for (VKApiAudio audio : list) {
                    mItems.add(new Audio(
                            audio.title, audio.url, audio.artist, audio.duration));
                }

                mAdapter = new AudioAdapter(mItems, new AudioAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Audio item) {
                        mListener.onAudioSelected(item.getUrl());
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        return v;
    }
}
