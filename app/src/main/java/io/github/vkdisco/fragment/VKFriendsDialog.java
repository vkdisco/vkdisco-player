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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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
    private View mHeader;
    private OnUserSelectedListener mListener;

    private VKApiUser mUser;
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
        loadUserInfo();
        loadListOfFriends();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_choose_user, null);
        mListView = (ListView) v.findViewById(R.id.lvUsers);

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

    private void loadUserInfo() {
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
                for (VKApiUser vkApiUser : list) {
                    mUser = vkApiUser;
                    break;
                }
                setupHeader(mUser);
            }
        });
    }

    private void loadListOfFriends() {
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
                    mData.add(user);
                }
                mAdapter = new VKUserAdapter(getContext(), mData, new OnUserClickListener() {
                    @Override
                    public void onUserClick(VKApiUser vkApiUser) {
                        mListener.onUserSelected(vkApiUser);
                        getDialog().dismiss();
                    }
                });
                mListView.setAdapter(mAdapter);
            }
        });
    }

    private void setupHeader(final VKApiUser user) {
        mHeader = getActivity().getLayoutInflater().inflate(R.layout.item_vk_user, null);

        ImageView photo = (ImageView) mHeader.findViewById(R.id.ivPhoto);
        TextView name = (TextView) mHeader.findViewById(R.id.tv_name);

        name.setText(user.first_name + " " + user.last_name);
        Picasso.with(getContext())
                .load(user.photo_200)
                .into(photo);

        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onUserSelected(user);
            }
        });
        mListView.addHeaderView(mHeader);
    }

}
