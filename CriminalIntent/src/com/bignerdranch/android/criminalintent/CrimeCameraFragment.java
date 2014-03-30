package com.bignerdranch.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	
	public static final String EXTRA_PHOTO_FILENAME = "com.bignerdranch.android.criminalintent.photo_filename";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mProgressContainer;
	
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			// Display the progress indicator
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};
	
	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// create a filename
			String filename = UUID.randomUUID().toString() + ".jpg";
			//save the file to disk
			FileOutputStream os = null;
			boolean success = true;
			try {
				os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error writing to file", e);
				success = false;
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} 
				catch (Exception e) {
					Log.e(TAG, "Error closing file " + filename, e);
					success = false;
				}
			}
			// set the photo filename on the result intent
			if (success) {
				Intent i = new Intent();
				i.putExtra(EXTRA_PHOTO_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK, i);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};
	
	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
		
		Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});
		
		mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		//setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated
		//but are required for Camera preview to work on pre-3.0 devices
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceCreated(SurfaceHolder holder) {
				//tell the camera to use this surface as its preview area
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException exception) {
					Log.e(TAG, "Error setting up preview display", exception);
				}
			}
			public void surfaceDestroyed(SurfaceHolder holder) {
				//we can no longer display on this surface, so stop preview
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}
			public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
				if (mCamera == null) return;
				//The surface has changed size; update the camera preview size
				Camera.Parameters parameters = mCamera.getParameters();
				Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
				parameters.setPreviewSize(s.width, s.height);
				s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}
		});
		
		mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE);
		
		return v;
	}
	@TargetApi(9)
	@Override 
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	//get largest preview size
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}
}
