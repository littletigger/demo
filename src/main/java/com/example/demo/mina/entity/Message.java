package com.example.demo.mina.entity;


import java.util.Arrays;

/**
 * @author ruanwenjiang
 * @date 18-12-14 上午11:17
 * 用于分包聚合信息中转
 */

public class Message {

    /**
     * 消息id
     */
    private short msgId;

    /**
     * 消息体属性
     */
    private short bodyAttr;
    /**
     * 消息流水号
     */
    private short msgNum;

    /**
     * 终端手机号
     */
    private String target;

    /**
     * 消息体
     */
    private byte[] body;

    /**
     * 消息体长度
     */
    private short bodyLen;
   /* *//**
        消息总长度
     *//*

    private int length;
*/



    /**
     * 是否分包
     */
    private boolean isPkg;
    /**
     * 包序号(当isPkg为true)
     */
    private short pkgNum;
    /**
     * 包数量(当isPkg为true)
     */
    private short totalPkg;

    public Message(){}


    public Message(short msgId, short msgNum, String target, short bodyLen, byte[] body) {
        this.msgId = msgId;
        this.body = body;
        this.msgNum=msgNum;
    }
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isPkg() {
        return isPkg;
    }

    public void setPkg(boolean pkg) {
        isPkg = pkg;
    }

    public short getPkgNum() {
        return pkgNum;
    }

    public void setPkgNum(short pkgNum) {
        this.pkgNum = pkgNum;
    }

    public short getTotalPkg() {
        return totalPkg;
    }

    public void setTotalPkg(short totalPkg) {
        this.totalPkg = totalPkg;
    }
    public short getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(short msgNum) {
        this.msgNum = msgNum;
    }
 /*   public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }*/
    public short getMsgId() {
        return msgId;
    }

    public void setMsgId(short msgId) {
        this.msgId = msgId;
    }
    public short getBodyAttr() {
        return bodyAttr;
    }

    public void setBodyAttr(short bodyAttr) {
        this.bodyAttr = bodyAttr;
    }


    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
    public short getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(short bodyLen) {
        this.bodyLen = bodyLen;
    }
    @Override
    public String toString() {
        return "Message{" +
                "msgId=" + msgId +
                ", msgNum=" + msgNum +
                ", target='" + target + '\'' +
                ", body=" + Arrays.toString(body) +
                ", bodyLen=" + bodyLen +
              /*  ", length=" + length +*/
                ", isPkg=" + isPkg +
                ", pkgNum=" + pkgNum +
                ", totalPkg=" + totalPkg +
                '}';
    }
}
