package br.com.trihum.oops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

//https://developer.android.com/guide/topics/media/camera.html

public class FotoActivity extends AppCompatActivity {

    public Camera mCamera;
    private CameraPreview mPreview;
    public byte[] arrayBytesFoto = null;
    public byte[] arrayBytesFotoMini = null;
    boolean fotoTirada;

    FloatingActionButton fabTirarFoto;
    FloatingActionButton fabConfirmarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        fabTirarFoto = (FloatingActionButton) findViewById(R.id.fabTirarFoto);
        fabConfirmarFoto = (FloatingActionButton) findViewById(R.id.fabConfirmarFoto);

        fotoTirada = false;
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Ajustando a câmera (melhor qualidade da imagem, autofoco...
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
        Camera.Size preferredSize = supportedSizes.get(0);
        for(int i = 1; i < supportedSizes.size(); i++){
            Log.d("OOPS","camera supported size = "+supportedSizes.get(i).width+" X "+supportedSizes.get(i).height);

            if (supportedSizes.get(i).width == 1280 && supportedSizes.get(i).height == 960)
            {
                preferredSize = supportedSizes.get(i);
                break;
            }
            if (supportedSizes.get(i).width == 1024 && supportedSizes.get(i).height == 768)
            {
                preferredSize = supportedSizes.get(i);
                break;
            }
            if (supportedSizes.get(i).width == 800 && supportedSizes.get(i).height == 600)
            {
                preferredSize = supportedSizes.get(i);
                break;
            }
        }
        params.setPictureSize(preferredSize.width, preferredSize.height);
        mCamera.setParameters(params);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



    @Override
    public void onBackPressed()
    {
        try {
            if (fotoTirada)
            {
                fabTirarFoto.setVisibility(View.VISIBLE);
                fabConfirmarFoto.setVisibility(View.INVISIBLE);

                fotoTirada = false;
                mCamera.startPreview();
            }
            else
            {
                mCamera.stopPreview();
                mCamera.release();
                finish();
            }
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
    }


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void onTirarFotoClick (View v){

        fabTirarFoto.setVisibility(View.INVISIBLE);
        fabConfirmarFoto.setVisibility(View.VISIBLE);
        mCamera.takePicture(null, null, new PhotoHandler(this,getApplicationContext()));

    }

    public void onConfirmarFotoClick (View v){

        mCamera.stopPreview();
        mCamera.release();

        Intent i = new Intent(FotoActivity.this, RegistraInfracaoActivity.class);
        if (arrayBytesFoto != null)
            i.putExtra("byteArray", arrayBytesFoto);
        if (arrayBytesFotoMini != null)
            i.putExtra("byteArrayMini", arrayBytesFotoMini);
        startActivity(i);
        finish();

    }

}
