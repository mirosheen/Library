package com.jnu.recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jnu.recyclerview.data.LabelSaver;

import java.util.ArrayList;
import java.util.Collections;

public class LabelActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLabel;
    private RecyclerView recyclerViewOtherLabel;
    private LabelRecycleViewAdapter labelRecycleViewAdapter;     //mylabel的适配器
    private LabelRecycleViewAdapter otherLabelRecycleViewAdapter;     //otherlabel的适配器
    public ArrayList<String> all;
    public ArrayList<String> labels=new ArrayList<>();
    public ArrayList<String> otherLabels=new ArrayList<>();
    int labelsCount;
    // 是否为编辑模式
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        //recyclerview主页面的设置
        recyclerViewLabel = findViewById(R.id.recycle_view_myLabels);
        //设置布局
        recyclerViewLabel.setLayoutManager(new GridLayoutManager(LabelActivity.this,4));
//        labels=new ArrayList<>();
//        labels.add("名著");
//        labels.add("编程");
//        labels.add("科幻");
//        labels.add("漫画");
//        labels.add("历史");
//        labels.add("中国");
        LabelSaver labelSaver=new LabelSaver();
//        labelSaver.Save(LabelActivity.this,labels);
//        labels.remove(0);

        //文件第一个为labels的数量，根据数量加载labels和其他labels
        all=new ArrayList<>();

        all=labelSaver.Load(LabelActivity.this);
//        all.remove(0);
//        labelSaver.Save(LabelActivity.this,all);
        labelsCount=Integer.parseInt(all.get(0));
        for(int i=1;i<=labelsCount;i++){
            labels.add(all.get(i));
        }
        for(int i=labelsCount+1;i<all.size();i++){
            otherLabels.add(all.get(i));
        }

        labelRecycleViewAdapter = new LabelRecycleViewAdapter(labels);
        recyclerViewLabel.setAdapter(labelRecycleViewAdapter);
        //设置监听
        recyclerViewLabel.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerViewLabel) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(LabelActivity.this, labels.get(vh.getLayoutPosition()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh) {
                //自己所属的view启动拖动，对方的view设置坚听
                RecycleViewOnDragListener recycleViewOnDragListener=new RecycleViewOnDragListener();
                recycleViewOnDragListener.startDrag(recyclerView, vh);
                recyclerViewOtherLabel.setOnDragListener(new RecycleViewOnDragListener());
            }
        });

//        otherLabels =new ArrayList<>();
//        otherLabels.add("小说");
//        otherLabels.add("日本");
//        otherLabels.add("外国文学");
//        otherLabels.add("文学");
//        otherLabels.add("心理学");
//        otherLabels.add("随笔");
//        otherLabels.add("哲学");
//        otherLabels.add("绘本");
//        otherLabels.add("中国文学");
//        otherLabels.add("推理");
//        otherLabels.add("美国");
//        otherLabels.add("爱情");
//        otherLabels.add("经典");
//        otherLabels.add("传记");
//        otherLabels.add("日本文学");
//        otherLabels.add("散文");
//        otherLabels.add("文化");
//        otherLabels.add("青春");
//        otherLabels.add("旅行");
//        otherLabels.add("社会学");
//        otherLabels.add("英国");
//        otherLabels.add("言情");
//        otherLabels.add("科普");
//        otherLabels.add("生活");
        //保存新的all labels

        recyclerViewOtherLabel = findViewById(R.id.recycle_view_otherLabels);
        recyclerViewOtherLabel.setLayoutManager(new GridLayoutManager(LabelActivity.this,4));
        otherLabelRecycleViewAdapter = new LabelRecycleViewAdapter(otherLabels);
        recyclerViewOtherLabel.setAdapter(otherLabelRecycleViewAdapter);
        //设置监听
        recyclerViewOtherLabel.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerViewOtherLabel) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(LabelActivity.this, otherLabels.get(vh.getLayoutPosition()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh) {
                RecycleViewOnDragListener recycleViewOnDragListener=new RecycleViewOnDragListener();
                recycleViewOnDragListener.startDrag(recyclerView, vh);
                recyclerViewLabel.setOnDragListener(new RecycleViewOnDragListener());
            }
        });
