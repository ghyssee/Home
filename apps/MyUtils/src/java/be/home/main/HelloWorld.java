package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.ArtistSongItem;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.mezzmo.domain.model.json.Composers;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class HelloWorld extends BatchJobV2 {

    private static final Logger log = getMainLog(HelloWorld.class);
    public static MezzmoServiceImpl mezzmoService = null;

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {


        //processArtistFile();
        //testMP3Prettifier();
        //System.out.println(MP3Helper.getInstance().test("A\\$2AP$2Test$2Test$3Test$4", "\\$2", "\\$3", 2));
        //System.out.println(MP3Helper.getInstance().checkRegExpDollar("$1Text$1", 1));
        //updateMP3();
        //batchProcess();
        //testMP3Prettifier();
        //TestMovieFile();
        //testAlbumArtist();
        //fileNotFound();
        //testJAudioTagger();
        //testStringToDate();
        //testDateToString();
        //convertDates();
        //convertStringToDate();
        //convertStringToDateTime();
        //testAlias();
        testiPod();

    }
    private static void testiPod() {
        // 03/05/2024 17:38
        Date date = SQLiteUtils.convertiPodDateToDate(736443482L);
        System.out.println(date);
    }
    private static void testAlias() {
        HashMap<String, String> map = new HashMap<>();
        addAlias(map, String.class .getName(), "cntCode", "countryCode");
        if (hasAlias(map, String.class .getName(),"cntCode")){
            System.out.println("Alias field: " + getAlias(map, String.class .getName(), "cntCode"));
            System.out.println("Alias field: " + getAlias(map, String.class .getName(), "countryCode"));
        }
    }

    private static boolean hasAlias(HashMap<String, String> map, String className, String key){
        if (map != null){
            return map.containsKey(className + "_" + key);
        }
        return false;
    }

    private static String getAlias(HashMap<String, String> map, String className, String key){
        if (map != null){
            return map.get(className + "_" + key);
        }
        return null;
    }
    private static void addAlias(HashMap<String, String> map, String className, String key, String alias) {
        map.put(className + "_" + key, alias);
        map.put(className + "_" + alias, key);
    }

    private static void convertDates(){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.ZONE_OFFSET);
        cal.set(Calendar.YEAR, 1937);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 02);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("Time Before Conversion: " + cal.getTime());
        Calendar cetDate = convertCETToUTC(cal);
        System.out.println("Time Conversion from UTC to CET: " + cetDate.getTime());
        // we have a calendar in CET timezone, but time is in UTC time
        cetDate = convertFromUTCToCET(cal);
        System.out.println("Time Conversion from CET to UTC: " + cetDate.getTime());

        String strDate = "2023-10-30+01:00";
        Calendar retCal = unmarshalDate(strDate);
        System.out.println(retCal.getTime());


    }

    private static void convertStringToDate(){

        String strDate = "2023-10-30+01:00";
        Calendar retCal = unmarshalDate(strDate);
        System.out.println(retCal.getTime());

        strDate = "2023-10-30";
        retCal = unmarshalDate(strDate);
        System.out.println(retCal.getTime());

        strDate = "2023-10-30Z";
        retCal = unmarshalDate(strDate);
        System.out.println(retCal.getTime());


    }

    private static void convertStringToDateTime(){

        String strDate = "2002-05-30T09:00:00";
        Calendar retCal = unmarshalDateTime(strDate);
        System.out.println(retCal.getTime());

        strDate = "2002-05-30T09:30:10.5";
        retCal = unmarshalDateTime(strDate);
        System.out.println(retCal.getTime());

        strDate = "2002-05-30T09:30:10Z";
        retCal = unmarshalDateTime(strDate);
        System.out.println(retCal.getTime());

        strDate = "2002-05-30T09:30:10+06:00";
        retCal = unmarshalDateTime(strDate);
        System.out.println(retCal.getTime());


    }

    private static Calendar convertCETToUTC(Calendar cal) {
        if (cal != null){
            // cal is in CET Timezone, but we should fill in a calendar in UTC time
            // 01/06/2023 12:00 is actually the UTC Time for ETC Time 01/06/2023 14:00
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, cal.get(Calendar.SECOND));
            return calendar;
            // returns calendar in UTC timezone, but getTime will always return the CET time
            // no matter what time zone you set in the Calendar, the Date object will always be printed with the default system time zone
        }
        return null;
    }

    private static Calendar convertFromUTCToCET(Calendar cal) {
        if (cal != null) {
            // we have for example 12:00 in UTC time, but we want 10:00 in CET time
            String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

            DateFormat df = new SimpleDateFormat(DATE_FORMAT);

            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            TimeZone tz = TimeZone.getDefault();
            formatter.setTimeZone(tz);

            String dateInString = df.format(cal.getTime()); // "06/01/2010 12:00:00M";
            Date date = null;
            try {
                date = formatter.parse(dateInString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // To TimeZone UTC
            SimpleDateFormat sdfUTC = new SimpleDateFormat(DATE_FORMAT);
            TimeZone tzUTC = TimeZone.getTimeZone("UTC");
            sdfUTC.setTimeZone(tzUTC);

            String sdateInUTC = sdfUTC.format(date); // Convert to String first
            Date dateInUTC = null; // Create a new Date object
            Calendar utcCal = null;
            try {
                dateInUTC = formatter.parse(sdateInUTC);
                cal = Calendar.getInstance();
                cal.setTime(dateInUTC);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return cal;
        }
        return null;
    }

    public static Calendar unmarshalDateTimeOld(String value) {
        if (value == null) {
            return null;
        }
        // format: yyyy-mm-ddThh:mi:ss
        LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        Calendar cal = convertLocalDateTimeToCalendar(localDateTime);
        return cal;
        //return (javax.xml.bind.DatatypeConverter.parseDateTime(value));
    }

    public static Calendar unmarshalDateTime(String value) {
        if (value == null) {
            return null;
        }
        // format: yyyy-mm-ddThh:mi:ss
        // or format yyyy-mm-ddx ex. 2023-10-30+01:00"
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Calendar cal = convertLocalDateTimeToCalendar(localDateTime);
            return cal;
        }
        catch ( DateTimeParseException ex) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            // parse input
            TemporalAccessor parsed = formatter.parse(value);
            // get data from the parsed object
            LocalDateTime localDateTime = LocalDateTime.from(parsed);
            ZoneId zone = ZoneId.from(parsed);
            ZonedDateTime restored = localDateTime.atZone(zone);
            Date date = Date.from(restored.toInstant());
            // convert java.util.Date to java.util.Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;

        }
        //return (javax.xml.bind.DatatypeConverter.parseDateTime(value));
    }

    public static Calendar unmarshalDate(String value) {
        if (value == null) {
            return null;
        }
        // format: yyyy-mm-ddThh:mi:ss
        // or format yyyy-mm-ddx ex. 2023-10-30+01:00"
        try {
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
            Calendar cal = convertLocalDateToCalendar(localDate);
            return cal;
        }
        catch ( DateTimeParseException ex) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE;
            // parse input
            TemporalAccessor parsed = formatter.parse(value);
            // get data from the parsed object
            LocalDate localDate = LocalDate.from(parsed);
            ZoneId zone = ZoneId.from(parsed);
            ZonedDateTime restored = localDate.atStartOfDay(zone);
            Date date = Date.from(restored.toInstant());
            // convert java.util.Date to java.util.Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        //return (javax.xml.bind.DatatypeConverter.parseDateTime(value));
    }
    public static Calendar convertLocalDateTimeToCalendar(ZonedDateTime zonedDateTime){
        // 1. get system default zone
        ZoneId zoneId = ZoneId.systemDefault();

        // 2. convert LocalDate to java.util.Date
        Date date = Date.from(zonedDateTime.toInstant());

        // 3. convert java.util.Date to java.util.Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar convertLocalDateTimeToCalendar(LocalDateTime localDateTime){
        // 1. get system default zone
        ZoneId zoneId = ZoneId.systemDefault();

        // 2. convert LocalDate to java.util.Date
        Date date = Date.from(localDateTime.atZone(zoneId).toInstant());

        // 3. convert java.util.Date to java.util.Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar convertLocalDateToCalendar(LocalDate localDate){
        // 1. get system default zone
        ZoneId zoneId = ZoneId.systemDefault();

        // 2. convert LocalDate to java.util.Date
        Date date = Date.from(localDate.atStartOfDay(zoneId).toInstant());

        // 3. convert java.util.Date to java.util.Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    private static void testDateToString(){
        Calendar cal = Calendar.getInstance();
        cal.set(1999, 2, 28);
        cal.setTimeZone(TimeZone.getTimeZone("CET"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("cal.getTimeZone(): " + cal.getTimeZone());
        System.out.println("Time: " + cal.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = dateFormat.format(cal.getTime());
        System.out.println("str: " + str);
        //dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        //String date = dateFormatter.format(cal.getTime()) + "+00:00";

        ZoneId zoneId = ZoneId.of("Europe/Brussels");
        LocalDateTime ldt = LocalDateTime.ofInstant(cal.toInstant(), zoneId);

        ZonedDateTime zdtNow = ZonedDateTime.of(ldt, zoneId);

        //String date = zdtNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String date = zdtNow.format(dtf);

        //System.out.println("dateFormatter: " + date);

/*
        LocalDateTime localDateTime = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        // since LocalDateTime never contain an offset, default offset +00:00 is added
        String formatDateTime = formatter.format(localDateTime);
        System.out.println("formatDateTime: " + formatDateTime);
*/
        LocalDateTime localDateTime = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // since LocalDateTime never contain an offset, default offset +00:00 is added
        String formatDateTime = formatter.format(localDateTime);
        System.out.println("formatDateTime: " + formatDateTime);

        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        // since LocalDateTime never contain an offset, default offset +00:00 is added
        String formatDateTime2 = localDateTime2.format(formatter2);
        System.out.println("formatDateTime2: " + formatDateTime2 + "+00:00");

        /*
        Date date = cal.getTime();
        //Instant instant = date.toInstant();
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
        //OffsetDateTime offsetDateTime = instant.atOffset(zoneOffset);
        OffsetDateTime offsetDateTime = date.toInstant().atOffset(zoneOffset);
        ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Europe/Amsterdam"));
        System.out.println("cet:" + zonedDateTime.toLocalDateTime());

        //DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyyXXX");
        //String offsetDateTimeString2 = offsetDateTime.format(dateTimeFormatter2);
        DateTimeFormatter BASIC_ISO_DATE = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(YEAR, 4)
                .appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(DAY_OF_MONTH, 2)
                .optionalStart()
                .appendOffset("+HHMMss", "+00:00")
                .toFormatter();
        String offsetDateTimeString15 = zonedDateTime.toLocalDateTime().format(BASIC_ISO_DATE);
        System.out.println("Calendar: " + cal.getTime());
        System.out.println("OffsetDateTime: " + offsetDateTimeString15);
        */

    }

private static void TestMovieFile(){
        /*
    try {
        List<MovieTO> listOfMoviesFromCSV = MovieBO.getListOfMoviesFromCSV("C:/My Programs/OneDrive/Movies/emdbV3.csv");
        for (MovieTO movieTO: listOfMoviesFromCSV){
            System.out.println(movieTO.getTitle());
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
*/
}

    private static void processArtistFile(){
        File file = new File(Setup.getInstance().getFullPath(Constants.Path.MAIN_CONFIG) +
                File.separator + "ListOfArtistsToCheck.txt");
        MP3Helper mp3Helper =MP3Helper.getInstance();
        try {
            MyFileWriter newFile = new MyFileWriter(Setup.getInstance().getFullPath(Constants.Path.MAIN_CONFIG) +
                    File.separator + "ArtistResult.txt", MyFileWriter.NO_APPEND);
            List<String> lines = FileUtils.getContents(file);
            for (String line : lines){
                if (StringUtils.isNotBlank(line)){
                    System.out.println(line + " => " + mp3Helper.prettifyArtist(line));
                    newFile.append(line + " => " + mp3Helper.prettifyArtist(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testMP3Prettifier(){


        MP3Helper mp3Helper =MP3Helper.getInstance();


        //System.out.println(getArtistTitleException("Kellis Feat. Too Short", "bossy"));
        //System.out.println(getArtistTitleException("Malvin Big", "Here Comes The Devil"));
        //System.out.println("The Partysquad Feat. Sjaak, Dio, Sef".replaceAll("((Sef|Dio|Sjaak)( ?& ?|, ?| |\\.|$)){3,}", "Dio, Sef & Sjaak"));
        //System.out.println(mp3Helper.prettifyArtist("Ll Cool J Feat. 7 Aurelius"));
        //System.out.println("Fuck It! (I Don't Want You Back)".replaceAll("F(?:uc|\\\\*\\\\*)k It!?(?:\\\\(I Don't Want You Back\\\\))?(.*)",
          //      "Fuck It (I Don't Want You Back)$1"));

        //System.out.println(mp3Helper.stripFilename("B.A.D. (Big Audio Dynamite) - E = MC2"));

        /*System.out.println(mp3Helper.prettifySong("Voulez Vous Coucher Avec Moi Ce Soir? (Lady Marmalade)"));
        System.out.println(mp3Helper.prettifyAlbum("The Flying Dutch 2017 Edition NL", null));
        System.out.println(getTitleArtistException("Lost Frequencies", "Reality"));
        System.out.println(getArtistTitleException("Lost Frequencies", "Reality"));
        System.out.println(mp3Helper.prettifySong("LOL (Radio Mix FT Fast Edit)"));
        System.out.println(mp3Helper.prettifyArtist("Axwell ^ Ingrosso"));
        System.out.println(mp3Helper.prettifyArtist("Person Z, (a"));
        System.out.println(mp3Helper.prettifyArtist("M.A.R.R.S"));
        System.out.println(getTitleArtistException("Tina Turner", "Teach Me Again"));
        */
        //System.out.println(mp3Helper.prettifyArtist("Dorothee Vegas & Like Maarten Feat. Sam Gooris"));
        //System.out.println("Axwell ^ Ingrosso".replaceAll("Axwell \\^ Ingrosso", "test"));
        //System.out.println(getTitleArtistException("Kontra K", "Zwischen Himmel Hlle"));
        //System.out.println(mp3Helper.prettifyArtist("Pink Sweat$"));
        System.out.println(mp3Helper.prettifyArtist("Mo‐Do"));
        //System.out.println(mp3Helper.prettifyArtist("Galantis Feat. Ship Wrek & Pink Sweat$"));
        //System.out.println(mp3Helper.prettifyArtist("Ella Henderson Feat. Roger Sanchez"));
        //System.out.println(mp3Helper.prettifyAlbum("Billboard Year-End Hot 100 singles of 2022", "Various Artists"));
        //System.out.println(getArtistTitleException("Galantis Feat. Ship Wrek & Pink Sweat$", "Only a Fool"));
        //System.out.println(getArtistTitleException("Reflekt ft. Delline Bass", "Need To Feel Loved (Cristoph Remix)"));
        System.out.println(getTitleArtistException("Loud Luxury feat. Brandon", "Body (PBH & Jack Shizzle Remix)"));
        //System.out.println(getTitleArtistException("Taylor Swift", "Message in a bottle"));
        //System.out.println(getArtistTitleException("Dna", "blabla"));
        //System.out.println(getArtistTitleException("Michael Patrick Kelly", "Love Goes On (Live) [aus \"Sing meinen Song, Vol. 7\"]"));

        //updateMP3();

        String tmp = "Odj Team";
        //ConvertArtistSong test = new ConvertArtistSong();
        //MultiArtistConfig.Item item = test.findMultiArtist("Raf Marchesini Feat. D'Amico, Valax Vs. Gabin");
        //System.out.println(mp3Helper.prettifySong(tmp));
        //System.out.println(mp3Helper.prettifyArtist("Willy Sommers Feat. Dorothee Vegas & Like Maarten"));
        //System.out.println(mp3Helper.prettifyArtist("Raf Marchesini Feat. D'Amico, Valax Vs Gavin"));
        //System.out.println(tmp.replaceAll("O\\.?[D|d]\\.?[J|j]\\.? Team", "Bla"));
        //System.out.println(getArtistTitleException("Emeli Sandé", "Read All About It (Part III)"));
        //System.out.println(getTitleArtistException("Eurythmics", "Sweet Dreams"));
        //System.out.println(getTitleArtistException("Tones And I", "Ur So F**kInG cOoL"));
        //System.out.println(mp3Helper.prettifyAlbum("...Baby One More Time", "Britney Spears"));
        //System.out.println(mp3Helper.stripFilename("...Baby One More Time/Britney - test.mp3"));

        //System.out.println(getArtistTitleException("Sarah Brightman", "Time To Say Goodbye (Con Te Partiro) (Sarah's Intimate Version)"));


    }

    private static String getArtistTitleException(String artist, String title){
        ArtistSongItem item =  MP3Helper.getInstance().formatSong(artist, title, true);
        return item.getArtist();
    }

    private static String getTitleArtistException(String artist, String title){
        ArtistSongItem item = MP3Helper.getInstance().formatSong(artist, title, true);
        return item.getSong();
    }

    private static void tmp() throws SAXException, DocumentException, IOException {
        SAXReader reader = new SAXReader();

        reader.setValidation(true);

        // specify the schema to use
        URI uri = new File("c:/reports/contacts.xsd").toURI();

        String file = uri.toString();
        reader.setProperty(
                "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                file
        );
        reader.setFeature("http://xml.org/sax/features/validation", true);
        reader.setFeature("http://apache.org/xml/features/validation/schema", true );
        reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

        // add error handler which turns any errors into XML
        XMLErrorHandler errorHandler = new XMLErrorHandler();
        reader.setErrorHandler( errorHandler );

        //HelloWorld m = new HelloWorld();
        //reader.setEntityResolver(m);

        //reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://example/url/contacts.xsd");
        // parse the document
        Document document = reader.read(new InputSource("C:\\reports\\contacts.xml"));

        // output the errors XML
        XMLWriter writer = new XMLWriter( OutputFormat.createPrettyPrint() );
        writer.write( errorHandler.getErrors() );
    }
    private static void testJAudioTagger() throws IOException {
        Composers composerFile = (Composers) JSONUtils.openJSONWithCode(Constants.JSON.COMPOSERS, Composers.class);


        File file = new File("c:\\My Data\\tmp\\Java\\MP3Processor\\test\\TestCases\\43 Custom Tag That Should Give A Warning.mp3");
        //File file = new File("c:\\My Data\\tmp\\Java\\MP3Processor\\test\\TestCases\\19 GEOB.mp3");
        File newFile = new File("c:\\My Data\\tmp\\Java\\MP3Processor\\test\\new.mp3");
       // TagOptionSingleton.getInstance().setOriginalSavedAfterAdjustingID3v2Padding(false);
       // TagOptionSingleton.getInstance().setRemoveTrailingTerminatorOnWrite(true);
        try {
            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MP3Service mp3File = null;
            try {
                mp3File = new MP3JAudioTaggerServiceImpl(newFile.getAbsolutePath());
                System.out.println("year: " + mp3File.getYear());
                System.out.println("Duration: " + mp3File.getDuration());
                System.out.println(mp3File.getComment());
                System.out.println(mp3File.getArtist());
                System.out.println(mp3File.getUrl());
                //mp3File.setArtist("Kings Of Leon");
                //mp3File.setTitle("On The Fly");
                //mp3File.setCompilation(true);
                //mp3File.setYear("2022");
                //mp3File.setRating(4);
                //mp3File.setDisc("1");
                //mp3File.setTrack("100");
                //mp3File.clearAlbumImage();
                //mp3File.setGenre("Pop");
                //mp3File.setAlbum("Test Album");
                //mp3File.setAlbumArtist("Various ArtistsXXX");
               // mp3File.clearAlbumImage();
                System.out.println(mp3File.getRatingAsString());
                System.out.println(mp3File.getAudioSourceUrl());
                //mp3File.cleanupTags();
                System.out.println(mp3File.getDuration());
                mp3File.analyze();
                mp3File.commit();
            } catch (MP3Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private static void test() throws IOException, NoSuchFieldException, IllegalAccessException, SAXException, ParserConfigurationException, DocumentException {

        SAXParserFactory factory = SAXParserFactory.newInstance();

        SchemaFactory schemaFactory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        URI uri = new File("c:/reports/contacts.xsd").toURI();

            String file = uri.toString();

        SAXParser parser = factory.newSAXParser();

        SAXReader reader = new SAXReader(parser.getXMLReader());
            reader.setProperty(
                    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                    file
            );
        reader.setValidation(true);
            XMLErrorHandler errorHandler = new XMLErrorHandler();
            reader.setErrorHandler( errorHandler );
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setFeature("http://apache.org/xml/features/validation/schema", true );
        reader.read((new InputSource("C:\\reports\\contacts.xml")));
            // output the errors XML
            XMLWriter writer = new XMLWriter( OutputFormat.createPrettyPrint() );
            writer.write( errorHandler.getErrors() );
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

    @Override
    public void run() {

    }
}
