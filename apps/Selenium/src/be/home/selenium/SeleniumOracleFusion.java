package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.common.utils.CSVUtils;
import be.home.selenium.to.CSVRole;
import be.home.selenium.to.DataAccess;
import be.home.selenium.to.Role;
import be.home.selenium.to.UMRRequest;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;


import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SeleniumOracleFusion extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumOracleFusion.class);
    public List<String> errors = new ArrayList<String>();

    public static void main(String[] args) {

        SeleniumOracleFusion instance = new SeleniumOracleFusion();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {


        List<CSVRole> rolesFromCSV = getRolesFromCSV(null);

        WebDriver driver = initDriver();
        UMRRequest umrRequest = processUMR(driver);
        logIn(driver);
        addRoles(driver, umrRequest, rolesFromCSV);
        /*
        UMRRequest umrRequest = dummyUMR(rolesFromCSV);
        goToSetupAndMaintenance(driver);
        goToDataAccessForUsers(driver);
        getExistingDataAcessForUsers(driver);
        addDataAccessForUsers(driver, umrRequest);
        if (errors.size() > 0){
            System.err.println("Following errors found:");
            for (String errorMsg : errors){
                System.err.println(errorMsg);
            }
        }
        */


        driver.quit();

    }

    public UMRRequest dummyUMR(List<CSVRole> rolesFromCSV){
        //List<String> rolesFromUMR = Arrays.asList("All_Requisitions GSO bpost", "Collections Manager bpost");
        List<String> rolesFromUMR = Arrays.asList("Procurement Application Administrator", "REPORTS AND ANALYTICS");
        List<String> dataAccessFromUMR = Arrays.asList("002 - bpost", "062 - Radial Netherlands B.V.");
        UMRRequest umrRequest = new UMRRequest();
        //umrRequest.setUserId("ghyssee");
        umrRequest.setUserId("u625808");
        umrRequest.setName("VANDER HEYDEN Veerle");
        umrRequest.setEmail("Veerle.VANDERHEYDEN@bnode.com");
        for (String role : rolesFromUMR) {
            umrRequest.addRole(role);
        }
        for (String dataAccess : dataAccessFromUMR){
            umrRequest.addDataAccess(dataAccess);
        }
        validateUMRRoles(umrRequest.getRoles(), rolesFromCSV);
        return umrRequest;

    }


    public UMRRequest processUMR(WebDriver driver){
        UMRRequest umrRequest = new UMRRequest();
        String url = "https://apps.powerapps.com/play/e/default-1183410f-6cf0-4d82-976e-994c1ce4cfce/a/0c43e546-db86-4872-bb0a-14507d7580a1?tenantId=1183410f-6cf0-4d82-976e-994c1ce4cfce?ItemID=4583";

        umrRequest.setRequestId(extractItemFromString(url, "^(.*)\\?ItemID=(.*)"));

        driver.get(url);
        String tmpUrl = driver.getCurrentUrl();
        String tmpTitle = driver.getTitle();
        if (needToLogin(driver)){
            enterPassword(driver, false);
            trustbPost(driver);
            staySignedIn(driver);

        }


        //WebElement iFrame = waitForElement(driver, "fullscreen-app-host", SELECTOR.ID, "Check if UMR Screen is loaded");

        WebDriver frame = waitForFrame(driver, "fullscreen-app-host", "//div[text()='Personal information']");
        //driver.switchTo().frame(waitForElement(driver, "fullscreen-app-host", SELECTOR.ID, "Check if UMR Screen is loaded"));
        //clickScreen(driver, "//div[text()='Personal information']", SELECTOR.XPATH, "UMR - Personal Information");
        //Set<String> handles = driver.getWindowHandles();
        //for(String handle : handles) {
        //    System.out.println("Valid handle value : " + handle);
            //adding -updated to valid handle value to demonstrate exception
            //due to invalid handle value
        //}
        waitForElementUntilValueNotEmpty(driver, "input[title='Name']", SELECTOR.CSS, "Loading UMR Page");
        waitForElementUntilValueNotEmpty(driver, "input[title^='User ID']", SELECTOR.CSS, "Loading UMR Page");
        waitForElementUntilValueNotEmpty(driver, "input[title='E-Mail']", SELECTOR.CSS, "Loading UMR Page");

        umrRequest.setName(getValue(driver, SELECTOR.CSS, "input[title='Name']"));
        umrRequest.setUserId(getValue(driver, SELECTOR.CSS, "input[title^='User ID']"));
        umrRequest.setEmail(getValue(driver, SELECTOR.CSS, "input[title='E-Mail']"));

        clickScreen(driver, "//div[text()='Product information']", SELECTOR.XPATH, "UMR - Product Information");

        File csvFile = new File("C:\\My Programs\\OneDrive\\Config\\Java\\OracleFusion\\roles.csv");
        List<CSVRole> roles = new ArrayList<CSVRole>();
        try {
            roles = getRolesFromCSV(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("CSV File with roles not found: " + csvFile.getAbsolutePath());
        }

        getUMRRoles(driver, umrRequest);

        if (validateUMRRoles(umrRequest.getRoles(), roles) > 0){
            // at least one of the umr roles could not be found in the csv
            log.info("at least one of the umr roles could not be found in the csv");
        }
        else {

            log.info("Following UMR roles found: ");
            for (Role umrRole : umrRequest.getRoles()){
                log.info("Role: "+ umrRole.getRole());
            }
        }

        return umrRequest;

    }

    public void getUMRRoles(WebDriver driver, UMRRequest umrRequest){
        clickScreen(driver, "//div[text()='Product information']", SELECTOR.XPATH, "UMR - Product Information");

        WebElement roleElement = waitForElementUntilValueNotEmpty(driver, "react-combobox-view-1", SELECTOR.ID, "Find Roles");

        String title = roleElement.getAttribute("title");
        String roles[] = null;

        if (title != null){
            roles = title.split("\n");
            for (String umrRole : roles){
                umrRequest.addRole(stripRole(umrRole));
            }
        }
        else {
            throw new RuntimeException("No roles found in UMR " + umrRequest.getRequestId());
        }
    }


    public String stripRole (String role){
        String strippedRole = role.replaceAll( " {0,10}\\(Annual Fee.*", "");
        strippedRole = strippedRole.replaceAll(" {0,10}- Free", "");
        return strippedRole;
    }

    public int validateUMRRoles(List<Role> umrRoles, List<CSVRole> roles){
        List<String> rolesNotFound = new ArrayList<String>();
        for (Role umrRole : umrRoles){
            if (!findRoleInCSV(umrRole.getRole(), roles)){
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

    public List<Role> checkRoles(List<Role> oracleRoles, List<Role> umrRoles, List<CSVRole> rolesFromCSV){
        List<Role> StrippedUMRRoles = new ArrayList<Role>();
        for (Role umrRole : umrRoles) {
            // look up if UMR Role is already added
            CSVRole csvRole = getRoleInCSV(umrRole.getRole(), rolesFromCSV);
            boolean found = false;
            for (Role oracleRole : oracleRoles) {
                if (csvRole.getRole().equalsIgnoreCase(oracleRole.getRole())) {
                    found = true;
                    break;
                }
            }
            if (!found){
                StrippedUMRRoles.add(umrRole);
            }
            else {
                System.out.println("Role already added: " + umrRole.getRole());
            }
        }
        return StrippedUMRRoles;
    }

    public boolean findRoleInCSV(String umrRole, List<CSVRole> roles){
        return getRoleInCSV(umrRole, roles) != null;
    }

    public CSVRole getRoleInCSV(String umrRole, List<CSVRole> roles){
        CSVRole foundRole = null;
        for (CSVRole role : roles){
            if (!umrRole.equalsIgnoreCase(role.getRole())){
                if (role.getUmrRole() != null){
                    if (!umrRole.equalsIgnoreCase(role.getUmrRole())){
                        // role not found
                    }
                    else {
                        foundRole = role;
                        break;
                    }
                }
                else{
                    // role not found
                }
            }
            else {
                foundRole = role;
            }
        }
        return foundRole;
    }

    public List<CSVRole> getRolesFromCSV(File csvFile) throws FileNotFoundException {

        CSVUtils csvUtils = new CSVUtils();

         Map<String, String> map = new HashMap<>();
        map.put("OracleRole", "role");
        map.put("UMRRole", "umrRole");
        map.put("DataAccessSet", "dataAccessSet");

//        HeaderColumnNameTranslateMappingStrategy<Role> h =
 //               new HeaderColumnNameTranslateMappingStrategy<>();
        CsvToBean<Object> test = csvUtils.readOpenCSV("C:\\My Programs\\OneDrive\\Config\\Java\\OracleFusion\\roles.csv", map, new HeaderColumnNameTranslateMappingStrategy<CSVRole>(), CSVRole.class);
        List<Object> objects = test.parse();

        // convert List<Object> -> List<Role>
        List<CSVRole> roles = objects.stream()
                .map(element->(CSVRole) element)
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
        WebElement element = waitForElement(driver, css, selectorType, "GetValue");
        String value = null;
        if (element != null) {
            value = element.getAttribute("value");
        }
        return value;

    }



    public WebElement waitForElementClickable(WebDriver driver, String css, SELECTOR selectorType, String comment){
        WebElement element = waitForElement(driver, css, selectorType, comment);
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(getBy(css, selectorType)));
        return element;

    }

    public void clickScreen (WebDriver driver, String css, SELECTOR selectorType, String comment) {

        WebElement element = waitForElementClickable(driver, css, selectorType, comment);
        if (element != null) {
            boolean exit = false;
            do {
                try {
                    element.click();
                    exit = true;
                } catch (ElementClickInterceptedException ex) {
                    //another element is obscuring the element to be clicked
                    System.out.println("Wait till clickable");
                }
            }
            while (!exit);
        }
    }
    public void clickScreen (WebElement webElement, String css, SELECTOR selectorType, String comment) {
        // Wait until everything is loaded
        WebElement elementRet = null;

        Wait<WebElement> wait = new FluentWait<WebElement>(webElement)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element2 = wait.until(new Function<WebElement, WebElement>() {
                public WebElement apply(WebElement element3) {
                    System.out.println("Waiting for " + comment + " with Anchor " + css);
                    return element3.findElement(getBy(css, selectorType));
                }
            });
            if (element2 == null) {
                throw new RuntimeException(comment + "Invalid Selector type: " + selectorType.name());
            }
            else {
                element2.click();
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + css);
        }
    }

    public void clickScreen2 (WebDriver driver, String css, SELECTOR selectorType, String comment) {
        // Wait until everything is loaded
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
                .ignoring(NoSuchElementException.class);
        try {

            wait.until(ExpectedConditions.invisibilityOfElementLocated(getBy("div[class='AFModalGlassPane'", selectorType)));
            WebElement element2 = element2 = driver.findElement(getBy("div[class='AFModalGlassPane'", selectorType));
            element2.click();
        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + css);
        }
    }

    public WebElement waitForElementUntilValueNotEmpty (WebDriver driver, String css, SELECTOR selectorType, String comment) {
        int count = 0;
        boolean exit = false;
        WebElement element;
        do {
            element = waitForElement(driver, css, selectorType, comment);
            String value = element.getAttribute("value");
            if (StringUtils.isNotBlank(value) || StringUtils.isNotBlank(getText(element))){
                exit = true;
                break;
            }
            else {
                sleep(driver, 1, "waitForElementUntilValueNotEmpty");
            }
            count++;
        }
        while (count < 5);
        if (!exit){
            throw new RuntimeException(comment + "Element should not be empty with anchor: " + css );
        }
        return element;

    }


    public WebElement waitForElement (WebElement element, String css, SELECTOR selectorType, String comment) {

        // Wait until everything is loaded
        WebElement elementRet = null;
        Wait<WebElement> wait = new FluentWait<WebElement>(element)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element2 = wait.until(new Function<WebElement, WebElement>() {
                public WebElement apply(WebElement element3) {
                    System.out.println("Waiting for " + comment + " with Anchor " + css);
                    return element3.findElement(getBy(css, selectorType));
                }
            });
            if (element2 == null) {
                throw new RuntimeException(comment + "Invalid Selector type: " + selectorType.name());
            }
            else {
                elementRet = element2;
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + css);
        }
        return elementRet;
    }

    public WebDriver waitForFrame(WebDriver driver, String id, String xpathSelector) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        WebDriver frame = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("fullscreen-app-host"));
        boolean exit = false;
        do {
            try {
                waitForElement(driver,xpathSelector, SELECTOR.XPATH, "iFrame loaded" );
                driver.findElement(By.xpath(xpathSelector));
                exit = true;
                System.out.println("frame loaded");

            } catch (NoSuchWindowException ex) {
                // frame not fully loaded
                System.out.println("frame not loaded. Retrying...");
                driver.switchTo().defaultContent();
                driver.switchTo().frame(id);

            }
        }
        while (!exit);

        return frame;

    }

    public void logIn(WebDriver driver){
        driver.get("https://ejfb.fa.em2.oraclecloud.com/fscmUI/faces/FuseWelcome");
        makeScreenshot(driver, "test");
        // click on button 'Sign in with Bpost SSO'

        clickScreen(driver, "span[id^='idcs-signin-idp-signin-form-idp-button-Bpost']", SELECTOR.CSS, "Tools Menu");
        if (needToLogin(driver)) {
            enterPassword(driver, true);
        }
    }

    public void enterPassword(WebDriver driver, boolean enterUsername){
        // these steps not necessary if logged in with VPN
        if (enterUsername) {
            WebElement userName = waitForElement(driver, "input[id^='username']", SELECTOR.CSS, "User Name Field");
            userName.sendKeys("ghyssee");
        }

        WebElement password = waitForElement(driver, "input[id^='password']", SELECTOR.CSS, "PasswordField");
        password.sendKeys("Gizmo202610");
        clickScreen(driver, "a[id^='signOnButton']", SELECTOR.CSS, "Log in");

    }


    public boolean needToLogin(WebDriver driver){
        boolean login = true;
        try {
            //WebElement element = driver.findElement(By.xpath("//span[@class='bpost-heading' and text()='Sign On']"));
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[id^='username']")));
            login = true;
        }
        catch (TimeoutException ex){
            // no need to login
            login = false;
        }
        return login;

    }

    public void trustbPost(WebDriver driver){
        try {
            //WebElement element = driver.findElement(By.xpath("//span[@class='bpost-heading' and text()='Sign On']"));
            new WebDriverWait(driver, Duration.ofSeconds(1)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id^='appConfirmTitle']")));
            clickScreen(driver, "input[id^='idSIButton'][value='Continue']", SELECTOR.CSS, "Continue Button");
        }
        catch (TimeoutException ex){
            // no need to login
        }
    }

    public void staySignedIn(WebDriver driver){
        try {
            //WebElement element = driver.findElement(By.xpath("//span[@class='bpost-heading' and text()='Sign On']"));
            new WebDriverWait(driver, Duration.ofSeconds(1)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='hidden'][name='LoginOptions']")));
            clickScreen(driver, "input[id^='idSIButton'][value='Yes']", SELECTOR.CSS, "Continue Button");
        }
        catch (TimeoutException ex){
            // no need to login
        }
    }


    public void goToSetupAndMaintenance(WebDriver driver){
        driver.get("https://ejfb.fa.em2.oraclecloud.com/fscmUI/faces/FuseTaskListManagerTop");
        WebElement setupElement = waitForElement(driver, "//h1[starts-with(text(),'Setup:')]", SELECTOR.XPATH, "Check Setup");
        String text = setupElement.getText();
        String setup = text.replaceAll("Setup: {0,3}", "");
        final String FINANCIALS = "Financials";
        final String MANUFACTURING = "Manufacturing and Supply Chain Materials Management";
        final String PROCUREMENT = "Procurement";
        final String ORDERMANAGEMENT = "Order Management";
        if (setup.equalsIgnoreCase(FINANCIALS)){
            // everything ok. No need to switch

        }
        else if (setup.equalsIgnoreCase(MANUFACTURING)){
            // switch to Financials
            switchToFinancials(driver);

        }
        else if (setup.equalsIgnoreCase(PROCUREMENT)){
            // switch to Financials
            switchToFinancials(driver);

        }
        else if (setup.equalsIgnoreCase(ORDERMANAGEMENT)){
            // switch to Financials
            switchToFinancials(driver);
        }
        else {
            // unknown setup
            log.warn("Unknown setup: " + setup);
            // try to switch to Financials
            switchToFinancials(driver);
        }
    }
    public void switchToFinancials(WebDriver driver){
        clickScreen(driver, "a[id$='AP1:soc2::drop']", SELECTOR.CSS, "Check Setup");
        WebElement toolboxElement = waitForElement(driver, "ul[id$='AP1:soc2::pop']", SELECTOR.CSS, "Setup Toolbox");
        clickScreen(toolboxElement, ".//li[text()='Financials']", SELECTOR.XPATH, "Check Setup");
        // wait till Link General Ledger is available
        waitForElement(driver, "//td[text()='General Ledger']", SELECTOR.XPATH,  "General Ledger");
    }

    public void goToDataAccessForUsers(WebDriver driver){
        // select all tasks
        clickScreen(driver, "//td[text()='Users and Security']", SELECTOR.XPATH, "Users And Security");
        waitForElement(driver, "//h1[text()='Users and Security']", SELECTOR.XPATH,  "Menu Users and Security");
        Select dropdown = new Select(driver.findElement(By.cssSelector("select[id$='ATp:soc1::content']")));
        dropdown.selectByVisibleText("All Tasks");
        clickScreen(driver, "//a[text()='Manage Data Access for Users']", SELECTOR.XPATH, "Click on Manage Data Access for Users");
        waitForElement(driver, "//h1[text()='Manage Data Access for Users']", SELECTOR.XPATH,  "Screen Manage Data Access for Users");
    }

    public void getExistingDataAcessForUsers(WebDriver driver){
        initDataAccessForUsers(driver);
        List<DataAccess> dataAccessList = getDataAccessInformation(driver);
        for (DataAccess dataAccess : dataAccessList){
            System.out.println("Role: "+ dataAccess.getRole());
            System.out.println("Security Context: "+ dataAccess.getSecurityContext());
            System.out.println("Security Context Value: "+ dataAccess.getSecurityContextValue());
        }
    }

    public void initDataAccessForUsers(WebDriver driver){
        WebElement element = waitForElement(driver, "input[id$='qryId2:value00::content']", SELECTOR.CSS,  "Screen Manage Data Access for Users");
        element.sendKeys("ghyssee");
        element.sendKeys(Keys.ENTER);
        clickScreen(driver, "//button[text()='Search']", SELECTOR.XPATH, "Search Data Access for Users");
        sleep(driver, 1, "initDataAccessForUsers.Sleep");
        System.out.println("test");

    }

    public List<DataAccess> getDataAccessInformation(WebDriver driver) {
        WebElement info = waitForElement(driver, "table[summary='Manage Data Access for Users']", SELECTOR.CSS, "Screen Manage Data Access for Users");
        List<WebElement> rows = info.findElements(By.cssSelector("tr table"));
        List<DataAccess> dataAccessList = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> rowInfo = row.findElements(By.cssSelector("table tr td"));
            // maybe check table with summary starts with This table contains column headers corresponding to the data body table below
            WebElement headerTable = info.findElement(By.xpath("//table[starts-with(@summary, 'This table contains column headers corresponding to the data body table below')]"));
            List<WebElement> tableHeaders = driver.findElements(By.cssSelector("table tr[id*='shwClmresId2c']"));
            DataAccess dataAccess = new DataAccess();
            dataAccess.setRole(getDataAccessElement(tableHeaders, rowInfo, "Role"));
            dataAccess.setSecurityContext(getDataAccessElement(tableHeaders, rowInfo, "Security Context"));
            dataAccess.setSecurityContextValue(getDataAccessElement(tableHeaders, rowInfo, "Security Context Value"));
            dataAccessList.add(dataAccess);
        }
        return dataAccessList;
    }

    public String searchTextFromElement(WebElement columnElement, String cssSelector){
        List<WebElement> cells = columnElement.findElements(By.cssSelector(cssSelector));
        String cellText = "";
        for (WebElement cell : cells){
            String text = getText(cell);
            if (!StringUtils.isBlank(text)){
                cellText = text;
                break;
            }
        }
        return cellText;
    }

        public String getDataAccessElement(List<WebElement> tableHeaders, List<WebElement> rowInfo, String daElement){
        int index = 0;
        for (WebElement header : tableHeaders){
                String headerText = searchTextFromElement(header, "td");
                if(headerText.equalsIgnoreCase(daElement)){
                    return rowInfo.get(index).getText();
                }
                index++;
            }
            return null;
        }

    public void addDataAccessForUsers (WebDriver driver, UMRRequest umrRequest){
        for (Role role :umrRequest.getRoles()){
            for (DataAccess dataAccess : umrRequest.getDataAccessList()){
               log.info("Role: " + role.getRole() + " - Adding data access " + dataAccess.getSecurityContext() );
               addDataAccess(driver, umrRequest.getUserId(), role.getRole(), dataAccess);
            }
        }

    }

    public void select(WebDriver driver, By locator, String visibleText){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(
                ExpectedConditions.refreshed(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                )
        );
        Select dropdown = new Select(element);
        dropdown.selectByVisibleText(visibleText);
    }

    public void addDataAccess(WebDriver driver, String userId, String role, DataAccess dataAccess){
        clickScreen(driver, "//img[contains(@id,'create::icon')]/ancestor::a", SELECTOR.XPATH, "Screen Create Data Access for Users");
        waitForElement(driver, "//div[text()='Create Data Access for Users']", SELECTOR.XPATH, "Create Data Access for Users");
        WebElement element = waitForElement(driver,"input[id$='userNameId::content'", SELECTOR.CSS, "User Name field");
        element.sendKeys(userId + Keys.TAB);
        element = getNonStaleElement(driver, By.cssSelector("input[id$='roleNameCrId::content'"));
        element.sendKeys(role + Keys.TAB);
        //Select dropdown = new Select(findElementRetryIfStale(driver, By.cssSelector("select[id$='soc2::content']")));
        //dropdown.selectByVisibleText("Business unit");
        select(driver, By.cssSelector("select[id$='soc2::content']"), dataAccess.getSecurityContext());
        element = getNonStaleElement(driver, By.cssSelector("input[id$='securityContextValueId::content'"));
        element.sendKeys(dataAccess.getSecurityContextValue() + Keys.TAB);
        //getNonStaleElement(driver, By.xpath("//img[contains(@id,'create::icon')][@title='Add Row']/ancestor::a")).click();
        // click on Save Button
        clickScreen2(driver, "button[id$='AT2:cb1']", SELECTOR.CSS, "Add");
        //  is not clickable at point (936,524) because another element <div class="AFModalGlassPane"> obscures it
        //getNonStaleElement(driver, By.cssSelector("button[id$='AT2:cb1']")).click();
        try {
            WebElement errorElement = driver.findElement(By.xpath("//img[contains(@src, 'error_status')]/ancestor::table"));
            String errorMsg = getText(errorElement);
            if (StringUtils.isNotBlank(errorMsg)){
                addError("Problem adding Data Access: Role= " + "xxx" + " Security context= " + "xxx" + " Security Context Value=" + "xxx");
                addError("Error Message: " + errorMsg);
                File dstFile = makeScreenshot(driver, "DataAccess.Problem.SaveButton");
                addError("screenshot: " + dstFile.getAbsolutePath());
            }
            else {
                // no error found, data Access saved
            }
        }
        catch (NoSuchElementException ex){
            // no error found, data Access saved
        }
        System.out.println("test");
        // Procurement Application Administrator
        // Business unit
        // check if error
        // driver.findElement(By.xpath("//img[contains(@src, 'error_status')]/ancestor::table"))
        // or No results found
        //<li role="option" id="pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:pt1:AP2:AT2:AT3:_ATp:ATt1:5:securityContextValueId::su0" data-afr-value="" data-afr-label="No results found." class="AFAutoSuggestItem">No results found.</li>


    }

    public void sendText(WebDriver driver, WebElement element, String textToBeSent){
        Wait<WebDriver> wait =
                new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(2))
                        .pollingEvery(Duration.ofMillis(300))
                        .ignoring(ElementNotInteractableException.class);

        wait.until(
                d -> {
                    element.sendKeys(textToBeSent);
                    return true;
                });
    }


    public void addRoles(WebDriver driver, UMRRequest umrRequest, List<CSVRole> csvRoles){

        // go to User Roles Screen
        // Tools Menu
        clickScreen(driver, "a[id^='groupNode_tools']", SELECTOR.CSS, "Tools Menu");
        // Roles Screen
        clickScreen(driver, "a[id^='ASE_FUSE_SECURITY_CONSOLE']", SELECTOR.CSS, "Roles Screen");
        // Select Users
        clickScreen(driver, "//div[text()='Users']", SELECTOR.XPATH, "Select Users Screen");
        // User Accounts
        waitForElement(driver, "//h1[text()='User Accounts']", SELECTOR.XPATH, "Header User Accounts");
        waitForElement(driver, "input[value^='User Name']", SELECTOR.CSS, "User Name");
        //WebElement searchElement = waitForElement(driver, "input[aria-label^='Search']", SELECTOR.CSS, "Search");
        WebElement searchElement = waitForElementClickable(driver, "input[aria-label^='Search']", SELECTOR.CSS, "Search");
        //searchElement.sendKeys(umrRequest.getUserId() + Keys.ENTER);
        sendText(driver, searchElement, umrRequest.getUserId() + Keys.ENTER);
        clickScreen(driver, "a[id*='sp1:usrList:0']", SELECTOR.CSS, "Select First name in list of users");

        // Getting roles linked to a user
        WebElement RolesElement = waitForElement(driver, "table[summary='Roles']", SELECTOR.CSS, "Check if roles are loaded");
        List<WebElement> Roles = RolesElement.findElements(By.cssSelector("tr"));
        List<Role> oracleRoles = new ArrayList<Role>();
        for (WebElement role : Roles){
            // get the first td element. This contains the role description
            List <WebElement> columns = role.findElements(By.cssSelector("td"));
            Role oracleRole = new Role();
            oracleRole.setRole(getText(columns.get(0)));
            oracleRoles.add(oracleRole);
        }
        List<Role> StrippedUMRRoles = checkRoles(oracleRoles, umrRequest.getRoles(), csvRoles);
        if (StrippedUMRRoles.size() > 0){
            log.info("Nr. of roles to add: " + StrippedUMRRoles.size());
            initAddRole(driver);
            for (Role roleToAdd : StrippedUMRRoles){
                log.info("Role to add: " + roleToAdd.getRole());
                addRole(driver, roleToAdd.getRole(), umrRequest);
            }
            closeAddRole(driver);
        }
        else {
            log.info("No roles to add!!!");
        }

    }

    public void initAddRole(WebDriver driver){
        clickScreen(driver, "button[id$='sp1:cb6'][title='Edit']", SELECTOR.CSS, "Click button Edit");
        WebElement element = waitForElement(driver, "button[id$='sp1:cb1'][title='Add Role']", SELECTOR.CSS, "Click button Add Role");
        element.click();
        waitForElement(driver, "//div[starts-with(text(), 'Add Role Membership from Role')]", SELECTOR.XPATH, "Screen Add Role");
    }
    public void closeAddRole(WebDriver driver){
        clickScreen(driver, "button[id$='sp1:rhSrCan'][title='Done']", SELECTOR.CSS, "Click button Done");
        // _FOpt1:_FOr1:0:_FONSr2:0:_FOTr1:3:sp1:saveBtn
        // save the roles
        WebElement element = waitForElement(driver, "button[id$='sp1:saveBtn'][title='Save and Close']", SELECTOR.CSS, "Click button Save");
        if (element.isEnabled()){
            System.out.println("We can save it!!");
        }
        else {
            File dstFile = makeScreenshot(driver, "Roles.Problem.SaveButton");
            throw new RuntimeException("There was a problem saving the roles. Check " + dstFile.getAbsolutePath());
        }
        // cancel
        // WebElement element = waitForElement(driver, "button[id$='sp1:canBtn'][title='Cancel']", SELECTOR.CSS, "Click button Cancel");

    }
    public void addRole(WebDriver driver, String role, UMRRequest umrRequest){

        WebElement element = waitForElement(driver, "input[id$='sp1:urSrcBx::content']", SELECTOR.CSS, "Input Field Search Role");
        element.clear();
        element.sendKeys(role);
        element.sendKeys(Keys.RETURN);
        clickScreen(driver, "a[id$='sp1:f1:cil121'][title='Search']", SELECTOR.CSS, "Input Field Search Role");
        // We have no way to know when the search is finished, so a hardcoded 1 second wait is added
        // Since the role is checked, we could assume the role must exist and build in a loop until Search Result Count > 0
        // and after some retries consider it as an error adding the role
        sleep(driver, 1, "AddRoleSearchSleepProblem");
        //get result count

        element = waitForElement(driver, "//span[starts-with(text(),'Search Result Count')]", SELECTOR.XPATH, "Search Result count");

        String count = element.getText();
        count = count.replaceAll("Search Result Count ?: ?", "");
        int searchCount = Integer.parseInt(count);
        if (searchCount > 0){
            clickScreen(driver, "//span[text()='" + role + "']", SELECTOR.XPATH, "Select Role from list");
            makeScreenshot(driver, "AddRoleStep4");
            // finally add the role
            clickScreen(driver, "button[id$='sp1:rhSrAdd'][title='Add Role Membership'", SELECTOR.CSS, "Select Role from list");
        }
        else {
            File file = makeScreenshot(driver, umrRequest.getUserId());
            addError("Problem adding role: " + role + " (screenshot: " + file.getAbsolutePath() + ")");
        }
    }

    public void sleep(WebDriver driver, int seconds, String errorMessage){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            makeScreenshot(driver, errorMessage);
        }
    }
    public void addError(String error){
        System.err.println(error);
        errors.add(error);

    }
}

