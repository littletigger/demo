package com.example.demo.mina.protocol;

import com.example.demo.mina.entity.Message;

import com.example.demo.mina.entity.PackageData;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
//合包

public class TestProtocolDecoder extends ProtocolDecoderAdapter {
    private Map<Short, PackageBuf> mMap;
    private final Charset charset;

    public TestProtocolDecoder() {
        this.charset = Charset.defaultCharset();
    }

    // 构造方法注入编码格式
    public TestProtocolDecoder(Charset charset) {
        this.charset = charset;
    }



    //转字节数组
    private byte[] ioBufferToBytes(IoBuffer ioBuffer) {
        byte[] bytes = new byte[ioBuffer.remaining()];
        ioBuffer.get(bytes);
        return bytes;
    }

    @Override
    public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput out) throws Exception {
        System.out.println("进入解码器2：");
        short pkgNum = 0;
        short totalPkg = 0;
        byte[] targeByte = new byte[12];
        short msgId = ioBuffer.getShort();
        short bodyAttr=ioBuffer.getShort();
        ioBuffer.get(targeByte);
        short msgNum = ioBuffer.getShort();
        /**
         * 此处我们通过与运算对消息体属性进行解析：
         * 具体消息体属性的解析方式需参看数据的封装方式见
         */
        short bodyLen = (short) (bodyAttr & 0x03FF);
        short encryp = (short) ((bodyAttr & 0x1C00) >> 10);
        boolean isPkg = ((bodyAttr & 0x2000) >> 13) != 0;

        if (isPkg) {
            totalPkg = ioBuffer.getShort();
            System.out.println("totalPkg" + totalPkg);

            pkgNum = ioBuffer.getShort();
            System.out.println("pkgNum" + pkgNum);
        }
        byte[] body = new byte[ioBuffer.remaining()];
        ioBuffer.get(body);
        String target = new String(targeByte).trim().replaceFirst("^0*","");
        //System.out.println("手机号为----" + target);
        //ioBuffer.flip();
        if (!isPkg) {
            //System.out.println("没有分包");
            PackageData  data=new PackageData();
            data.setBody(body);
            data.setEncryp(encryp);
            data.setMsgId(msgId);
            data.setMsgNum(msgNum);
            data.setTarget(target);
            out.write(data);
            return;

        }else{
            //System.out.println("合包");
            if (mMap == null) {
                mMap = new HashMap<Short,PackageBuf>(totalPkg);
            }
            Message msg = new Message();
            msg.setMsgId(msgId);
            msg.setMsgNum(msgNum);
            msg.setTarget(target);
            msg.setBodyLen(bodyLen);
            msg.setBody(body);
            msg.setTotalPkg(totalPkg);
            msg.setPkgNum(pkgNum);
            System.out.println("..........");
            if (!mMap.containsKey(msgId)) {
               // System.out.println("进入map");
                //System.out.println("进入map"+);
                if (msg.getPkgNum()==1) {
                    //System.out.println("创建buf");

                    PackageBuf packageBuf = new PackageBuf(msg.getTotalPkg());
                    packageBuf.addMessage(msg);
                    mMap.put(msgId, packageBuf);
                }
            } else {
                //System.out.println("添加消息");
                PackageBuf packageBuf = mMap.get(msgId);
                int ret = packageBuf.addMessage(msg);
                if (ret == 0) {
                   // System.out.println("解析对象");
                    mMap.remove(msgId);
                    byte[] bodys = packageBuf.getBufArray();
                    PackageData  data=new PackageData();
                    data.setBody(bodys);
                    data.setEncryp(encryp);
                    data.setMsgId(msgId);
                    data.setMsgNum(msgNum);
                    data.setTarget(target);
                    out.write(data);

                }
            }

        }



    }

    private class PackageBuf {
        private int maxSize;

        private IoBuffer byteBufs;

        public PackageBuf(int maxSize) {
            this.maxSize = maxSize;
            byteBufs = IoBuffer.allocate(10).setAutoExpand(true);
        }


        public int addMessage(Message message) {


            //顺序接收
            byteBufs.put(message.getBody());

           /* System.out.println("body---");
            for (int i = 0; i < message.getBody().length; i++)
                System.out.print(Integer.toHexString(message.getBody()[i] & 0xff) + " ");

*/
            //接收完成
            if (message.getPkgNum() == maxSize) {
                return 0;
            }
            return -1;
        }
        public byte[] getBufArray() {
            byteBufs.flip();
            byte[] bytes = new byte[byteBufs.remaining()];
            byteBufs.get(bytes);
            return bytes;
        }
    }
}
