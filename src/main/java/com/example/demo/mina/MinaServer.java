package com.example.demo.mina;

import com.example.demo.mina.protocol.JT808WrapperEncoder;
import com.example.demo.mina.protocol.Test2ProtocolCodecFactory;
import com.example.demo.mina.protocol.TestProtocolCodecFactory;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class MinaServer {
    static int PORT=7074;
    static IoAcceptor acceptor=null;

    public static void main(String[] args) throws IOException {
        acceptor=new NioSocketAcceptor();
        //设置 编码解码过滤器
        acceptor.getFilterChain().addLast("first",new ProtocolCodecFilter(new Test2ProtocolCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TestProtocolCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getSessionConfig().setReadBufferSize(1024);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,1000);
        acceptor.setHandler(new Myhandler());
        //绑定端口
        acceptor.bind(new InetSocketAddress(PORT));
        System.out.println("sever-->"+PORT);

    }
}
