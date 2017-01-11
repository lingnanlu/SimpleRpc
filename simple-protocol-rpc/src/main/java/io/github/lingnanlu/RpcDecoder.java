package io.github.lingnanlu;


import io.craft.atom.util.ByteUtil;
import io.github.lingnanlu.model.RpcBody;
import io.github.lingnanlu.model.RpcHeader;
import io.github.lingnanlu.model.RpcMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rico on 2017/1/11.
 *
 * 这是一个状态机， 状态表示正在处理某一个字段， 当该字段处理完后， 会自动转入下一个状态
 */
public class RpcDecoder extends AbstractProtocolDecoder implements ProtocolDecoder<RpcMessage> {


    private RpcMessage rm;

    private static final int MAGIC = 11;
    private static final int HEADER_SIZE = 12;
    private static final int VERSION = 13;
    private static final int BIT_FLAG = 14;
    private static final int STATUS_CODE = 15;
    private static final int RESERVED = 16;
    private static final int MESSAGE_ID = 17;
    private static final int BODY_SIZE = 18;
    private static final int BODY = 19;


    public List<RpcMessage> decode(byte[] bytes){
        List<RpcMessage> msgs = new ArrayList<RpcMessage>();
        removeHandledBytesInBuf();
        addNewBytesToBuf(bytes);

        while (searchIndex < buf.length() || state == END) {
            switch (state) {
                case START : _start(); break;
                case MAGIC : _magic(); break;
                case HEADER_SIZE : _headerSize(); break;
                case VERSION : _version(); break;
                case BIT_FLAG : _bitFLag(); break;
                case STATUS_CODE : _statusCode(); break;
                case RESERVED : _reserved(); break;
                case MESSAGE_ID : _messageID(); break;
                case BODY_SIZE : _bodySize(); break;
                case BODY : _body(); break;
                case END : _end(msgs); break;
            }
        }

        return msgs;
    }

    private void _end(List<RpcMessage> msgs) {
        msgs.add(rm);
        rm = null;
        state = START;
        splitIndex = searchIndex;
    }

    private void _body() {
        int hs = rm.getHeader().getHeaderSize();
        int bs = rm.getHeader().getBodySize();
        if (buf.length() <  hs + bs + splitIndex) { searchIndex = buf.length(); return; }

        Serialization<RpcBody> deserializer = KyroSerialization.getInstance();
        RpcBody rb = deserializer.deserialize(buf.buffer(), 20 + splitIndex);
        rm.setBody(rb);
        searchIndex = hs + bs + splitIndex;
        state = END;
    }

    private void _bodySize() {
        if (buf.length() < 20 + splitIndex) { searchIndex = buf.length(); return; }

        int bs = ByteUtil.bytes2int(buf.buffer(), 16 + splitIndex);
        rm.getHeader().setBodySize(bs);
        state = BODY;
        searchIndex = 20 + splitIndex;
    }

    private void _messageID() {
        if (buf.length() < 16 + splitIndex) { searchIndex = buf.length(); return; }

        long id = ByteUtil.bytes2long(buf.buffer(), 8 + splitIndex);
        rm.getHeader().setId(id);
        state = BODY_SIZE;
        searchIndex = 16 + splitIndex;
    }

    private void _reserved() {
        if (buf.length() < 8 + splitIndex) { searchIndex = buf.length(); return; }

        rm.getHeader().setReserved(buf.byteAt(7 + splitIndex));
        state = MESSAGE_ID;
        searchIndex = 8 + splitIndex;
    }

    private void _statusCode() {
        if (buf.length() < 7 + splitIndex) { searchIndex = buf.length(); return; }

        rm.getHeader().setStatusCode(buf.byteAt(6 + splitIndex));
        state = RESERVED;
        searchIndex = 7 + splitIndex;
    }

    private void _bitFLag() {
        if (buf.length() < 6 + splitIndex) { searchIndex = buf.length(); return; }

        rm.getHeader().setSt(buf.byteAt(5 + splitIndex));
        rm.getHeader().setHb(buf.byteAt(5 + splitIndex));
        rm.getHeader().setOw(buf.byteAt(5 + splitIndex));
        rm.getHeader().setRp(buf.byteAt(5 + splitIndex));
        state = STATUS_CODE;
        searchIndex = 6 + splitIndex;
    }

    private void _version() {
        if (buf.length() < 5 + splitIndex) { searchIndex = buf.length(); return; }

        rm.getHeader().setVersion(buf.byteAt(4 + splitIndex));
        state = BIT_FLAG;
        searchIndex = 5 + splitIndex;
    }

    private void _headerSize() {
        if(buf.length() < splitIndex + 4) {
            searchIndex = buf.length();
            return;
        }

        short hs = ByteUtil.bytes2short(buf.buffer(), 2 + splitIndex);
        rm.getHeader().setHeaderSize(hs);
        state = VERSION;
        searchIndex = splitIndex + 4;
    }

    private void _magic() {
        if(buf.length() < splitIndex + 2) {
            searchIndex = buf.length();
            return;
        }
        RpcHeader header = new RpcHeader();
        rm = new RpcMessage();
        rm.setHeader(header);
        state = HEADER_SIZE;

        searchIndex = 2 + splitIndex;

    }

    private void _start() {
        if(buf.length() > 0 + splitIndex) {
            state = MAGIC;
        }
    }

    private void addNewBytesToBuf(byte[] bytes) {
        buf.append(bytes);
    }


}
