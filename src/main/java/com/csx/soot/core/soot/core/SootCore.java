package com.csx.soot.core.soot.core;

import com.csx.soot.core.soot.insertion.ActivityInsertion;
import com.csx.soot.core.soot.insertion.ServiceInsertion;
import com.csx.soot.core.soot.manifest.ManifestChecker;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: SootCore</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/6/2 13:23
 */
public class SootCore{
    private final static String androidPath = "D:\\AndroidSDK\\platforms";
    private final static String apkPath = "D:\\ServiceTest-V1.apk";
    private final static String outputPath = "./sootoutput";
    private static void sootInit(){
        Options.v().set_allow_phantom_refs(true);//设置允许伪类（Phantom class），指的是soot为那些在其classpath找不到的类建立的模型
        Options.v().set_prepend_classpath(true);//prepend the VM's classpath to Soot's own classpath
        Options.v().set_output_format(Options.output_format_dex);//设置soot的输出格式
        Options.v().set_android_jars(androidPath);//设置android jar包路径
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_force_overwrite(true);
        Options.v().set_output_dir(outputPath);
        Options.v().set_process_dir(Collections.singletonList(apkPath));

        Scene.v().loadNecessaryClasses();
    }

    public Map<String, List<String>> runSoot(String apkPath, Map<String, String> checkMap){

        // 初始化Soot
        sootInit();

        // 生成ManifestMap
        Map<String, List<String>> manifestMap = ManifestChecker.getManifest(apkPath);

        // 服务插装
        checkMap = ServiceInsertion.serviceInsertion(manifestMap);
        // ActivityInsertion.activityCheck(manifestMap);
        // SootServiceMethodInsertion.activityCheck(map);

        // 执行soot并输出
        PackManager.v().runPacks();
        PackManager.v().writeOutput();

        return manifestMap;
    }
}
