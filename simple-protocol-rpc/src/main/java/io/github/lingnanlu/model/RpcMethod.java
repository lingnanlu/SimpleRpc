package io.github.lingnanlu.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcMethod {

    @Getter @Setter private String name;
    @Getter private Class<?>[] parameterTypes;
    @Getter private Object[] parameter;
}
