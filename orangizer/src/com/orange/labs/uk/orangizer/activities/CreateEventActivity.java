package com.orange.labs.uk.orangizer.activities;

import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.activities.fragment.DatePickerFragment;
import com.orange.labs.uk.orangizer.activities.fragment.DatePickerFragment.OnDateSelected;
import com.orange.labs.uk.orangizer.activities.fragment.TimePickerFragment;
import com.orange.labs.uk.orangizer.activities.fragment.TimePickerFragment.OnTimeSelected;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;
import com.orange.labs.uk.orangizer.event.Event;
import com.orange.labs.uk.orangizer.utils.Logger;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

public class CreateEventActivity extends SherlockFragmentActivity implements OnTimeSelected,
		OnDateSelected {

	private static final Logger sLogger = Logger.getLogger(CreateEventActivity.class);

	private TextView mNameTv;
	private TextView mDescriptionTv;
	private TextView mTimeTv;
	private TextView mDateTv;
	private TextView mDurationTv;
	private TextView mAddressTv;
	private ProgressDialog mProgressDialog;

	private DependencyResolver mResolver;

	private Date mStartDate = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);

		getSherlock().getActionBar().setDisplayHomeAsUpEnabled(true);

		mResolver = DependencyResolverImpl.getInstance();

		mNameTv = (TextView) findViewById(R.id.create_event_name_tv);
		mDescriptionTv = (TextView) findViewById(R.id.create_event_description_tv);
		mTimeTv = (TextView) findViewById(R.id.create_event_time_value_tv);

		mDateTv = (TextView) findViewById(R.id.create_event_date_value_tv);
		setTodayDate(mDateTv);

		mDurationTv = (TextView) findViewById(R.id.create_event_howlong_value_tv);
		mAddressTv = (TextView) findViewById(R.id.create_event_address_tv);

		ImageButton mapButton = (ImageButton) findViewById(R.id.create_event_map_b);
		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String location = mAddressTv.getText().toString();
				if (location != null && location.length() > 0) {
					Intent intent = OrangizerUtils.getMapIntent(location);
					startActivity(intent);
				}
			}
		});

		LinearLayout timeLayout = (LinearLayout) findViewById(R.id.create_event_time_ll);
		timeLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment timeFragment = new TimePickerFragment();
				timeFragment.show(getSupportFragmentManager(), "timePicker");
			}
		});

		LinearLayout dateLayout = (LinearLayout) findViewById(R.id.create_event_date_ll);
		dateLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment dateFragment = new DatePickerFragment();
				dateFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		// Prepare progress dialog indicating on progress posting.
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(R.string.create_event_posting);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setIndeterminate(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_create_event, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent eventsIntent = new Intent(this, EventsActivity.class);
			startActivity(eventsIntent);
		} else if (item.getItemId() == R.id.create_event_menu_done) {
			Event event = createEventFromInput();

			mProgressDialog.show();
			mResolver.getEventPoster().post(event, new Callback<Event>() {

				@Override
				public void onSuccess(Event result) {
					sLogger.d("Success!!");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									getString(R.string.create_event_success), Toast.LENGTH_SHORT)
									.show();
							mProgressDialog.dismiss();
							finish();
						}
					});
				}

				@Override
				public void onFailure(Exception e) {
					sLogger.d("Failure!!");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									getString(R.string.create_event_ko), Toast.LENGTH_SHORT).show();
							mProgressDialog.dismiss();
						}
					});
				}
			});
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Set value of the text view to today's date.
	 */
	private void setTodayDate(TextView dateTv) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		onDateSelected(year, month, day);
	}

	@Override
	public void onTimeSelected(int hourOfDay, int minutes) {
		String minute = (minutes < 10) ? String.format("0%d", minutes) : String.valueOf(minutes);
		mTimeTv.setText(String.format("%d:%s", hourOfDay, minute));

		// Update the current date
		mStartDate = new Date(Date.UTC(mStartDate.getYear(), mStartDate.getMonth(),
				mStartDate.getDay(), hourOfDay, minutes, 0));
	}

	@Override
	public void onDateSelected(int year, int monthOfYear, int dayOfMonth) {
		int adjustedMonth = monthOfYear + 1; // starts at 0
		String month = (adjustedMonth < 10) ? String.format("0%d", adjustedMonth) : String
				.valueOf(adjustedMonth);

		mDateTv.setText(String.format("%d/%s/%d", dayOfMonth, month, year));

		// Update current date
		mStartDate = new Date(Date.UTC(year, monthOfYear, dayOfMonth, mStartDate.getHours(),
				mStartDate.getMinutes(), mStartDate.getSeconds()));
	}

	private Event createEventFromInput() {
		Event.Builder builder = new Event.Builder();
		builder.setName(mNameTv.getText().toString());
		builder.setDescription(mDescriptionTv.getText().toString());
		builder.setAddress(mAddressTv.getText().toString());
		builder.setStartingDate(mStartDate);
		return builder.build();
	}

}
