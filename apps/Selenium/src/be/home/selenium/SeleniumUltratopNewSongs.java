package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.model.json.AlbumInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;


import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumUltratopNewSongs extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumUltratopNewSongs.class);

    public static void main(String[] args) {

        SeleniumUltratopNewSongs instance = new SeleniumUltratopNewSongs();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uMMdd", Locale.ENGLISH);

        DateTimeFormatter yearF = DateTimeFormatter.ofPattern("u", Locale.ENGLISH);

        WebDriver driver = initDriver();

        LocalDate localDate = getLastSaturday();

        String url = localDate.format(yearF) + "/" + localDate.format(dtf);

        driver.get("https://www.ultratop.be/nl/ultratop50/" + url);

        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        printAlbumInfo(log, configAlbum);

        driver.quit();
        //writeAlbumConfiguration(configAlbum);

    }

    public void printAlbumInfo(Logger log, AlbumInfo.Config configAlbum) {
        System.out.println(configAlbum.getAlbum());
        System.out.println("-".repeat(configAlbum.getAlbum().length()));
        StringUtils st;
        for (AlbumInfo.Track track : configAlbum.tracks){
            System.out.println(StringUtils.leftPad(track.getStatus(), 2)  + " " +
                               StringUtils.leftPad(track.getTrack(), 2, '0') + " " +
                               track.getArtist() + " - " + track.getTitle());
        }

    }

    public LocalDate getLastSaturday()
    {
        //LocalDate localDate = LocalDate.parse(fromDate, dtf);

        LocalDate localDate = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));

        return localDate;

    }
    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {
        WebElement element = driver.findElement(By.xpath("//div[starts-with(@class,'heading')]"));

        String text = element.getText();
        // replace new line with space
        text = text.replaceAll("[\\t\\n\\r]+"," ");

        // get the selected date
        WebElement dateElement = element.findElement(By.xpath("//select[starts-with(@id,'chartdate')]"));
        Select select = new Select(dateElement);
        String strDate = select.getFirstSelectedOption().getText();
        configAlbum.setAlbum(text + " " + strDate);
    }


    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig){

        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='content chartitem']"));
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();
        Pattern artistPattern = Pattern.compile("<b>(.*)<\\/b>", Pattern.CASE_INSENSITIVE);
        Pattern titlePattern = Pattern.compile("<br>(.*)", Pattern.CASE_INSENSITIVE);

        // find artist + song title
        for (WebElement element : elements){
            //WebElement track = element.findElement(By.xpath(".//div[@class='chart_title']"));
            WebElement track = element.findElement(By.xpath(".//a[starts-with(@href,'/nl/song')]"));
            String innerHTML = track.getAttribute("innerHTML");
            // ex: <b>Cameron Whitcomb</b><br>Kingdom Of Fear
            // artist is between <b> </b> tags
            // title if after <br> tag
            String artist = findPattern(artistPattern, innerHTML);
            String title = findPattern(titlePattern, innerHTML);
            String status = getStatus(element);
            if (status != null){
                AlbumInfo.Track trackRec = new AlbumInfo().new Track();
                // find track number
                WebElement trackNumberElement = element.findElement(By.xpath(".//div[@class='chart_pos']"));
                trackRec.setTrack(trackNumberElement.getText());
                StringEscapeUtils st;
                trackRec.setArtist((StringEscapeUtils.unescapeHtml4(artist)));
                trackRec.setTitle(StringEscapeUtils.unescapeHtml4(title));
                trackRec.setStatus(StringEscapeUtils.unescapeHtml4(status));
                tracks.add(trackRec);
            }
        }
        albumConfig.setTracks(tracks);
    }

    String getStatus(WebElement track){
        String status = null;
        try {
            WebElement statusOfTrack = track.findElement(By.xpath(".//div[@class='chart_neu_re']"));
            status = statusOfTrack.getText();
        }
        catch (NoSuchElementException ex){
            status = null;
        }

        return status;
    }


    String findPattern(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()){
            return matcher.group(1);
        }
        return null;


    }

    public enum SONG_TYPE {

        TRACK, ARTIST_TITLE, AUDIO, LENGTH_TRACK
    }

}