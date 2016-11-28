package jp.co.tis.stc.photoquiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created By @seiketkm
 */

public class ImageSearchTask extends AsyncTask<String, Integer, Bitmap> {
    private final static String TAG = ImageSearchTask.class.getSimpleName();
    private final Activity activity;
    ProgressDialog dialog;

    public ImageSearchTask(Activity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading data...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        final String imgUrl = params[0];

        Log.i(TAG, imgUrl);
        return getBitmapFromUrl(imgUrl);
    }

    private Bitmap getBitmapFromUrl(String imgUrl){
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        dialog.dismiss();

        ImageView imageView = (ImageView)activity.findViewById(R.id.imageview);
        imageView.setImageBitmap(result);
    }
}
