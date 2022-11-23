package com.jnu.recyclerview;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.jnu.recyclerview.data.DataSaver;
import com.jnu.recyclerview.data.shopItem;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    //    private String[] mStrings = new String[]{"说好不哭", "等你下课", "不爱我就拉到", "123456"};
    public static final int menu_id_add = 1;
    public static final int menu_id_delete = 2;
    public static final int menu_id_update = 3;
    public static final int RESULT_CODE_SUCCESS = 666;
    public Boolean flag=true;      //flag来判断是在全部书还是在检索后的结果页面
    private ArrayList<shopItem> shopItems;
    private ArrayList<shopItem> newShopItems;
    private RecyclerView recyclerViewMain;
    private MainRecycleViewAdapter mainRecycleViewAdapter;
    private MainRecycleViewAdapter spinnerRecycleViewAdapter;
    int position;

    //设置一个数据传输器，用于输入页面和主页面之间数据的传回,根据类型intent设置的模版，返回结果为result作为参数，然后执行匿名函数
    private ActivityResultLauncher<Intent> addDataLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(null!=result){
                    //获取数据页面的数据intent
                    Intent intent=result.getData();
                    if(result.getResultCode()== RESULT_CODE_SUCCESS){
                        Bundle bundle=intent.getExtras();
                        shopItem book= (shopItem) bundle.getSerializable("book");
                        position=bundle.getInt("position");
                        int position_spinner=bundle.getInt("position_spinner");
                        Boolean flag_=bundle.getBoolean("flag");  //判断是否在spinner中
                        //在对应的位置添加一个，然后在通知更新器更新
                        shopItems.add(position,book);

                        if(!flag_){         //设置一个flag来判断是否是spinner回来的，如果是用新的数组更新页面，同时主页面更新；如果不是只更新主页面
                            //判断新增加的书的书架是否在选中的暑假内，如果不是不增加到新的数组中
                            if(book.getTitle().equals(newShopItems.get(position_spinner).getTitle())){
                                newShopItems.add(position_spinner,book);
                            }
                            spinnerRecycleViewAdapter.notifyItemInserted(position_spinner);
                            recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
                        }
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
                    if(result.getResultCode()== RESULT_CODE_SUCCESS){
                        Bundle bundle=intent.getExtras();
                        shopItem book= (shopItem) bundle.getSerializable("book");
                        position=bundle.getInt("position");
                        int position_spinner=bundle.getInt("position_spinner");
                        Boolean flag_=bundle.getBoolean("flag");  //判断是否在spinner中

                        if(!flag_){         //设置一个flag来判断是否是spinner回来的，如果是用新的数组更新页面，同时主页面更新；如果不是只更新主页面
                            //判断更新的书的书架是否在选中的暑假内，如果是更新新数组，如果不是在新数组中删除
                            if(book.getTitle().equals(shopItems.get(position).getTitle())){
                                newShopItems.set(position_spinner,book);
                            }
                            else{
                                newShopItems.remove(position_spinner);
                            }
                            spinnerRecycleViewAdapter.notifyItemChanged(position_spinner);
                            recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
                        }
                        //在对应的位置添加一个，然后在通知更新器更新
                        Log.i("text",""+position);
                        shopItems.set(position,book);
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
        setContentView(R.layout.activity_search);

        //添加菜单栏
        Toolbar toolbar_search = (Toolbar)findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar_search);  //加载Toolbar控件
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题

        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) {
            lActionBar.setDisplayHomeAsUpEnabled(true);//显示返回按钮
        }

        //从数据文件中读取数据
        DataSaver dataSaver=new DataSaver();
        shopItems=dataSaver.Load(this);
        recyclerViewMain=findViewById(R.id.recycle_view_books);
        //设置布局
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMain.setLayoutManager(linearLayoutManager);
        //设置数据接收渲染器
        mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
        recyclerViewMain.setAdapter(mainRecycleViewAdapter);

//        final ListView listView = findViewById(R.id.listview);
//        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, mStrings));
//        //设置ListView启用过滤
//        listView.setTextFilterEnabled(true);

        SearchView searchView = findViewById(R.id.searchview);
        //设置该SearchView默认是否自动缩小为图标
        searchView.setIconifiedByDefault(false);
        //设置该SearchView显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("查找");
        //为该SearchView组件设置事件监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //单机搜索按钮时激发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                newShopItems=new ArrayList<>();
                //实际应用中应该在该方法内执行实际查询，此处仅使用Toast显示用户输入的查询内容
                for(int i=0;i<shopItems.size();i++){
                    if(query.equals(shopItems.get(i).getTitle())){
                        newShopItems.add(shopItems.get(i));
                    }
                }
                if(newShopItems.size()!=0){
                    flag=false;
                    spinnerRecycleViewAdapter = new MainRecycleViewAdapter(newShopItems);
                    recyclerViewMain.setAdapter(spinnerRecycleViewAdapter);
                }
                else{
                    Toast.makeText(SearchActivity.this, "没有书名为： " + query+" 的书",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            //用户输入字符时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //如果newText不是长度为0的字符串
                if (TextUtils.isEmpty(newText)) {
                    //清除ListView的过滤
                    flag=true;
                    mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
                    recyclerViewMain.setAdapter(mainRecycleViewAdapter);
//                    listView.clearTextFilter();
                } else {
                    //使用用户输入的内容对ListView的列表项进行过滤
                    flag=true;
//                    listView.setFilterText(newText);
                }
                return true;
            }
        });
    }

    //实现返回按钮返回主页面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            //设置成功的返回结果:数字和intent
            Intent intent=new Intent();
            intent.putExtra("position",position);
            setResult(RESULT_CODE_SUCCESS,intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
                Bundle bundle=new Bundle();
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
                bundle.putBoolean("flag",flag);
                intent.putExtras(bundle);
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
                                new DataSaver().Save(SearchActivity.this,shopItems);
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

    //adapter重写三个方法，并且还得在内部类设置viewholder类
    //三个方法：返回传进来数组的大小，根据view生成一个viewhodler，根据位置获得的内容写入到viewholder中
    public static class MainRecycleViewAdapter extends RecyclerView.Adapter<SearchActivity.MainRecycleViewAdapter.ViewHolder> {
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
        public SearchActivity.MainRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            //提取出view出来用在viewholder
            View view= LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_main,viewGroup,false);
            return new SearchActivity.MainRecycleViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchActivity.MainRecycleViewAdapter.ViewHolder holder, int position) {
            //holder设置数据
            holder.getTextViewTitle().setText(localDataset.get(position).getTitle());
            holder.getTextViewIntroduction().setText(localDataset.get(position).getAuthor()+" 著，"+localDataset.get(position).getPublisher());
            holder.getTextViewPubDate().setText(localDataset.get(position).getPubDate());
            holder.getImageView().setImageResource(localDataset.get(position).getResourceId());
        }

        @Override
        public int getItemCount() {
            return localDataset.size();
        }
    }

}