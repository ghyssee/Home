package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;


import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumOracleFusion extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumOracleFusion.class);

    public static void main(String[] args) {

        SeleniumOracleFusion instance = new SeleniumOracleFusion();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        WebDriver driver = initDriver();

        driver.get("https://ejfb.fa.em2.oraclecloud.com/fscmUI/faces/FuseWelcome");

        // click on button 'Sign in with Bpost SSO'

        clickScreen(driver, "span[id^='idcs-signin-idp-signin-form-idp-button-Bpost']", SELECTOR.CSS, "Tools Menu");

        // these steps not necessary if loggd in with VPN
        WebElement userName = waitForElement(driver, "input[id^='username']", SELECTOR.CSS, "User Name Field");
        userName.sendKeys("ghyssee");

        WebElement password = waitForElement(driver, "input[id^='password']", SELECTOR.CSS, "PasswordField");
        password.sendKeys("Yorki202607");

        // go to User Roles Screen
        // Tools Menu
        clickScreen(driver, "a[id^='groupNode_tools']", SELECTOR.CSS, "Tools Menu");
        // Roles Screen
        clickScreen(driver, "a[id^='ASE_FUSE_SECURITY_CONSOLE']", SELECTOR.CSS, "Roles Screen");
        // Select Users
        clickScreen(driver, "//div[text()='Users']", SELECTOR.XPATH, "Select Users Screen");

        driver.findElement(By.cssSelector("input[value^='User Name']")).sendKeys("User Name");
        WebElement searchElement = driver.findElement(By.cssSelector("input[aria-label^='Search']"));
        searchElement.sendKeys("ghyssee");
        searchElement.sendKeys(Keys.ENTER);
        clickScreen(driver, "a[id*='sp1:usrList:0']", SELECTOR.CSS, "Select First name in list of users");

        // Getting roles linked to a user
        WebElement RolesElement = waitForElement(driver, "table[summary='Roles']", SELECTOR.CSS, "Check if roles are loaded");
        List<WebElement> Roles = RolesElement.findElements(By.cssSelector("tr"));
        for (WebElement role : Roles){
            // get the first td element. This contains the role description
            List <WebElement> columns = role.findElements(By.cssSelector("td"));
            System.out.println(getText(columns.get(0)));
            System.out.println(getText(columns.get(1)));
        }

        driver.quit();

    }

    enum SELECTOR {
        CSS, XPATH;
    }

    public void clickScreen (WebDriver driver, String css, SELECTOR selectorType, String comment) {

        WebElement element = waitForElement(driver, css, selectorType, comment);
        if (element != null) {
                element.click();
            }

    }

    public WebElement waitForElement (WebDriver driver, String css, SELECTOR selectorType, String comment) {

        // Wait until everything is loaded
        WebElement elementRet = null;
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element = wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    System.out.println("Waiting for " + comment + " with Anchor " + css);
                    WebElement element = null;
                    switch (selectorType){
                        case CSS:
                            element = driver.findElement(By.cssSelector(css));
                            break;
                        case XPATH:
                            element = driver.findElement(By.xpath(css));
                            break;
                    }
                    return element;
                }
            });
            if (element == null) {
                throw new RuntimeException(comment + "Invalid Selector type: " + selectorType.name());
            }
            else {
                elementRet = element;
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + css);
        }
        return elementRet;
    }



}