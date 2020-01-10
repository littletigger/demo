package com.example.demo.mina.protocol;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import java.nio.charset.Charset;

/*
    自定义编解码工厂类
*/
public class Test2ProtocolCodecFactory implements ProtocolCodecFactory {
   // private final JT808WrapperEncoder encoder;
    private final JT808ProtocolDecoder decoder;
    private final TestProtocolEncoder encoder;
    public Test2ProtocolCodecFactory() {
        this(Charset.forName("UTF-8"));
    }

    public Test2ProtocolCodecFactory(Charset charset) {
        //this.encoder = new JT808WrapperEncoder();
        this.decoder = new JT808ProtocolDecoder(charset);
        this.encoder = new TestProtocolEncoder(charset);
    }


    @Override
    public org.apache.mina.filter.codec.ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public org.apache.mina.filter.codec.ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
