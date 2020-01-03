package com.example.demo.mina.protocol;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import java.nio.charset.Charset;

/*
    自定义编解码工厂类
*/
public class TestProtocolCodecFactory implements ProtocolCodecFactory {
    private final TestProtocolEncoder encoder;
    private final TestProtocolDecoder decoder;
    public TestProtocolCodecFactory() {
        this(Charset.forName("UTF-8"));
    }

    public TestProtocolCodecFactory(Charset charset) {
        this.encoder = new TestProtocolEncoder(charset);
        this.decoder = new TestProtocolDecoder(charset);
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
