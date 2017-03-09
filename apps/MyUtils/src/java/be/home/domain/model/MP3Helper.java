package be.home.domain.model;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.model.json.AlbumInfo;
import be.home.model.json.MP3Prettifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 23/06/2016.
 */
public class MP3Helper {

    private static MP3Helper mp3Helper = new MP3Helper();
    private static MP3Prettifier mp3Prettifer;
    private static final Logger log = Logger.getLogger(MP3Helper.class);

    private MP3Helper() {
        mp3Prettifer = (MP3Prettifier) JSONUtils.openJSONWithCode(Constants.JSON.MP3PRETTIFIER, MP3Prettifier.class);
        MP3Prettifier.Word word;
        Collections.sort(mp3Prettifer.global.filenames, (a1, b1) -> a1.priority - b1.priority);
        Collections.sort(mp3Prettifer.artist.names, (a1, b1) -> a1.priority - b1.priority);
    }

    public static MP3Helper getInstance() {
        return mp3Helper;
    }

    private static char[] startChars = new char[]{'(', ' ','.', '-', '"', '['};
    private static final String OPEN_BRACKET = "[\\(|\\[]";
    private static final String CLOSE_BRACKET = "[\\)|\\]]";

    public String removeDurationFromString(String text){
        String prettifiedText = text;
        prettifiedText = prettifiedText.replaceAll("\\[[0-9]{1,2}:[0-9][0-9]\\]$", "");
        prettifiedText = prettifiedText.replaceAll("\\([0-9]{1,2}:[0-9][0-9]\\)$", "");
        prettifiedText = prettifiedText.replaceAll("[0-9]{1,2}:[0-9][0-9]$", "");
        return prettifiedText.trim();
    }

    public String replaceSpecialCharacters(String text){
        text = text.replaceAll("’", "'");
        text = text.replaceAll("´", "'");
        text = text.replaceAll("`", "'");
        text = text.replaceAll("''", "\"");
        return text;
    }

    public String prettifyString(String text){
        String prettifiedText = replaceSpecialCharacters(text);
        if (StringUtils.isNotBlank(text)) {
            prettifiedText = WordUtils.capitalizeFully(prettifiedText, startChars);
        }
        return prettifiedText;
    }

    public String replaceString(String text, MP3Prettifier.Word word){
        String oldWord = word.oldWord;
        if (word.parenthesis) {
            oldWord = replaceBetweenBrackets(oldWord);
        }
        if (word.exactMatch){
            oldWord = "^" + oldWord + "$";
        }
        text = text.replaceAll(oldWord, word.newWord);
        return text;
    }

    public String prettifySong(String text){
        String prettifiedText = prettifyString(text);
        if (StringUtils.isNotBlank(text)) {
            prettifiedText = checkWords(prettifiedText, Mp3Tag.TITLE);
            for (MP3Prettifier.Word wordObj : mp3Prettifer.song.replacements) {

                String oldText = prettifiedText;
                prettifiedText = replaceString(oldText, wordObj);
                if (!oldText.equals(prettifiedText)) {
                    logRule("Song Replacements", wordObj);
                }
                prettifiedText = stripSong(prettifiedText);
                prettifiedText = prettifiedText.replace("  ", " ");
                prettifiedText = prettifiedText.trim();
            }
        }
        return prettifiedText;
    }

    public String replaceBetweenBrackets(String text){
        return OPEN_BRACKET + text + CLOSE_BRACKET;

    }

    public String prettifyArtist(String text){
        String prettifiedText = prettifyString(text);
        if (StringUtils.isNotBlank(text)) {
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Remix"), "");
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Black Box Radio Edit"), "");

            prettifiedText = stripSong(prettifiedText);
            prettifiedText = prettifiedText.replace("  ", " ");
            prettifiedText = prettifiedText.trim();
            prettifiedText = checkWords(prettifiedText, Mp3Tag.ARTIST);
            for (MP3Prettifier.Word wordObj : mp3Prettifer.artist.names){
               //prettifiedText = prettifiedText.replaceAll(wordObj.oldWord, wordObj.newWord);
                String oldText = prettifiedText;
                //System.out.println(wordObj.oldWord);
                prettifiedText = replaceString(oldText, wordObj);
                if (!oldText.equals(prettifiedText)){
                    logRule("Artist Names", wordObj);
                }
            }
        }
        return prettifiedText;

    }

    private void logRule(String category, MP3Prettifier.Word word){
        log.info("Rule applied: Category: " + category + " / Old Word: " + word.oldWord + " / New Word: " + word.newWord);
    }

    private enum Mp3Tag {
        ARTIST, TITLE, ALBUM
    }

