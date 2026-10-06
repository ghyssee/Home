package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.common.utils.CSVUtils;
import be.home.selenium.bo.RoleBO;
import be.home.selenium.to.*;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SeleniumOracleFusion extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumOracleFusion.class);

    public static final String UMR = "https://apps.powerapps.com/play/e/default-1183410f-6cf0-4d82-976e-994c1ce4cfce/a/0c43e546-db86-4872-bb0a-14507d7580a1?tenantId=1183410f-6cf0-4d82-976e-994c1ce4cfce?ItemID=4638";
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

        if (true) {

            WebDriver driver = initDriver();
            //UMRRequest umrRequest = processUMR(driver);
            UMRRequest umrRequest = dummyUMR(rolesFromCSV);
            if (true) {
                logIn(driver);
                if (false) {
                    addRoles(driver, umrRequest, rolesFromCSV);
                }

                if (true) {
                    // sometimes login is not finished yet before goint to setup, resulting in the log in screen again
                    sleep(driver, 1, "test");
                    goToSetupAndMaintenance(driver);
                    goToDataAccessForUsers(driver);
                    getExistingDataAcessForUsers(driver, umrRequest);
                    addDataAccessForUsers(driver, umrRequest, rolesFromCSV);
                }
            }
            if (errors.size() > 0) {
                System.err.println("Following errors found:");
                for (String errorMsg : errors) {
                    System.err.println(errorMsg);
                }
            }


            driver.quit();
        }

    }

    public UMRRequest dummyUMR(List<CSVRole> rolesFromCSV){
        //List<String> rolesFromUMR = Arrays.asList("All_Requisitions GSO bpost", "Collections Manager bpost");
        File csvFile = new File("C:\\My Programs\\OneDrive\\Config\\Java\\OracleFusion\\roles.csv");
        List<CSVRole> roles = new ArrayList<CSVRole>();
        try {
            roles = getRolesFromCSV(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("CSV File with roles not found: " + csvFile.getAbsolutePath());
        }
        List<String> rolesFromUMR = Arrays.asList("Procurement Application Administrator");
        List<String> dataAccessFromUMR = Arrays.asList("002 - bpost", "062 - Radial Netherlands B.V.");
        UMRRequest umrRequest = new UMRRequest();
        umrRequest.setUserId("ghyssee");
        //umrRequest.setUserId("u625808");
        umrRequest.setName("VANDER HEYDEN Veerle");
        umrRequest.setEmail("Veerle.VANDERHEYDEN@bnode.com");
        for (String role : rolesFromUMR) {
            umrRequest.addRole(role);
        }
        for (String dataAccess : dataAccessFromUMR){
            umrRequest.addEntity(dataAccess, dataAccess.substring(0,3));
        }
        validateUMRRoles(umrRequest.getRoles(), rolesFromCSV);
        RoleBO roleBO = new RoleBO(roles);
        List<DataAccess> dataAccessSet = roleBO.getDatAccessSet(umrRequest);
        umrRequest.setDataAccessList(dataAccessSet);
        return umrRequest;

    }


    public UMRRequest processUMR(WebDriver driver){
        UMRRequest umrRequest = new UMRRequest();
        String url = UMR;

        umrRequest.setRequestId(extractItemFromString(url, "^(.*)\\?ItemID=(.*)"));

        boolean exit = false;
        do {
            driver.get(url);
            if (needToLogin(driver)) {
                log.info("Entering password");
                enterPassword(driver, false);
                trustbPost(driver);
                staySignedIn(driver);

            }
            try {
                WebDriver frame = waitForFrame(driver, "fullscreen-app-host", "//div[text()='Personal information']");
                exit = true;
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        while (!exit);


        waitForElementUntilValueNotEmpty(driver, By.cssSelector("input[title='Name']"), "Loading UMR Page");
        waitForElementUntilValueNotEmpty(driver, By.cssSelector("input[title^='User ID']"), "Loading UMR Page");
        waitForElementUntilValueNotEmpty(driver, By.cssSelector("input[title='E-Mail']"), "Loading UMR Page");

        umrRequest.setName(getValue(driver, By.cssSelector( "input[title='Name']")));
        umrRequest.setUserId(getValue(driver, By.cssSelector("input[title^='User ID']")));
        umrRequest.setEmail(getValue(driver, By.cssSelector( "input[title='E-Mail']")));

        clickScreen(driver, By.xpath( "//div[text()='Product information']"), "UMR - Product Information");

        File csvFile = new File("C:\\My Programs\\OneDrive\\Config\\Java\\OracleFusion\\roles.csv");
        List<CSVRole> roles = new ArrayList<CSVRole>();
        try {
            roles = getRolesFromCSV(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("CSV File with roles not found: " + csvFile.getAbsolutePath());
        }

        getUMRRoles(driver, umrRequest);
        getUMROracleEntities(driver, umrRequest);

        if (validateUMRRoles(umrRequest.getRoles(), roles) > 0){
            // at least one of the umr roles could not be found in the csv
            log.info("at least one of the umr roles could not be found in the csv");
        }
        else {
            log.info("Following UMR roles found: ");
            for (Role umrRole : umrRequest.getRoles()){
                log.info("Role: "+ umrRole.getRole());
            }
            for (OracleEntity entity : umrRequest.getEntities()){
                log.info("Entity: "+ entity.getName());
                log.info("Code: "+ entity.getCode());
            }
            RoleBO roleBO = new RoleBO(roles);
            List<DataAccess> dataAccessSet = roleBO.getDatAccessSet(umrRequest);
            umrRequest.setDataAccessList(dataAccessSet);
            for (DataAccess dataAccess : dataAccessSet){
                log.info(dataAccess.getRole() + " | " + dataAccess.getSecurityContext() + " | " + dataAccess.getSecurityContextValue());
            }

        }

        return umrRequest;

    }

    public void getUMRRoles(WebDriver driver, UMRRequest umrRequest){
        clickScreen(driver, By.xpath("//div[text()='Product information']"), "UMR - Product Information");

        WebElement roleElement = waitForElementUntilTextNotEmpty(driver, By.id("react-combobox-view-1"), "Find Roles");

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

    public void getUMROracleEntities(WebDriver driver, UMRRequest umrRequest){
        WebElement entityElement = waitForElementUntilTextNotEmpty(driver, By.id("react-combobox-view-10"), "Find UMR Oracle Entities");

        String title = entityElement.getAttribute("title");
        String entities[] = null;

        if (title != null){
            entities = title.split("\n");
            for (String umrEntity : entities){
                String code = umrEntity.substring(0,3);
                umrRequest.addEntity(umrEntity, code);
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
                rolesNotFound.add("UMR Role not found in csv: " + umrRole.getRole());
            }
            else {
                System.out.println("UMR Role found in csv: " + umrRole.getRole());
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
                if (csvRole.getOracleRole().equalsIgnoreCase(oracleRole.getRole())) {
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
            if (!umrRole.equalsIgnoreCase(role.getOracleRole())){
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
        map.put("OracleRole", "oracleRole");
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

    public String getValue(WebDriver driver, By locator){
        WebElement element = waitForElement(driver, locator, "GetValue");
        String value = null;
        if (element != null) {
            value = element.getAttribute("value");
        }
        return value;

    }

    public WebElement waitForElementClickable(WebDriver driver, By locator, String comment){
        WebElement element = null;
        boolean exit = false;
        do {
            try {
                element = waitForElement(driver, locator, comment);
                Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.elementToBeClickable(locator));
                exit = true;
            } catch (StaleElementReferenceException ex) {
                System.err.println("Element with anchor " + locator.toString() + " is stale. Retrying...");
            }
        }
        while (!exit);
        return element;

    }
    public WebElement waitForElementInvisible(WebDriver driver, By locator, String comment){
        WebElement element = waitForElement(driver, locator, comment);
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("AFModalGlassPane")));
        return element;

    }
    public void clickScreen (WebDriver driver, By locator, String comment) {

            boolean exit = false;
            do {
                WebElement element = waitForElementClickable(driver, locator, comment);
                try {
                    element.click();
                    exit = true;
                } catch (ElementClickInterceptedException ex) {
                    //another element is obscuring the element to be clicked
                    sleep(driver, 1, "ElementClickInterceptedException");
                    System.out.println("Wait till clickable");
                }
                catch (StaleElementReferenceException ex2) {
                       // do nothing
                    System.out.println("Retrying to click");
                    }
            }
            while (!exit);
    }

    public void clickScreen (WebElement webElement, By locator, String comment) {
        // Wait until everything is loaded
        WebElement elementRet = null;

        Wait<WebElement> wait = new FluentWait<WebElement>(webElement)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element2 = wait.until(new Function<WebElement, WebElement>() {
                public WebElement apply(WebElement element3) {
                    System.out.println("Waiting for " + comment + " with Anchor " + locator.toString());
                    return element3.findElement(locator);
                }
            });
            if (element2 == null) {
                throw new RuntimeException(comment + "Invalid anchor: " + locator.toString());
            }
            else {
                element2.click();
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + locator.toString());
        }
    }

    public WebElement waitForElementUntilValueNotEmpty (WebDriver driver, By locator, String comment) {
        int count = 0;
        boolean exit = false;
        WebElement element;
        do {
            element = waitForElement(driver, locator, comment);
            String value = element.getAttribute("value");
            if (StringUtils.isNotBlank(value)){
//            if (StringUtils.isNotBlank(value) || StringUtils.isNotBlank(getText(element))){
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
            makeScreenshot(driver, "waitForElementUntilValueNotEmpty");
            throw new RuntimeException(comment + " Element should not be empty with anchor: " + locator.toString() );
        }
        return element;

    }

    public WebElement waitForElementUntilTextNotEmpty (WebDriver driver, By locator, String comment) {
        int count = 0;
        boolean exit = false;
        WebElement element;
        do {
            element = waitForElement(driver, locator, comment);
            String value = element.getAttribute("value");
            if (StringUtils.isNotBlank(getText(element))){
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
            throw new RuntimeException(comment + "Element should not be empty with anchor: " + locator.toString() );
        }
        return element;

    }

    public WebDriver waitForFrame(WebDriver driver, String id, String xpathSelector) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebDriver frame = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("fullscreen-app-host"));
        boolean exit = false;
        do {
            try {
                waitForElement(driver,By.xpath(xpathSelector), "iFrame loaded" );
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
        // click on button 'Sign in with Bpost SSO'

        clickScreen(driver, By.cssSelector("span[id^='idcs-signin-idp-signin-form-idp-button-Bpost']"), "Single Sign On");
        if (needToLogin(driver)) {
            log.info("Entering password");
            enterPassword(driver, true);
        }
    }

    public void enterPassword(WebDriver driver, boolean enterUsername){
        // these steps not necessary if logged in with VPN
        if (enterUsername) {
            WebElement userName = waitForElement(driver, By.cssSelector("input[id^='username']"), "User Name Field");
            userName.sendKeys("ghyssee");
        }

        WebElement password = waitForElement(driver, By.cssSelector("input[id^='password']"), "PasswordField");
        password.sendKeys("Gizmo202610");
        clickScreen(driver, By.cssSelector("a[id^='signOnButton']"), "Log in");

    }


    public boolean needToLogin(WebDriver driver){
        boolean login = true;
        String locator = "input[id^='username']";
        log.info("Checking if we need to login");
        WebElement element = waitForElement(driver, By.cssSelector("a[id='signOnButton']"), 1,5, false, "Check for Log In Screen");
        //WebElement element = driver.findElement(By.xpath("//span[@class='bpost-heading' and text()='Sign On']"));
        //new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locator)));
        if (element == null){
            login = false;
        }
        else {
            login = true;
        }
        System.out.println("Login: " + login);
        return login;
    }

    public void trustbPost(WebDriver driver){
        log.info("Checking for trust button");
        try {
            String locator = "input[id^='idSIButton'][value='Continue']";
            //WebElement element = driver.findElement(By.xpath("//span[@class='bpost-heading' and text()='Sign On']"));
            //new WebDriverWait(driver, Duration.ofSeconds(1)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id^='appConfirmTitle']")));
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locator)));
            clickScreen(driver, By.cssSelector("input[id^='idSIButton'][value='Continue']"), "Continue Button");
        }
        catch (TimeoutException ex){
            // no need to login
        }
    }

    public void staySignedIn(WebDriver driver){
        log.info("Checking for Stay Signed In button");
        try {
            //WebElement element = driver.findElement(By.xpath("//span[@class='bpost-heading' and text()='Sign On']"));
            new WebDriverWait(driver, Duration.ofSeconds(1)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='hidden'][name='LoginOptions']")));
            clickScreen(driver, By.cssSelector("input[id^='idSIButton'][value='Yes']"), "Continue Button");
        }
        catch (TimeoutException ex){
            // no need to login
        }
    }


    public void goToSetupAndMaintenance(WebDriver driver){
        driver.get("https://ejfb.fa.em2.oraclecloud.com/fscmUI/faces/FuseTaskListManagerTop");
        WebElement setupElement = waitForElement(driver, By.xpath("//h1[starts-with(text(),'Setup:')]"),"Check Setup");
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
        clickScreen(driver, By.cssSelector("a[id$='AP1:soc2::drop']"), "Check Setup");
        WebElement toolboxElement = waitForElement(driver, By.cssSelector("ul[id$='AP1:soc2::pop']"), "Setup Toolbox");
        clickScreen(toolboxElement, By.xpath(".//li[text()='Financials']"), "Check Setup");
        // wait till Link General Ledger is available
        waitForElement(driver, By.xpath("//td[text()='General Ledger']"),  "General Ledger");
    }

    public void goToDataAccessForUsers(WebDriver driver){
        // select all tasks
        clickScreen(driver, By.xpath("//td[text()='Users and Security']"), "Users And Security");
        waitForElement(driver, By.xpath("//h1[text()='Users and Security']"),  "Menu Users and Security");
        Select dropdown = new Select(driver.findElement(By.cssSelector("select[id$='ATp:soc1::content']")));
        dropdown.selectByVisibleText("All Tasks");
        clickScreen(driver, By.xpath("//a[text()='Manage Data Access for Users']"), "Click on Manage Data Access for Users");
        waitForElement(driver, By.xpath("//h1[text()='Manage Data Access for Users']"), "Screen Manage Data Access for Users");
    }

    public void getExistingDataAcessForUsers(WebDriver driver, UMRRequest request){
        initDataAccessForUsers(driver, request);
        List<DataAccess> dataAccessList = getDataAccessInformation(driver);
        for (DataAccess dataAccess : dataAccessList){
            System.out.println("Role: "+ dataAccess.getRole());
            System.out.println("Security Context: "+ dataAccess.getSecurityContext());
            System.out.println("Security Context Value: "+ dataAccess.getSecurityContextValue());
        }
    }

    public void initDataAccessForUsers(WebDriver driver, UMRRequest request){
        // click on Users with Data Access
        clickScreen(driver, By.xpath(".//label[text()='Users with Data Access']"), "Users with Data Access");
        // qryId1 Users with Data Access
        // qryId2 Users without Data Access
        //boolean exit = false;
        //do {
         //   try {
                //WebElement element = waitForElementClickable(driver, By.cssSelector("input[id$='qryId1:value00::content'][aria-label*='User Name']"), "Input Field User name");
        WebElement element = getNonStaleElement(driver, By.cssSelector("input[id$='qryId1:value00::content'][aria-label*='User Name']"));
                element.sendKeys(request.getUserId());
                element.sendKeys(Keys.ENTER);
         //       exit = true;
         //   }
         //   catch (StaleElementReferenceException ex) {
         //       System.err.println("Entering userId is stale. Retrying...");
         //   }
        //}
        //while (!exit);
        clickScreen(driver, By.cssSelector("button[id$='qryId1::search']"), "Search Data Access for Users");
        sleep(driver, 1, "initDataAccessForUsers.Sleep");

    }

    public List<DataAccess> getDataAccessInformation(WebDriver driver) {
        WebElement info = waitForElement(driver, By.cssSelector("table[summary='Manage Data Access for Users']"), "Screen Manage Data Access for Users");
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

    public void addDataAccessForUsers (WebDriver driver, UMRRequest umrRequest, List<CSVRole> csvRoles){
        List<DataAccess> dataAccessSet = umrRequest.getDataAccessList();
        for (DataAccess dataAccess : dataAccessSet){
            log.info("Role: " + dataAccess.getRole() + " - Adding data access " + dataAccess.getSecurityContext() + ' ' + dataAccess.getSecurityContextValue());
            addDataAccess(driver, umrRequest.getUserId(), dataAccess.getRole(), dataAccess.getSecurityContext(), dataAccess.getSecurityContextValue());
        }
    }

    public void select(WebDriver driver, By locator, String visibleText){
        boolean exit=false;
        do {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(
                    ExpectedConditions.refreshed(
                            ExpectedConditions.visibilityOfElementLocated(locator)
                    )
            );
            Select dropdown = new Select(element);
            try {
                dropdown.selectByVisibleText(visibleText);
                exit = true;
            }
            catch (ElementClickInterceptedException ex){
                // retry
            }
        }
        while (!exit);
    }

    public String findErrorImage(WebDriver driver){
        String errorMsg = null;
        try {
            WebElement errorElement = driver.findElement(By.xpath("//img[contains(@src, 'error_status')]/ancestor::table"));
            errorMsg = getText(errorElement);
        }
        catch (NoSuchElementException ex){
            // no error found
        }
        return errorMsg;
    }


    public void addDataAccess(WebDriver driver, String userId, String role, String securityContext, String value){
        // with data access: pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:pt1:AP2:AT1:_ATp:create::icon
        //without: pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:pt1:AP2:AT2:_ATp:create::icon
        clickScreen(driver, By.xpath("//img[contains(@id,'AT1:_ATp:create::icon')]/ancestor::a"), "Screen Create Data Access for Users");
        waitForElement(driver, By.xpath("//div[text()='Create Data Access for Users']"), "Create Data Access for Users");
        sendKeys(driver, By.cssSelector("input[id$='userNameId::content'"), userId);
//        sendKeysCheckStale2(driver,By.cssSelector("input[id$='userNameId::content'"), userId);
        //waitForNotBlank(driver, By.cssSelector("input[id$='userNameId::content'"), "title;value");
        //sleep(driver, 1, "Sleep");
        // check if input box has title value
        //sendKeysCheckStale2(driver, By.cssSelector("input[id$='roleNameCrId::content'"), role);
        sendKeys(driver, By.cssSelector("input[id$='roleNameCrId::content'"), role);
        //waitForNotBlank(driver, By.cssSelector("input[id$='roleNameCrId::content'"), "value");
        //sleep(driver, 1, "Sleep");
        // check for value="Human Resource Specialist  Job bpost"
        select(driver, By.cssSelector("select[id$='soc2::content']"), securityContext);
        //waitForNotBlank(driver, By.cssSelector("select[id$='soc2::content']"), "title");
        // check for attribute title
        //sleep(driver, 1, "Sleep");
        //sendKeysCheckStale3(driver, By.cssSelector("input[id$='securityContextValueId::content'"), value);
        sendKeys(driver, By.cssSelector("input[id$='securityContextValueId::content'"), value);

        // check for attribute title/value
        // Oracle Fusion uses the UI Events Framework
        // we have no way of checking if background process is finsihed
        // only check we can do is the attribute title or value to be not empty
        // but even then, Selenium is sometimes faster than the Browser GUI
        // resulting in a screenshot where the value you just typed is not shown, although is already set
        //waitForNotBlank(driver, By.cssSelector("input[id$='securityContextValueId::content'"), "title;value");
        //sleep(driver, 5, "Sleep");
        // click on Save Button
        boolean exit = false;
        String errorMsg = null;
        By locator = By.cssSelector("button[id$='AT2:cb1']");
        clickScreen(driver, locator, "Save Button");

        do {
            System.out.println("Clicking Save button");
            System.out.println("Looking for Error");
            sleep(driver, 1, "Sleep");
            errorMsg = findErrorImage(driver);
            if (StringUtils.isBlank(errorMsg)){
                try {
                    WebElement addButton = driver.findElement(locator);
                    addButton.click();
                    System.out.println("add button found, try to click it again");
                    // add button found, try to click it again
                }
                catch (NoSuchElementException ex){
                    // No save button found, and no error, we can assume everything is saved correctly
                    System.out.println("No save button found, and no error, we can assume everything is saved correctly");
                    exit = true;
                }
            }
            else {
                // save button clicked but error found
                System.out.println("Error found while saving");
                exit = true;
            }
        }
        while (!exit);
        //makeScreenshot(driver, "DataAccess.Afteradd");
        //  is not clickable at point (936,524) because another element <div class="AFModalGlassPane"> obscures it
        //getNonStaleElement(driver, By.cssSelector("button[id$='AT2:cb1']")).click();
            //makeScreenshot(driver, "DataAccess.BeforeCheckError");
            if (StringUtils.isNotBlank(errorMsg)){
                addError("Problem adding Data Access");
                addError("Role: " + role);
                addError("Security context: " + securityContext);
                addError("Security Context Value: " + value);
                addError("Error Message: " + errorMsg);
                File dstFile = makeScreenshot(driver, "DataAccess.Screenshot.ErrorSave");
                addError("screenshot: " + dstFile.getAbsolutePath());
                // click cancel button
                //pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:pt1:AP2:AT2:cb3
                clickScreen(driver, By.cssSelector("button[id$='AP2:AT2:cb3']"), "Cancel Button");
            }
            else {
                File dstFile = makeScreenshot(driver, "DataAccess.SaveButton");
                log.info("screenshot: " + dstFile.getAbsolutePath());
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
        WebElement element;
        int retries = 0;
        do {
            clickScreen(driver, By.cssSelector("a[id^='groupNode_tools']"), "Tools Menu");
            element = waitForElement(driver, By.cssSelector("div[aria-controls='cluster_groupNode_tools'"), "Check Tools Menu");
            if (hasClass(element, "selected")){
                break;
            }
            else {
                retries++;
            }
        }
        while ( retries < 5);
        if (retries >= 5){
            throw new RuntimeException("Tools Menu not loaded");
        }

            // check if tools menu is clicked. If not, retry
            // Roles Screen
            clickScreen(driver, By.cssSelector("a[id^='ASE_FUSE_SECURITY_CONSOLE']"), "Roles Screen");

        // Select Users
        clickScreen(driver, By.xpath("//div[text()='Users']"), "Select Users Screen");
        // User Accounts
        waitForElement(driver, By.xpath("//h1[text()='User Accounts']"), "Header User Accounts");
        waitForElement(driver, By.cssSelector("input[value^='User Name']"), "User Name");
        //WebElement searchElement = waitForElement(driver, "input[aria-label^='Search']", SELECTOR.CSS, "Search");
        WebElement searchElement = waitForElementClickable(driver, By.cssSelector("input[aria-label^='Search']"), "Search");
        //searchElement.sendKeys(umrRequest.getUserId() + Keys.ENTER);
        sendText(driver, searchElement, umrRequest.getUserId() + Keys.ENTER);
        clickScreen(driver, By.cssSelector("a[id*='sp1:usrList:0']"), "Select First name in list of users");

        // Getting roles linked to a user
        WebElement RolesElement = waitForElement(driver, By.cssSelector("table[summary='Roles']"), "Check if roles are loaded");
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
                addRole(driver, getRoleInCSV(roleToAdd.getRole(), csvRoles).getOracleRole(), umrRequest);
            }
            closeAddRole(driver);
        }
        else {
            log.info("No roles to add!!!");
        }

    }

    public void initAddRole(WebDriver driver){
        clickScreen(driver, By.cssSelector("button[id$='sp1:cb6'][title='Edit']"), "Click button Edit");
        WebElement element = waitForElement(driver, By.cssSelector("button[id$='sp1:cb1'][title='Add Role']"), "Click button Add Role");
        element.click();
        waitForElement(driver, By.xpath("//div[starts-with(text(), 'Add Role Membership from Role')]"), "Screen Add Role");
    }
    public void closeAddRole(WebDriver driver){
        clickScreen(driver, By.cssSelector("button[id$='sp1:rhSrCan'][title='Done']"), "Click button Done");
        // _FOpt1:_FOr1:0:_FONSr2:0:_FOTr1:3:sp1:saveBtn
        // save the roles
        waitForElementInvisible(driver, By.className("AFModalGlassPane"), "Click button Save");
        System.out.println("We can save it!!");
        clickScreen(driver, By.cssSelector("button[id$='sp1:saveBtn'][title='Save and Close']"), "Click button Save");
        //else {
        //    File dstFile = makeScreenshot(driver, "Roles.Problem.SaveButton");
        //    throw new RuntimeException("There was a problem saving the roles. Check " + dstFile.getAbsolutePath());
        //}
        // cancel
        // WebElement element = waitForElement(driver, "button[id$='sp1:canBtn'][title='Cancel']", SELECTOR.CSS, "Click button Cancel");

    }
    public void addRole(WebDriver driver, String role, UMRRequest umrRequest) {

        WebElement element = waitForElement(driver, By.cssSelector("input[id$='sp1:urSrcBx::content']"), "Input Field Search Role");
        element.clear();
        element.sendKeys(role);
        element.sendKeys(Keys.RETURN);
        clickScreen(driver, By.cssSelector("a[id$='sp1:f1:cil121'][title='Search']"), "Input Field Search Role");
        // We have no way to know when the search is finished, so a hardcoded 1 second wait is added
        // Since the role is checked, we could assume the role must exist and build in a loop until Search Result Count > 0
        // and after some retries consider it as an error adding the role
        sleep(driver, 1, "AddRoleSearchSleepProblem");
        //get result count

        element = waitForElement(driver, By.xpath("//span[starts-with(text(),'Search Result Count')]"), "Search Result count");

        String count = element.getText();
        count = count.replaceAll("Search Result Count ?: ?", "");
        int searchCount = Integer.parseInt(count);
        if (searchCount > 0){
            clickScreen(driver, By.xpath("//span[text()='" + role + "']"), "Select Role from list");
            // finally add the role
            clickScreen(driver, By.cssSelector("button[id$='sp1:rhSrAdd'][title='Add Role Membership'"), "Select Role from list");
        }
        else {
            File file = makeScreenshot(driver, umrRequest.getUserId());
            addError("Problem adding role: " + role + " (screenshot: " + file.getAbsolutePath() + ")");
        }
    }

    public void addError(String error){
        System.err.println(error);
        errors.add(error);

    }
}

