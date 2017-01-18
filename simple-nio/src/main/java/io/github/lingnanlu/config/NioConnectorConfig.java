package io.github.lingnanlu.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/16.
 */
public class NioConnectorConfig extends NioConfig {

    @Getter @Setter private int connectTimeoutInMillis = 2000;
}
