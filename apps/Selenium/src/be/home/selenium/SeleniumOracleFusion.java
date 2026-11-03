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

    public static final String UMR = "https://apps.powerapps.com/play/e/default-1183410f-6cf0-4d82-976e-994c1ce4cfce/a/0c43e546-db86-4872-bb0a-14507d7580a1?tenantId=1183410f-6cf0-4d82-976e-994c1ce4cfce?ItemID=4684";
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

                // sometimes login is not finished yet before going to setup, resulting in the log in screen again
                sleep(driver, 1, "test");
                if (true) {
                    goToSetupAndMaintenance(driver, "Financials", "General Ledger");
                    goToDataAccessForUsers(driver);
                    List<DataAccess> dataAccessList = getExistingDataAcessForUsers(driver, umrRequest);
                    List<DataAccess> filteredList = filterDataAccess(dataAccessList, umrRequest);
                    umrRequest.setDataAccessList(filteredList);
                    if (filteredList.size() > 0){
                        addDataAccessForUsers(driver, umrRequest, rolesFromCSV);
                    }
                    else {
                        log.info("No data access to add!!!");
                    }
                }
                if (false){
                    addPOAgent(driver, umrRequest);
                }
                if (false) {
                    manageBusinessUnits(driver);
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

    public void manageBusinessUnits(WebDriver driver){
        goToSetupAndMaintenance(driver, "Financials", "General Ledger");
        goToSubMenu(driver, "Organization Structures","Manage Business Unit", "Manage Business Units");
        processBusinessUnits(driver);
    }

    public void goToSubMenu(WebDriver driver, String subMenuLink, String subMenuItem, String subMenuTitle){

        clickScreen(driver, By.xpath("//td[text()='" + subMenuLink + "']"), subMenuLink);
        waitForElement(driver, By.xpath("//h1[text()='" + subMenuLink + "']"),  "Menu " + subMenuLink);
        //select(driver, By.cssSelector("select[id$='ATp:soc1::content']"), "All Tasks");
        clickScreen(driver, By.xpath("//a[text()='" + subMenuItem + "']"), "Click on " + subMenuItem);
        waitForElement(driver, By.xpath("//h1[text()='" + subMenuTitle + "']"), "Screen " + subMenuTitle);

    }

    public void scroll(WebDriver driver){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

// De basis-ID van jouw Oracle ADF tabel (zonder ::db)
        String tabelId = "pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1";
        String scrollerid = "pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1::scroller";
        String gezochteTekst = "0BL - RADIAL OMNICHANNEL INTERNATIONAL SL"; // Pas aan naar jouw doel
        boolean elementGevonden = false;

// We zoeken de scroller-div die gekoppeld is aan jouw tabel-ID
        String scrollerId = "pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1::scroller";

        for (int i = 0; i < 30; i++) {
            List<WebElement> opties = driver.findElements(By.xpath("//table[@summary='Business Units']//a[text()='" + gezochteTekst + "']"));

            if (!opties.isEmpty() && opties.get(0).isDisplayed()) {
                opties.get(0).click();
                break;
            }

            // We verhogen de scrollTop van de verborgen scroller-div met 200 pixels per stap
            String scrollScript =
                    "var scroller = document.getElementById('" + scrollerId + "');" +
                            "if (scroller) {" +
                            "    scroller.scrollTop = scroller.scrollTop + 200;" +
                            "    return true;" +
                            "}" +
                            "return false;";

            Boolean scrollerGevonden = (Boolean) js.executeScript(scrollScript);

            if (!scrollerGevonden) {
                // Fallback: probeer de scroll op de ::db div zelf, voor het geval de css-override werkt
                js.executeScript(
                        "var db = document.getElementById('" + tabelId + "::db'); if(db){ db.scrollTop = db.scrollTop + 200; }");
            }

            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
    }

    public void processBusinessUnits(WebDriver driver){
        // pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1::ch::d2::t2
        boolean exit = false;
        String tabelId = "pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1";
        String scrollerId = "pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1::scroller";
        WebElement scrollElement = driver.findElement(By.cssSelector("div[id$='table1::scroller'"));
        scrollerId = scrollElement.getAttribute("id");
        JavascriptExecutor js = (JavascriptExecutor) driver;

        do {
            try {
                List <WebElement> tableBU = driver.findElements(By.cssSelector("table[summary='Business Units'] tbody"));
                // img src
                // inactive : /fscmUI/afr/remote/%7B%22uri%22:%22https:__fa-internal.oracleoutsourcing.com:10663_hcmUI_rr_%22%7D/afr/alta-v1/checkMark_NS_readonly.png
                // active: /fscmUI/afr/remote/%7B%22uri%22:%22https:__fa-internal.oracleoutsourcing.com:10663_hcmUI_rr_%22%7D/afr/alta-v1/checkMark_readonly.png
                // divid pt1:r1:0:rt:1:r2:0:dynamicRegion1:0:AP3:AT1:_ATp:table1
                // a[id$='inputText1'] = Business Unit
                String scrollScript =
                        "var scroller = document.getElementById('" + scrollerId + "');" +
                                "if (scroller) {" +
                                "    scroller.scrollTop = scroller.scrollTop + 200;" +
                                "    return true;" +
                                "}" +
                                "return false;";

                Boolean scrollerGevonden = (Boolean) js.executeScript(scrollScript);

                if (!scrollerGevonden) {
                    // Fallback: probeer de scroll op de ::db div zelf, voor het geval de css-override werkt
                    js.executeScript(
                            "var db = document.getElementById('" + tabelId + "::db'); if(db){ db.scrollTop = db.scrollTop + 200; }");
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}

                /*

                WebElement tableBU = waitForElement(driver, By.cssSelector("table[summary='Business Units'"), "Table Business Units");
                List<WebElement> rowsBU = tableBU.findElements(By.cssSelector("tr"));
                List<WebElement> rowsBU2 = tableBU.findElements(By.cssSelector("tr table"));
                WebElement el = rowsBU.get(rowsBU.size() - 1);
                //JavascriptExecutor executor = (JavascriptExecutor) driver;
                //executor.executeScript("arguments[0].scrollIntoView();", el);
                rowsBU = tableBU.findElements(By.cssSelector("tr"));
                Actions action = new Actions(driver);
                for (WebElement row : rowsBU) {
                    WebElement col = row.findElement(By.cssSelector("td[class^='xwn']"));
                    action.click(col).build().perform();
                    System.out.println(row.getText());
                }

                 */
            }
            catch (StaleElementReferenceException ex){
                log.info("StaleElementReferenceException. Retrying...");
            }


        }
        while (!exit);
    }

    public List<DataAccess> filterDataAccess(List<DataAccess> dataAccessList, UMRRequest umrRequest){
        List<DataAccess> umrDataAccessList = umrRequest.getDataAccessList();
        List<DataAccess> filteredList = new ArrayList<>();
        for (DataAccess umrDataAccess : umrDataAccessList){
            if (findDataAccess(dataAccessList, umrDataAccess)){
                log.info("Data Access already exist: ");
                log.info("role: " + umrDataAccess.getRole());
                log.info("security Context: " + umrDataAccess.getSecurityContext());
                log.info("security Context Value: " + umrDataAccess.getSecurityContextValue());
            }
            else {
                filteredList.add(umrDataAccess);
            }
        }
        return filteredList;
    }

    public boolean findDataAccess(List<DataAccess> dataAccessList, DataAccess dataAccess){

        boolean found = false;
        for (DataAccess item : dataAccessList){
            if (item.getRole().equals(dataAccess.getRole())
                && item.getSecurityContext().equals(dataAccess.getSecurityContext())
                && item.getSecurityContextValue().equals(dataAccess.getSecurityContextValue())) {
                found = true;
                break;
                // same data access
            }
        }
        return found;
    }
    public Name splitName (String name) {
        final Set<String> SURNAME_PREFIXES = new HashSet<>(Arrays.asList(
                "van", "von", "de", "di", "da", "del", "della", "du", "la", "le",
                "lo", "dos", "das", "der", "den", "al", "el", "san", "santa", "st", "mwana"
        ));
        Name nameObject = new Name();
        if (name != null) {
            String cleanedName = name.trim().replaceAll("\\s+", " ");
            String[] tokens = cleanedName.split(" ");


            String[] result = name.split(" ");
            if (result.length == 1) {
                nameObject.setLastName(result[0]);
            }
            else if (result.length == 2) {
                nameObject.setLastName(result[0]);
                nameObject.setFirstName(result[1]);

            } else if (result.length >= 3) {
                int splitIndex = -1;
                for (int i = 0; i < tokens.length-1; i++) {
                    if (SURNAME_PREFIXES.contains(tokens[i].toLowerCase())) {
                        splitIndex = i;
                    }
                    else {
                        // String doesn't start with a suffix
                        break;
                    }
                }
                // If no prefix was found, fall back to splitting at the very last space
                if (splitIndex == -1) {
                    nameObject.setLastName(String.join(" ", Arrays.copyOfRange(tokens, 0, 1)));
                    nameObject.setFirstName(String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length)));
                }
                else {
                    // Reconstruct the first name and last name
                    nameObject.setFirstName(String.join(" ", Arrays.copyOfRange(tokens, splitIndex + 2, tokens.length)));
                    nameObject.setLastName(String.join(" ", Arrays.copyOfRange(tokens, 0, splitIndex + 2)));
                }
            }
        }
        return nameObject;
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
        List<String> dataAccessFromUMR = Arrays.asList("000 - Test", "0CD - Freight 4U");
        UMRRequest umrRequest = new UMRRequest();
        umrRequest.setUserId("ghyssee"); // u619488
        //umrRequest.setUserId("u625808");
        umrRequest.setName("GILHUIS Dick");
        umrRequest.setEmail("Dick.GILHUIS.ext@bnode.com");
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
        // Note: Power apps uses knockout Framework
        UMRRequest umrRequest = new UMRRequest();
        String url = UMR;

        umrRequest.setRequestId(extractItemFromString(url, "^(.*)\\?ItemID=(.*)"));

        boolean exit = false;
        do {
            driver.get(url);
            sleep(driver, 5, "Sleep");
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
                //System.out.println("UMR Role found in csv: " + umrRole.getRole());
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
        if (foundRole == null){
            throw new RuntimeException("Role not found: " + umrRole);
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
        CsvToBean<Object> test = csvUtils.readOpenCSV("m:\\Documenten\\OracleUserManagement\\OracleFusion\\roles.csv", map, new HeaderColumnNameTranslateMappingStrategy<CSVRole>(), CSVRole.class);
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




    public void clickScreen (WebDriver driver, By locator, String comment) {

        clickScreen(driver, locator, 20, CLICK_TYPE.ELEMENT, comment);
    }

    public void clickJavaScript (WebDriver driver, By locator, String comment) {

        boolean exit = false;
        int counter = 0;
        do {
            System.out.println("JavaScript Clicking " + comment + " with Anchor " + locator.toString());
            WebElement element = driver.findElement(locator);
            try {
                // Forceer de klik via JavaScript
                counter++;
                JavascriptExecutor js = (JavascriptExecutor) driver;
                //js.executeScript("arguments[0].click();", element);
                String script =
                        "arguments[0].click();" +
                                "arguments[0].dispatchEvent(new Event('click', { bubbles: true }));" +
                                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));";
                js.executeScript(script, element);
                exit = true;
            }
            catch (StaleElementReferenceException ex) {
                log.info("StaleElementReferenceException: Retrying to click");
            }
        }
        while (!exit && counter < 10);
    }

    public void activateRadioButtonJavaScript (WebDriver driver, By locator, String comment) {

        boolean exit = false;
        int counter = 0;
        do {
            System.out.println("JavaScript Clicking " + comment + " with Anchor " + locator.toString());
            WebElement element = driver.findElement(locator);
            try {
                // Forceer de klik via JavaScript
                counter++;
                JavascriptExecutor js = (JavascriptExecutor) driver;
                //js.executeScript("arguments[0].click();", element);
                String script =
                        "arguments[0].checked = true;" +
                                "arguments[0].dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true }));" +
                                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));";
                js.executeScript(script, element);
                exit = true;
            }
            catch (StaleElementReferenceException ex) {
                log.info("StaleElementReferenceException: Retrying to click");
            }
        }
        while (!exit && counter < 10);
    }

    enum CLICK_TYPE {
        ELEMENT,
        ACTION
    }


    public static String detectJavaScriptFramework(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. Check voor React
        // React gebruikt vaak __REACT_DEVTOOLS_GLOBAL_HOOK__ of specifieke interne attributen op DOM-elementen
        Boolean isReact = (Boolean) js.executeScript(
                "return (window.__REACT_DEVTOOLS_GLOBAL_HOOK__ !== undefined || " +
                        "document.querySelector('[data-reactroot], #root, #app') !== null && " +
                        "Object.keys(document.querySelector('#root') || {}).some(k => k.startsWith('__react')));"
        );
        if (isReact) return "React";

        // 2. Check voor Angular
        // Angular (2+) definieert vaak window.ng of window.getAllAngularTestabilities
        Boolean isAngular = (Boolean) js.executeScript(
                "return (window.ng !== undefined || window.getAllAngularTestabilities !== undefined || " +
                        "document.querySelector('[ng-version]') !== null);"
        );
        if (isAngular) return "Angular";

        // 3. Check voor AngularJS (Oude 1.x versie)
        Boolean isAngularJS = (Boolean) js.executeScript(
                "return (window.angular !== undefined);"
        );
        if (isAngularJS) return "AngularJS";

        // 4. Check voor Vue.js
        // Vue (v2/v3) zet vaak specifieke markers of registreert zich in het window object bij dev-versies
        Boolean isVue = (Boolean) js.executeScript(
                "return (window.__VUE__ !== undefined || " +
                        "document.querySelector('[data-v-nd]') !== null || " +
                        "([...document.querySelectorAll('*')].some(el => el.__vue__ || el.__vue_app__)));"
        );
        if (isVue) return "Vue.js";

        // 5. Check voor jQuery (Geen modern framework, wel veelgebruikte bibliotheek)
        Boolean isJQuery = (Boolean) js.executeScript(
                "return (window.jQuery !== undefined || window.$ !== undefined);"
        );
        if (isJQuery) return "jQuery";

        return "UNKNOWN";
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


    public void goToSetupAndMaintenance(WebDriver driver, String category, String testItem){
        driver.get("https://ejfb.fa.em2.oraclecloud.com/fscmUI/faces/FuseTaskListManagerTop");
        WebElement setupElement = waitForElement(driver, By.xpath("//h1[starts-with(text(),'Setup:')]"),"Check Setup");
        String text = setupElement.getText();
        String setup = text.replaceAll("Setup: {0,3}", "");
        final String FINANCIALS = "Financials";
        final String MANUFACTURING = "Manufacturing and Supply Chain Materials Management";
        final String PROCUREMENT = "Procurement";
        final String ORDERMANAGEMENT = "Order Management";
        if (setup.equalsIgnoreCase(FINANCIALS)){

            if (category.equalsIgnoreCase(FINANCIALS)){
                // everything ok. No need to switch
            }
            else {
                switchToCategory(driver, category, testItem);
            }

        }
        else if (setup.equalsIgnoreCase(MANUFACTURING)){
            if (category.equalsIgnoreCase(MANUFACTURING)){
                // everything ok. No need to switch
            }
            else {
                switchToCategory(driver, category, testItem);
            }
            // switch to Financials


        }
        else if (setup.equalsIgnoreCase(PROCUREMENT)){
            if (category.equalsIgnoreCase(PROCUREMENT)){
                // everything ok. No need to switch
            }
            else {
                switchToCategory(driver, category, testItem);
            }

        }
        else if (setup.equalsIgnoreCase(ORDERMANAGEMENT)){
            if (category.equalsIgnoreCase(ORDERMANAGEMENT)){
                // everything ok. No need to switch
            }
            else {
                switchToCategory(driver, category, testItem);
            }

        }
        else {
            // unknown setup
            log.warn("Unknown setup: " + setup);
            // try to switch to Financials
            switchToCategory(driver, category, testItem);
        }
    }
    public void switchToCategory(WebDriver driver, String category, String testItem){
        clickScreen(driver, By.cssSelector("a[id$='AP1:soc2::drop']"), "Check Setup");
        WebElement toolboxElement = waitForElement(driver, By.cssSelector("ul[id$='AP1:soc2::pop']"), "Setup Toolbox");
        clickScreen(toolboxElement, By.xpath(".//li[text()='" + category + "']"), "Check Setup");
        // wait till Link General Ledger is available
        waitForElement(driver, By.xpath("//td[text()='" + testItem + "']"),  testItem);
    }

    public void goToDataAccessForUsers(WebDriver driver){
        // select all tasks
        clickScreen(driver, By.xpath("//td[text()='Users and Security']"), "Users And Security");
        waitForElement(driver, By.xpath("//h1[text()='Users and Security']"),  "Menu Users and Security");
        //Select dropdown = new Select(driver.findElement(By.cssSelector("select[id$='ATp:soc1::content']")));
        //dropdown.selectByVisibleText("All Tasks");
        select(driver, By.cssSelector("select[id$='ATp:soc1::content']"), "All Tasks");
        clickScreen(driver, By.xpath("//a[text()='Manage Data Access for Users']"), "Click on Manage Data Access for Users");
        waitForElement(driver, By.xpath("//h1[text()='Manage Data Access for Users']"), "Screen Manage Data Access for Users");
    }

    public List<DataAccess> getExistingDataAcessForUsers(WebDriver driver, UMRRequest request){
        initDataAccessForUsers(driver, request);
        List<DataAccess> dataAccessList = getDataAccessInformation(driver);
        for (DataAccess dataAccess : dataAccessList){
            System.out.println("Role: "+ dataAccess.getRole());
            System.out.println("Security Context: "+ dataAccess.getSecurityContext());
            System.out.println("Security Context Value: "+ dataAccess.getSecurityContextValue());
        }
        return dataAccessList;
    }

    public void initDataAccessForUsers(WebDriver driver, UMRRequest request){
        // click on Users with Data Access
        // Note: there are different ids for Radio Buttons with/without Data Access
        // qryId2 - AP2 = with
        // qryId1 - AP1 = without

        // select radio button Users with Data Access
        By locator = By.cssSelector("input[id$='AP2:SearchBy:_1'");
        //By.xpath(".//label[text()='Users with Data Access']");
        By locator2 = By.xpath("//input[@type='radio' and @value='1' and contains(@id,'SearchBy:_1')]");
        boolean selected = false;
        do {
            //clickScreen(driver, locator, "Users with Data Access");
            //activateRadioButtonJavaScript(driver, locator, "Users with Data Access");
            WebElement radioButton = driver.findElement(By.xpath("//input[@type='radio' and @value='1' and contains(@id,'SearchBy:_1')]"));
            Actions actions = new Actions(driver);
            actions.moveToElement(radioButton).click().perform();
            waitForADFBackgroundProcess(driver);
            WebElement element = driver.findElement(locator);
            selected = element.isSelected();
            if (!selected){
                log.info("Radio button Users with Data Access not selected. Retrying...");
            }
        }
        while (!selected);


        // qryId1 Users with Data Access
        // qryId2 Users without Data Access
        //sendKeysADF(driver, By.cssSelector("input[id$='qryId1:value00::content]'][aria-label*='User Name']"), request.getUserId());
        sendKeysADF(driver, By.cssSelector("input[id$='AP2:qryId1:value00::content']"), request.getUserId());
        //sendKeysKeyboardADF(driver, By.cssSelector("input[id$='value00::content']"), request.getUserId());

        // 3. Vind de Shadow Host (de tag vlak BOVEN '#shadow-root')
        // Op basis van je HTML is dit vermoedelijk de span met klasse 'xuu'
        //WebElement shadowHost = driver.findElement(By.cssSelector("input[id$='qryId1:value00::content]'][aria-label*='User Name']"));

        // 4. Haal de shadow root op (dit werkt nu omdat de extensie hem 'open' heeft gemaakt)
        //SearchContext shadowRoot = shadowHost.getShadowRoot();

        // 5. Zoek het invoerveld binnen de shadow root
        //WebElement inputField = shadowRoot.findElement(By.cssSelector("input[id*='::content']"));

        //if (inputField.isDisplayed()) {
         //   System.out.println("Het invoerveld in Firefox is succesvol bereikt!");
         //   inputField.sendKeys("MijnGebruikersnaam");
        //}


        clickScreen(driver, By.cssSelector("button[id$='AP2:qryId1::search']"), 5, CLICK_TYPE.ACTION, "Search Data Access for Users");
        waitForADFBackgroundProcess(driver);
    }

    public List<DataAccess> getDataAccessInformation(WebDriver driver) {
        List<WebElement> tables = driver.findElements(By.cssSelector("table[summary='Manage Data Access for Users']"));
        // returns 2 tables. First is without Data Access, second is with
        WebElement info = tables.get(1);
        List<WebElement> rows = info.findElements(By.cssSelector("tr table"));
        List<DataAccess> dataAccessList = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> rowInfo = row.findElements(By.cssSelector("table tr td"));
            // maybe check table with summary starts with This table contains column headers corresponding to the data body table below
            //WebElement headerTable = info.findElement(By.xpath("//table[starts-with(@summary, 'This table contains column headers')]"));
            List<WebElement> headers = driver.findElements(By.xpath("//table[starts-with(@summary, 'This table contains column headers')]"));
            // returns 5 tables. 2 is without Data Access, 4 is with
            List<WebElement> tableHeaders = headers.get(4).findElements(By.cssSelector("span[class='af_column_label-text'"));
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
                String headerText = getText(header);
                        //searchTextFromElement(header, "td");
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
        clickScreen(driver, By.xpath("//img[contains(@id,'AP2:AT1:_ATp:create::icon')]/ancestor::a"), "Screen Create Data Access for Users");
        waitForElement(driver, By.xpath("//div[text()='Create Data Access for Users']"), "Create Data Access for Users");
        //sendKeysADF(driver, By.cssSelector("input[id$='userNameId::content'"), userId);
        sendKeys(driver, By.cssSelector("input[id$='userNameId::content'"), userId);

        clickScreen(driver, By.xpath("//ul[contains(@id, 'afrautosuggestpopup')]/li[contains(@id, 'userNameId')]"), 10, CLICK_TYPE.ELEMENT, "Select User");
        //sendKeysADF(driver, By.cssSelector("input[id$='roleNameCrId::content'"), role);
        fillADFCombobox(driver, By.cssSelector("input[id$='roleNameCrId::content'"),
                                By.xpath("//ul[contains(@id, 'afrautosuggestpopup')]/li[contains(@id, 'roleNameCrId')]"),
                role, "Select Role");


        //sendKeys(driver, By.cssSelector("input[id$='roleNameCrId::content'"), role);
        //clickScreen(driver, By.xpath("//ul[contains(@id, 'afrautosuggestpopup')]/li[contains(@id, 'roleNameCrId')]"), 10, CLICK_TYPE.ELEMENT, "Select Role");
        select(driver, By.cssSelector("select[id$='soc2::content']"), securityContext);
        //sendKeysADF(driver, By.cssSelector("input[id$='securityContextValueId::content'"), value);
        //sendKeys(driver, By.cssSelector("input[id$='securityContextValueId::content'"), value);
        //clickScreen(driver, By.xpath("//ul[contains(@id, 'afrautosuggestpopup')]/li[contains(@id, 'securityContextValueId')]"), 10, CLICK_TYPE.ELEMENT, "Select Context Value");
        fillADFCombobox(driver, By.cssSelector("input[id$='securityContextValueId::content'"),
                By.xpath("//ul[contains(@id, 'afrautosuggestpopup')]/li[contains(@id, 'securityContextValueId')]"),
                role, "Select Security Context Value");
        // click on Save Button
        boolean exit = false;
        String errorMsg = null;
        By locator = By.cssSelector("button[id$='AT2:cb1']");
        clickScreen(driver, locator, "Save Button");

        do {
            errorMsg = findErrorImage(driver);
            if (StringUtils.isBlank(errorMsg)){
                try {
                    WebElement addButton = driver.findElement(locator);
                    addButton.click();
                    // add button found, try to click it again
                    System.out.println("add button found, try to click it again");
                }
                catch (ElementClickInterceptedException ex) {
                    //another element is obscuring the element to be clicked
                    log.info("ElementClickInterceptedException: Retrying to click");
                }
                catch (ElementNotInteractableException ex) {
                    // No save button found, we can assume everything is saved correctly
                    System.out.println("Save button probably already clicked");
                    exit = true;
                }
                catch (StaleElementReferenceException ex2){
                    log.info("StaleElementReferenceException: Retrying to click");
                }
                catch (NoSuchElementException ex){
                    // No save button found, and no error, we can assume everything is saved correctly
                    System.out.println("No save button found, and no error, we can assume everything is saved correctly");
                    exit = true;
                }
            }
            else {
                // save button clicked but error found
                log.info("Error found while saving Data Access:" + errorMsg);
                exit = true;
            }
        }
        while (!exit);
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
            //File dstFile = makeScreenshot(driver, "DataAccess.SaveButton");
            //log.info("screenshot: " + dstFile.getAbsolutePath());
            log.info("Data Access Saved");
            // no error found, data Access saved
        }
        System.out.println("test");

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

    public void clickScreenCheck(WebDriver driver, By locator, String comment, By checkLocator, String checkComment, String errorMsg){
        int retries = 0;
        do {
            clickScreen(driver, locator, comment);
            try {
                waitForElement(driver, checkLocator, checkComment);
                break;
            }
            catch (TimeoutException ex){
                log.info("Element not found: " + checkLocator.toString() + ". Retrying...");
            }
        }
        while (retries < 5);
        if (retries >= 5){
            throw new RuntimeException(errorMsg);
        }
    }


    public void addRoles(WebDriver driver, UMRRequest umrRequest, List<CSVRole> csvRoles){

        // go to User Roles Screen
        // Tools Menu
        WebElement element;
        int retries = 0;
        do {
            clickScreen(driver, By.cssSelector("a[id^='groupNode_tools']"), "Tools Menu");
            // check if tools menu is clicked. If not, retry
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

        By usersLocator = By.xpath("//div[text()='Users']");

        // Roles Screen
        clickScreenCheck(driver, By.cssSelector("a[id^='ASE_FUSE_SECURITY_CONSOLE']"),
                "Roles Screen", usersLocator,  "Check Users Screen", "Users Menu not loaded");

        // Select Users
        clickScreen(driver, usersLocator, "Select Users Screen");
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
            initAddRole(driver, true);
            for (Role roleToAdd : StrippedUMRRoles){
                log.info("Role to add: " + roleToAdd.getRole());
                addRole(driver, getRoleInCSV(roleToAdd.getRole(), csvRoles).getOracleRole(), umrRequest);
            }
            closeAddRole(driver);
            saveRole(driver);
        }

        else {
            log.info("No roles to add!!!");
            checkStatusUser(driver);
        }

    }

    public boolean getCheckboxStatus(WebDriver driver, By locator, String message){
        WebElement checkbox = waitForElement(driver, locator, message);
        return checkbox.isSelected();
    }

    public void checkStatusUser(WebDriver driver){
        boolean update = false;
        if (!getCheckboxStatus(driver, By.xpath("//input[@type='checkbox' and contains(@id,'sbc512::content')]"), "Checkbox User Status")){
            update = true;
        }
        if (getCheckboxStatus(driver, By.xpath("//input[@type='checkbox' and contains(@id,'sbc1::content')]"), "Checkbox User Lock Status")){
            update = true;
        }
        System.out.println("Update status: " + update);
        if (update) {
            initAddRole(driver, false);
            saveRole(driver);
        }
    }

    public void doCheckbox(WebDriver driver, By locator, boolean enable, String comment){
        boolean exit = false;
        do {
            try {
                WebElement checkbox = waitForElement(driver, locator, "Checkbox");
                if (checkbox.isSelected()) {
                    if (enable) {
                        log.info("Checkbox is enabled");
                    } else {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(checkbox).click().perform();
                        waitForADFBackgroundProcess(driver);
                    }
                } else {
                    if (enable) {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(checkbox).click().perform();
                        waitForADFBackgroundProcess(driver);
                    } else {
                        log.info("Checkbox is disabled");
                    }
                }
                exit = true;
            }
            catch (StaleElementReferenceException ex){
                log.info("StaleElementReferenceException: retrying");
            }
        }
        while (!exit);
    }

    public void initAddRole(WebDriver driver, boolean addRole){
        By selectorAddRole = By.cssSelector("button[id$='sp1:cb1'][title='Add Role']");
        clickScreen(driver, By.cssSelector("button[id$='sp1:cb6'][title='Edit']"), "Click button Edit");
        waitForElement(driver, selectorAddRole, "Add Role Button");
        doCheckbox(driver, By.xpath("//input[@type='checkbox' and contains(@id,'sbc4::content')]"), true, "Activate user");
        doCheckbox(driver, By.xpath("//input[@type='checkbox' and contains(@id,'sbc5::content')]"), false, "Unlock user");
        if (addRole) {
            clickScreen(driver, selectorAddRole, "Click button Add Role");
            waitForElement(driver, By.xpath("//div[starts-with(text(), 'Add Role Membership from Role')]"), "Screen Add Role");
        }
    }
    public void closeAddRole(WebDriver driver) {
        clickScreen(driver, By.cssSelector("button[id$='sp1:rhSrCan'][title='Done']"), "Click button Done");
        waitForElementInvisible(driver, By.className("AFModalGlassPane"), "Element invisible");
    }

    public void saveRole(WebDriver driver){
        System.out.println("We can save it!!");
        clickScreen(driver, By.cssSelector("button[id$='sp1:saveBtn'][title='Save and Close']"), "Click button Save");
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
    public void addPOAgent(WebDriver driver, UMRRequest umrRequest){
        String category = "Procurement Foundation";
        goToSetupAndMaintenance(driver, "Procurement", category);
        initPOAgent(driver, category);
        checkIfPOAgentExist(driver, umrRequest);

    }

    public void initPOAgent(WebDriver driver, String category){
        clickScreen(driver, By.xpath("//td[text()='" + category+"']"), category);
        waitForElement(driver, By.xpath("//h1[text()='" + category + "']"),  "Menu " + category);
        String link = "Manage Procurement Agents";
        clickScreen(driver, By.xpath("//a[text()='" + link + "']"), "Click on " + link);
        waitForElement(driver, By.xpath("//h1[text()='Procurement Agents']"), "Screen " + link);

    }

    public void checkIfPOAgentExist(WebDriver driver, UMRRequest umrRequest){
        Name name = splitName(umrRequest.getName());
        // ojHcmAdvancedSearchBox_procurement-agents-search|input
        //String searchName = name.getLastName() + ", " + name.getFirstName();
        String searchName = "GHYSSENS Eric";
        sendKeys(driver, By.cssSelector("input[id*='SearchBox_procurement-agents-search|input']"), searchName);
        clickScreen(driver, By.xpath("//button[@aria-label='Search']"), "Search Button");
        List<WebElement> rows = new ArrayList<>();
        boolean exit=false;
        boolean create = true;
        do {
            try {
                WebElement resultTable = waitForElementClickable(driver, By.cssSelector("table[data-oj-internal=''][role='application'"), "Agents Search Result");
                rows = resultTable.findElements(By.cssSelector("tr"));
                if (rows.size() >= 2){
                    String text = rows.get(1).getText();
                    if (StringUtils.isBlank(text)){
                        // Selenium was too fast. Result on screen not completed yet
                        log.info("Result empty. Retrying...");
                        makeScreenshot(driver, umrRequest.getRequestId() + "_POAgent.EmptyResult");
                        continue;
                    }
                    if (text.equalsIgnoreCase("No data to display.")){
                        // PO Agent not found
                        create = true;
                    }
                    else {
                        // PO Agent already exist
                        log.info("PO Agent already exist: " + searchName);
                        makeScreenshot(driver, umrRequest.getRequestId() + "_POAgent.AgentExist");
                    }
                }
                else {
                    addError("Error searching for PO Agent with the name " + searchName);
                    makeScreenshot(driver, umrRequest.getRequestId() + "_POAgent.SearchError");
                }
                exit = true;
            }
            catch (StaleElementReferenceException ex) {
                log.info("StaleElementReferenceException. Retry fetching PO Agents...");
            }
        }
        while (!exit);
        if (create) {
            createNewPOAgent(driver, umrRequest, searchName);
        }


        // results should always have at least 2 lines
        // first line is the header
        // second: either contains the name or the text No data to display.
    }

    public void dropDown(WebDriver driver, By inputLocator, By optionLocator, String text){
        sendKeys(driver, inputLocator, text);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // We zoeken naar de 'gridcell' binnen het resultaten-id die de gezochte naam bevat
        WebElement dropdownOption = wait.until(ExpectedConditions.elementToBeClickable(
                optionLocator)
        );

        // 3. Klik op de optie om de selectie te bevestigen
        dropdownOption.click();
    }

    public void dropDownOracleJET(WebDriver driver, By middleContainer, By filterContainer, By optionLocator, String text){

        //By containerLocator = By.id(middleContainer);

        //middleContainer = "//oj-select-single[@id='procurement-agent-procurement-BU']//div[contains(@class, 'oj-text-field-middle')]";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // 1. Klik op het tekstgedeelte van de specifieke select-box
        WebElement middleContainerElement = wait.until(ExpectedConditions.elementToBeClickable(middleContainer));
        middleContainerElement.click();

        // 2. Wacht tot het filter-inputveld verschijnt en typ de tekst
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(filterContainer));
        // By.id("oj-searchselect-filter-procurement-agent-procurement-BU|input")
        inputField.clear();
        inputField.sendKeys(text + Keys.ENTER);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // We zoeken naar de 'gridcell' binnen het resultaten-id die de gezochte naam bevat
        WebElement dropdownOption = wait.until(ExpectedConditions.elementToBeClickable(
                optionLocator)
        );

        // 3. Klik op de optie om de selectie te bevestigen
        dropdownOption.click();
    }

    public void dropDownLOV(WebDriver driver, String middleContainer, String lovDropDown, String text){

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement arrow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//oj-input-text[@id='oj-searchselect-filter-procurement-agents-status']//a")));

        // Beweeg de muis naar de pijl en klik erop
        Actions actions = new Actions(driver);
        actions.moveToElement(arrow).click().perform();


        By middleContainerLoc = By.xpath(middleContainer + "//div[contains(@class, 'oj-text-field-middle')]");
        By lovDropDownLoc = By.xpath(lovDropDown + "//span[text()='" + text + "']");
        WebElement middleContainerElement = wait.until(ExpectedConditions.presenceOfElementLocated(middleContainerLoc));
        middleContainerElement.click();

        WebElement activeOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                lovDropDownLoc
        ));
        activeOption.click();
    }
    public void checkbox (WebDriver driver, By locator){

            // Zoek het element op basis van het ID
            WebElement checkbox = driver.findElement(locator);

            // Controleer of de checkbox niet al is aangevinkt (optioneel)
            // Let op: bij aangepaste componenten moet je soms de 'aria-checked' attribuut controleren
            String isChecked = checkbox.findElement(By.tagName("input")).getAttribute("aria-checked");

            if (!"true".equalsIgnoreCase(isChecked)) {
                checkbox.click();
                System.out.println("Checkbox is succesvol aangevinkt.");
            }
    }

    public void createNewPOAgent(WebDriver driver, UMRRequest umrRequest, String searchName){
        String label="Create Procurement Agent";
        searchName = "GHYSSENS, Eric";
        clickScreen(driver, By.cssSelector("button[aria-label='" + label + "']"), "Button " + label);
        waitForElement(driver, By.cssSelector("div[title='New procurement agent']"), "Title New procurement agent");
        clickScreen(driver, By.cssSelector("oj-select-single[id='oj-selectsingle-1']"), "Dropdown PO Agent");
        // Oracle JET combobox element
        dropDown(driver, By.cssSelector("input[id='oj-searchselect-filter-oj-selectsingle-1|input']"),
                By.xpath("//oj-list-view[@id='oj-searchselect-results-oj-selectsingle-1']//div[@role='gridcell' and contains(., '" + searchName + "')]"),
                searchName);
        searchName = "002 - bpost";

    dropDownOracleJET(driver,
            By.xpath("//oj-select-single[@id='procurement-agent-procurement-BU']//div[contains(@class, 'oj-text-field-middle')]"),
            By.id("oj-searchselect-filter-procurement-agent-procurement-BU|input"),
            By.xpath("//oj-list-view[@id='oj-searchselect-results-procurement-agent-procurement-BU']//div[@role='gridcell' and contains(., '" + searchName + "')]"),
            searchName);

    dropDown(driver,
            By.cssSelector("input[id='oj-selectsingle-2|input']"),
            By.xpath("//oj-list-view[@id='oj-searchselect-results-oj-selectsingle-2']//div[@role='gridcell' and contains(., '" + searchName + "')]"),
            searchName);

        searchName = "Active";
    //dropDown(driver,
    //            By.cssSelector("input[id='procurement-agents-status|input']"),
    //            By.xpath("//oj-list-view[@id='oj-searchselect-results-procurement-agents-status']//div[@role='gridcell' and contains(., '" + searchName + "')]"),
    //            searchName);
        dropDownLOV(driver,
                //By.xpath("//div[@id='lovDropdown_procurement-agents-status']//span[text()='Active"),
                "//oj-input-text[@id='oj-searchselect-filter-procurement-agents-status']",
                "//div[@id='lovDropdown_procurement-agents-status']",
                searchName);

    checkbox(driver, By.id("manage-requisition-cbox"));


}
    public void addError(String error){
        errors.add(error);

    }
}

