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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.jnu.recyclerview.data.DataSaver;
import com.jnu.recyclerview.data.shopItem;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    //    private String[] mStrings = new String[]{"说好不哭", "等你下课", "不爱我就拉到", "123456"};
    private ArrayList<shopItem> shopItems;
    private ArrayList<shopItem> newShopItems;
    private RecyclerView recyclerViewMain;
    private MainRecycleViewAdapter mainRecycleViewAdapter;


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
                    mainRecycleViewAdapter = new MainRecycleViewAdapter(newShopItems);
                    recyclerViewMain.setAdapter(mainRecycleViewAdapter);

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
                    mainRecycleViewAdapter = new MainRecycleViewAdapter(shopItems);
                    recyclerViewMain.setAdapter(mainRecycleViewAdapter);
//                    listView.clearTextFilter();
                } else {
                    //使用用户输入的内容对ListView的列表项进行过滤

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
            finish();
        }

        return super.onOptionsItemSelected(item);
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
//                contextMenu.add(0,0,getAdapterPosition(),"detail of "+getAdapterPosition());
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