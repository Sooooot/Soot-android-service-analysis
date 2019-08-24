package com.csx.soot.core.soot.manifest;

import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.util.*;

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
                List<AXmlNode> intentFilter = activity.getChildrenWithTag("intent-filter");
                if (intentFilter.size() > 0) {
                    String action = intentFilter.get(0).getChildrenWithTag("action").get(0).getAttribute("name").getValue().toString();
                    String category = intentFilter.get(0).getChildrenWithTag("category").get(0).getAttribute("name").getValue().toString();
                    if("android.intent.action.MAIN".equals(action) && "android.intent.category.LAUNCHER".equals(category)){
                        List<String> launchActivityList = new ArrayList<String>();
                        launchActivityList.add(activity.getAttribute("name").getValue().toString());
                        result.put("launchActivity", launchActivityList);
                    }
                }


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
