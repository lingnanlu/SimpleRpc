package io.github.lingnanlu;

import io.craft.atom.io.Channel;
import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.github.lingnanlu.spi.RpcChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by rico on 2017/1/11.
 */
@ToString(of = "channel")
public class DefaultRpcChannel implements RpcChannel{

    @Getter @Setter private ProtocolEncoder<RpcMessage> encoder;
    @Getter @Setter private ProtocolDecoder<RpcMessage> decoder;
    @Getter @Setter private Channel<byte[]> channel;


    public DefaultRpcChannel(ProtocolEncoder<RpcMessage> encoder, ProtocolDecoder<RpcMessage> decoder, Channel<byte[]> channel) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.channel = channel;
    }

    public void write(RpcMessage msg) {

        byte[] bytes = encoder.encode(msg);
        channel.write(bytes);
    }

    public List<RpcMessage> read(byte[] bytes) {

        List<RpcMessage> msgs = decoder.decode(bytes);
        return msgs;
    }
}
