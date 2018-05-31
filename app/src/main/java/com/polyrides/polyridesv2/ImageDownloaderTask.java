package com.polyrides.polyridesv2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class ImageDownloaderTask extends AsyncTask<String,Void,Bitmap> {

    ImageView image;

    public ImageDownloaderTask(ImageView img) {
        this.image = img;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap img = null;
        try {
            InputStream inputStream = new java.net.URL(url).openStream();
            img = BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception e) {
            Log.e("HTTPFail", e.getMessage());
        }
        return img;
    }

    protected void onPostExecute(Bitmap result) {
        image.setImageBitmap(result);
    }
}
