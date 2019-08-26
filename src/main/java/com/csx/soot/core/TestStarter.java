package com.csx.soot.core;

import com.csx.soot.core.appium.AppiumTest;
import com.csx.soot.core.soot.core.SootCore;
import com.csx.soot.core.utils.GlobalSettings;

import java.util.HashMap;
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
 */
public class TestStarter{
    // 程序分析入口点
    public static void main(String[] args){
        // 静态分析阶段
        SootCore sootCore = new SootCore();
        Map<String, String> activityCheckMap = new HashMap<>();
        Map<String, String> serviceCheckMap = new HashMap<>();

        // 获取manifestMap并产生app文件
        Map<String, List<String>> manifestMap = sootCore
                .runSoot(GlobalSettings.SOOT_PARSE_APK_PATH, serviceCheckMap, activityCheckMap);

        // 动态分析阶段
        AppiumTest.startTest(manifestMap, serviceCheckMap, activityCheckMap);

    }
}
