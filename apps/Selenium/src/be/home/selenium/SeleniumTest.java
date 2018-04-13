package be.home.selenium;

import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SeleniumTest {
    public static void main(String[] args) {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.


        WebDriver driver = makeWebDriver2();
        //driver = new FirefoxDriver(capabilities);
        //driver.manage().window().maximize();

        UltratopList(driver);

        //FacebookLogin(driver);

        //Close the browser
        //driver.close();
        driver.quit();
    }

    public static void test(WebDriver driver){
        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds


        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
    }

    public static WebDriver makeWebDriver(){
        File pathToBinary = new File("C:\\My Programs\\Firefox\\Test\\App\\Firefox\\Firefox.exe");
        FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
        //FirefoxProfile firefoxProfile = new FirefoxProfile(new File("C:\\My Programs\\Firefox\\FirefoxPortableV49.Node1\\Data\\profile"));
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File("C:\\My Programs\\Firefox\\Test\\Data\\profile"));
        //FirefoxProfile firefoxProfile = new FirefoxProfile();
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(ffBinary);
        options.setProfile(firefoxProfile);
        //options.setCapability(CapabilityType.BROWSER_NAME, "Firefox");

        //DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        //capabilities.setCapability(FirefoxDriver.BINARY, ffBinary);
        //capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/php/test.logs");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        options.setLogLevel(FirefoxDriverLogLevel.INFO);
        WebDriver driver = new FirefoxDriver(options);
        return driver;
    }

    public static WebDriver makeWebDriver2(){
        File pathToBinary = new File("C:\\My Programs\\Firefox\\Test\\App\\Firefox\\Firefox.exe");
        FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
        //FirefoxProfile firefoxProfile = new FirefoxProfile(new File("C:\\My Programs\\Firefox\\FirefoxPortableV49.Node1\\Data\\profile"));
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File("C:\\My Programs\\Firefox\\Test\\Data\\profile"));
        //FirefoxProfile firefoxProfile = new FirefoxProfile();
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(ffBinary);
        options.setProfile(firefoxProfile);
        //options.setCapability(CapabilityType.BROWSER_NAME, "Firefox");

        //DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        //capabilities.setCapability(FirefoxDriver.BINARY, ffBinary);
        //capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/php/test.logs");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        options.setLogLevel(FirefoxDriverLogLevel.INFO);
        //WebDriver driver = new FirefoxDriver(options);
        WebDriver driver = null;
        DesiredCapabilities capabilities =  DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        /*
        capabilities.setPlatform(Platform.WIN10);
        capabilities.setAcceptInsecureCerts(true);
        capabilities.setJavascriptEnabled(true);
        capabilities.setVersion("10.0");
        */
        //capabilities.setCapability("marionette", true);
        //capabilities.setCapability("networkConnectionEnabled", true);
        //capabilities.setCapability("browserConnectionEnabled", true);        //capability.setCapability("marionette", true);
        try {
            driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444"), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }
    public static void FacebookLogin(WebDriver driver) {

        driver.get("https://www.facebook.com");
        System.out.println("Successfully opened the website");
        //driver.manage().window().maximize();
        driver.findElement(By.id("email")).sendKeys("Enter the USERNAME");
        driver.findElement(By.id("pass")).sendKeys("Enter the PASSWORD");
        //driver.findElement(By.id("u_0_r")).click();
        //System.out.println("Successfully logged in");
        //Thread.sleep(3000);
        //driver.findElement(By.id("userNavigationLabel")).click();
        //Thread.sleep(2000);
        //driver.findElement(By.partialLinkText("Log out")).click();
        //System.out.println("Successfully logged out");
    }

    public static void UltratopList(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        try {
            driver.get("http://www.ultratop.be/nl/ultratop50");
        } catch (org.openqa.selenium.TimeoutException ex) {
            System.out.println("Timeout occured");
        }
        System.out.println("Successfully opened the website");
        //List<WebElement> elements = driver.findElements(By.xpath("//span[@class='CR_artist']"));
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='chartRow']"));
        System.out.println(elements.size());
        for (WebElement element : elements) {
            //String txt = element.getAttribute("innerHTML");
            //String txt2 = StringEscapeUtils.unescapeHtml4(txt);
            WebElement newEle = element.findElement(By.className("CR_artist"));
            System.out.println("Artist: " + getSeleniumText(newEle));
            newEle = element.findElement(By.className("CR_title"));
            System.out.println("Title: " + getSeleniumText(newEle));
        }
    }

        private static String getSeleniumText(WebElement element){
        String txt = element.getAttribute("textContext");
        txt = StringEscapeUtils.unescapeHtml4(txt);
        return txt;
    }



}