package com.harshilhshah.rufree.rufree.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by harshilhshah on 8/27/16.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView bmImage;
    private Drawable drawable;
    private int width;

    public DownloadImageTask(ImageView bmImage, Drawable d, int width) {
        this.bmImage = bmImage;
        this.drawable = d;
        this.width = width;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            if(urldisplay.length() < 1){
                mIcon11 = ((BitmapDrawable)drawable).getBitmap();
            }else {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }
            mIcon11 = Bitmap.createScaledBitmap(mIcon11, width, 480, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}