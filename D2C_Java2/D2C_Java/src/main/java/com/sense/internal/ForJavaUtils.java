package com.sense.internal;

import com.sun.jna.Platform;

public class ForJavaUtils {
    // 根据系统或取JNA依赖动态库前缀路径
    public static String getJNAPrefixPath()
    {
        if(Platform.isWindows())
            return Platform.is64Bit()? "win32-x86-64\\" : "win32-x86\\";
        if(Platform.isLinux())
            return Platform.is64Bit()? "linux-x86-64\\" : "linux-x86\\";

        return "";
    }

    public static void setDebugOn()
    {
        System.setProperty("SenseShield.Debug", "1");
    }

    public static void setDebugOff()
    {
        System.setProperty("SenseShield.Debug", "0");
    }

}
