package com.jnu.recyclerview;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jnu.recyclerview.data.BookShelfSaver;
import com.jnu.recyclerview.data.Capture;
import com.jnu.recyclerview.data.DataSaver;
import com.jnu.recyclerview.data.HttpDataLoader;
import com.jnu.recyclerview.data.shopItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BookListMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int menu_id_add = 1;
    public static final int menu_id_delete = 2;
    public static final int menu_id_update = 3;

    private ArrayList<com.jnu.recyclerview.data.shopItem> shopItems;  //主页面的数组
    ArrayList<com.jnu.recyclerview.data.shopItem> newShopItems;  //spinner页面的数组
    private MainRecycleViewAdapter mainRecycleViewAdapter;     //主页面的适配器
    private MainRecycleViewAdapter spinnerRecycleViewAdapter;//spinner页面的适配器
    private DrawerLayout mDlMain;
    private NavigationView mNavView;
    private Spinner spinner;
    public ArrayList<String> BookShelf;
    private RecyclerView recyclerViewMain;
    public Boolean flag=true;  //用一个flag来判断是在主页面还是在spinner页面，通过点击spinner选项进行更新
    Bitmap bitmap=null;

    //设置一个数据传输器，用于输入页面和主页面之间数据的传回,根据类型intent设置的模版，返回结果为result作为参数，然后执行匿名函数
    private ActivityResultLauncher<Intent> addDataLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(null!=result){
                    //获取数据页面的数据intent
                    Intent intent=result.getData();
                    if(result.getResultCode()==ShopItemActivity.RESULT_CODE_SUCCESS){
                        Bundle bundle=intent.getExtras();
                        shopItem book= (shopItem) bundle.getSerializable("book");
                        int position=bundle.getInt("position");   //获取增加的书在主页面的位置
                        int position_spinner=bundle.getInt("position_spinner");  //获取增加的书在spinner页面的位置
                        Boolean flag_=bundle.getBoolean("flag");  //判断是否在spinner中
                        //在对应的位置添加一个，然后在通知更新器更新
                        shopItems.add(position,book);

                        if(!flag_){         //设置一个flag来判断是否是spinner回来的，如果是用新的数组更新页面，同时主页面更新；如果不是只更新主页面
                            //判断新增加的书的书架是否在选中的暑假内，如果不是不增加到新的数组中
                            if(book.getBookShelf().equals(newShopItems.get(position_spinner).getBookShelf())){
                                newShopItems.add(position_spinner,book);
                            }
                            spinnerRecycleViewAdapter.notifyItemInserted(position_spinner);
                            recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
                        }
                        //保存更改到文件中
                        new DataSaver().Save(this,shopItems);
                        mainRecycleViewAdapter.notifyItemInserted(position);
                        Log.i("wuwu","wuuw");
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
                        shopItem book= (shopItem) bundle.getSerializable("book");
                        int position=bundle.getInt("position");
                        int position_spinner=bundle.getInt("position_spinner");
                        Boolean flag_=bundle.getBoolean("flag");  //判断是否在spinner中

                        if(!flag_){         //设置一个flag来判断是否是spinner回来的，如果是用新的数组更新页面，同时主页面更新；如果不是只更新主页面
                            //判断更新的书的书架是否在选中的暑假内，如果是更新新数组，如果不是在新数组中删除
                            if(book.getBookShelf().equals(shopItems.get(position).getBookShelf())){
                                newShopItems.set(position_spinner,book);
                            }
                            else{
                                newShopItems.remove(position_spinner);
                            }
                            spinnerRecycleViewAdapter.notifyItemChanged(position_spinner);
                            recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
                        }
                        //在对应的位置添加一个，然后在通知更新器更新
                        shopItems.set(position,book);
                        //保存更改到文件中
                        new DataSaver().Save(this,shopItems);
                        mainRecycleViewAdapter.notifyItemChanged(position);
                    }
                }
            });
    private ActivityResultLauncher<Intent> searchDataLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(null!=result){
                    if(result.getResultCode()==SearchActivity.RESULT_CODE_SUCCESS){
                        //获取search页面的更新，然后同步更新主页面
                        shopItems=new DataSaver().Load(this);
                        mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
                        recyclerViewMain.setAdapter(mainRecycleViewAdapter);
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

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Intent intent=new Intent(BookListMainActivity.this,SearchActivity.class);
                        searchDataLauncher.launch(intent);
                        //Toast.makeText(BookListMainActivity.this, "Search !", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_scan:
                        IntentIntegrator intentIntegrator=new IntentIntegrator(BookListMainActivity.this);
                        intentIntegrator.setPrompt("For flash use volume up key");
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setOrientationLocked(true);
                        intentIntegrator.setCaptureActivity(Capture.class);
                        intentIntegrator.initiateScan();
                        break;
                }
                return true;
            }
        });



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
        // 去除图标颜色显示规则, 显示为原色
        mNavView.setItemIconTintList(null);



        //spinner 下拉框的数据加载
        spinner = (Spinner) findViewById(R.id.spinner);
        BookShelf=new ArrayList<String>();
        BookShelf.add(0,"All");
        BookShelf.add("Default Bookshelf");//第一次先写入一些书架
        for(int i=0;i<5;i++)
            BookShelf.add("Bookshelf"+i);
        //从数据文件中读取书架数据
        BookShelfSaver bookShelfSaver=new BookShelfSaver();
        bookShelfSaver.Save(this,BookShelf);
        BookShelf=bookShelfSaver.Load(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BookShelf);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
        //执行spinner的执行函数
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());



        //实现悬浮按钮的添加功能
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookListMainActivity.this,ShopItemActivity.class);
                //把index传过去，不然可能在那边的页面的时候这个页面的index数据被销毁了，所以传过去在传回来，不会错误
                Bundle bundle=new Bundle();
                bundle.putInt("position",0);
                bundle.putInt("position_spinner",0);
                bundle.putBoolean("flag",flag);
                intent.putExtras(bundle);
                //可以简单的显示这个页面，这里把这个页面的结果设置到数据传输器：显示并且接收页面传回来的结果
                addDataLauncher.launch(intent);
            }
        });


        //recyclerview主页面的设置
        recyclerViewMain=findViewById(R.id.recycle_view_books);
        //设置布局
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMain.setLayoutManager(linearLayoutManager);

        //从数据文件中读取数据
        DataSaver dataSaver=new DataSaver();
        shopItems=dataSaver.Load(this);
