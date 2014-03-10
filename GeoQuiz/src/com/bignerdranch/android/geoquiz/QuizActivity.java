package com.bignerdranch.android.geoquiz;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {
	
	//constants
	private static final String Tag = "QuizActivity";
	private static final String KEY_INDEX = "index";
	public static final String KEY_BOOLEAN = "boolean";
	
	//buttons
	private Button mTrueButton;
	private Button mFalseButton;
	private Button mCheatButton;
	private ImageButton mNextButton;
	private ImageButton mPreviousButton;
	private TextView mQuestionTextView;
	private TextView mBuildVersion;
	
	//composition
	private TrueFalse[] mQuestionBank = new TrueFalse[] {
			new TrueFalse(R.string.question_oceans, true, false),
			new TrueFalse(R.string.question_mideast, false, false),
			new TrueFalse(R.string.question_africa, false, false),
			new TrueFalse(R.string.question_americas, true, false),
			new TrueFalse(R.string.question_asia, true, false)
	};
	
	//variables
	private int mCurrentIndex = 0;
	private boolean mIsCheater;
	
	//get other activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		mIsCheater = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
	}
	
	private void updateQuestion() {
		int question = mQuestionBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(question);
	}

	private void checkAnswer(boolean userPressedTrue) {
		boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();		
		int messageResId = 0;		
		if (mIsCheater) {
			messageResId = R.string.judgement_toast;
		} else {		
			if (userPressedTrue == answerIsTrue) {
				messageResId = R.string.correct_toast;
			} else {
				messageResId = R.string.incorrect_toast;
			}
		}
		Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
	}
	
	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(Tag, "OnCreat Bundle Called");
		setContentView(R.layout.activity_quiz);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionbar = getActionBar();
			actionbar.setSubtitle("Bodies of water");
		}
		mBuildVersion = (TextView)findViewById(R.id.build_version);
		mBuildVersion.setText("API level " + Build.VERSION.SDK_INT);
		
		
		mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
		mQuestionTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex < 4) ? (mCurrentIndex + 1) : 0;
				updateQuestion();
			}
		});
		
		mTrueButton = (Button)findViewById(R.id.true_button);
		mTrueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer(true);
			}
		});
		mFalseButton = (Button)findViewById(R.id.false_button);
		mFalseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer(false);
			}
		});
		mNextButton = (ImageButton)findViewById(R.id.right_button);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex < (mQuestionBank.length - 1)) ? (mCurrentIndex + 1) : 0;
				mIsCheater = mQuestionBank[mCurrentIndex].isCheater();
				updateQuestion();
			}
		});
		mPreviousButton = (ImageButton)findViewById(R.id.left_button);
		mPreviousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex > 0) ? (mCurrentIndex - 1) : (mQuestionBank.length - 1);
				mIsCheater = mQuestionBank[mCurrentIndex].isCheater();
				updateQuestion();
			}
		});
		mCheatButton = (Button)findViewById(R.id.cheat_button);
		mCheatButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(QuizActivity.this, CheatActivity.class);
				boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
				i.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, answerIsTrue);
				startActivityForResult(i, 0);
			}
		});
		
		if (savedInstanceState != null) {
			mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
			mIsCheater = savedInstanceState.getBoolean(KEY_BOOLEAN, false);
			mQuestionBank[mCurrentIndex].setCheated(mIsCheater);
		}
		
		updateQuestion();
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.i(Tag, "onSaveInstanceState");
		savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
		savedInstanceState.putBoolean(KEY_BOOLEAN, mIsCheater);
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(Tag, "onStart() called");
	}
	@Override
	public void onPause() {
		super.onPause();
		Log.d(Tag, "onPause() called");
	}
	@Override
	public void onResume() {
		super.onResume();
		Log.d(Tag, "onResume() called");
	}
	@Override
	public void onStop() {
		super.onStop();
		Log.d(Tag, "onStop() called");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(Tag, "onDestroy() called");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}

}
