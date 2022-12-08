package com.jnu.recyclerview.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpDataLoader {
    // 获取网页的html源代码
    public String getHtml(String isbn) {
        try{
            String uri="http://118.31.113.49/api/isbn/v1/index?key=d7ba9fa7634764f2fd5bb81e8183ce18&isbn="+isbn;
//            String uri="http://file.nidama.net/class/mobile_develop/data/bookstore2022.json";
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);//设置链接时间
            conn.setReadTimeout(5000);//设置读取时间
            conn.setUseCaches(true);//设置每一次都从网络读取
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStreamReader inputStreamReader=new InputStreamReader(conn.getInputStream());
                BufferedReader reader=new BufferedReader(inputStreamReader);
                String tempLine=null;
                StringBuffer resultBuffer = new StringBuffer();//可以动态分配内存
                while((tempLine=reader.readLine())!=null){
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
//                Log.i("test data",resultBuffer.toString());
//                return "wo";
                return resultBuffer.toString();
            }
        } catch (Exception e) {
            Log.i("woo","wuu888"+e.toString());
            return e.getMessage();
        }
        return " ";
    }
    //解析网络获取的数据
    public shopItem ParseJsonData(String JsonText){
        shopItem shopItem=new shopItem();
        try {
            JSONObject root = new JSONObject(JsonText);
//            shopItem.setAuthor(root.getString("");
            JSONObject book = root.getJSONObject("data");//获取属性为shops的列表
            shopItem.setTitle(book.getString("title"));
            shopItem.setAuthor(book.getString("author"));
//            shopItem.setISBN(book.getString("isbn"));
            shopItem.setPublisher(book.getString("publisher"));
            shopItem.setPubDate(book.getString("pubdate"));
            shopItem.setUrl(book.getString("img"));

//            shopItem.setTranslator(book.getString("translator"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shopItem;
    }
}
