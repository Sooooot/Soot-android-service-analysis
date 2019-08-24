package com.csx.soot.core.appium;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.ArrayList;
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
 * @date 2019/5/30 20:43
 */
public class AppiumTest {

    public static void startTest(Map<String, List<String>> manifestMap, Map<String, String> checkMap) throws Exception{
        DesiredCapabilities cap = new DesiredCapabilities();
        AndroidDriver driver = null;
        String apkPath = "D:\\SicongChen\\UnshareFiles\\Workspace\\JavaWorkspace\\soot-android-static-analysis\\sootOutput\\ServiceTest-V1.apk";

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
        cap.setCapability("noReset", true);
        cap.setCapability("noSign", false);
        cap.setCapability("newCommandTimeout", "30");


        try{
            driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (driver != null) {

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
            List<String> logcatStringList = new ArrayList<String>();
            for (LogEntry logEntry : logEntries) {
                String logString = logEntry.getMessage();
                if (logString.contains("SootTest")){
                    System.out.println(logString);
                    String[] strings = logString.split(" ");
                    checkMap.put(strings[1], strings[2]);
                    logcatStringList.add(logString);
                }
            }
            System.out.println();
        }


    }

}
