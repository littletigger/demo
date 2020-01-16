package com.example.demo.mina;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.PackageData;
import com.example.demo.mina.util.CompareFile;
import com.example.demo.mina.util.TxtToJson;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class Myhandler extends IoHandlerAdapter {
    public Myhandler() {
        super();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
       System.out.println("sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("sessionOpened");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("sessionClosed");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("sessionIdle");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        System.out.println(" exceptionCaught"+cause.getMessage());
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {

        System.out.println("服务端接收到数据"+message.toString());
        TxtToJson.transDataToTxt((PackageData)message,"/home/fjkj/result.txt");
       boolean rs=CompareFile.isSameFile("/home/fjkj/result.txt","/home/fjkj/sendMessage.txt");
        System.out.println("数据对比结果："+rs);
        //session.write(message);



    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

        System.out.println("服务端发送数据"+message);

    }
}
