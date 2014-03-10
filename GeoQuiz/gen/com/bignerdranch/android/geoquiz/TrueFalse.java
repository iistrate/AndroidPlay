package com.bignerdranch.android.geoquiz;

public class TrueFalse {
	private int mQuestion;
	private boolean  mTrueQuestion;
	private boolean mHasCheated;
	
	public TrueFalse(int question, boolean trueQuestion, boolean hasCheated) {
		mQuestion = question;
		mTrueQuestion = trueQuestion;
		mHasCheated = hasCheated;
	}

	public boolean isCheater() {
		return mHasCheated;
	}
	public void setCheated(boolean b) {
		mHasCheated = b;
	}
	
	public int getQuestion() {
		return mQuestion;
	}

	public void setQuestion(int question) {
		mQuestion = question;
	}

	public boolean isTrueQuestion() {
		return mTrueQuestion;
	}

	public void setTrueQuestion(boolean trueQuestion) {
		mTrueQuestion = trueQuestion;
	}
}
