package be.home.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

public class SeleniumTest  {
    public static void main(String[] args) {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.



        //DesiredCapabilities capabilities = DesiredCapabilities.firefox();

        //capabilities.setCapability(CapabilityType.BROWSER_NAME, "IE");
        //File pathToBinary = new File("C:\\My Programs\\Firefox\\FirefoxPortableV49.Node1\\FirefoxPortable.exe");
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
        WebDriver driver = new FirefoxDriver(options);
        //driver = new FirefoxDriver(capabilities);
        //driver.manage().window().maximize();

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

        //Close the browser
        //driver.close();
        //driver.quit();
    }
}