package io.github.lingnanlu.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/16.
 */
public class NioAcceptorConfig extends NioConfig {

    @Getter @Setter private boolean reuseAddress = true;
    @Getter @Setter private int channelSize = Integer.MAX_VALUE;
    @Getter @Setter private int backlog = 50;
}
