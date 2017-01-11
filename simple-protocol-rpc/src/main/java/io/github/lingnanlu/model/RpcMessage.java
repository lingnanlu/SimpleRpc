package io.github.lingnanlu.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcMessage {

    @Getter @Setter private RpcHeader header;
    @Getter @Setter private RpcBody body;


}
