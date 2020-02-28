package com.example.demo.mina;

import com.example.demo.mina.entity.MinaConstant;
import com.example.demo.mina.entity.PackageData;
import com.example.demo.mina.protocol.JT808WrapperEncoder;
import com.example.demo.mina.protocol.Test2ProtocolCodecFactory;
import com.example.demo.mina.protocol.TestProtocolCodecFactory;
import com.example.demo.mina.protocol.TestProtocolEncoder;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.transport.tcp.TCPConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

public class MinaServer {
    static int PORT= MinaConstant.HOST_PORT;
    static IoAcceptor acceptor=null;
    /** 30秒后超时 */
    private static final int IDELTIMEOUT = 30;
    /** 15秒发送一次心跳包 */
    private static final int HEARTBEATRATE = 5;
    /** 心跳包内容 */


    public static void main(String[] args) throws IOException {
        acceptor=new NioSocketAcceptor();
        //设置 编码解码过滤器
        acceptor.getFilterChain().addLast("first",new ProtocolCodecFilter(new Test2ProtocolCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TestProtocolCodecFactory(Charset.forName("UTF-8"))));
        // 为IoFilterChain添加线程池
       /* acceptor.getFilterChain().addLast("threadPool",
                new ExecutorFilter(Executors.newCachedThreadPool()));*/
        KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();

        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
                IdleStatus.BOTH_IDLE);

        //设置是否forward到下一个filter
        heartBeat.setForwardEvent(true);
        //设置心跳频率
        heartBeat.setRequestInterval(HEARTBEATRATE);
        heartBeat.setRequestTimeoutHandler(new KeepAliveRequestTimeoutHandlerImpl());

        //acceptor.getFilterChain().addLast("heartbeat", heartBeat);

        acceptor.getSessionConfig().setReadBufferSize(1024);

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,10);
        acceptor.setHandler(new Myhandler());
        //绑定端口
        acceptor.bind(new InetSocketAddress(PORT));

        System.out.println("sever-->"+PORT);

    }
    private static class KeepAliveMessageFactoryImpl implements
            KeepAliveMessageFactory {
        private final Logger log = LoggerFactory.getLogger(TestProtocolEncoder.class);


        @Override
        public boolean isRequest(IoSession session, Object message) {
            log.info("服务端收到请求心跳包信息: " + message);
            PackageData data=(PackageData) message;
            if (data.getMsgId()==0)
                return true;
            return false;
        }

        @Override
        public boolean isResponse(IoSession session, Object message) {
            //log.info("响应心跳包信息: " + message);
            //if(message.equals(HEARTBEATRESPONSE))
                //return true;
            return false;
        }

        @Override
        public Object getRequest(IoSession session) {
           // log.info("请求预设信息: " + HEARTBEATREQUEST);
           // /** 返回预设语句 */
            return null;
        }

        @Override
        public Object getResponse(IoSession ioSession, Object o) {
            PackageData pack = new PackageData();
            pack.setTarget("123456");
            pack.setMsgId((short) 1);
            pack.setEncryp((short) 1);
            byte[] body = new byte[3];
            body[0] = 0x7e;
            body[1] = 0x7d;
            body[2] = 0x7e;
            pack.setBody(body);
            log.info("服务端发送心跳回复包: " +pack);
            return pack;
        }
    }
    private static class  KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {
        @Override
        public void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception {
            ioSession.close(true);
            System.out.print("客户端挂了  我把session给干掉了！");
        }
    }
}