//
//        all=new ArrayList<>();
//        all.add(""+labels.size());
//        all.addAll(labels);
//        all.addAll(otherLabels);
//        labelSaver.Save(LabelActivity.this,all);

    }


//    public class labelCallback extends ItemTouchHelper.Callback {
//        ArrayList<String>localData;
//        LabelRecycleViewAdapter adapter;
//
//        labelCallback(ArrayList<String> data,LabelRecycleViewAdapter adapter){
//            this.localData=data;
//            this.adapter=adapter;
//        }
//        @Override
//        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//            //拖拽方向
//            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
//                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//            //侧滑删除
//            final int swipeFlags = 0;
//            return makeMovementFlags(dragFlags, swipeFlags);
//        }
//
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            //得到当拖拽的viewHolder的Position
//            int fromPosition = viewHolder.getAdapterPosition();
//            //拿到当前拖拽到的item的viewHolder
//            int toPosition = target.getAdapterPosition();
//            if (fromPosition < toPosition) {
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(localData, i, i + 1);
//                }
//            } else {
//                for (int i = fromPosition; i > toPosition; i--) {
//                    Collections.swap(localData, i, i - 1);
//                }
//            }
//            adapter.notifyItemMoved(fromPosition, toPosition);
//            return true;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//        }
//
//    }

    public static class LabelRecycleViewAdapter extends RecyclerView.Adapter<LabelActivity.LabelRecycleViewAdapter.ViewHolder> {
        private ArrayList<String> localDataset;

        public ArrayList<String> getDatas() {
            return localDataset;
        }

        //创建viewholder，针对每一个item生成一个viewholder,相当一个容器，里面的东西自定义
        //这个viewholder负责把view里面的子组件找到，并返回，而且可以增加菜单栏选项
        public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final TextView textViewLabel;

            public ViewHolder(View view) {
                super(view);
                //找到传进来的大view中的小构件
                textViewLabel=view.findViewById(R.id.textView_item_label);
                //设置这个holder的监听事件
                view.setOnCreateContextMenuListener(this);
            }
            public TextView getTextViewLabel() {
                return textViewLabel;
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                //viewholder添加的菜单选项样式，那个选项，哪一个item，显示信息
//                //执行菜单选项的函数在上面，onContextItemSelected
//                contextMenu.add(0,menu_id_add,getAdapterPosition(),"add"+getAdapterPosition());
//                contextMenu.add(0, menu_id_delete,getAdapterPosition(),"delete"+getAdapterPosition());
//                contextMenu.add(0, menu_id_update,getAdapterPosition(),"update"+getAdapterPosition());
            }

        }
        public LabelRecycleViewAdapter(ArrayList<String> dataset){
            localDataset=dataset;
        }
        @NonNull
        @Override
        public LabelActivity.LabelRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            //提取出view出来用在viewholder
            View view= LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_label,viewGroup,false);
            return new LabelActivity.LabelRecycleViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LabelActivity.LabelRecycleViewAdapter.ViewHolder holder, int position) {
            //holder设置数据
//            holder.getTextViewTitle().setText(localDataset.get(position).getTitle());
//            holder.getTextViewIntroduction().setText(localDataset.get(position).getAuthor()+" 著，"+localDataset.get(position).getPublisher());
//            holder.getTextViewPubDate().setText(localDataset.get(position).getPubDate());
//            holder.getImageView().setImageResource(localDataset.get(position).getResourceId());
            holder.getTextViewLabel().setText(localDataset.get(position));

        }

        @Override
        public int getItemCount() {
            return localDataset.size();
        }

    }


    public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private GestureDetectorCompat mGestureDetector;
        private RecyclerView recyclerView;

        public OnRecyclerItemClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), (GestureDetector.OnGestureListener) new ItemTouchHelperGestureListener());
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                    onItemClick(vh);
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                    onItemLongClick(recyclerView, vh);
                }
            }
        }

        public abstract void onItemClick(RecyclerView.ViewHolder vh);

        public abstract void onItemLongClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh);
    }

    private class RecycleViewOnDragListener implements View.OnDragListener {
        public void startDrag(RecyclerView recyclerView, RecyclerView.ViewHolder vh) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(vh.itemView);
//            我们将触发拖拽的条目信息传递到拖拽事件中去，即传递给目的地的view
            vh.itemView.startDrag(null, shadowBuilder,
                    new RecycleViewOnDragListener.DragData(recyclerView, vh), 0);
        }

        public  class DragData {

            public RecyclerView recyclerView;
            public RecyclerView.ViewHolder viewHolder;

            public DragData(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                this.recyclerView = recyclerView;
                this.viewHolder = viewHolder;
            }
        }

        @Override
        public boolean onDrag(View view, @NonNull DragEvent dragEvent) {
            //只要拖动的item经过view，view就会自动响应这个函数
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP://处理
                    handleDrag(view, dragEvent);
                    break;
            }
            return true;
        }

        public void handleDrag(View view, DragEvent dragEvent) {
            RecyclerView endRecycleView = null;
            ArrayList<String> all=new ArrayList<>();
            if (view instanceof RecyclerView) {
                endRecycleView = (RecyclerView) view;
            }

            if (endRecycleView == null) {
                return;
            }

            View item = endRecycleView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
            if (item == null) {
                return;
            }

            DragData dragData = (DragData) dragEvent.getLocalState();
            int fromPosition = dragData.viewHolder.getAdapterPosition();
            int toPosition = endRecycleView.getChildLayoutPosition(item);

            LabelRecycleViewAdapter endAdapter = (LabelRecycleViewAdapter) endRecycleView.getAdapter();

            if (fromPosition < 0 || toPosition < 0) {
                return;
            }

            if (dragData.recyclerView == endRecycleView) {
                // 同一个RecycleView中

                if (fromPosition == toPosition) {
                    return;
                }

                ArrayList<String> temp = endAdapter.getDatas();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(temp, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(temp, i, i - 1);
                    }
                }

                endAdapter.notifyItemMoved(fromPosition, toPosition);
                endAdapter.notifyDataSetChanged();
                //保存数据
                all=new LabelSaver().Load(LabelActivity.this);
                for(int i=1;i<=temp.size();i++){
                    all.set(i,temp.get(i-1));
                }
                new LabelSaver().Save(LabelActivity.this,all);
            } else {
                // 不同RecycleView之间交换

                LabelRecycleViewAdapter fromAdapter = (LabelRecycleViewAdapter) dragData.recyclerView.getAdapter();


                String fData = fromAdapter.getDatas().remove(fromPosition);
                endAdapter.getDatas().add(toPosition, fData);

                fromAdapter.notifyItemRemoved(fromPosition);
                endAdapter.notifyItemInserted(toPosition);

                //保存数据
                if(fromAdapter==otherLabelRecycleViewAdapter){
                    all=new ArrayList<>();
                    int count=endAdapter.getDatas().size();
                    all.add(""+count);
                    all.addAll(endAdapter.getDatas());
                    all.addAll(fromAdapter.getDatas());
                    new LabelSaver().Save(LabelActivity.this,all);
                }
                else{
                    all=new ArrayList<>();
                    int count=fromAdapter.getDatas().size();
                    all.add(""+count);
                    all.addAll(fromAdapter.getDatas());
                    all.addAll(endAdapter.getDatas());
                    new LabelSaver().Save(LabelActivity.this,all);
                }

            }
        }
    }
}