package br.com.trihum.oops;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private FotoActivity fotoActivity;

    public CameraPreview(FotoActivity fotoActivity, Camera camera) {
        super(fotoActivity);
        this.fotoActivity = fotoActivity;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if (mCamera != null) mCamera.setPreviewDisplay(holder);
            //mCamera.startPreview();
        } catch (IOException e) {
            Log.d("OOPS", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (fotoActivity.fotoTirada) return;


        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        if (!Constantes.isEmulator()) {
            Display display = (fotoActivity).getWindowManager().getDefaultDisplay();

            /*if(display.getRotation() == Surface.ROTATION_0) {
                Log.i("TESTE","0");
                mCamera.setDisplayOrientation(90);
            } else if(display.getRotation() == Surface.ROTATION_270) {
                Log.i("TESTE","270");
                mCamera.setDisplayOrientation(180);
            } else if(display.getRotation() == Surface.ROTATION_90) {
                Log.i("TESTE","90");
                mCamera.setDisplayOrientation(0);
            } else if(display.getRotation() == Surface.ROTATION_180) {
                Log.i("TESTE","180");
                mCamera.setDisplayOrientation(90);
            }*/

            setCameraDisplayOrientation(fotoActivity, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        }


        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("OOPS", "Error starting camera preview: " + e.getMessage());
        }
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
