package com.orange.labs.uk.orangizer.activities.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TimePickerFragment extends SherlockDialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	OnTimeSelected mCallback;

	// Container Activity must implement this interface
	public interface OnTimeSelected {
		public void onTimeSelected(int hourOfDay, int minuted);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnTimeSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnTimeSelected");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mCallback.onTimeSelected(hourOfDay, minute);
	}
}