package com.csx.soot.core;

import com.csx.soot.core.soot.core.SootCore;

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
    public static void main(String[] args){
        String apkPath = "D:\\ServiceTest-V1.apk";

        //静态分析阶段
        // Map<String, List<String>> map = ManifestChecker.getManifest(apkPath);
        SootCore sootCore = new SootCore();
        sootCore.runSoot(apkPath);

    }
}
