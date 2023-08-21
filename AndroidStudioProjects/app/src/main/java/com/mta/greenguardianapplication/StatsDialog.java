package com.mta.greenguardianapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.mta.greenguardianapplication.R;

public class StatsDialog extends DialogFragment {
    public interface DialogListener {
        void onProceedClicked(String fromDate, String toDate, int selectedRadioId);
        void onCancelClicked();
    }

    private DialogListener dialogListener;

    private Button btnFromDate, btnToDate;
    private RadioGroup radioGroup;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_stats_dialog, null);

        btnFromDate = view.findViewById(R.id.btnFromDate);
        btnToDate = view.findViewById(R.id.btnToDate);
        radioGroup = view.findViewById(R.id.radioGroup);

        builder.setView(view)
                .setTitle("Humidity Statistics")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedFromDate = btnFromDate.getText().toString();
                        String selectedToDate = btnToDate.getText().toString();
                        int selectedRadioId = radioGroup.getCheckedRadioButtonId();

                        if (selectedRadioId == R.id.radioStats) {
                            // Handle "Show average humidity per day" radio button
                        } else if (selectedRadioId == R.id.radioHistory) {
                            // Handle "Show full history" radio button
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();

        // Disable the "Proceed" button initially
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setEnabled(false);
            }
        });

        return alertDialog;
    }
}
