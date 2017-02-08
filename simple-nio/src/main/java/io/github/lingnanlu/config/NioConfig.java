package io.github.lingnanlu.config;


import io.github.lingnanlu.IoConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class NioConfig extends IoConfig {

    @Getter @Setter protected int processorPoolSize = Runtime.getRuntime().availableProcessors();
    @Getter @Setter protected int executorSize = processorPoolSize << 3;
}
