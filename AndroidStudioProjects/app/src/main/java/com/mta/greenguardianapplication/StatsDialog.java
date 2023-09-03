package com.mta.greenguardianapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StatsDialog extends DialogFragment {

    public interface DialogListener {
        void onProceedClicked(String fromDate, String toDate, boolean isStats);
        void onCancelClicked();
    }

    private DialogListener dialogListener;

    private Button btnFromDate, btnToDate, btnPositive;
    private RadioGroup radioGroup;
    private int selectedYear, selectedMonth, selectedDay;

    private boolean fromDate = false, toDate = false;

    public void setDialogListener(DialogListener listener) {
        this.dialogListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_stats_dialog, null);
        View viewTitle = inflater.inflate(R.layout.stats_dialog_title, null);

        btnFromDate = view.findViewById(R.id.btnFromDate);
        btnToDate = view.findViewById(R.id.btnToDate);
        radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton radioDefault = view.findViewById(R.id.radioStats);
        radioDefault.setChecked(true);
        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(btnFromDate);
            }
        });

        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(btnToDate);
            }
        });

        builder.setCustomTitle(viewTitle);
        builder.setView(view)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedFromDate = btnFromDate.getText().toString();
                        String selectedToDate = btnToDate.getText().toString();
                        int selectedRadioId = radioGroup.getCheckedRadioButtonId();

                        if (selectedRadioId == R.id.radioStats) {
                            // Handle "Show average humidity per day" radio button
                            dialogListener.onProceedClicked(selectedFromDate, selectedToDate, true);
                        } else if (selectedRadioId == R.id.radioHistory) {
                            // Handle "Show full history" radio button
                            dialogListener.onProceedClicked(selectedFromDate, selectedToDate, false);
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
                btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setEnabled(false);
            }
        });

        return alertDialog;
    }

    private void showDatePickerDialog(final Button targetButton) {
        // Set the initial year, month, and day
        int initialYear = 2023; // Change this to the year you want
        int initialMonth = 8;   // 0-based index for January, adjust as needed
        int initialDay = 1;     // Day of the month (1-based)

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = monthOfYear;
                        selectedDay = dayOfMonth;
                        updateButtonDate(targetButton);
                    }
                },
                initialYear, initialMonth, initialDay // Set the initial date here
        );

        datePickerDialog.show();
    }


    private void updateButtonDate(Button button) {
        String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

        if(button.getText().equals("Starting Date")){
            fromDate = true;
        }
        if(button.getText().equals("Ending Date")){
            toDate = true;
        }
        button.setText(dateString);
        if(fromDate && toDate){
            btnPositive.setEnabled(true);
        }
    }
}
