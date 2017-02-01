package br.com.trihum.oops.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by raphaelmoraes on 04/01/17.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    boolean roundAndCrop;

    public DownloadImageTask(ImageView bmImage, boolean roundAndCrop) {
        this.bmImage = bmImage;
        this.roundAndCrop = roundAndCrop;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        //pd.show();
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        //pd.dismiss();

        if (result != null)
        {
            if (roundAndCrop)
            {
                bmImage.setImageBitmap(Funcoes.getRoundedShape(Funcoes.cropToSquare(result)));
            }
            else
            {
                bmImage.setImageBitmap(result);
            }
        }
    }
}
