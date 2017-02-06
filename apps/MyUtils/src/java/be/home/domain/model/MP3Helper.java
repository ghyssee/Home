package be.home.domain.model;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.model.json.AlbumInfo;
import be.home.model.json.MP3Prettifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 23/06/2016.
 */
public class MP3Helper {

    private static MP3Helper mp3Helper = new MP3Helper();
    private static MP3Prettifier mp3Prettifer;

    private MP3Helper() {
        mp3Prettifer = (MP3Prettifier) JSONUtils.openJSONWithCode(Constants.JSON.MP3PRETTIFIER, MP3Prettifier.class);
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
        if (StringUtils.isNotBlank(text)){
            for (MP3Prettifier.Word wordObj : mp3Prettifer.song.replacements){

                prettifiedText = replaceString(prettifiedText, wordObj);
                /*
                if (wordObj.parenthesis) {
                    prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets(wordObj.oldWord), wordObj.newWord);
                }
                else {
                    prettifiedText = prettifiedText.replaceAll(wordObj.oldWord, wordObj.newWord);
                }
                */
            }
            //prettifiedText = prettifiedText.replaceAll("\\[]", "(");
            //prettifiedText = prettifiedText.replaceAll("\\]]", ")");
            /*
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Album Version"), "");
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Radio Edit"), "");
            prettifiedText = prettifiedText.replace("(Radio Mix)", "");
            prettifiedText = prettifiedText.replace("(Vocal Radio Edit)", "");
            prettifiedText = prettifiedText.replace("(Vocal Radio Cut)", "");
            prettifiedText = prettifiedText.replace("(Video Edit)", "");
            prettifiedText = prettifiedText.replace("(Video Version)", "");
            prettifiedText = prettifiedText.replace("(Single Cut)", "");
            prettifiedText = prettifiedText.replace("(Single Edit)", "");
            prettifiedText = prettifiedText.replace("(Single Version)", "");
            prettifiedText = prettifiedText.replace("(Original Radio Cut)", "");
            prettifiedText = prettifiedText.replace("(Radio Version / Original)", "");
            prettifiedText = prettifiedText.replace("(Original)", "");
            prettifiedText = prettifiedText.replaceAll("\\[[eE]xplicit\\]", "");
            prettifiedText = prettifiedText.replace("(Uk Radio Version)", "");
            */

            prettifiedText = stripSong(prettifiedText);
            prettifiedText = prettifiedText.replace("  ", " ");
            prettifiedText = prettifiedText.trim();
            prettifiedText = checkWords(prettifiedText, Mp3Tag.TITLE);
        }
        return prettifiedText;
    }

    public String replaceBetweenBrackets(String text){
        return OPEN_BRACKET + text + CLOSE_BRACKET;

    }

    public String prettifyArtist(String text){
        String prettifiedText = prettifyString(text);
        if (StringUtils.isNotBlank(text)) {
            /*
            prettifiedText = prettifiedText.replace("Fpi Project", "FPI Project");
            prettifiedText = prettifiedText.replace("Tourist Lemc", "Tourist LeMC");
            prettifiedText = prettifiedText.replace("Bart Kaell", "Bart Kaëll");
            prettifiedText = prettifiedText.replace("Rene Froger", "René Froger");
            */

            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Remix"), "");
            prettifiedText = prettifiedText.replaceAll(replaceBetweenBrackets("Black Box Radio Edit"), "");

            prettifiedText = stripSong(prettifiedText);
            prettifiedText = prettifiedText.replace("  ", " ");
            prettifiedText = prettifiedText.trim();
            prettifiedText = checkWords(prettifiedText, Mp3Tag.ARTIST);
            for (MP3Prettifier.Word wordObj : mp3Prettifer.artist.names){
               //prettifiedText = prettifiedText.replaceAll(wordObj.oldWord, wordObj.newWord);
                prettifiedText = replaceString(prettifiedText, wordObj);
            }
        }
        return prettifiedText;

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
                    word = replaceWord(word, wordObj);
                }

