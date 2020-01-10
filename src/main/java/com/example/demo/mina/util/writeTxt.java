package com.example.demo.mina.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class writeTxt {
    public static void output(Object data){
        ObjectOutputStream oos=null;

    try {
        System.out.println("gogogo");
    //创建对象输出流对象，制定保存的文件路径
    oos=new ObjectOutputStream(new FileOutputStream("/home/fjkj/text.txt"));
    //把该对象输出到文本中
    oos.writeObject(data);
    } catch (Exception e) {
    e.printStackTrace();
    }finally{
    try {
    oos.close();
    } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
}
}
}
