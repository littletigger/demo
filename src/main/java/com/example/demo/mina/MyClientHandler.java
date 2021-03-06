package com.example.demo.mina;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.PackageData;
import com.example.demo.mina.util.TxtToJson;
import com.example.demo.mina.util.writeTxt;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class MyClientHandler  extends IoHandlerAdapter {


    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
       System.out.println("exception"+cause.getMessage());
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
       System.out.println("客户端接受到数据-->"+message.toString());
    }
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

         super.messageSent(session,message);
        System.out.println("客户端发送消息成功：" + message.toString());
        TxtToJson.transDataToTxt((PackageData)message,"/home/fjkj/sendMessage.txt");
        //writeTxt.output(message);
    }

    }