//        shopItem book=new shopItem();
//        book.setUrl("a");
//        book.setAuthor("ab");
//        book.setBookShelf("a");
//        book.setTitle("a");
//        book.setISBN("a");
//        book.setLabel("a");
//        book.setNote("a");
//        book.setPubDate("a");
//        book.setPublisher("a");
//        book.setTranslator("a");
//        book.setState("b");
//        book.setResourceId(R.mipmap.ic_launcher);
//        shopItems.add(0,book);
        //设置数据接收渲染器
        mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
        recyclerViewMain.setAdapter(mainRecycleViewAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK){
            if (intentResult.getContents() != null) {
                //新建一个页面，用于显示数据输入
                Intent intent = new Intent(this, ShopItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position_spinner", 0);
                bundle.putInt("position", 0);
                bundle.putBoolean("flag", flag);  //把在哪个页面的标志传过去

                String message = intentResult.getContents();
                bundle.putString("isbn", message);
                intent.putExtras(bundle);
            addDataLauncher.launch(intent);
//                startActivity(intent,bundle);
            } else {
                Toast.makeText(getApplicationContext(), "OOPS... Did not scan anything", Toast.LENGTH_SHORT).show();
            }
        }

    }

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
                Bundle bundle=new Bundle();
                //把index传过去，不然可能在那边的页面的时候这个页面的index数据被销毁了，所以传过去在传回来，不会错误
                //主页面和spinner页面需要同步更新，所以需要传两个，一个是在spinner，一个是在主页面的index
                if(!flag){
                    bundle.putInt("position_spinner",item.getOrder());
                    int i;
                    for(i=0;i<shopItems.size();i++){
                        if(newShopItems.get(item.getOrder())==shopItems.get(i)){
                            break;
                        }
                    }
                    bundle.putInt("position",i);
                }
                else{
                    bundle.putInt("position",item.getOrder());
                }
                bundle.putBoolean("flag",flag);  //把在哪个页面的标志传过去
                intent.putExtras(bundle);
                //可以简单的显示这个页面，这里把这个页面的结果设置到数据传输器：显示并且接收页面传回来的结果
                addDataLauncher.launch(intent);
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
                                int j;
                                if(!flag){
                                    for(j=0;j<shopItems.size();j++){
                                        if(newShopItems.get(item.getOrder())==shopItems.get(j)){
                                            break;
                                        }
                                    }
                                    shopItems.remove(j);
                                    newShopItems.remove(item.getOrder());
                                    spinnerRecycleViewAdapter.notifyItemRemoved(item.getOrder());
                                    recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
                                }
                                else{
                                    shopItems.remove(item.getOrder());
                                    j=item.getOrder();
                                }
                                //保存更改到文件中
                                new DataSaver().Save(BookListMainActivity.this,shopItems);
                                mainRecycleViewAdapter.notifyItemRemoved(j);
                            }
                        })
                        .create();
                alertDialog.show();
                break;
            case menu_id_update:
                Intent intentUpdate=new Intent(this,ShopItemActivity.class);
                Bundle bundle_=new Bundle();
                shopItem book;
                if(!flag){
                    bundle_.putInt("position_spinner",item.getOrder());
                    int i;
                    for(i=0;i<shopItems.size();i++){
                        if(newShopItems.get(item.getOrder())==shopItems.get(i)){
                            break;
                        }
                    }
                    bundle_.putInt("position",i);
                    //把这个item的数据都传过去，不然有可能这个页面在那个页面的过程中销毁了
                    //如果spinner内，那么如果按原来的，会选中其他书
                    book=new shopItem(shopItems.get(i));
                }
                else{
                    bundle_.putInt("position",item.getOrder());
                    //把这个item的数据都传过去，不然有可能这个页面在那个页面的过程中销毁了
                    book=new shopItem(shopItems.get(item.getOrder()));
                }
                bundle_.putSerializable("book",book);
                bundle_.putBoolean("flag",flag);
                intentUpdate.putExtras(bundle_);
                updateDataLauncher.launch(intentUpdate);
