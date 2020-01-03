package com.example.demo.mina;

import com.example.demo.mina.entity.Message;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Date;

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
        byte[] bytes=(byte[]) message;
        System.out.println("服务端接收到数据");
        for (int i=0;i<bytes.length;i++)
            System.out.print( Integer.toHexString(bytes[i]& 0xff)+" ");


    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

        System.out.println("服务端发送数据"+message);

    }
}
