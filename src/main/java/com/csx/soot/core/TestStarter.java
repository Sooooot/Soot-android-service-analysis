package com.csx.soot.core;

import com.csx.soot.core.appium.AppiumTest;
import com.csx.soot.core.soot.core.SootCore;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: TestStarter</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/6/16 10:35
 */
public class TestStarter {
    //程序分析入口点
    public static void main(String[] args) throws Exception {
        String apkPath = "D:\\ServiceTest-V1.apk";

        //静态分析阶段
        // Map<String, List<String>> map = ManifestChecker.getManifest(apkPath);
        SootCore sootCore = new SootCore();

        // 获取manifestMap并产生app文件
        Map<String, List<String>> manifestMap = sootCore.runSoot(apkPath);

        AppiumTest.startTest(manifestMap);

    }
}
