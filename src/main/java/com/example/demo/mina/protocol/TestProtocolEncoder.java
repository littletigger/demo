package com.example.demo.mina.protocol;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;


public class TestProtocolEncoder extends ProtocolEncoderAdapter {
    private  final Charset charset;

    public TestProtocolEncoder() {
        this.charset =Charset.defaultCharset();
    }

    public TestProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    //编码 将数据包转成字节数组
    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput output) throws Exception {

        Message value=(Message)message;
        byte[] bytes=ioBufferToBytes(wrapperIobuffer(value.getMsgId(),value.getMsgNum(),value.getBody(),value.getTarget()));

        System.out.println("转义前数据为：");
        for (int i=0;i<bytes.length;i++)
        System.out.print( Integer.toHexString(bytes[i]& 0xff)+" ");
        byte[] bytes1=ioBufferToBytes(encodeNewByte(bytes));
        System.out.println("转义后数据为：");
        for (int i=0;i<bytes1.length;i++)
            System.out.print(Integer.toHexString(bytes1[i]& 0xff)+" ");

        //将报文发送出去
        output.write(encodeNewByte(bytes));

    }

    private IoBuffer wrapperIobuffer(short msgId,short msgNum, byte[] body,String target){
        byte[] targets=target.getBytes();
        IoBuffer ioBuffer=IoBuffer.allocate(10);
        ioBuffer.setAutoExpand(true);
        ioBuffer.putShort(msgId);
        ioBuffer.putShort(msgNum);
        ioBuffer.put(targets);
        ioBuffer.put(body);
        ioBuffer.flip();
        return ioBuffer;

    }
    private byte[] ioBufferToBytes(IoBuffer ioBuffer) {
        byte[] bytes = new byte[ioBuffer.remaining()];
        ioBuffer.get(bytes);
        return bytes;
    }

    //数据转义
    private IoBuffer encodeNewByte(byte[] bytes) {
        IoBuffer ioBuffer=IoBuffer.allocate(10).setAutoExpand(true);
        //校验码
        byte checkByte = 0;
        //添加头标识
        ioBuffer.put(MinaConstant.IDENTIFIER);
        //转义、计算校验码
        for(byte b:bytes){
            if(b == MinaConstant.BYTE_7E){
                ioBuffer.putShort(MinaConstant.BYTE_RET_7E);
                checkByte^=MinaConstant.BYTE_7E;

            }
            else if(b==MinaConstant.BYTE_7D){
                ioBuffer.putShort(MinaConstant.BYTE_RET_7D);
                checkByte^=MinaConstant.BYTE_7D;
            }
            else{
                ioBuffer.put(b);
                checkByte^=b;
            }
        }
        //校验码转义
        if(checkByte==MinaConstant.BYTE_7D){
            ioBuffer.putShort(MinaConstant.BYTE_RET_7D);
        }
        else if(checkByte==MinaConstant.BYTE_7E){
            ioBuffer.putShort(MinaConstant.BYTE_RET_7E);
        }
        else{
            ioBuffer.put(checkByte);
        }
        //添加结尾标识
        ioBuffer.put(MinaConstant.IDENTIFIER);
        ioBuffer.flip();
        return ioBuffer;

    }
}