//                shopItems.get(item.getOrder()).setTitle(getString(R.string.update_title));
//                mainRecycleViewAdapter.notifyItemChanged(item.getOrder());
                break;

        }
        return super.onContextItemSelected(item);
    }
    //侧滑栏item的执行函数
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_nav_menu_books:
                mDlMain.closeDrawer(GravityCompat.START);
                break;
            case R.id.item_nav_menu_search:
                //建一个数据接收器，同步在search页面的更新
                Intent intent=new Intent(BookListMainActivity.this,SearchActivity.class);
                searchDataLauncher.launch(intent);
                break;
            case R.id.item_nav_menu_add:
                Intent intent_label=new Intent(BookListMainActivity.this,LabelActivity.class);
                startActivity(intent_label);
//                mNavView.getMenu().add("ok");
                break;
            case R.id.item_nav_menu_set:
                break;
            default:
                break;
        }

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
            private final TextView textViewPubDate;

            public ViewHolder(View view) {
                super(view);
                //找到传进来的大view中的小构件
                imageView=view.findViewById(R.id.imageView_item_image);
                textViewTitle = view.findViewById(R.id.textView_item_caption);
                textViewIntroduction = view.findViewById(R.id.textView_item_introduction);
                textViewPubDate = view.findViewById(R.id.textView_item_pubDate);
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
            public TextView getTextViewPubDate() {
                return textViewPubDate;
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
            holder.getTextViewIntroduction().setText(localDataset.get(position).getAuthor()+localDataset.get(position).getPublisher());
            holder.getTextViewPubDate().setText(localDataset.get(position).getPubDate());
            String url=localDataset.get(position).getUrl();
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

                    }
                }).start();
                holder.getImageView().setImageBitmap(bitmap);
            }
            else
                holder.getImageView().setImageResource(R.mipmap.ic_launcher);

        }

        @Override
        public int getItemCount() {
            return localDataset.size();
        }
    }

    //spinner选项的执行函数：根据书架显示书
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            if(parent.getItemAtPosition(pos).toString().equals("All")){
                flag=true;      //返回主页面一定要通过这里，所以需要转换标志
                mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
                recyclerViewMain.setAdapter(mainRecycleViewAdapter);
                return;
            }
            newShopItems=new ArrayList<>();  //找到同一个暑假的书
            for(int i=0;i<shopItems.size();i++){
                if(shopItems.get(i).getBookShelf().equals(parent.getItemAtPosition(pos).toString())){
                    newShopItems.add(shopItems.get(i));
                }
            }
            flag=false;   //进入到这里说明在spinner页面，转换标志
            spinnerRecycleViewAdapter = new MainRecycleViewAdapter(newShopItems);
            recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
