package com.jnu.recyclerview;

import static com.jnu.recyclerview.R.color.black;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jnu.recyclerview.data.BookShelfSaver;
import com.jnu.recyclerview.data.DataSaver;
import com.jnu.recyclerview.data.shopItem;

import java.util.ArrayList;

public class BookListMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int menu_id_add = 1;
    public static final int menu_id_delete = 2;
    public static final int menu_id_update = 3;

    private ArrayList<com.jnu.recyclerview.data.shopItem> shopItems;
    private MainRecycleViewAdapter mainRecycleViewAdapter;
    private DrawerLayout mDlMain;
    private NavigationView mNavView;
    private Spinner spinner;
    private ArrayList<String> BookShelf;

    //设置一个数据传输器，用于输入页面和主页面之间数据的传回,根据类型intent设置的模版，返回结果为result作为参数，然后执行匿名函数
    private ActivityResultLauncher<Intent> addDataLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(null!=result){
                    //获取数据页面的数据intent
                    Intent intent=result.getData();
                    if(result.getResultCode()==ShopItemActivity.RESULT_CODE_SUCCESS){
                        Bundle bundle=intent.getExtras();
                        String title=bundle.getString("title");
                        String introduction=bundle.getString("introduction");
                        int position=bundle.getInt("position");
                        //在对应的位置添加一个，然后在通知更新器更新
                        shopItems.add(position,new shopItem(title,introduction,R.drawable.ic_launcher_background));
                        //保存更改到文件中
                        new DataSaver().Save(this,shopItems);
                        mainRecycleViewAdapter.notifyItemInserted(position);
                    }
                }
            });
    private ActivityResultLauncher<Intent> updateDataLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(null!=result){
                    //获取数据页面的数据intent
                    Intent intent=result.getData();
                    if(result.getResultCode()==ShopItemActivity.RESULT_CODE_SUCCESS){
                        Bundle bundle=intent.getExtras();
                        String title=bundle.getString("title");
                        String introduction=bundle.getString("introduction");
                        int position=bundle.getInt("position");
                        //在对应的位置添加一个，然后在通知更新器更新
                        shopItems.get(position).setTitle(title);
                        shopItems.get(position).setAuthor(introduction);
                        //保存更改到文件中
                        new DataSaver().Save(this,shopItems);
                        mainRecycleViewAdapter.notifyItemChanged(position);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//改成这样来隐藏原来的标题栏，而不是改动整体的主题为noactionbar
        setContentView(R.layout.activity_main);

        //添加菜单栏
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);  //加载Toolbar控件
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题
//        toolbar.setNavigationIcon(R.mipmap.menu);
//        ActionBar lActionBar = getSupportActionBar();
//        if (lActionBar != null) {
//            lActionBar.setDisplayHomeAsUpEnabled(true);//显示返回按钮
//            lActionBar.setHomeAsUpIndicator(R.mipmap.menu);//替换返回按钮图标，改为导航按钮
//        }

        //找到抽屉，设置监听器（链接导航栏和侧滑）
        mDlMain = (DrawerLayout) findViewById(R.id.dl_nav_buttom);
        //DrawerLayout监听器
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDlMain,
                toolbar,
                R.string.app_name,R.string.app_name
        );
        mDlMain.addDrawerListener(toggle);
        toggle.syncState();

        //设置侧滑栏的点击事件
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mNavView.setCheckedItem(R.id.item_nav_menu_books);//设置默认点击的是书架，即主页面
        mNavView.setNavigationItemSelectedListener(this);

        //spinner 下拉框的数据加载
        spinner = (Spinner) findViewById(R.id.spinner);
        BookShelf=new ArrayList<String>();
        //从数据文件中读取书架数据
        BookShelfSaver bookShelfSaver=new BookShelfSaver();
        BookShelf=bookShelfSaver.Load(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BookShelf);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        //执行spinner的执行函数
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(BookListMainActivity.this, "Search !", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_settings:
                        Toast.makeText(BookListMainActivity.this, "Settings !", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });


        RecyclerView recyclerViewMain=findViewById(R.id.recycle_view_books);
        //设置布局
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMain.setLayoutManager(linearLayoutManager);

        //从数据文件中读取数据
        DataSaver dataSaver=new DataSaver();
        shopItems=dataSaver.Load(this);
        //实现悬浮按钮的添加功能
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookListMainActivity.this,ShopItemActivity.class);
                //把index传过去，不然可能在那边的页面的时候这个页面的index数据被销毁了，所以传过去在传回来，不会错误
                intent.putExtra("position",0);
                //可以简单的显示这个页面，这里把这个页面的结果设置到数据传输器：显示并且接收页面传回来的结果
                addDataLauncher.launch(intent);
            }
        });

        //设置数据接收渲染器
        mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
        recyclerViewMain.setAdapter(mainRecycleViewAdapter);
    }

