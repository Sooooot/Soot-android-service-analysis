package com.csx.soot.core.soot.core;

import com.csx.soot.core.soot.insertion.NewActivityInsertion;
import com.csx.soot.core.soot.insertion.ServiceInsertion;
import com.csx.soot.core.soot.manifest.ManifestChecker;
import com.csx.soot.core.utils.GlobalSettings;
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
 */
public class SootCore{

    private static void sootInit(){
        Options.v().set_allow_phantom_refs(true);// 设置允许伪类（Phantom class），指的是soot为那些在其classpath找不到的类建立的模型
        Options.v().set_prepend_classpath(true);// prepend the VM's classpath to Soot's own classpath
        Options.v().set_output_format(Options.output_format_dex);// 设置soot的输出格式
        Options.v().set_android_jars(GlobalSettings.ANDROID_SDK_PATH);// 设置android jar包路径
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_force_overwrite(true);
        Options.v().set_output_dir(GlobalSettings.SOOT_OUTPUT_PATH);
        Options.v().set_process_dir(Collections.singletonList(GlobalSettings.SOOT_PARSE_APK_PATH));

        Scene.v().loadNecessaryClasses();
    }

    public Map<String, List<String>> runSoot(String apkPath, Map<String, String> serviceCheckMap,
                                             Map<String, String> activityCheckMap){

        // 初始化Soot
        sootInit();

        // 生成ManifestMap
        Map<String, List<String>> manifestMap = ManifestChecker.getManifest(apkPath);

        NewActivityInsertion newActivityInsertion = new NewActivityInsertion();
        ServiceInsertion serviceInsertion = new ServiceInsertion();

        // Activity插装
        newActivityInsertion.activityInsertion(manifestMap, activityCheckMap);
        // Service插装
        serviceInsertion.serviceInsertion(manifestMap, serviceCheckMap);

        // 执行soot并输出
        PackManager.v().runPacks();
        PackManager.v().writeOutput();

        return manifestMap;
    }
}
