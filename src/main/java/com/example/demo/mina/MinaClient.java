package com.example.demo.mina;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import com.example.demo.mina.entity.PackageData;
import com.example.demo.mina.protocol.Test2ProtocolCodecFactory;
import com.example.demo.mina.protocol.TestProtocolCodecFactory;
import com.example.demo.mina.protocol.TestProtocolEncoder;
import com.example.demo.mina.util.TxtToJson;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class MinaClient {
    private static String host = "127.0.0.1";
    private static int port = MinaConstant.HOST_PORT;
    /** 30秒后超时 */
    private static final int IDELTIMEOUT = 30000;
    /** 15秒发送一次心跳包 */
    private static final int HEARTBEATRATE = 5;


    public static void main(String[] args) throws InterruptedException {
        final Logger log = LoggerFactory.getLogger(MinaClient.class);

        IoSession session = null;
        IoConnector connector = new NioSocketConnector();
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,10);
        connector.getSessionConfig().setWriteTimeout(200);
        connector.setConnectTimeoutMillis(IDELTIMEOUT); //设置连接超时
//      断线重连回调拦截器
        connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
            @Override
            public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
                for (; ; ) {
                    try {
                        Thread.sleep(3000);
                        ConnectFuture future = connector.connect();
                        future.awaitUninterruptibly();// 等待连接创建成功
                        session = future.getSession();// 获取会话
                        if (session.isConnected()) {
                            log.info("断线重连[" + connector.getDefaultRemoteAddress() + "成功");
                            //session.close();
                            break;
                        }
                    } catch (Exception ex) {
                        log.info("重连服务器登录失败,3秒再连接一次:" + ex.getMessage());
                    }
                }
            }
        });


        //设置过滤器
        KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();

        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
                IdleStatus.BOTH_IDLE);
        //设置是否forward到下一个filter
        heartBeat.setForwardEvent(true);
        //设置心跳频率
        heartBeat.setRequestInterval(HEARTBEATRATE);
        heartBeat.setRequestTimeoutHandler(new KeepAliveRequestTimeoutHandlerImpl());

        connector.getFilterChain().addLast("first", new ProtocolCodecFilter(new Test2ProtocolCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TestProtocolCodecFactory(Charset.forName("UTF-8"))));
        // connector.getFilterChain().addLast("heartbeat", heartBeat);

        connector.setHandler(new MyClientHandler());

        connector.getSessionConfig().setReadBufferSize(1024);   // 设置接收缓冲区的大小
        connector.setDefaultRemoteAddress(new InetSocketAddress(host, port));
        for (; ; ) {
            try {
                ConnectFuture future = connector.connect();
                // 等待连接创建成功
                future.awaitUninterruptibly();
                // 获取会话
                session = future.getSession();
                log.error("连接服务端" + host + ":" + port + "[成功]" + ",,时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                break;
            } catch (RuntimeIoException e) {
                log.error("连接服务端" + host + ":" + port + "失败" + ",时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ", 连接MSG异常,请检查MSG端口、IP是否正确,MSG服务是否启动,异常内容:" + e.getMessage(), e);
                Thread.sleep(5000);// 连接失败后,重连间隔5s
            }




        /*PackageData pack = new PackageData();
        pack.setTarget("123456");
        pack.setMsgId((short) 100);
        pack.setEncryp((short) 1);
        byte[] body = new byte[3];
        body[0] = 0x7e;
        body[1] = 0x7d;
        body[2] = 0x7e;
        pack.setBody(body);
        session.write(pack);
        PackageData pack2 = new PackageData();
        pack2.setTarget("123456");
        pack2.setMsgId((short) 200);
        pack2.setEncryp((short) 1);
        byte[] body2 = new byte[3];
        body2[0] = 0x7e;
        body2[1] = 0x7e;
        body2[2] = 0x7e;
        pack2.setBody(body2);
        session.write(pack2);
        System.out.println("发送完毕");*/
           /* JSONObject json = TxtToJson.readTxtToJson("/home/fjkj/test.txt");
            int msgId = (int) json.get("msgId");
            int encryp = (int) json.get("encryp");
            String target = (String) json.get("target");
            String body = (String) json.get("body");
            PackageData data = new PackageData();
            data.setMsgId((short) msgId);
            data.setEncryp((short) encryp);
            data.setBody(body.getBytes());
            data.setTarget(target);
            session.write(data);
            // session.write(data);
            session.getCloseFuture().awaitUninterruptibly();
            //connector.dispose();*/

        }
       // session.close();

    }

    /**
     * @author cruise
     * @ClassName KeepAliveMessageFactoryImpl
     * @Description 内部类，实现KeepAliveMessageFactory（心跳工厂）
     */

    private static class KeepAliveMessageFactoryImpl implements
            KeepAliveMessageFactory {
        private final Logger log = LoggerFactory.getLogger(TestProtocolEncoder.class);
        /** 心跳包内容 */


        @Override
        public boolean isRequest(IoSession session, Object message) {
            //log.info("客户端请求心跳包信息: " + message);
            //if (message.equals(HEARTBEATREQUEST))
                //return true;
            return false;
        }

        @Override
        public boolean isResponse(IoSession session, Object message) {
            PackageData data=(PackageData) message;
			log.info("客户端收到响应心跳包信息: " + message);
			if(data.getMsgId()==1)
				return true;
            return false;
        }

        @Override
        public Object getRequest(IoSession session) {
            PackageData pack = new PackageData();
            pack.setTarget("123456");
            pack.setMsgId((short) 0);
            pack.setEncryp((short) 1);
            byte[] body = new byte[3];
            body[0] = 0x7e;
            body[1] = 0x7d;
            body[2] = 0x7e;
            pack.setBody(body);

            log.info("客户端发送请求预设信息: " +pack);
            /** 返回预设语句 */
            return pack;
        }

        @Override
        public Object getResponse(IoSession ioSession, Object o) {
            return null;
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
