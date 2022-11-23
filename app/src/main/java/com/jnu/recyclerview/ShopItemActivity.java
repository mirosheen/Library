package com.jnu.recyclerview;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jnu.recyclerview.data.BookShelfSaver;
import com.jnu.recyclerview.data.shopItem;

import java.util.ArrayList;

public class ShopItemActivity extends AppCompatActivity {

    public static final int RESULT_CODE_SUCCESS = 666;
    private shopItem book;
    public ArrayList<String> BookShelfs;
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
        EditText editTextTitle=findViewById(R.id.editText_shop_item_title);
        EditText editTextAuthor=findViewById(R.id.editText_shop_item_Author);
        EditText editTextTranslator=findViewById(R.id.editText_shop_item_Translator);
        EditText editTextPublisher=findViewById(R.id.editText_shop_item_Publisher);
        EditText editTextPubDate=findViewById(R.id.editText_shop_item_PubDate);
        EditText editTextISBN=findViewById(R.id.editText_shop_item_ISBN);
        EditText editTextNote=findViewById(R.id.editText_shop_item_Note);
        EditText editTextLabel=findViewById(R.id.editText_shop_item_Label);
        EditText editTextUrl=findViewById(R.id.editText_shop_item_Src);
        Spinner spinnerBookShelf=findViewById(R.id.spinner_shop_item_bookShelf);
        Spinner spinnerState=findViewById(R.id.spinner_shop_item_state);

        BookShelfSaver bookShelfSaver=new BookShelfSaver();
        BookShelfs=bookShelfSaver.Load(this);
        BookShelfs.remove(0);//把all删掉
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BookShelfs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookShelf.setAdapter(dataAdapter);

        Bundle bundle=getIntent().getExtras();
        int position= bundle.getInt("position");//获取到传过来的position
        int position_spinner=bundle.getInt("position_spinner");
        Boolean flag=bundle.getBoolean("flag");
        book= (shopItem) bundle.getSerializable("book");
        //有可能没有传过来title，比如创建一个新的item的时候
        if(book!=null){
            String author=book.getAuthor();
            String title=book.getTitle();
            String Translator=book.getTranslator();
            String Publisher=book.getPublisher();
            String PubDate=book.getPubDate();
            String ISBN=book.getISBN();
            String Note=book.getNote();
            String Label=book.getLabel();
            String url=book.getUrl();
            int resourceId=book.getResourceId();
            String BookShelf=book.getBookShelf();
            String state=book.getState();
            editTextTitle.setText(title);
            editTextAuthor.setText(author);
            editTextTranslator.setText(Translator);
            editTextPublisher.setText(Publisher);
            editTextPubDate.setText(PubDate);
            editTextISBN.setText(ISBN);
            editTextNote.setText(Note);
            editTextLabel.setText(Label);
            editTextUrl.setText(url);

            spinnerBookShelf.setSelection(BookShelfs.indexOf(BookShelf));
            spinnerState.setSelection(state.equals("Reading")?0:1);
        }
        else{
            book=(shopItem) new shopItem();
        }
        toolbar_add.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        Intent intent=new Intent();
                        //打包数据
                        Bundle bundle=new Bundle();
                        book.setUrl(editTextUrl.getText().toString());
                        book.setAuthor(editTextAuthor.getText().toString());
                        book.setResourceId(R.mipmap.ic_launcher);
                        book.setTitle(editTextTitle.getText().toString());
                        book.setISBN(editTextISBN.getText().toString());
                        book.setLabel(editTextLabel.getText().toString());
                        book.setNote(editTextNote.getText().toString());
                        book.setPubDate(editTextPubDate.getText().toString());
                        book.setPublisher(editTextPublisher.getText().toString());
                        book.setTranslator(editTextTranslator.getText().toString());

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