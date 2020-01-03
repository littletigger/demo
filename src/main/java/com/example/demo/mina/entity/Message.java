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
    /**
        消息总长度
     */

    private int length;

    /**
     * 是否分包
     */
    private boolean isPkg;
    /**
     * 包序号(当isPkg为true)
     */
    private int pkgNum;
    /**
     * 包数量(当isPkg为true)
     */
    private int totalPkg;


    public Message(short msgId,short msgNum, byte[] body) {
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

    public int getPkgNum() {
        return pkgNum;
    }

    public void setPkgNum(int pkgNum) {
        this.pkgNum = pkgNum;
    }

    public int getTotalPkg() {
        return totalPkg;
    }

    public void setTotalPkg(int totalPkg) {
        this.totalPkg = totalPkg;
    }
    public short getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(short msgNum) {
        this.msgNum = msgNum;
    }
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    public short getMsgId() {
        return msgId;
    }

    public void setMsgId(short msgId) {
        this.msgId = msgId;
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
                ", body=" + Arrays.toString(body) +
                ", length=" + length +
                '}';
    }
}
