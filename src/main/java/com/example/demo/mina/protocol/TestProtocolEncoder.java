package com.example.demo.mina.protocol;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

@Slf4j
public class TestProtocolEncoder extends ProtocolEncoderAdapter {
    private  final Charset charset;
    private final Logger log= LoggerFactory.getLogger(TestProtocolEncoder.class);

    public TestProtocolEncoder() {
        this.charset =Charset.defaultCharset();
    }

    public TestProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    //编码 将数据包转成字节数组
    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput output) throws Exception {

        Message msg=(Message)message;
        byte[] bytes=ioBufferToBytes(wrapperIobuffer(msg.getMsgId(),msg.getBodyAttr(),msg.getTarget(),msg.getMsgNum(),msg.getBody(),msg.isPkg(),msg.getPkgNum(),msg.getTotalPkg()));

        //System.out.println("转义前数据为：");
       // for (int i=0;i<bytes.length;i++)
        //System.out.print( Integer.toHexString(bytes[i]& 0xff)+" ");
        //byte[] bytes1=ioBufferToBytes(encodeNewByte(bytes));
       // System.out.println("转义后数据为：");
       // for (int i=0;i<bytes1.length;i++)
            //System.out.print(Integer.toHexString(bytes1[i]& 0xff)+" ");

        //将报文发送出去



      output.write(encodeNewByte(bytes));
        //log.info("发送成功");

    }

    private IoBuffer wrapperIobuffer(short msgId,short bodyAtrr,String target, short msgNum,byte[] body,boolean ispkg,short pkgNum,short totalPkg){
        byte[] targets=target.getBytes();
        IoBuffer ioBuffer=IoBuffer.allocate(10).setAutoExpand(true);
        ioBuffer.putShort(msgId);
        ioBuffer.putShort(bodyAtrr);
        ioBuffer.put(targets);
        ioBuffer.putShort(msgNum);
        if(ispkg) {
            ioBuffer.putShort(totalPkg);
            ioBuffer.putShort(pkgNum);
        }

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


