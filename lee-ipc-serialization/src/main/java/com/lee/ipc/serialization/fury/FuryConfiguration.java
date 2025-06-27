package com.lee.ipc.serialization.fury;

import org.apache.fory.Fory;
import org.apache.fory.ThreadLocalFory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.config.CompatibleMode;
import org.apache.fory.config.Language;

/**
 * fury序列化全局配置
 * @author yanhuai lee
 */
public class FuryConfiguration {

    public static ThreadSafeFory fory = new ThreadLocalFory(classLoader -> Fory.builder()
            .withLanguage(Language.JAVA)
            .withRefTracking(true)
            .withAsyncCompilation(true)
            .withCompatibleMode(CompatibleMode.COMPATIBLE)
            .withMetaShare(true)
            .build());

}
