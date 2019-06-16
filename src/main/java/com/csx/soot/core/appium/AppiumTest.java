package com.csx.soot.core.appium;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ws.StringWebSocketClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.List;
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
    public static void main(String[] args) throws Exception{
//
//        List elements = driver.findElementsById("com.example.jyunmauchan.startservicetest");
//        LogEntries logEntries = driver.manage().logs().get("logcat");
//        for (LogEntry logEntry : logEntries) {
//            if (logEntry.getMessage().contains("invoke") || logEntry.getMessage().contains("HELLO")){
//                System.out.println(logEntry.toString());
//            }
//        }
//        Thread.sleep(500);

    }

    public void startTest(){
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
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
