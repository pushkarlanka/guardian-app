package com.example.pushkar.guardian;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;

/**
 * Created by pushkar on 1/30/16.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private ArrayList<User> mUsersList;
    private DrawerActivity mDrawerActivity;

    public UserListAdapter(ArrayList<User> list, DrawerActivity drawerActivity) {
        mUsersList = list;
        mDrawerActivity = drawerActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUserName;
        public TextView mStars;
        public TextView mDistance;
        public CircleImageView mImageView;

        public ViewHolder (View v) {
            super(v);
            mUserName = (TextView) v.findViewById(R.id.list_user_name);
            mStars = (TextView) v.findViewById(R.id.list_stars);
            mDistance = (TextView) v.findViewById(R.id.list_distance);
            mImageView = (CircleImageView) v.findViewById(R.id.list_profile_image);
        }
    }


    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        User user = mUsersList.get(position);

        holder.mUserName.setText(user.getName());
        holder.mStars.setText(String.valueOf(user.getStars()));

        holder.mDistance.setText(mDrawerActivity.getDistance(user) + " miles away");

        Picasso.with(mDrawerActivity).load(user.getImageURL()).noFade().into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
