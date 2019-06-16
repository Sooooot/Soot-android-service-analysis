package com.csx.soot.core.soot;

import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: ManifestChecher</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/6/16 10:40
 */
public class ManifestChecker {

    public static Map<String, List<String>> getManifest(String apkPath){
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        try{
            ProcessManifest processManifest = new ProcessManifest(apkPath);

            //获取包名
            //System.out.println(processManifest.getManifest().getAttribute("package"));
            List<String> packageList = new LinkedList<String>();
            packageList.add(processManifest.getPackageName());
            result.put("package", packageList);

            //System.out.println(processManifest.getActivities());
            List<String> activityList = new LinkedList<String>();
            for (AXmlNode activity : processManifest.getActivities()) {
                activityList.add(activity.getAttribute("name").getValue().toString());
            }
            result.put("activities", activityList);

            //System.out.println(processManifest.getServices());
            List<String> serviceList = new LinkedList<String>();
            for (AXmlNode service : processManifest.getServices()) {
                serviceList.add(service.getAttribute("name").getValue().toString());
            }
            result.put("services", serviceList);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
