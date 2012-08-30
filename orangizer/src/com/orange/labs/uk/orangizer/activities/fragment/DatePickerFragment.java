package com.orange.labs.uk.orangizer.activities.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DatePickerFragment extends SherlockDialogFragment implements OnDateSetListener  {

	OnDateSelected mCallback;

	// Container Activity must implement this interface
	public interface OnDateSelected {
		public void onDateSelected(int year, int monthOfYear, int dayOfMonth);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnDateSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDateSelected");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default values for the picker
		final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of TimePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		mCallback.onDateSelected(year, monthOfYear, dayOfMonth);
	}

}
