package be.home.mezzmo.domain.bo;

        import be.home.common.constants.Constants;
        import be.home.common.exceptions.ApplicationException;
        import be.home.common.utils.JSONUtils;
        import be.home.mezzmo.domain.enums.MultiArtistMaster;
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
            if (!item.exactPosition) {
                // construct artist Search String
                int size = getMultiArtistMasterSize(item);
                word.oldWord = constructArtistSearch(item.artists, splitterText, size);
            }
            else {
                word.oldWord = constructArtistSearchExact(item.artists, splitterText);
            }
            // construct new Artist Name
            word.newWord = constructNewArtistName(item.artistSequence);

            //String tst = "Bodyrox & Luciana".replaceAll(word.oldWord, word.newWord);
            log.info("Artist Name Old Word: " + word.oldWord);
            log.info("Artist Name New Word: " + word.newWord);
            names.add(word);
        }
        return names;
    }

    private int getMultiArtistMasterSize(MultiArtistConfig.Item item){
        MultiArtistMaster multiArtistMaster;
        int size = item.artistSequence.size();
        try {
            multiArtistMaster = MultiArtistMaster.valueOf(item.master);
        }
        catch (IllegalArgumentException e){
            multiArtistMaster = MultiArtistMaster.artistSequence;
        }
        catch (NullPointerException e){
            multiArtistMaster = MultiArtistMaster.artistSequence;
        }
        switch (multiArtistMaster){
            case artists :
                size = item.artists.size();
                break;
            case artistSequence :
                size = item.artistSequence.size();
                break;
        }
        return size;
    }

    private String constructArtistSearchExact(List<MultiArtistConfig.Item.Artist> list, String splitterText){
        //((Person A)( Feat\. | ?& ?|, ?| |$)(Person B))
        String artistSearch = "";
        for (MultiArtistConfig.Item.Artist artist : list){
            artistSearch += "(";
            boolean lastItem = list.indexOf(artist) == list.size()-1;
            Artists.Artist artistObj = getArtist(artist.id);
            if (artistObj == null){
                throw new ApplicationException(("Artist Id not found in config:" + artist.id));
            }
            String separator = lastItem ? "" : splitterText;
            artistSearch += artistObj.name + ")" + separator;
        }
        return artistSearch;
    }

    private String constructArtistSearch(List<MultiArtistConfig.Item.Artist> list, String splitterText, int size){
        String artistSearch = "(";
        for (MultiArtistConfig.Item.Artist artist : list){
            Artists.Artist artistObj = getArtist(artist.id);
            if (artistObj == null){
                throw new ApplicationException(("Artist Id not found in config:" + artist.id));
            }
            String separator = artist.equals(list.get(0)) ? "" : "|";
            artistSearch += separator + artistObj.name;
        }
        artistSearch += ")";
        String word = "(" + artistSearch + splitterText + "){" + size + "}";
        return word;
    }

    private String constructNewArtistName(List<MultiArtistConfig.Item.ArtistSequenceItem> list){
        String word = "";
        for (MultiArtistConfig.Item.ArtistSequenceItem artistSequenceItem : list){

            Artists.Artist artistObj = getArtist(artistSequenceItem.artistId);
            if (artistObj == null){
                throw new ApplicationException(("Artist Id not found in config:" + artistSequenceItem.artistId));
            }

            MultiArtistConfig.Splitter splitterObj = getSplitter(artistSequenceItem.splitterId);
            if (splitterObj == null){
                throw new ApplicationException(("Splitter Id not found in config:" + artistSequenceItem.splitterId));
            }
            word += artistObj.name + splitterObj.value2;
        }
        return word;
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

