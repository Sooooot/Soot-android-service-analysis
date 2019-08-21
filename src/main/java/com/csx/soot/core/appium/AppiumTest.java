package com.csx.soot.core.appium;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ws.StringWebSocketClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
 * @date 2019/5/30 20:43
 */
public class AppiumTest {

    public static void startTest(Map<String, List<String>> manifestMap) throws Exception{
        DesiredCapabilities cap = new DesiredCapabilities();
        AndroidDriver driver = null;

        cap.setCapability("automationName", "uiautomator2");
        cap.setCapability("app", "D:\\ServiceTest-V1-new.apk");
        cap.setCapability("deviceName", "test");
        cap.setCapability("platformName", "Android");
        cap.setCapability("platformVersion", "9.0");
        cap.setCapability("udid", "192.168.194.102:5555");
        cap.setCapability("appPackage", "com.example.jyunmauchan.startservicetest");
        cap.setCapability("appActivity", ".MainActivity");
        cap.setCapability("unicodeKeyboard", true);
        cap.setCapability("resetKeyboard", true);
        cap.setCapability("noSign", false);
        cap.setCapability("newCommandTimeout", "30");


        try{
            driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (driver != null) {
            // TODO 遍历
            for (String activityPackageString : manifestMap.get("activities")) {
                String[] packageAndName = new String[2];
                int lastDot = activityPackageString.lastIndexOf(".");
                packageAndName[0] = activityPackageString.substring(0, lastDot);
                packageAndName[1] = activityPackageString.substring(lastDot + 1);
                driver.startActivity(new Activity(packageAndName[0], packageAndName[1]));

                List<MobileElement> clickableList = driver.findElementsByClassName("android.view.View");

                for (MobileElement mobileElement : clickableList) {
                    mobileElement.click();
                }
            }



            // 分析日志
            LogEntries logEntries = driver.manage().logs().get("logcat");
            for (LogEntry logEntry : logEntries) {
                if (logEntry.getMessage().contains("SootTest")){
                    System.out.println(logEntry.toString());
                }
            }
        }




    }

}
