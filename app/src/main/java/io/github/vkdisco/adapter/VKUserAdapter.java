package io.github.vkdisco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUser;

import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.interfaces.OnUserClickListener;

/**
 * Created by tkaczenko on 21.11.16.
 */

public class VKUserAdapter extends BaseAdapter {
    private Context mContext;
    private List<VKApiUser> mData;
    private OnUserClickListener mListener;

    public VKUserAdapter(Context mContext, List<VKApiUser> mData, OnUserClickListener mListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public VKApiUser getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mData.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_vk_user, viewGroup, false);
        }
        final VKApiUser vkApiUser = getItem(i);

        ImageView photo = (ImageView) v.findViewById(R.id.ivPhoto);
        TextView name = (TextView) v.findViewById(R.id.tv_name);

        name.setText(vkApiUser.first_name + " " + vkApiUser.last_name);
        Picasso.with(mContext)
                .load(vkApiUser.photo_200)
                .into(photo);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onUserClick(vkApiUser);
            }
        });

        return v;
    }
}
