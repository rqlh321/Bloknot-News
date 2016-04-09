package com.example.sic.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private final static String TAG = "Image_Manager";
    String FOLDER_TO_SAVE_PICS = Environment.getExternalStorageDirectory().toString()+"/"+R.string.app_name;

    public ArrayList<String> titleList = new ArrayList();
    public ArrayList<String> picListUrl = new ArrayList();
    public ArrayList<Bitmap> bitmapList = new ArrayList();

    private ArrayAdapter<String> adapter;

    private int pageNumber = 2;
    private int picsOnList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File checkDir = new File(FOLDER_TO_SAVE_PICS);

        if(!checkDir.exists()){
            Log.v(TAG, "directory NOT exist");
            checkDir.mkdirs();
            Log.v(TAG, "directory create");
        }

        ListView lv;
        adapter = new СustomAdapter(this, titleList, bitmapList);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnScrollListener(new EndlessScrollListener());

        new ParsThread().execute("1");
    }

    public class ParsThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Document doc;
            Elements contentPictures;

            try {
                doc = Jsoup.connect("http://bloknot-taganrog.ru/?PAGEN_1=" + params[0]).get();
                contentPictures = doc.select(".preview_picture");
                for (int i = 0; i < contentPictures.size(); i++) {
                    if (!titleList.contains(contentPictures.get(i).attributes().get("title"))) {
                        if (contentPictures.get(i).attributes().get("src").contains("//bloknot-taganrog.ru"))
                            picListUrl.add("http:" + contentPictures.get(i).attributes().get("src"));
                        else
                            picListUrl.add("http://bloknot-taganrog.ru" + contentPictures.get(i).attributes().get("src"));
                        titleList.add(contentPictures.get(i).attributes().get("title"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            while (picsOnList < picListUrl.size()) {
                //проверка наличия картинки на телефоне
                File checkDir = new File(FOLDER_TO_SAVE_PICS+"/" + titleList.get(picsOnList) + ".jpg");
                if (!checkDir.exists()) {
                    new DownloadThread().execute(picListUrl.get(picsOnList));
                }else{
                    bitmapList.add(BitmapFactory.decodeFile(checkDir.toString()));
                }
                adapter.notifyDataSetChanged();
                picsOnList++;
            }
        }
    }

    public class DownloadThread extends AsyncTask<String, Void, String> {
        private Bitmap bitmap = null;

        @Override
        protected String doInBackground(String... params) {
            String iUrl = params[0];
            HttpURLConnection conn = null;
            BufferedInputStream buf_stream = null;
            try {
                Log.v(TAG, "Starting loading image by URL: " + iUrl);
                conn = (HttpURLConnection) new URL(iUrl).openConnection();
                conn.setDoInput(true);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.connect();
                buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
                bitmap = BitmapFactory.decodeStream(buf_stream);
                SavePicture(bitmap, titleList.get(bitmapList.size()).toString());//сохраняем картинку на телефон
                buf_stream.close();
                conn.disconnect();
                buf_stream = null;
                conn = null;
            } catch (MalformedURLException ex) {
                Log.e(TAG, "Url parsing was failed: " + iUrl);
            } catch (IOException ex) {
                Log.d(TAG, iUrl + " does not exists");
            } catch (OutOfMemoryError e) {
                Log.w(TAG, "Out of memory!!!");
                return null;
            } finally {
                if (buf_stream != null)
                    try {
                        buf_stream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            bitmapList.add(bitmap);
            adapter.notifyDataSetChanged();
        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int previousTotal = 0;
        private boolean loading = true;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                new ParsThread().execute(String.valueOf(pageNumber));
                pageNumber++;
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}
    }

    private String SavePicture(Bitmap bitmap, String name){
        try {
            File file = new File(FOLDER_TO_SAVE_PICS,name+".jpg");
            OutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // сохранять картинку в jpeg-формате с 0% сжатия.
            fOut.flush();
            fOut.close();
          //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName()); // регистрация в фотоальбоме
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
        return "";
    }

}