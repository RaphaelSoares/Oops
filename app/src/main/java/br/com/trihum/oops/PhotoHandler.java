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
    private final FotoActivity fotoActivity;

    public PhotoHandler(FotoActivity fotoActivity, Context context) {
        this.context = context;
        this.fotoActivity = fotoActivity;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data != null) {
            int screenWidth = fotoActivity.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = fotoActivity.getResources().getDisplayMetrics().heightPixels;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

            if (fotoActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                /*
                // Notice that width and height are reversed
                Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                int w = scaled.getWidth();
                int h = scaled.getHeight();
                // Setting post rotate to 90
                Matrix mtx = new Matrix();
                mtx.postRotate(90);
                // Rotating Bitmap
                bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);*/

                Matrix mtx = new Matrix();
                mtx.postRotate(90);
                // Rotating Bitmap
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);

            }else{// LANDSCAPE MODE
                //No need to reverse width and height
                //Log.i("OOPS","rotation = "+activity.getWindowManager().getDefaultDisplay().getRotation());

                //Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth,screenHeight , true);
                int rotation = fotoActivity.getWindowManager().getDefaultDisplay().getRotation();
                if (rotation == Surface.ROTATION_270)
                {
                    //int w = scaled.getWidth();
                    //int h = scaled.getHeight();
                    Matrix mtx = new Matrix();
                    mtx.postRotate(180);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
                }
                /*else
                {
                    bm=scaled;
                }*/
            }

            // Salva a foto original full
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            fotoActivity.arrayBytesFoto = bs.toByteArray();
            //Log.d("OOPS","arrayBytesFoto = "+fotoActivity.arrayBytesFoto.length);

            // Salva a foto versao mini
            bm = Constantes.getSquareReduced(Constantes.cropToSquare(bm),125,125);
            bs = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            fotoActivity.arrayBytesFotoMini = bs.toByteArray();
            //Log.d("OOPS","arrayBytesFotoMini = "+fotoActivity.arrayBytesFotoMini.length);

            fotoActivity.fotoTirada = true;
            fotoActivity.mCamera.stopPreview();
        }

    }
}
