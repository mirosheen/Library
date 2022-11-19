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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShopItemActivity extends AppCompatActivity {

    public static final int RESULT_CODE_SUCCESS = 666;
    private int position;
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

        position= getIntent().getIntExtra("position",0);//获取到传过来的position
        String title=getIntent().getStringExtra("title");
        String author=getIntent().getStringExtra("author");
        //有可能没有传过来title，比如创建一个新的item的时候
        if(null!=title){
            editTextTitle.setText(title);
            editTextAuthor.setText(author);
        }
        toolbar_add.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        Intent intent=new Intent();
                        //打包数据
                        Bundle bundle=new Bundle();
                        bundle.putString("title",editTextTitle.getText().toString());
                        bundle.putString("introduction",editTextAuthor.getText().toString());
                        bundle.putInt("position",position);
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