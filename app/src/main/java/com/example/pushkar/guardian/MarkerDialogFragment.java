package com.example.pushkar.guardian;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;

/**
 * Created by pushkar on 1/29/16.
 */
public class MarkerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.marker_dialog, null);

        Bundle bundle = getArguments();

        User user = bundle.getParcelable("user");
        String distance = bundle.getString("distance");

        TextView dialogUserName = (TextView) view.findViewById(R.id.dialog_user_name);
        dialogUserName.setText(user.getName());

        TextView dialogStars = (TextView) view.findViewById(R.id.dialog_stars);
        dialogStars.setText(String.valueOf(user.getStars()));

        TextView dialogDistance = (TextView) view.findViewById(R.id.dialog_distance);
        dialogDistance.setText(distance + " miles away");

        CircleImageView imageView = (CircleImageView) view.findViewById(R.id.profile_image);
        Picasso.with(getActivity()).load(user.getImageURL()).noFade().into(imageView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
