package io.github.vkdisco.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.adapters.FriendAdapter;
import io.github.vkdisco.models.Friend;

/**
 * Created by tkaczenko on 03.11.16.
 */

public class FriendDialogFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    private FriendAdapter mAdapter;
    private List<Friend> mItems;

    private OnFriendSelectedListener mListener;
    private Friend mUser;

    public interface OnFriendSelectedListener {
        void onFriendSelected(int id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFriendSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement OnFriendSelected");
        }
        loadUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Choose a friend");
        View v = inflater.inflate(R.layout.fragment_friend, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvFriend);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        mItems = new ArrayList<>();

        VKRequest vkRequest = VKApi.users().get(
                VKParameters.from(
                        VKApiConst.FIELDS, "id, first_name, last_name, photo_200")
        );
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                super.onComplete(response);
                VKList<VKApiUser> list = (VKList) response.parsedModel;

                for (VKApiUser user : list) {
                    mItems.add(
                            new Friend(user.id, user.first_name, user.last_name, user.photo_200)
                    );
                }

                mAdapter = new FriendAdapter(getContext(), mItems, new FriendAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Friend item) {
                        mListener.onFriendSelected(item.getId());
                        getDialog().dismiss();
                    }
                });

                mRecyclerView.setAdapter(mAdapter);
            }
        });

        return v;
    }

    private void loadUserInfo() {

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
