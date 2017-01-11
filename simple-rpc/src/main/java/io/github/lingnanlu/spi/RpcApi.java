package io.github.lingnanlu.spi;

import io.github.lingnanlu.api.RpcParameter;

/**
 * Created by rico on 2017/1/11.
 */
public interface RpcApi {

    //for unique mapping
    String getKey();

    String getName();

    String getId();

    Class<?> getInterface();

    String getMethodName();

    Class<?>[] getMethodParameterTypes();

    Object getRpcObject();

    RpcParameter getRpcParameter();

}