    private String checkWords(String text, Mp3Tag tag) {
        String newText = "";
        if (StringUtils.isNotBlank(text)) {
            String words[] = text.split(" ");
            for (int i=0; i < words.length; i++){
                String prefix = "";
                String suffix = "";
                if (i > 0){
                    newText += " ";
                }
                String word = words[i];
                // check if word starts with a " or a (
                if (word.matches(("^(\"|\\()(.*)"))){
                    prefix = word.substring(0,1);
                    word = word.substring(1);
                }
                // check if word ends with a " or a (
                if (word.matches(".*(\\)|\")$")){
                    suffix = word.substring(word.length()-1,word.length());
                    word = word.substring(0, word.length()-1);
                }

                for (MP3Prettifier.Word wordObj : mp3Prettifer.global.words){
                    String oldWord = word;
                    word = replaceWord(oldWord, wordObj);
                    if (!oldWord.equals(word)){
                        logRule("Global Words", wordObj);
                    }

                }

                switch (tag){
                    case ARTIST:
                        for (MP3Prettifier.Word wordObj : mp3Prettifer.artist.words){
                            String oldWord = word;
                            word = replaceWord(word, wordObj);
                            if (!oldWord.equals(word)){
                                logRule("Artist Words", wordObj);
                            }
                        }
                        break;
                    case TITLE:
                        break;
                }
                newText += prefix + word + suffix;
            }
        }
        else {
            newText = text;
        }
        return newText;
    }

    private String replaceWord(String word, MP3Prettifier.Word wordObj){
        String returnWord;
        if (wordObj.exactMatch){
            returnWord = replaceExactWord(word, wordObj.oldWord, wordObj.newWord);
        }
        else {
            returnWord = word.replaceAll(wordObj.oldWord, wordObj.newWord);
        }
        return returnWord;
    }

    private String replaceExactWord(String word, String oldword, String newWord){
        String returnWord = word;
        if (StringUtils.isNotBlank(returnWord)){
            if (returnWord.equals(oldword)){
                returnWord = newWord;
            }
        }
        return returnWord;

    }

    private String stripSong(String text){
        String prettifiedText = text;
        if (StringUtils.isNotBlank(prettifiedText)){
            if (prettifiedText.endsWith("*")){
                prettifiedText = prettifiedText.substring(0, prettifiedText.length()-1);
            }
            prettifiedText = prettifiedText.replaceAll("\\([0-9]\\)", "");
        }
        return prettifiedText;
    }

    public void checkTrack(AlbumInfo.Track track){
        // search for (Feat. xxx) or Feat. xxx or Feat xxx or (Feat xxx)

        String FEAT = ".*[\\(|\\[| ][Ff](ea)?t[.| ]";
        String CLOSE_FEAT = "(?!^Edit\\))\\)|\\]";
        if (!StringUtils.endsWith(track.title, "Edit)")) {
            checkTrackPattern(track, FEAT, CLOSE_FEAT);
        }
    }

    private void checkTrackPattern(AlbumInfo.Track track, String startPattern, String endPattern){
        Pattern pattern = Pattern.compile(startPattern + "(.*)"); // + endPattern);
        Matcher matcher = pattern.matcher(track.title);
        if (matcher.matches()) {
            String extraArtist = track.title.replaceAll(startPattern, "").replaceFirst(endPattern, "");
            extraArtist = prettifyArtist(prettifySong(extraArtist));
            track.artist += " Feat. " + extraArtist;
            Pattern p = Pattern.compile("(.*)" + startPattern );
            Matcher m = p.matcher(track.title);
            if (m.find()) {
                String s = m.group(1);
                track.title = s.trim();
            }
        }
    }

    public String prettifyAlbum(String title){
        String prettifiedText = prettifySong(title);
        /*
        if (StringUtils.isNotBlank(prettifiedText)) {
            prettifiedText = prettifyString(prettifiedText) ;
            prettifiedText = prettifiedText.replaceFirst("\\[[Ee]xplicit\\]", "");
            prettifiedText = checkWords(prettifiedText, Mp3Tag.ALBUM);
            prettifiedText = prettifiedText.replaceAll("R'n'b", "R'n'B");


            prettifiedText = prettifiedText.trim();
        }
        */
        return prettifiedText;
    }

    public String formatTrack(AlbumInfo.Config albumInfo, String track) {
        int trackSize = albumInfo.trackSize == 0 ? 2 : albumInfo.trackSize;
        return StringUtils.leftPad(track, trackSize, "0");

    }

    public String stripFilename(String filename) {
        String prettifiedText = filename;
        for (MP3Prettifier.Word wordObj : mp3Prettifer.global.filenames) {
            String oldText = prettifiedText;
            prettifiedText = prettifiedText.replaceAll(wordObj.oldWord, wordObj.newWord);
            if (!oldText.equals(prettifiedText)) {
                logRule("Global Filenames", wordObj);
            }

        }
        return prettifiedText;
    }

}
