package io.github.vkdisco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;
import java.util.List;

import io.github.vkdisco.R;
import io.github.vkdisco.adapter.interfaces.OnUserClickListener;

/**
 * Created by tkaczenko on 21.11.16.
 */

public class VKUserAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<VKApiUser> mList;
    private List<VKApiUser> mOriginalValues;
    private OnUserClickListener mListener;

    public VKUserAdapter(Context mContext, List<VKApiUser> list, OnUserClickListener listener) {
        this.mContext = mContext;
        this.mList = list;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public VKApiUser getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mList.get(i).id;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<VKApiUser> filteredList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(mList);
                }

                if (charSequence == null || charSequence.length() == 0) {
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    String constraint = charSequence.toString().toLowerCase();
                    for (VKApiUser track : mOriginalValues) {
                        String lastName = track.last_name;
                        String firstName = track.first_name;
                        if (lastName != null && lastName.toLowerCase().contains(constraint)) {
                            filteredList.add(track);
                        } else if (firstName != null && firstName.toLowerCase().contains(constraint)) {
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
                mList = (List<VKApiUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
