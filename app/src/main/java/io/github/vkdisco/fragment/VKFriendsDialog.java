package io.github.vkdisco.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vk.sdk.VKAccessToken;
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
import io.github.vkdisco.adapter.VKUserAdapter;
import io.github.vkdisco.adapter.interfaces.OnUserClickListener;
import io.github.vkdisco.fragment.interfaces.OnUserSelectedListener;

/**
 * Created by tkaczenko on 21.11.16.
 */

public class VKFriendsDialog extends DialogFragment {
    private static final String requestParams = "id, first_name, last_name, photo_200";

    private ListView mListView;
    private OnUserSelectedListener mListener;

    private List<VKApiUser> mData;
    private VKUserAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnUserSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement " +
                    "OnUserSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        loadUserInfo(mData);
        loadListOfFriends(mData);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_choose_user, null);
        mListView = (ListView) v.findViewById(R.id.lvUsers);

        mAdapter = new VKUserAdapter(getContext(), mData, new OnUserClickListener() {
            @Override
            public void onUserClick(VKApiUser vkApiUser) {
                mListener.onUserSelected(vkApiUser);
                getDialog().dismiss();
            }
        });
        mListView.setAdapter(mAdapter);

        builder.setView(v)
                .setTitle(R.string.title_vk_friends_dialog);

        return builder.create();
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

    private void loadUserInfo(final List<VKApiUser> users) {
        VKRequest vkRequest = VKApi.users().get(
                VKParameters.from(
                        VKApiConst.USER_ID, VKAccessToken.USER_ID,
                        VKApiConst.FIELDS, requestParams
                )
        );
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList<VKApiUser> list = (VKList) response.parsedModel;
                for (VKApiUser user : list) {
                    users.add(user);
                    break;
                }
            }
        });
    }

    private void loadListOfFriends(final List<VKApiUser> users) {
        VKRequest vkRequest = VKApi.friends().get(
                VKParameters.from(
                        VKApiConst.FIELDS, requestParams
                )
        );
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                VKList<VKApiUser> list = (VKList) response.parsedModel;
                for (VKApiUser user : list) {
                    users.add(user);
                }
            }
        });
    }

}