//    public void addListenerOnSpinnerItemSelection() {
//        spinner = (Spinner) findViewById(R.id.spinner);
//       // spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
//    }

    //设置辛的导航栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //item的菜单执行函数
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //菜单menu的选项执行事件，根据传进来的选项序号执行对应的内容
        switch (item.getItemId())
        {
            case menu_id_add:
                //新建一个页面，用于显示数据输入
                Intent intent=new Intent(this,ShopItemActivity.class);
                //把index传过去，不然可能在那边的页面的时候这个页面的index数据被销毁了，所以传过去在传回来，不会错误
                intent.putExtra("position",item.getOrder());
                //可以简单的显示这个页面，这里把这个页面的结果设置到数据传输器：显示并且接收页面传回来的结果
                addDataLauncher.launch(intent);
//                //在对应的位置添加一个，然后在通知更新器更新
//                shopItems.add(item.getOrder(),new shopItem("added"+item.getOrder(),Math.random()*10,R.drawable.ic_launcher_background));
//                mainRecycleViewAdapter.notifyItemInserted(item.getOrder());
                break;
            case menu_id_delete:
                AlertDialog alertDialog=new AlertDialog.Builder(this)
                        .setTitle(R.string.confirmation)
                        .setMessage(R.string.sure_to_delete_item)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                shopItems.remove(item.getOrder());
                                //保存更改到文件中
                                new DataSaver().Save(BookListMainActivity.this,shopItems);
                                mainRecycleViewAdapter.notifyItemRemoved(item.getOrder());
                            }
                        })
                        .create();
                alertDialog.show();
                break;
            case menu_id_update:
                Intent intentUpdate=new Intent(this,ShopItemActivity.class);
                //把这个item的数据都传过去，不然有可能这个页面在那个页面的过程中销毁了
                intentUpdate.putExtra("position",item.getOrder());
                intentUpdate.putExtra("title",shopItems.get(item.getOrder()).getTitle());
                intentUpdate.putExtra("price",shopItems.get(item.getOrder()).getAuthor());
                addDataLauncher.launch(intentUpdate);
//                shopItems.get(item.getOrder()).setTitle(getString(R.string.update_title));
//                mainRecycleViewAdapter.notifyItemChanged(item.getOrder());
                break;

        }
        return super.onContextItemSelected(item);
    }
    //侧滑栏item的执行函数
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    //adapter重写三个方法，并且还得在内部类设置viewholder类
    //三个方法：返回传进来数组的大小，根据view生成一个viewhodler，根据位置获得的内容写入到viewholder中
    public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.ViewHolder> {
        //private String[]localDataset;
        private ArrayList<shopItem> localDataset;
        //创建viewholder，针对每一个item生成一个viewholder,相当一个容器，里面的东西自定义
        //这个viewholder负责把view里面的子组件找到，并返回，而且可以增加菜单栏选项
        public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final TextView textViewTitle;
            private final TextView textViewIntroduction;
            private final ImageView imageView;

            public ViewHolder(View view) {
                super(view);
                //找到传进来的大view中的小构件
                imageView=view.findViewById(R.id.imageView_item_image);
                textViewTitle = view.findViewById(R.id.textView_item_caption);
                textViewIntroduction = view.findViewById(R.id.textView_item_introduction);

                //设置这个holder的监听事件
                view.setOnCreateContextMenuListener(this);
            }
            public TextView getTextViewIntroduction() {
                return textViewIntroduction;
            }
            public TextView getTextViewTitle() {
                return textViewTitle;
            }
            public ImageView getImageView() {
                return imageView;
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                //viewholder添加的菜单选项样式，那个选项，哪一个item，显示信息
                //执行菜单选项的函数在上面，onContextItemSelected
                contextMenu.add(0,menu_id_add,getAdapterPosition(),"add"+getAdapterPosition());
                contextMenu.add(0, menu_id_delete,getAdapterPosition(),"delete"+getAdapterPosition());
                contextMenu.add(0, menu_id_update,getAdapterPosition(),"update"+getAdapterPosition());
            }
        }
        public MainRecycleViewAdapter(ArrayList<shopItem> dataset){
            localDataset=dataset;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            //提取出view出来用在viewholder
            View view= LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_main,viewGroup,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //holder设置数据
            holder.getTextViewTitle().setText(localDataset.get(position).getTitle());
            holder.getTextViewIntroduction().setText(localDataset.get(position).getAuthor());
            holder.getImageView().setImageResource(localDataset.get(position).getResourceId());
        }

        @Override
        public int getItemCount() {
            return localDataset.size();
        }
    }

    //spinner选项的执行函数：根据书架显示书
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }


}