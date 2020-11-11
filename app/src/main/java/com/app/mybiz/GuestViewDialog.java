//package com.app.mybiz;
//
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.view.Window;
//
//
///**
// * Created by hannashulmah on 04/03/2017.
// */
//
//
//    public class GuestViewDialog extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            final AlertDialog.Builder builder = new AlertDialog.Builder();
//            builder.setMessage("you are not registered")
//                    .setPositiveButton("sign up", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // FIRE ZE MISSILES!
//                            Intent intent = new Intent(getActivity(), CreateAccountChoiceActivity.class);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                        }
//                    });
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//    }
