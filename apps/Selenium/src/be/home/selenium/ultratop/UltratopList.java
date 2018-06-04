package be.home.selenium.ultratop;

import be.home.common.model.UltratopConfigBO;
import be.home.common.utils.DateUtils;
import be.home.model.M3uTO;
import be.home.selenium.common.FirefoxDriverSetup;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UltratopList {

    private static final Logger log = Logger.getLogger(UltratopList.class);

    public List<M3uTO> start(Date strDate) throws IOException {
        String computerName = be.home.common.utils.NetUtils.getHostName();
        log.info("Computer Name: " + computerName);
        String instanceId = "1";
        FirefoxDriverSetup setup = new FirefoxDriverSetup(computerName, instanceId);
        FirefoxDriver driver = setup.setupWebDriver();
        driver.manage().window().maximize();
        List<M3uTO> list = UltratopList(driver, strDate);
        setup.closeWebDriver(driver);
        return list;
    }

    public List<M3uTO> UltratopList(RemoteWebDriver driver, Date date) {
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        String url = "";
        String strDate = "";
        if (date == null){
            url = "http://www.ultratop.be/nl/ultratop50";
            date = new Date();
            strDate = UltratopConfigBO.getFormattedDate(date);
        }
        else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            strDate = UltratopConfigBO.getFormattedDate(date);
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
        return list;
    }

    private String getSeleniumText(WebElement element){
        String txt = element.getAttribute("textContent");
        txt = txt.replaceAll("\\r|\\n|\\t", "").trim();
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
}