package be.home.selenium.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.logging.Level;

public class FirefoxDriverSetup {

    public FirefoxDriver getWebDriver(){
        File pathToBinary = new File("C:\\My Programs\\Firefox\\Test\\App\\Firefox\\Firefox.exe");
        FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File("C:\\My Programs\\Firefox\\Test\\Data\\profile"));
        firefoxProfile.setPreference(FirefoxProfile.PORT_PREFERENCE, 35710);
        firefoxProfile.setPreference("EricTest", "35710");
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        System.setProperty("webdriver.firefox.port","7046");
        System.setProperty("webdriver.gecko.port","7046");
        FirefoxOptions options = new FirefoxOptions();
        //DesiredCapabilities cap=new DesiredCapabilities();
        options.setCapability("webdriver_firefox_port",7046);
        options.setBinary(ffBinary);
        options.setProfile(firefoxProfile);

        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/php/test.logs");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        options.setLogLevel(FirefoxDriverLogLevel.INFO);
        FirefoxDriver driver = new FirefoxDriver(options);
        System.out.println(driver.getCurrentUrl());
        return driver;
    }

}
