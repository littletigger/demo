package com.example.demo.mina.protocol;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

public class JT808ProtocolDecoder  extends CumulativeProtocolDecoder {
    private final Charset charset;

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
        //标记，便于重置
        ioBuffer.mark();
        //





        byte[] bytes=ioBufferToBytes(ioBuffer);
        IoBuffer ioBuffer1=IoBuffer.allocate(10).setAutoExpand(true);
        ioBuffer1.put(bytes);
        ioBuffer1.flip();
        out.write(ioBuffer1);
        return true;
    }

    // 构造方法注入编码格式

    private byte[] ioBufferToBytes(IoBuffer ioBuffer) {
        byte[] bytes = new byte[ioBuffer.remaining()];
        ioBuffer.get(bytes);
        return bytes;
    }
    private byte[] recoverData(byte[] bytes) {
        IoBuffer ioBuffer = IoBuffer.allocate(10).setAutoExpand(true);
        int length = bytes.length;
        if (length <= 0) {
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
                        ioBuffer.put(MinaConstant.BYTE_7D);
                        checkByte ^= MinaConstant.BYTE_7D;
                    }
                }
                else if (b == MinaConstant.BYTE_02) {//数据为0x7d02--->原数据为7e
                    if (i == (bs.length - 2)) {//校验位是特殊字符
                        oldCheckByte = MinaConstant.BYTE_7E;
                    }
                    else {
                        ioBuffer.put(MinaConstant.BYTE_7E);
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

            ioBuffer.put(b);

        }

        if (checkByte != oldCheckByte) {
            System.out.println("数据校验码验证不通过！！！！");
            return null;
        }
        else {
            System.out.println("数据验证通过");
            ioBuffer.flip();

        }
        return ioBufferToBytes(ioBuffer);
    }
}
