package io.github.lingnanlu;

import io.craft.atom.util.ByteUtil;
import io.github.lingnanlu.model.RpcBody;
import io.github.lingnanlu.model.RpcHeader;
import io.github.lingnanlu.model.RpcMessage;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcEncoder implements ProtocolEncoder<RpcMessage> {

    public byte[] encode(RpcMessage message) {

        RpcHeader header = message.getHeader();
        RpcBody body = message.getBody();
        Serialization<RpcBody> serializer = KyroSerialization.getInstance();

        byte[] bodyBytes = serializer.serialize(body);

        byte[] encoded = new byte[header.getHeaderSize() + bodyBytes.length];
        header.setBodySize(bodyBytes.length);

        encodeHeader(encoded, header);

        System.arraycopy(bodyBytes, 0, encoded, header.getHeaderSize(), bodyBytes.length);

        return encoded;

    }


    private void encodeHeader(byte[] b, RpcHeader rh) {
        // magic
        ByteUtil.short2bytes(rh.getMagic(), b, 0);
        // header siez
        ByteUtil.short2bytes(rh.getHeaderSize(), b, 2);
        // version
        b[4] = rh.getVersion();
        // st | hb | tw | rr
        b[5] = (byte) (rh.getSt() | rh.getHb() | rh.getOw() | rh.getRp());
        // status code
        b[6] = rh.getStatusCode();
        // message id
        ByteUtil.long2bytes(rh.getId(), b, 8);
        // body size
        ByteUtil.int2bytes(rh.getBodySize(), b, 16);
    }

}