                /*
                word = replaceWord(word, "Vs", "Vs.");
                word = replaceWord(word, "Ft", "Feat.");
                word = replaceWord(word, "Ft.", "Feat.");
                word = replaceWord(word, "Feat", "Feat.");
                word = replaceWord(word, "Dj", "DJ");
                word = replaceWord(word, "Ii", "II");
                word = replaceWord(word, "ii", "III");
                word = replaceWord(word, "Pm", "PM");
                word = replaceWord(word, "Dcup", "DCup");
                word = replaceWord(word, "Deus", "dEUS");
                word = replaceWord(word, "Deville", "DeVille");
                word = replaceWord(word, "Pres.", "Presents");
                word = replaceWord(word, "Atb", "ATB");
                word = replaceWord(word, "Mc's", "MC's");
                word = replaceWord(word, "Mk", "MK");
                word = replaceWord(word, "Mcs", "MC's");
                word = replaceWord(word, "Mc", "MC");
                word = replaceWord(word, "Sq-1", "SQ-1");
                word = replaceWord(word, "Dnce", "DNCE");
                word = replaceWord(word, "In-grid", "In-Grid");
                word = replaceWord(word, "Rns", "RNS");
                word = replaceWord(word, "P!Nk", "P!nk");
                word = replaceWord(word, "Tp4y", "TP4Y");
                word = replaceWord(word, "Omi", "OMI");
                word = replaceWord(word, "Tjr", "TJR");
                word = replaceWord(word, "Featuring", "Feat.");
                word = replaceWord(word, "Clmd", "CLMD");
                word = replaceWord(word, "Dna", "DNA");
                word = replaceWord(word, "3lw", "3LW");
                word = replaceWord(word, "Tlc", "TLC");
                word = replaceWord(word, "Lp", "LP");
                word = replaceWord(word, "Kt", "KT");
                word = replaceWord(word, "O'connor", "O'Connor");
                word = replaceWord(word, "Klubbb3", "KLUBBB3");
                word = replaceWord(word, "Dvbbs", "DVBBS");
                word = replaceWord(word, "Will.I.Am", "Will.i.am");
                word = replaceWord(word, "Onerepublic", "OneRepublic");
                word = replaceWord(word, "Lmfao", "LMFAO");
                word = replaceWord(word, "Chef'special", "Chef'Special");
                word = replaceWord(word, "Tiesto", "Tiësto");
                word = replaceWord(word, "Kshmr", "KSHMR");
                word = replaceWord(word, "W&w", "W&W");
                word = replaceWord(word, "Kvr", "KVR");
                word = replaceWord(word, "Mtv", "MTV");
                */

                switch (tag){
                    case ARTIST:
                        for (MP3Prettifier.Word wordObj : mp3Prettifer.artist.words){
                            word = replaceWord(word, wordObj);
                        }
                        /*
                        word = replaceWord(word, "And", "&");
                        word = replaceWord(word, "Lvndscape", "LVNDSCAPE");
                        word = replaceWord(word, "Redone", "RedOne");
                        */

                        break;
                    case TITLE:
                        break;
                }



                // 19eighty7 => 19Eighty7
                // (17) => remove



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

    private String replaceRegExWord(String word, String regExp, String newWord){
        String returnWord = word;
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
        //FEAT = ".*[\\(|\\[| ][Ff]t[.| ]";
        //CLOSE_FEAT = "";
        //checkTrackPattern(track, FEAT, CLOSE_FEAT);
        /*
        Pattern pattern = Pattern.compile(FEAT + "(.*)" + CLOSE_FEAT);
        Matcher matcher = pattern.matcher(track.title);
        if (matcher.matches()) {
            String extraArtist = track.title.replaceAll(FEAT, "").replaceFirst(CLOSE_FEAT, "");
            extraArtist = prettifyArtist(prettifySong(extraArtist));
            track.artist += " Feat. " + extraArtist;
            Pattern p = Pattern.compile("(.*)" + FEAT );
            Matcher m = p.matcher(track.title);
            if (m.find()) {
                String s = m.group(1);
                track.title = s.trim();
            }
        }*/
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
        String prettifiedText = title;
        if (StringUtils.isNotBlank(prettifiedText)) {
            prettifiedText = prettifyString(prettifiedText) ;
                    //WordUtils.capitalizeFully(prettifiedText, startChars);
            prettifiedText = prettifiedText.replaceFirst("\\[[Ee]xplicit\\]", "");
            prettifiedText = checkWords(prettifiedText, Mp3Tag.ALBUM);
            prettifiedText = prettifiedText.replaceAll("R'n'b", "R'n'B");


            prettifiedText = prettifiedText.trim();
        }
        return prettifiedText;
    }

    public String formatTrack(AlbumInfo.Config albumInfo, String track) {
        int trackSize = albumInfo.trackSize == 0 ? 2 : albumInfo.trackSize;
        return StringUtils.leftPad(track, trackSize, "0");

    }

}
