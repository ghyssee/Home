package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.common.utils.CSVUtils;
import be.home.selenium.to.Role;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


        List<Role> rolesFromCSV = getRolesFromCSV(null);

        WebDriver driver = initDriver();
        processUMR(driver);


        //driver.quit();

    }

    public void processUMR(WebDriver driver){
        String url = "https://apps.powerapps.com/play/e/default-1183410f-6cf0-4d82-976e-994c1ce4cfce/a/0c43e546-db86-4872-bb0a-14507d7580a1?tenantId=1183410f-6cf0-4d82-976e-994c1ce4cfce?ItemID=4580";

        String umrRequest = extractItemFromString(url, "^(.*)\\?ItemID=(.*)");

        driver.get(url);

        //WebElement iFrame = waitForElement(driver, "fullscreen-app-host", SELECTOR.ID, "Check if UMR Screen is loaded");


        //WebElement iFrame = driver.findElement(By.id("fullscreen-app-host"));
        waitForFrame(driver, "fullscreen-app-host']");
        //driver.switchTo().frame(waitForElement(driver, "fullscreen-app-host", SELECTOR.ID, "Check if UMR Screen is loaded"));
        //clickScreen(driver, "//div[text()='Personal information']", SELECTOR.XPATH, "UMR - Personal Information");
        String name = getValue(driver, SELECTOR.CSS, "input[title='Name']");
        String userId = getValue(driver, SELECTOR.CSS, "input[title^='User ID']");
        String email = getValue(driver, SELECTOR.CSS, "input[title='E-Mail']");

        clickScreen(driver, "//div[text()='Product information']", SELECTOR.XPATH, "UMR - Product Information");

        File csvFile = new File("C:\\My Programs\\OneDrive\\Config\\Java\\OracleFusion\\roles.csv");
        List<Role> roles = new ArrayList<Role>();
        try {
            roles = getRolesFromCSV(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("CSV File with roles not found: " + csvFile.getAbsolutePath());
        }

        List<String> umrRoles = getUMRRoles(driver, umrRequest);

        if (validateUMRRoles(umrRoles, roles) > 0){
            // at least one of the umr roles could not be found in the csv
        }
        else {

        }



        //addRoles(driver);

    }

    public List<String> getUMRRoles(WebDriver driver, String umrRequest){
        clickScreen(driver, "//div[text()='Product information']", SELECTOR.XPATH, "UMR - Product Information");

        WebElement roleElement = waitForElement(driver, "react-combobox-view-1", SELECTOR.ID, "Find Roles");

        String title = roleElement.getAttribute("title");
        String roles[] = null;
        List<String> strippedRoles = new ArrayList<String>();

        if (title != null){
            roles = title.split("\n");
            for (String umrRole : roles){
                strippedRoles.add(stripRole(umrRole));
            }
            return strippedRoles;
        }
        else {
            throw new RuntimeException("No roles found in UMR " + umrRequest);
        }
    }


    public String stripRole (String role){
        String strippedRole = role.replaceAll( " {0,10}\\(Annual Fee.*", "");
        strippedRole = strippedRole.replaceAll(" {0,10}- Free", "");
        return strippedRole;
    }

    public int validateUMRRoles(List<String> umrRoles, List<Role> roles){
        List<String> rolesNotFound = new ArrayList<String>();
        for (String umrRole : umrRoles){
            if (!findRoleInCSV(umrRole, roles)){
                rolesNotFound.add("UMR Role not found in csv: " + umrRole);
            }
            else {
                System.out.println("UMR Role found in csv: " + umrRole);
            }
        }
        if (rolesNotFound.size() > 0){
            for (String roleNotFound : rolesNotFound){
                System.err.println(roleNotFound);
            }
        }
        return rolesNotFound.size();
    }

    public boolean findRoleInCSV(String umrRole, List<Role> roles){
        boolean found = false;
        for (Role role : roles){
            if (!umrRole.equalsIgnoreCase(role.getRole())){
                if (role.getUmrRole() != null){
                    if (!umrRole.equalsIgnoreCase(role.getUmrRole())){
                        // role not found
                    }
                    else {
                        found = true;
                        break;
                    }
                }
                else{
                    // role not found
                }
            }
            else {
                found = true;
            }
        }
        return found;
    }

    public List<Role> getRolesFromCSV(File csvFile) throws FileNotFoundException {

        CSVUtils csvUtils = new CSVUtils();

         Map<String, String> map = new HashMap<>();
        map.put("OracleRole", "role");
        map.put("UMRRole", "umrRole");

//        HeaderColumnNameTranslateMappingStrategy<Role> h =
 //               new HeaderColumnNameTranslateMappingStrategy<>();
        CsvToBean<Object> test = csvUtils.readOpenCSV("C:\\My Programs\\OneDrive\\Config\\Java\\OracleFusion\\roles.csv", map, new HeaderColumnNameTranslateMappingStrategy<Role>(), Role.class);
        List<Object> objects = test.parse();

        List<Role> roles = objects.stream()
                .map(element->(Role) element)
                .collect(Collectors.toList());

        return roles;

    }

    public String extractItemFromString(String text, String pattern){
        // create matcher for pattern p and given string
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);

        // if an occurrence of a pattern was found in a given string...
        String found = null;
        if (m.find()) {
            // ...then you can use group() methods.
            found = m.group(2);
        }
        return found;
    }

    public String getValue(WebDriver driver, SELECTOR selectorType, String css){
        WebElement element = null;
        switch (selectorType){
            case CSS:
                element = waitForElement(driver, css, SELECTOR.CSS, "GetValue");
                //element = driver.findElement(By.cssSelector(css));
                break;
            case XPATH:
                //element = driver.findElement(By.xpath(css));
                element = waitForElement(driver, css, SELECTOR.XPATH, "GetValue");
                break;
        }
        String value = null;
        if (element != null) {
            value = element.getAttribute("value");
        }
        return value;

    }

    enum SELECTOR {
        CSS, XPATH, ID;
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
                        case ID:
                            element = driver.findElement(By.id(css));
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

    public void waitForFrame(WebDriver driver, String id) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("fullscreen-app-host"));
    }


    public void addRoles(WebDriver driver){
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

    }



}