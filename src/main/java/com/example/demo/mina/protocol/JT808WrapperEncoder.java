package com.example.demo.mina.protocol;
import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import com.example.demo.mina.entity.PackageData;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


public class JT808WrapperEncoder  extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput out) throws Exception {
       // System.out.println(o.toString());
        PackageData msg=(PackageData)message;
        byte[] body = msg.getBody();
        int bodyLen = body != null ? body.length : 0;

        short msgId = msg.getMsgId();
        short msgNum=0;
        short encryp = msg.getEncryp();

        IoBuffer ioBuffer=IoBuffer.allocate(10).setAutoExpand(true);
        ioBuffer.put(body);
        ioBuffer.flip();

        //target校验长度不足用0代替
        //String target = msg.getTarget();
        String rsTarget=wrapperTarget( msg.getTarget());


        //没有分包
        if (bodyLen < MinaConstant.MAX_BODY_LENGTH) {
            System.out.println("没有分包");
            Message message1=wrapperMessage(msgId,bodyLen,encryp,rsTarget,msgNum, (short) 1,(short)1,ioBuffer,false);
            out.write(message1);
            return;
        }

        /**
         * 走到此处标识需要进行分包操作，这里需要注意的一点是我们需要对剩余数据进行独立处理
         * 当我们的总包数大于255时，JT808协议中用于描述分包总数的字节不足以描述大于255个数据包
         */
        short totalPkg =(short)(bodyLen / MinaConstant.MAX_BODY_LENGTH+1);
        int lastBodyLen = bodyLen % MinaConstant.MAX_BODY_LENGTH;


        if (totalPkg > 255) throw new Exception("分包总数大于255");
        //分包处理
        System.out.println("开始分包,分成包数："+totalPkg);
        for (int i = 1; i < totalPkg; i++) {

            Message message1=wrapperMessage(msgId,MinaConstant.MAX_BODY_LENGTH,encryp,rsTarget,msgNum,totalPkg,(short)i,ioBuffer,true);

            out.write(message1);
        }


        //做最后一个包的处理

        Message message1=wrapperMessage(msgId,lastBodyLen,encryp,rsTarget,msgNum,totalPkg,totalPkg,ioBuffer,true);
        out.write(message1);

        return;




    }

    /**
     * 进行数据封包操作
     *
     * @param msgid    消息包id
     * @param bodyLen  当前消息包的消息体长度
     * @param target   设备标识
     * @param totalPkg 总包数
     * @param pkgNum   包序号
     * @param ioBuffer      待操作数据
     * @return :
     */
    private Message wrapperMessage(short msgid, int bodyLen, short encryp,String target, short msgNum, short totalPkg, short pkgNum, IoBuffer ioBuffer,boolean ispkg) {
         Message message=new Message();
         message.setMsgId(msgid);

        /**
         * 这里我们对消息体属性进行封装，也是整个808协议比较难封装的一部分
         */
        short bodyAttr = (short) (bodyLen & 0x03FF);
        bodyAttr |= (encryp << 10);
        /*这里是封装分包位的*/
        if (totalPkg > 1) bodyAttr |= 0x2000;
        //bodyAttr |= (source << 15);
        bodyAttr &= 0x7FFF;
        message.setBodyAttr(bodyAttr);
        byte[] body=new byte[bodyLen];
        ioBuffer.get(body);
        System.out.println("bodyData---");
        for (int i=0;i<body.length;i++)
            System.out.print(Integer.toHexString(body[i]& 0xff)+" ");
        message.setBody(body);
        message.setMsgNum(msgNum);
        message.setTarget(target);
        message.setTotalPkg(totalPkg);
        message.setPkgNum(pkgNum);
        message.setPkg(ispkg);
        return  message;

    }

    private String wrapperTarget(String target) throws Exception{
        if (target == null || target.length() == 0) throw new Exception("手机号[target]不能为空!");
        StringBuffer targetBuff = new StringBuffer();
        if (target.length() < MinaConstant.TARGET_LENGTH) {
            short nullStr = (short) (MinaConstant.TARGET_LENGTH - target.length());
            for (int i = 0; i < nullStr; i++) {
                targetBuff.append("0");
            }
        }
        targetBuff.append(target);
        String result=targetBuff.toString();
        // System.out.println("补齐的手机号为："+targetBuff.toString());
       // byte[] targetBytes = targetBuff.toString().getBytes();
        return  result;

    }

}
