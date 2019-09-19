package com.csx.soot.core.utils;

/**
 * <p>Title: GlobalSettings</p>
 * <p>Description: 全局配置项</p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 */
public class GlobalSettings{
    /**
     * 安卓SDK位置
     */
    public static final String ANDROID_SDK_PATH = "D:\\AndroidSDK\\platforms";

    /**
     * Soot所要处理的单个APK的位置
     */
    public static final String SOOT_PARSE_APK_PATH = "D:\\soot-test-apk.apk";

    /**
     * Soot批处理的APKs存放目录
     */
    public static final String SOOT_PARSE_DIR_PATH = "";

    /**
     * Soot输出目录
     */
    public static final String SOOT_OUTPUT_PATH = "./sootoutput";

    /**
     * Appium所需要安装和测试的单个APK位置
     */
    public static final String APPIUM_INSTALL_APK_PATH =
            "D:\\SicongChen\\UnshareFiles\\Workspace\\JavaWorkspace\\soot-android-static-analysis\\sootOutput" +
            "\\soot-test-apk.apk";

    /**
     * Appium批处理的APKs存放目录
     */
    public static final String APPIUM_INSTALL_DIR_PATH = "";
}
