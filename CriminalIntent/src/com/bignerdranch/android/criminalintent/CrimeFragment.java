package com.bignerdranch.android.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckbox;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());
			}
			public void beforeTextChanged(CharSequence c, int start, int before, int after) {
				//blank
			}
			public void afterTextChanged(Editable c) {
				//blank
			}
		});
		
		mDateButton = (Button)v.findViewById(R.id.crime_date);
		//mDateButton.setText(DateFormat.getDateInstance().format((mCrime.getDate())));
		String monthFormat =  Build.VERSION.SDK_INT < 9 ? "MM" : "LLL";
		DateFormat df = new SimpleDateFormat("E, " + monthFormat + " dd, yyyy");
		mDateButton.setText(df.format(mCrime.getDate()));
		mDateButton.setEnabled(false);
		
		mSolvedCheckbox = (CheckBox)v.findViewById(R.id.crime_solved);
		mSolvedCheckbox.setChecked(mCrime.isSolved());
		mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				//set crime to solved
				mCrime.setSolved(isChecked);
			}
		});
		
		
		return v;
	}
	public static CrimeFragment newInstance(UUID crimeID) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeID);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
}
