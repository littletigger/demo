package com.example.demo.mina.protocol;

import com.example.demo.mina.entity.Message;
import com.example.demo.mina.entity.MinaConstant;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class JT808WrapperEncoder  extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput out) throws Exception {
        System.out.println(o.toString());
         Message msg=(Message)o;
        //target校验长度不足用0代替
        String target = msg.getTarget();
        if (target == null || target.length() == 0) throw new Exception("手机号[target]不能为空!");
        StringBuffer targetBuff = new StringBuffer();
        if (target.length() < MinaConstant.TARGET_LENGTH) {
            short nullStr = (short) (MinaConstant.TARGET_LENGTH - target.length());
            for (int i = 0; i < nullStr; i++) {
                targetBuff.append("0");
            }
        }
        targetBuff.append(target);
        System.out.println("补齐的手机号为："+targetBuff.toString());
        byte[] targetBytes = targetBuff.toString().getBytes();
        //分包处理


        int totalPkg=2;
        if(msg.getBodyLen()>8){
             totalPkg=msg.getBodyLen()/8+1;
         }


         for(int i=0;i<totalPkg;i++){
             msg.setLength(i);
             byte[] bytes=new byte[3];
             bytes[0]=0x7e;
             bytes[1]=0;
             bytes[2]=(byte) i;
             msg.setTarget(targetBuff.toString());
             msg.setBody(bytes);
             out.write(msg);
         }
    }
}
