package com.example.demo.mina.protocol;
import com.example.demo.mina.entity.MinaConstant;


import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.charset.Charset;


/*
处理半包，粘包问题
 */
@Slf4j
public class JT808ProtocolDecoder  extends CumulativeProtocolDecoder {
    private final Charset charset;
    private final Logger log= LoggerFactory.getLogger("Decoder");
    public JT808ProtocolDecoder() {
        this.charset = Charset.defaultCharset();
    }
    // 构造方法注入编码格式
    public JT808ProtocolDecoder(Charset charset) {
        this.charset = charset;
    }
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput out) throws Exception {

        System.out.println("进入解码器1");
        ioBuffer.mark();
        int count=0;

        IoBuffer ioBuffer1 = IoBuffer.allocate(10).setAutoExpand(true);
        while(ioBuffer.remaining()>0){
            byte b=ioBuffer.get();
            ioBuffer1.put(b);
            if(b== MinaConstant.BYTE_7E){
                count++;
            }
            if(count%2==0){

                ioBuffer1.flip();
                byte[] bytes1=ioBufferToBytes(ioBuffer1);
             //   System.out.println("接收到的数据为：");
               // for (int i = 0; i < bytes1.length; i++)
                   // System.out.print(Integer.toHexString(bytes1[i] & 0xff) + " ");
                byte[] bytes=recoverData(bytes1);
                IoBuffer ioBuffer2=bytesToIobuffer(bytes);
               // System.out.println("还原后的数据为：");
                //for (int i = 0; i < bytes.length; i++)
                   // System.out.print(Integer.toHexString(bytes[i] & 0xff) + " ");
              //  System.out.println("remain"+ioBuffer2.remaining());
                ioBuffer1.flip();
                out.write(ioBuffer2);
                log.info("发送到解码器二");
                return true;
            }

        }
        //if(count%2==0) return  true;

        ioBuffer.reset();
        log.info("断包等待下一包数据");
        return false;




    }
    private byte[] recoverData(byte[] bytes) {

        IoBuffer ioBuffer2 = IoBuffer.allocate(10).setAutoExpand(true);
        int length = bytes.length;
        if (length <= 0) {
            System.out.println("bytesnull");
            return null;
        }
        byte[] bs =bytes;

        byte checkByte = 0;
        byte oldCheckByte = bs[bs.length - 2];

        for (int i = 1; i < bs.length-2; i++) {
            byte b = bs[i];
            if (b == MinaConstant.BYTE_7D) {//遍历到转义的数据
                i++;
                b = bs[i];
                if (b == MinaConstant.BYTE_01) {//数据为0x7d01--->原数据为7d
                    if (i == (bs.length - 2)) {//校验位是特殊字符
                        oldCheckByte = MinaConstant.BYTE_7D;
                    }
                    else {
                        ioBuffer2.put(MinaConstant.BYTE_7D);
                        checkByte ^= MinaConstant.BYTE_7D;
                    }
                }
                else if (b == MinaConstant.BYTE_02) {//数据为0x7d02--->原数据为7e
                    if (i == (bs.length - 2)) {//校验位是特殊字符
                        oldCheckByte = MinaConstant.BYTE_7E;
                    }
                    else {
                        ioBuffer2.put(MinaConstant.BYTE_7E);
                        checkByte ^= MinaConstant.BYTE_7E;
                    }
                }
                else {
                    //do nothing 理论上程序不可能来到这里，如果程序走到这里表示编码的代码逻辑本身就有问题
                    return null;
                }
                continue;

            }
            checkByte ^= b;

            ioBuffer2.put(b);

        }

        if (checkByte != oldCheckByte) {
            System.out.println("数据校验码验证不通过！！！！");
            return null;
        }
        else {
            System.out.println("数据验证通过");
            ioBuffer2.flip();

        }
        return ioBufferToBytes(ioBuffer2);
    }
    //转字节数组
    private byte[] ioBufferToBytes(IoBuffer ioBuffer3) {
        byte[] bytes = new byte[ioBuffer3.remaining()];
        ioBuffer3.get(bytes);
        return bytes;
    }
    //byte[]转buffer
    private IoBuffer bytesToIobuffer(byte[] bytes) {
        IoBuffer ioBuffer=IoBuffer.allocate(10).setAutoExpand(true);
        for(byte b:bytes){
            ioBuffer.put(b);
        }
        ioBuffer.flip();

        return ioBuffer;
    }

}
