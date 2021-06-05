package com.example.triporganizer.Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteTripDialog extends AppCompatDialogFragment {
    private DeleteTripDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attention!");
        builder.setMessage("You surely want to delete this trip?");

        // Negative - dont delete trip
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Positive - delete trip;
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDeleteClicked();
            }
        });
        return builder.create();
    }

    public interface DeleteTripDialogListener{
        void onDeleteClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteTripDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DeleteTripDialogListener");
        }
    }
}
