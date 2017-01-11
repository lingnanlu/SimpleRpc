package io.github.lingnanlu;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import io.github.lingnanlu.model.RpcBody;
import io.github.lingnanlu.model.RpcMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Created by rico on 2017/1/11.
 */
public class KyroSerialization implements Serialization<RpcBody> {

    private Kryo kryo;

    private static final KyroSerialization INSTANCE = new KyroSerialization();
    public static KyroSerialization getInstance() {return INSTANCE;}
    private KyroSerialization(){
        kryo = new Kryo();
        kryo.register(RpcBody.class);
        kryo.register(RpcMethod.class);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
    }

    public byte[] serialize(RpcBody body) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeObject(output, body);
        output.close();
        return baos.toByteArray();
    }

    public RpcBody deserialize(byte[] bytes) {
        return null;
    }

    public RpcBody deserialize(byte[] bytes, int offset) {

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes, offset, bytes.length - offset);

        Input input = new Input(bais);
        RpcBody rb = kryo.readObject(input, RpcBody.class);

        input.close();
        return rb;
    }
}
