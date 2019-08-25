package com.csx.soot.core.appium;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: AppiumTest</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 */
public class AppiumTest {

    public static void startTest(Map<String, List<String>> manifestMap,
                                 Map<String, String> serviceCheckMap,
                                 Map<String, String> activityCheckMap){
        DesiredCapabilities cap = new DesiredCapabilities();
        AndroidDriver driver = null;
        String apkPath = "D:\\SicongChen\\UnshareFiles\\Workspace\\JavaWorkspace\\soot-android-static-analysis\\sootOutput\\ServiceTest-V1.apk";

        // 设置appium启动参数
        cap.setCapability("automationName", "uiautomator2");
        cap.setCapability("app", apkPath);
        cap.setCapability("deviceName", "test");
        cap.setCapability("platformName", "Android");
        cap.setCapability("platformVersion", "9.0");
        cap.setCapability("udid", "192.168.194.102:5555");
        cap.setCapability("appPackage", manifestMap.get("package").get(0));
        cap.setCapability("appActivity", manifestMap.get("launchActivity").get(0));
        cap.setCapability("unicodeKeyboard", true);
        cap.setCapability("resetKeyboard", true);
        cap.setCapability("autoGrantPermissions", true);
        cap.setCapability("fullReset", true);
        cap.setCapability("noSign", false);
        cap.setCapability("newCommandTimeout", "30");


        try{
            driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (driver != null) {
            long startTime = System.currentTimeMillis();
            for (String activityPackageString : manifestMap.get("activities")) {
                try{
                    Activity activity = new Activity(manifestMap.get("package").get(0), activityPackageString);
                    driver.startActivity(activity);

                    List elements = driver.findElementsByXPath("//android.widget.Button[@clickable='true']");
                    for (Object element : elements) {
                        AndroidElement androidElement = (AndroidElement) element;
                        androidElement.click();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            // 分析日志
            LogEntries logEntries = driver.manage().logs().get("logcat");
            for (LogEntry logEntry : logEntries) {
                String logString = logEntry.getMessage();
                if (logString.contains("SootTest")){
                    if (logEntry.getTimestamp() > startTime) {
                        System.out.println(logString);
                        String message = logString.split("SootTest:")[1];
                        if (message.lastIndexOf("->") > -1) {
                            String[] splitString = message.split("->");
                            String activityIdName = splitString[0].trim();
                            String status = splitString[1].trim().split("@")[1].trim();
                            if (activityCheckMap.containsKey(activityIdName)) {
                                activityCheckMap.put(activityIdName, status);
                            }
                        }
                        else{
                            String[] splitString = message.split("@");
                            String serviceIdName = splitString[0].trim();
                            String status = splitString[1].trim();
                            if (serviceCheckMap.containsKey(serviceIdName)) {
                                serviceCheckMap.put(serviceIdName, status);
                            }
                        }
                    }
                }
            }


            // 打印检查结果
            System.out.println("=============== ACTIVITY CHECK RESULT START ===============");
            activityCheckMap.forEach((key, value) -> System.out.println(key + " -> " + value));
            System.out.println("=============== ACTIVITY CHECK RESULT END ===============");

            System.out.println("=============== SERVICE CHECK RESULT START ===============");
            serviceCheckMap.forEach((key, value) -> System.out.println(key + " -> " + value));
            System.out.println("=============== SERVICE CHECK RESULT END ===============");

            int activityFinishCount = 0;
            int serviceFinishCount = 0;
            for (String value : activityCheckMap.values()) {
                if("INVOKED".equals(value)) activityFinishCount++;
            }
            for (String value : serviceCheckMap.values()) {
                if("FINISHED".equals(value)) serviceFinishCount++;
            }
            System.out.println("=============== CHECK RATIO ===============");
            System.out.println("ACTIVITY: " + ((float)activityFinishCount / (float)activityCheckMap.size()));
            System.out.println("SERVICE: " + ((float)serviceFinishCount / (float)serviceCheckMap.size()));

        }


    }

}
