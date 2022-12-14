package com.jnu.recyclerview;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jnu.recyclerview.data.BookShelfSaver;
import com.jnu.recyclerview.data.HttpDataLoader;
import com.jnu.recyclerview.data.LabelSaver;
import com.jnu.recyclerview.data.shopItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShopItemActivity extends AppCompatActivity {

    public static final int RESULT_CODE_SUCCESS = 666;
    private shopItem book;
    public ArrayList<String> BookShelfs;
    public ArrayList<String> labels;
    String ISBN=null;
    String url=null;
    Bitmap bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//改成这样来隐藏原来的标题栏，而不是改动整体的主题为noactionbar
        setContentView(R.layout.activity_shop_item);

        //添加菜单栏
        Toolbar toolbar_add = (Toolbar)findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar_add);  //加载Toolbar控件

        getSupportActionBar().setTitle("Edit Book Details");//修改标题

        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) {
            lActionBar.setDisplayHomeAsUpEnabled(true);//显示返回按钮
        }

        //设置按钮的监听执行函数：把输入栏的数据打包bundle，然后设置进intent中，然后intent回传给主页面
        ImageView imageView=findViewById(R.id.imageView_image);

        EditText editTextTitle=findViewById(R.id.editText_shop_item_title);
        EditText editTextAuthor=findViewById(R.id.editText_shop_item_Author);
        EditText editTextPublisher=findViewById(R.id.editText_shop_item_Publisher);
        EditText editTextPubDate=findViewById(R.id.editText_shop_item_PubDate);
        EditText editTextISBN=findViewById(R.id.editText_shop_item_ISBN);
        EditText editTextNote=findViewById(R.id.editText_shop_item_Note);
        Spinner spinnerBookShelf=findViewById(R.id.spinner_shop_item_bookShelf);
        Spinner spinnerState=findViewById(R.id.spinner_shop_item_state);
        Spinner spinnerLabel=findViewById(R.id.spinner_shop_item_Label);


        BookShelfSaver bookShelfSaver=new BookShelfSaver();
        BookShelfs=bookShelfSaver.Load(this);
        BookShelfs.remove(0);//把all删掉
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BookShelfs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookShelf.setAdapter(dataAdapter);

        LabelSaver labelSaver=new LabelSaver();
        labels=labelSaver.Load(this);
        int count=Integer.parseInt(labels.get(0));
        ArrayAdapter<String> dataAdapter_ = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels.subList(1,count+1));
        dataAdapter_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLabel.setAdapter(dataAdapter_);

        Bundle bundle=getIntent().getExtras();
        int position= bundle.getInt("position");//获取到传过来的position
        int position_spinner=bundle.getInt("position_spinner");
        Boolean flag=bundle.getBoolean("flag");
        book= (shopItem) bundle.getSerializable("book");

        //有可能没有传过来title，比如创建一个新的item的时候
        if(book!=null ){
            String author=book.getAuthor();
            String title=book.getTitle();
            String Publisher=book.getPublisher();
            String PubDate=book.getPubDate();
            ISBN=book.getISBN();
            String Note=book.getNote();
            String Label=book.getLabel();
            url=book.getUrl();
            String BookShelf=book.getBookShelf();
            String state=book.getState();
            editTextTitle.setText(title);
            editTextAuthor.setText(author);
            editTextPublisher.setText(Publisher);
            editTextPubDate.setText(PubDate);
            editTextISBN.setText(ISBN);
            editTextNote.setText(Note);
//            editTextUrl.setText(url);
            if(url!=null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url_ = new URL(url);
                            HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
                            conn.setConnectTimeout(10000);//设置链接时间
                            conn.setReadTimeout(5000);//设置读取时间
                            conn.setUseCaches(true);//设置每一次都从网络读取
                            conn.setRequestMethod("GET");
                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                                InputStream inputStream = conn.getInputStream();
                                bitmap = BitmapFactory.decodeStream(inputStream);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //更新界面的ui不能放在子线程会崩溃，需要切换到主线程进行更新
                        ShopItemActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }).start();
            }
            else
                imageView.setImageResource(R.mipmap.ic_launcher);


            spinnerLabel.setSelection(labels.indexOf(Label));
            spinnerBookShelf.setSelection(BookShelfs.indexOf(BookShelf));
            spinnerState.setSelection(state.equals("Reading")?0:1);
        }
        else{
            ISBN=bundle.getString("isbn");
            editTextISBN.setText(ISBN);

            book=(shopItem) new shopItem();
//            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        toolbar_add.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        Intent intent=new Intent();
                        //打包数据
                        Bundle bundle=new Bundle();
                        book.setAuthor(editTextAuthor.getText().toString());
                        book.setTitle(editTextTitle.getText().toString());
                        book.setISBN(editTextISBN.getText().toString());
                        book.setNote(editTextNote.getText().toString());
                        book.setPubDate(editTextPubDate.getText().toString());
                        book.setPublisher(editTextPublisher.getText().toString());

                        book.setUrl(url);

                        book.setLabel(spinnerLabel.getSelectedItem().toString());
                        book.setBookShelf(spinnerBookShelf.getSelectedItem().toString());
                        book.setState(spinnerState.getSelectedItem().toString());

                        bundle.putSerializable("book",book);
                        bundle.putInt("position",position);
                        bundle.putInt("position_spinner",position_spinner);
                        bundle.putBoolean("flag",flag);
                        //把打包的东西放进intent中
                        intent.putExtras(bundle);
                        //设置成功的返回结果:数字和intent
                        setResult(RESULT_CODE_SUCCESS,intent);

                        //关闭页面
                        ShopItemActivity.this.finish();
                        break;
                }
                return true;
            }
        });

        Button button=this.findViewById(R.id.button_ISBN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ISBN=editTextISBN.getText().toString();
                if(ISBN!=null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpDataLoader httpDataLoader=new HttpDataLoader();
                            String html=httpDataLoader.getHtml(ISBN);
                            book=httpDataLoader.ParseJsonData(html);
                            String author=book.getAuthor();
                            String title=book.getTitle();
                            String Publisher=book.getPublisher();
                            String PubDate=book.getPubDate();
                            String Note=book.getNote();
                            url=book.getUrl();
                            editTextTitle.setText(title);
                            editTextAuthor.setText(author);
                            editTextPublisher.setText(Publisher);
                            editTextPubDate.setText(PubDate);
                            editTextNote.setText(Note);

                            try {
                                URL url_ = new URL(url);
                                HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
                                conn.setConnectTimeout(10000);//设置链接时间
                                conn.setReadTimeout(5000);//设置读取时间
                                conn.setUseCaches(true);//设置每一次都从网络读取
                                conn.setRequestMethod("GET");
                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                                    InputStream inputStream = conn.getInputStream();
                                    bitmap = BitmapFactory.decodeStream(inputStream);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //更新界面的ui不能放在子线程会崩溃，需要切换到主线程进行更新
                            ShopItemActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }).start();
                }

            }
        });



    }
    //设置辛的导航栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    //实现返回按钮返回主页面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}