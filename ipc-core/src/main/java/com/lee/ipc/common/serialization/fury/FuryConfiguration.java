package com.lee.ipc.common.serialization.fury;

import org.apache.fory.Fory;
import org.apache.fory.config.CompatibleMode;
import org.apache.fory.config.Language;

/**
 * fury序列化全局配置
 * @author yanhuai lee
 */
public class FuryConfiguration {

    public static Fory fory = Fory.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .withAsyncCompilation(true)
            .withCompatibleMode(CompatibleMode.COMPATIBLE)
            .build();

}
