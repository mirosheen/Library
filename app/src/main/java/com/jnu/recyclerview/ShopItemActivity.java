package com.jnu.recyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ShopItemActivity extends AppCompatActivity {

    public static final int RESULT_CODE_SUCCESS = 666;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_item);

        //设置按钮的监听执行函数：把输入栏的数据打包bundle，然后设置进intent中，然后intent回传给主页面
        EditText editTextTitle=findViewById(R.id.editText_shop_item_title);
        EditText editTextIntroduction=findViewById(R.id.editText_shop_item_Introduction);

        position= getIntent().getIntExtra("position",0);//获取到传过来的position
        String title=getIntent().getStringExtra("title");
        String introduction=getIntent().getStringExtra("introduction");
        //有可能没有传过来title，比如创建一个新的item的时候
        if(null!=title){
            editTextTitle.setText(title);
            editTextIntroduction.setText(introduction);
        }

        Button buttonOk=findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                //打包数据
                Bundle bundle=new Bundle();
                bundle.putString("title",editTextTitle.getText().toString());
                bundle.putString("introduction",editTextIntroduction.getText().toString());
                bundle.putInt("position",position);
                //把打包的东西放进intent中
                intent.putExtras(bundle);
                //设置成功的返回结果:数字和intent
                setResult(RESULT_CODE_SUCCESS,intent);
                //关闭页面
                ShopItemActivity.this.finish();
            }
        });
    }
}