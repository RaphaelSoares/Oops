package br.com.trihum.oops.utilities;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.Surface;

import java.io.ByteArrayOutputStream;

import br.com.trihum.oops.FotoActivity;
import br.com.trihum.oops.utilities.Constantes;

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
            // Ajuste para tentar corrigir problema de OutOfMemoryError
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPurgeable = true; // inPurgeable is used to free up memory while required

            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0,options);

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

                try
                {
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
                }
                catch (OutOfMemoryError OOM)
                {
                    // dá recycle e tenta de novo...
                    bm.recycle();
                    bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0,options);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
                }


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
                    try
                    {
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
                    }
                    catch (OutOfMemoryError OOM)
                    {
                        // dá recycle e tenta de novo...
                        bm.recycle();
                        bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0,options);
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mtx, true);
                    }

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
            bm = Funcoes.getSquareReduced(Funcoes.cropToSquare(bm),125,125);
            bs = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            fotoActivity.arrayBytesFotoMini = bs.toByteArray();
            //Log.d("OOPS","arrayBytesFotoMini = "+fotoActivity.arrayBytesFotoMini.length);

            bm.recycle();

            fotoActivity.fotoTirada = true;
            fotoActivity.mCamera.stopPreview();
        }

    }
}
