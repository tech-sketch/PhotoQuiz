package jp.co.tis.stc.photoquiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.co.sharp.android.voiceui.VoiceUIManager;

/**
 * Created By @seiketkm
 */

public class ImageSearchTask extends AsyncTask<String, Integer, Bitmap> {
    private final static String TAG = ImageSearchTask.class.getSimpleName();
    private final Activity activity;
    private VoiceUIManager mVoiceUIManager = null;
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

    // GCE parse
    // Google Custome Engineの応答を解析し一つの画像URLを取得する

    /**
     *
     * @param json GoogleCustomeEngine の応答
     * @return itemsの一つ目のString
     */
    private String parseFirstImageUrl(JSONObject json){
        try{
            JSONArray items = json.getJSONArray("items");
            if(items.length() == 0)
                return "";
            JSONObject item = items.getJSONObject(0);
            String link = item.getString("link");
            return link;
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
            return "";
        }
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
        // nothing to do
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        dialog.dismiss();

        ImageView imageView = (ImageView)activity.findViewById(R.id.imageview);
        imageView.setImageBitmap(result);
    }
}
