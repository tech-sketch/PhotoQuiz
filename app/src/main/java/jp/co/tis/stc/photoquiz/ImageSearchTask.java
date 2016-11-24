package jp.co.tis.stc.photoquiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created By @seiketkm
 */

public class ImageSearchTask extends AsyncTask<String, Integer, String> {
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
    protected String doInBackground(String... params) {
        try {
            final String param = params[0];
            // https://www.googleapis.com/customsearch/v1?key=AIzaSyABeoWRLxjGhkwjOJHmvIdr-tIghh7HgRg&cx=000210841253776044894:4z94vsgvmz0&q=%E3%82%82%E3%81%BF%E3%81%98&searchType=image
            final String urlString = new StringBuilder()
                    .append("https://")
                    .append("www.googleapis.com/customsearch/v1?")
                    .append("key=AIzaSyABeoWRLxjGhkwjOJHmvIdr-tIghh7HgRg") // APIKey
                    .append("&")
                    .append("searchType=image") // 画像検索
                    .append("&")
                    .append("cx=000210841253776044894:4z94vsgvmz0") // カスタム検索
                    .append("&")
                    .append("q=")
                    .append(param)
                    .toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("accept", "json/application");
            conn.setRequestMethod("GET");
            conn.connect();
            // jsonを読み込む
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            for(String line = bufferedReader.readLine(); line != null; ){
                sb.append(line);
                line = bufferedReader.readLine();
            }
            JSONObject json = new JSONObject(sb.toString());

            String imgUrl = parseFirstImageUrl(json);
            conn.disconnect();

            Log.i(TAG, imgUrl);
            return imgUrl;
        }
        catch(IOException|JSONException e){
            Log.i(TAG, e.getMessage());
        }

        return null;
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // nothing to do
    }

    @Override
    protected void onPostExecute(String url) {
        super.onPostExecute(url);
        dialog.dismiss();

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        WebView webView = (WebView) activity.findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode( false );
        webView.getSettings().setUseWideViewPort( false );
        webView.setInitialScale((int)(metrics.density * 100));
        webView.loadUrl(url);

    }
}
