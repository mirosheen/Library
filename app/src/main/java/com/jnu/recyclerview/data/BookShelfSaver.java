package com.jnu.recyclerview.data;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BookShelfSaver {
    public void Save(Context context, ArrayList<String> data){
        try {
            //这里的context为app，传进来一个data，把data序列化写进文件中，模式为清零写入
            //并且这里的异常简单处理
            FileOutputStream dataStream=context.openFileOutput("BookShelf.dat",Context.MODE_PRIVATE);
            ObjectOutputStream out=new ObjectOutputStream(dataStream);
            out.writeObject(data);
            out.close();
            dataStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @NonNull
    public  ArrayList<String> Load(Context context){
        ArrayList<String> data=new ArrayList<>();
        try {
            //这里的context为app,读取文件返回data
            //并且这里的异常简单处理
            FileInputStream dataStream=context.openFileInput("BookShelf.dat");
            ObjectInputStream in=new ObjectInputStream(dataStream);
            data=(ArrayList<String>)in.readObject();
            in.close();
            dataStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
