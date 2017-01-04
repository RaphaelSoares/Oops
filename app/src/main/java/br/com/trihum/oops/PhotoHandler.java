package br.com.trihum.oops;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;

import java.io.ByteArrayOutputStream;

/**
 * Created by raphaelmoraes on 03/01/17.
 */

public class PhotoHandler implements PictureCallback {

    private final Context context;
    private final AppCompatActivity activity;

    public PhotoHandler(AppCompatActivity activity, Context context) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data != null) {
            int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Notice that width and height are reversed
                Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                int w = scaled.getWidth();
                int h = scaled.getHeight();
                // Setting post rotate to 90
                Matrix mtx = new Matrix();
                mtx.postRotate(90);
                // Rotating Bitmap
                bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            }else{// LANDSCAPE MODE
                //No need to reverse width and height
                //Log.i("OOPS","rotation = "+activity.getWindowManager().getDefaultDisplay().getRotation());

                Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth,screenHeight , true);
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                if (rotation == Surface.ROTATION_270)
                {
                    int w = scaled.getWidth();
                    int h = scaled.getHeight();
                    Matrix mtx = new Matrix();
                    mtx.postRotate(180);
                    bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                }
                else
                {
                    bm=scaled;
                }
            }

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bs);

            Intent i = new Intent(context, ConfirmaFotoActivity.class);
            i.putExtra("byteArray", bs.toByteArray());
            activity.startActivity(i);
        }

    }
}
