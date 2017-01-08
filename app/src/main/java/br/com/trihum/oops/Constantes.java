package br.com.trihum.oops;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Base64;

/**
 * Created by raphaelmoraes on 19/12/16.
 */

public class Constantes {

    public static Bitmap decodeFrom64toRound(String encodedString) {

        //final String encodedString = Constantes.base64foto1;
        final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);

        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap bImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return getRoundedShape(cropToSquare(bImage));
    }

    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 125;
        int targetHeight = 125;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);

        Path path = new Path();

        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);

        Bitmap sourceBitmap = scaleBitmapImage;

        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
                targetHeight), null);

        return targetBitmap;
    }

    public static Bitmap getSquareReduced(Bitmap scaleBitmapImage, int targetWidth, int targetHeight) {
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);

        Bitmap sourceBitmap = scaleBitmapImage;

        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
                targetHeight), null);

        return targetBitmap;
    }

    public static String dataDiaMesAno(String dataAnoMesDia)
    {
        if (dataAnoMesDia.length()==10)
        {
            String dia = dataAnoMesDia.substring(8,10);
            String mes = dataAnoMesDia.substring(5,7);
            String ano = dataAnoMesDia.substring(0,4);
            return dia+"/"+mes+"/"+ano;
        }
        else return "";

    }

    public static final boolean isEmulator()
    {
        if ("goldfish".equals(Build.HARDWARE)) return true;
        if ("google_sdk".equals(Build.PRODUCT) || "sdk".equals(Build.PRODUCT) || "sdk_x86".equals(Build.PRODUCT) || "vbox86p".equals(Build.PRODUCT)) return true;
        if(Build.PRODUCT.matches(".*_?sdk_?.*")) return true;

        return false;
    }

}
