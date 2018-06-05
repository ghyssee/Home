package be.home.selenium;

import be.home.common.main.BatchJobV2;
import be.home.common.model.UltratopConfigBO;
import be.home.common.model.json.UltratopConfig;
import be.home.common.utils.DateUtils;
import be.home.model.M3uTO;
import be.home.selenium.common.FirefoxDriverSetup;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.http.W3CHttpCommandCodec;
import org.openqa.selenium.remote.http.W3CHttpResponseCodec;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SeleniumTest extends BatchJobV2 {

    private static final Logger log = getMainLog(BatchJobV2.class);

    public static void main(String[] args) throws MalformedURLException {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        SeleniumTest instance = new SeleniumTest();
        try {
            instance.start(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //RemoteWebDriver driver = instance.makeWebDriver3();
        //SessionId session = driver.getSessionId();
        //System.out.println("Session id: " + session.toString());
        //driver = new FirefoxDriver(capabilities);
        //driver.manage().window().maximize();

        //UltratopList(driver);
        //test(driver);
        //instance.UltratopList(driver);

        //FacebookLogin(driver);

        //Close the browser
        //driver.close();
        //driver.quit();
    }

    public void run(){

    }

    public void start(Date strDate) throws IOException {
        UltratopConfig.Month month = UltratopConfigBO.getInstance().getNewMonth(new Date());
        System.out.println("Base:" + month.baseDir);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 07);
        UltratopConfigBO.getInstance().addUltratopConfigItem(cal.getTime());

        /*
        String computerName = be.home.common.utils.NetUtils.getHostName();
        log.info("Computer Name: " + computerName);
        String instanceId = "1";
        FirefoxDriverSetup setup = new FirefoxDriverSetup(computerName, instanceId);
        FirefoxDriver driver = setup.setupWebDriver();
        driver.manage().window().maximize();
        UltratopList(driver, strDate);

        setup.closeWebDriver(driver);
        */
    }

    public void test(WebDriver driver){
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

    public WebDriver makeWebDriver(){
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

    public WebDriver makeWebDriver2(){
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

    public RemoteWebDriver makeWebDriver3() throws MalformedURLException {
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File("C:\\My Programs\\Firefox\\Test\\Data\\profile"));
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
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
        DesiredCapabilities capabilities =  DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        /*
        capabilities.setPlatform(Platform.WIN10);
        capabilities.setAcceptInsecureCerts(true);
        capabilities.setJavascriptEnabled(true);
        capabilities.setVersion("10.0");
        */
        SessionId session_id = new SessionId("05c68e07-736a-478e-9014-1d24edb7f171");
        RemoteWebDriver driver = createDriverFromSession(session_id, new URL("http://127.0.0.1:43451"));

        return driver;
    }

    public void FacebookLogin(WebDriver driver) {

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

    public void UltratopList(RemoteWebDriver driver, Date date) {
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        String url = "";
        String strDate = "";
        if (date == null){
            url = "http://www.ultratop.be/nl/ultratop50";
            date = new Date();
            strDate = DateUtils.formatDate(date, DateUtils.YYYYMMDD);
        }
        else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            strDate = DateUtils.formatDate(date, DateUtils.YYYYMMDD);
            url = "http://www.ultratop.be/nl/weekchart.asp?cat=s&year=" + cal.get(Calendar.YEAR) +
                    "&date=" + strDate;
        }
        System.out.println("url = " + url);
        try {
            driver.get(url);
        } catch (org.openqa.selenium.TimeoutException ex) {
            System.out.println("Timeout occured");
        }
        System.out.println("Successfully opened the website");
        WebDriverWait wait = new WebDriverWait(driver, 40);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.chartRow")));
        //List<WebElement> elements = driver.findElements(By.xpath("//span[@class='CR_artist']"));
        //List<WebElement> elements= driver.findElements(By.xpath("//div" + matchClass("chartRow")));
        List<WebElement> elements= driver.findElements(By.cssSelector("div.chartRow"));
        System.out.println(elements.size());
        List<M3uTO> list = new ArrayList<>();
        for (WebElement element : elements) {
            //String txt = element.getAttribute("innerHTML");
            //String txt2 = StringEscapeUtils.unescapeHtml4(txt);
            M3uTO m3uTO = new M3uTO();
            WebElement newEle = element.findElement(By.className("CR_artist"));
            m3uTO.setArtist(getSeleniumText(newEle));
            newEle = element.findElement(By.className("CR_title"));
            m3uTO.setSong(getTitle(newEle));
            newEle = element.findElement(By.className("CR_position"));
            m3uTO.setTrack(getTitle(newEle));
            list.add(m3uTO);
        }
        for (M3uTO m3uTO : list){
            System.out.println("Artist: " + m3uTO.getArtist());
            System.out.println("Title: " + m3uTO.getSong());
            System.out.println("Position: " + m3uTO.getTrack());
        }
        try {
            UltratopConfigBO.getInstance().saveUltratopList(strDate, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private String getSeleniumText(WebElement element){
        String txt = element.getAttribute("textContent");
            txt = txt.replaceAll("\\r|\\n|\\t", "");
        txt = StringEscapeUtils.unescapeHtml4(txt);
        return txt;
    }

    private String getTitle(WebElement element){
        String title = getSeleniumText(element);
        try {
            WebElement newEle = element.findElement(By.className("CR_version"));
            String tmp = getSeleniumText(newEle);
            title = title.replace(tmp, "").trim();
        }
        catch (org.openqa.selenium.NoSuchElementException ex){

        }
        return title;
    }

    private String matchClass(String className) {
        return "[contains(concat(' ', normalize-space(@class), ' ')," + "' " + className + " ')]";
    }

    public RemoteWebDriver createDriverFromSession(final SessionId sessionId, URL command_executor){
        CommandExecutor executor = new HttpCommandExecutor(command_executor) {

            @Override
            public Response execute(Command command) throws IOException {
                Response response = null;
                if (command.getName() == "newSession") {
                    response = new Response();
                    response.setSessionId(sessionId.toString());
                    response.setStatus(0);
                    response.setValue(Collections.<String, String>emptyMap());

                    try {
                        Field commandCodec = null;
                        commandCodec = this.getClass().getSuperclass().getDeclaredField("commandCodec");
                        commandCodec.setAccessible(true);
                        commandCodec.set(this, new W3CHttpCommandCodec());

                        Field responseCodec = null;
                        responseCodec = this.getClass().getSuperclass().getDeclaredField("responseCodec");
                        responseCodec.setAccessible(true);
                        responseCodec.set(this, new W3CHttpResponseCodec());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                } else {
                    response = super.execute(command);
                }
                return response;
            }
        };

        return new RemoteWebDriver(executor, new DesiredCapabilities());
    }

}