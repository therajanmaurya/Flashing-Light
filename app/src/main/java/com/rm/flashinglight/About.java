package com.rm.flashinglight;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by rajanmaurya on 16/4/15.
 */
public class About extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(
                "Developed By:\n\nRajan Maurya\nCluster Innovation Centre\n" +
                        "Sheel Pragya\nRamjus College \nUniversity of Delhi\n\nThis App is the helping hand of disable people ")
                .setTitle("About")
//                .setPositiveButton("CIC official site",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                Intent browserIntent = new Intent(
//                                        Intent.ACTION_VIEW, Uri
//                                        .parse("http://ducic.ac.in/"));
//                                startActivity(browserIntent);
//                            }
//                        })
                .setNeutralButton("My Facebook Profile",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int id) {
                                Intent facebookIntent = getOpenFacebookIntent(getActivity()
                                        .getApplicationContext());
                                startActivity(facebookIntent);

                            }
                        })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);

            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/100003329500155"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/therajanmaurya"));
        }
    }

}
