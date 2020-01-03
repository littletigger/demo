package com.example.demo.mina.protocol;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

public class TestProtocolDecoder extends CumulativeProtocolDecoder {
    private final Charset charset;

    public TestProtocolDecoder() {
        this.charset = Charset.defaultCharset();
    }

    // 构造方法注入编码格式
    public TestProtocolDecoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput out) throws Exception {

        System.out.println("进入解码器2：");

        byte[] bytes = ioBufferToBytes(ioBuffer);
        if (bytes != null) {
            System.out.println("接受到的数据为：");
            for (int i = 0; i < bytes.length; i++)
                System.out.print(Integer.toHexString(bytes[i] & 0xff) + " ");
            byte[] bytes2 = recoverData(bytes);
            System.out.println("还原后的数据为：");
            for (int i = 0; i < bytes2.length; i++)
                System.out.print(Integer.toHexString(bytes2[i] & 0xff) + " ");
            byte[] bytes1=new byte[2];
            bytes1[0]=bytes2[0];
            bytes1[1]=bytes2[1];
            short msgId=byte2short(bytes1);
            bytes1[0]=bytes2[bytes2.length-1];
            bytes1[1]=bytes2[bytes2.length-2];
            short msgNum=byte2short(bytes1);

            Message message=new Message(msgId,msgNum,bytes2);
            System.out.println("message"+message);

            ioBuffer.flip();
            out.write(bytes2);
            return true;
        }

        if (ioBuffer.remaining() < 6) {
            return false;
        }
        if (ioBuffer.remaining() > 1) {
            // 标记设为当前
            ioBuffer.mark();
            // 获取总长度
            int length = ioBuffer.getInt(ioBuffer.position());
            // 如果可读取数据的长度 小于 总长度 - 包头的长度 ，则结束拆包，等待下一次
            if (ioBuffer.remaining() < (length - 6)) {
                ioBuffer.reset();
                return false;
            } else {
                // 重置，并读取一条完整记录
                ioBuffer.reset();
                short msgId = ioBuffer.getShort();
                short msgNum = ioBuffer.getShort();
                byte[] bytes1 = new byte[3];
                // 获取长度4个字节、版本1个字节、内容
                ioBuffer.get(bytes);
                // 封装为自定义的java对象
                Message pack = new Message(msgId, msgNum, bytes);
                out.write(pack);
                // 如果读取一条记录后，还存在数据（粘包），则再次进行调用
                return ioBuffer.remaining() > 0;
            }

        }
        return false;

    }

    //byte[]转short
    public static short byte2short(byte[] b){
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l<<=8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }
    //转字节数组
    private byte[] ioBufferToBytes(IoBuffer ioBuffer) {
        byte[] bytes = new byte[ioBuffer.remaining()];
        ioBuffer.get(bytes);
        return bytes;
    }
    //转义还原、验证校验码

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



