package com.jnu.recyclerview.data;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

//把app的数据保存，有大概四种方法，保存到设置中，保存到文件中，保存到数据库中，保存到网络中
//这里保存到文件中，文件两种：一种是这个app的私有文件，一种是安卓系统的其他文件，访问其他文件需要安卓系统的允许
//文件会逐渐变大，所以小规模的可以使用，大规模的要使用其他方式
//view-toolwindows-device-file-explorer中的data/data/jnu.recyclerview
public class DataSaver {
    public void Save(Context context, ArrayList<shopItem>data){
        try {
            //这里的context为app，传进来一个data，把data序列化写进文件中，模式为清零写入
            //并且这里的异常简单处理
            FileOutputStream dataStream=context.openFileOutput("mydata.dat",Context.MODE_PRIVATE);
            ObjectOutputStream out=new ObjectOutputStream(dataStream);
            out.writeObject(data);
            out.close();
            dataStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @NonNull
    public  ArrayList<shopItem> Load(Context context){
        ArrayList<shopItem> data=new ArrayList<>();
        try {
            //这里的context为app,读取文件返回data
            //并且这里的异常简单处理
            FileInputStream dataStream=context.openFileInput("mydata.dat");
            ObjectInputStream in=new ObjectInputStream(dataStream);
            data=(ArrayList<shopItem>)in.readObject();
            in.close();
            dataStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
