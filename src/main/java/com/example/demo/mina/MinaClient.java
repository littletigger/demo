package com.example.demo.mina;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import com.example.demo.mina.entity.PackageData;
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
    private  static int port= MinaConstant.HOST_PORT;
    public static void main(String[] args) {
        IoSession session=null;
        IoConnector connector=new NioSocketConnector();
        connector.setConnectTimeout(3000);
        //设置过滤器

        connector.getFilterChain().addLast("first",new ProtocolCodecFilter(new Test2ProtocolCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TestProtocolCodecFactory(Charset.forName("UTF-8"))));
        connector.setHandler(new MyClientHandler());
        ConnectFuture future=connector.connect(new InetSocketAddress(host,port));
        future.awaitUninterruptibly();
        session=future.getSession();

        PackageData pack=new PackageData();
        pack.setTarget("123456");
        pack.setMsgId((short)100);
        pack.setEncryp((short)1);
        byte[] body=new byte[3];
        body[0]=0x7e;
        body[1]=0x7d;
        body[2]=0x7e;
        pack.setBody(body);
        session.write(pack);
        PackageData pack2=new PackageData();
        pack2.setTarget("123456");
        pack2.setMsgId((short)200);
        pack2.setEncryp((short)1);
        byte[] body2=new byte[3];
        body2[0]=0x7e;
        body2[1]=0x7e;
        body2[2]=0x7e;
        pack2.setBody(body2);
        session.write(pack2);
        System.out.println("发送完毕");
        session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();

    }
}
