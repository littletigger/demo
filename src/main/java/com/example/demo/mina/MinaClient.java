package com.example.demo.mina;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.protocol.Test2ProtocolCodecFactory;
import com.example.demo.mina.protocol.TestProtocolCodecFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

public class MinaClient {
    private  static  String host="127.0.0.1";
    private  static int port=7074;
    public static void main(String[] args) {
        IoSession session=null;
        IoConnector connector=new NioSocketConnector();
        connector.setConnectTimeout(3000);
        //设置过滤器
        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TestProtocolCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("first",new ProtocolCodecFilter(new Test2ProtocolCodecFactory(Charset.forName("UTF-8"))));
        connector.setHandler(new MyClientHandler());
        ConnectFuture future=connector.connect(new InetSocketAddress(host,port));
        future.awaitUninterruptibly();
        session=future.getSession();
        byte[] bytes=new byte[3];
        bytes[0]=0;
        bytes[1]=0;

        Message pack=new Message((short)1,(short)1,bytes);
        pack.setTarget("123456");
        session.write(pack);
        System.out.println("发送完毕");
        session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();

    }
}
