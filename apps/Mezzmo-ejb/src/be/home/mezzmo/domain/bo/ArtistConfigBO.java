package be.home.mezzmo.domain.bo;

        import be.home.common.constants.Constants;
        import be.home.common.exceptions.ApplicationException;
        import be.home.common.utils.JSONUtils;
        import be.home.mezzmo.domain.model.json.Artists;
        import be.home.mezzmo.domain.model.json.MP3Prettifier;
        import be.home.mezzmo.domain.model.json.MultiArtistConfig;
        import org.apache.commons.lang3.StringUtils;
        import org.apache.log4j.Logger;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;
        import java.util.stream.Collectors;

public class ArtistConfigBO {

    private static ArtistConfigBO artistConfigBO = new ArtistConfigBO();
    private static Artists artists;
    private static MultiArtistConfig multiArtistConfig;
    private static final Logger log = Logger.getLogger(ArtistConfigBO.class);
    private static Map<String, Artists.Artist> mapArtists;
    private static Map<String, MultiArtistConfig.Splitter> mapSplitters;

    private ArtistConfigBO() {
        artists = (Artists) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTS, Artists.class);
        mapArtists =
                artists.list.stream().collect(Collectors.toMap(Artists.Artist::getId, artist -> artist));
        multiArtistConfig = (MultiArtistConfig) JSONUtils.openJSONWithCode(Constants.JSON.MULTIARTISTCONFiG, MultiArtistConfig.class);
        mapSplitters =
                multiArtistConfig.splitters.stream().collect(Collectors.toMap(MultiArtistConfig.Splitter::getId, c -> c));
    }

    public static ArtistConfigBO getInstance() {
        return artistConfigBO;
    }

    public Map<String, Artists.Artist> getArtistNames(){
        return mapArtists;
    }

    public List<MP3Prettifier.Word> construct(){
        List<MP3Prettifier.Word> names = new ArrayList<>();

        // construct the splitters
        String splitterText = "(";
        for (MultiArtistConfig.Splitter splitter : multiArtistConfig.splitters){
            if (StringUtils.isNotBlank(splitter.value1)) {
                String separator = splitter.equals(multiArtistConfig.splitters.get(0)) ? "" : "|";
                splitterText += separator + splitter.value1;
            }
        }
        splitterText += "|$)";

        for (MultiArtistConfig.Item item : multiArtistConfig.list){
            MP3Prettifier.Word word = new MP3Prettifier().new Word();
            word.oldWord = "";
            word.oldWord += "(";
            // construct artist Search String
            String artistSearch = "(";
            for (MultiArtistConfig.Item.Artist artist : item.artists){
                Artists.Artist artistObj = getArtist(artist.id);
                if (artistObj == null){
                    throw new ApplicationException(("Artist Id not found in config:" + artist.id));
                }
                String separator = artist.equals(item.artists.get(0)) ? "" : "|";
                artistSearch += separator + artistObj.name;
            }
            artistSearch += ")";
            word.oldWord = "(" + artistSearch + splitterText + "){" + item.artists.size() + "}";
            // construct new Artist Name
            word.newWord = "";
            for (MultiArtistConfig.Item.ArtistSequenceItem artistSequenceItem : item.artistSequence){

                Artists.Artist artistObj = getArtist(artistSequenceItem.artistId);
                if (artistObj == null){
                    throw new ApplicationException(("Artist Id not found in config:" + artistSequenceItem.artistId));
                }

                MultiArtistConfig.Splitter splitterObj = getSplitter(artistSequenceItem.splitterId);
                if (splitterObj == null){
                    throw new ApplicationException(("Splitter Id not found in config:" + artistSequenceItem.splitterId));
                }
                word.newWord += artistObj.name + splitterObj.value2;
            }

            //String tst = "Bodyrox & Luciana".replaceAll(word.oldWord, word.newWord);
            log.info("Artist Name Old Word: " + word.oldWord);
            log.info("Artist Name New Word: " + word.newWord);
            names.add(word);
        }
        return names;
    }

    public Artists.Artist getArtist(String id){
        Artists.Artist artist = mapArtists.get(id);
        return artist;
    }


    public MultiArtistConfig.Splitter getSplitter(String id){
        MultiArtistConfig.Splitter splitter = mapSplitters.get(id);
        if (splitter != null){
            splitter.value2 = splitter.id.equals(multiArtistConfig.splitterEndId) ? "": splitter.value2;
        }
        return splitter;
    }
}

