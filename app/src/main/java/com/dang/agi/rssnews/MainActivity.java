package com.dang.agi.rssnews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
List<BaiViet> listBaiViet;
    AdapterNews adapterNews;
    RecyclerView recyclerView;
    String strUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RssAsynTask(this).execute("http://www.bongda.com.vn/bong-da-anh.rss");
    }


}
@TargetApi(Build.VERSION_CODES.CUPCAKE)
class RssAsynTask extends AsyncTask<String,List<BaiViet>,List<BaiViet>> {
    List<BaiViet> listBaiViet;
    Context context;
    

    public RssAsynTask(Context context) {
        this.context = context;
        listBaiViet = new ArrayList<>();
    }

    @Override
    protected List<BaiViet> doInBackground(String... params) {
        String strUrl = params[0];
        InputStream inputStream;
        URL url = null;
        try {
            url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(inputStream, "UTF-8");
            parseXML(xmlPullParser);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        publishProgress(listBaiViet);
        return listBaiViet;
    }

    @Override
    protected void onProgressUpdate(List<BaiViet>... values) {
        TextView textview = (TextView) ((AppCompatActivity)context).findViewById(R.id.textview_category);
        RecyclerView recyclerView = (RecyclerView) ((AppCompatActivity)context).findViewById(R.id.recyclerNews);
        List<BaiViet> list = values[0];
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        AdapterNews adapterNews = new AdapterNews(context,list);
        recyclerView.setAdapter(adapterNews);
        super.onProgressUpdate(values);
    }

    private void parseXML(XmlPullParser xmlPullParser) {
        String text = "";
        BaiViet baiViet = null;
        try {
            int event  = xmlPullParser.getEventType();
            while (event!=XmlPullParser.END_DOCUMENT){
                String name = xmlPullParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG: {
                        if (name.equals("item")) {
                            baiViet = new BaiViet();
                        }
                    }
                        break;
                    case XmlPullParser.TEXT: {
                        text = xmlPullParser.getText();
                        }
                     break;
                    case XmlPullParser.END_TAG:{
                        String result;
                        if (text!="" && baiViet!=null){
                            if (text.contains("![CDATA[")){
                                result = text.substring(text.indexOf("![CDATA["),text.indexOf("]]"));
                            }else{
                                result =text;
                            }
                            if (name.equals("title")){
                                baiViet.setTitle(result);
                            }else if (name.equals("description")){
                                baiViet.setDescription(result);
                            }else if (name.equals("image")){
                                baiViet.setImage(result);
                            }else if (name.equals("link")){
                                baiViet.setLink(result);
                            }else if (name.equals("pubDate")){
                                baiViet.setPubDate(result);
                            }
                        }
                        if (name.equals("item")){
                            listBaiViet.add(baiViet);
                        }
                    }break;
                    default:
                        break;
                }
              event= xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}