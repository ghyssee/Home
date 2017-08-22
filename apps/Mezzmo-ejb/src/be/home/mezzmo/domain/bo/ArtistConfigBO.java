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
        import java.util.Collections;
        import java.util.List;
        import java.util.Map;
        import java.util.function.Predicate;
        import java.util.stream.Collectors;

public class ArtistConfigBO {

    private static ArtistConfigBO artistConfigBO = new ArtistConfigBO();
    private static MultiArtistConfig multiArtistConfig;
    private static final Logger log = Logger.getLogger(ArtistConfigBO.class);
    private static Map<String, MultiArtistConfig.Splitter> mapSplitters;
    private static Map<String, MultiArtistConfig.Item> mapItems;
    private static String splitter;
    private static ArtistBO artistBO;
    private static boolean LOG_ARTIST_CONFIG = false;

    private ArtistConfigBO() {
        artistBO = ArtistBO.getInstance();
        multiArtistConfig = (MultiArtistConfig) JSONUtils.openJSONWithCode(Constants.JSON.MULTIARTISTCONFiG, MultiArtistConfig.class);
        Collections.sort(multiArtistConfig.list, (a1, b1) -> b1.artists.size() - a1.artists.size());
        mapSplitters =
                multiArtistConfig.splitters.stream().collect(Collectors.toMap(MultiArtistConfig.Splitter::getId, c -> c));
        mapItems =
                multiArtistConfig.list.stream().collect(Collectors.toMap(MultiArtistConfig.Item::getId, c -> c));
        splitter = constructSplitter();
    }

    public static ArtistConfigBO getInstance() {
        return artistConfigBO;
    }

    public boolean isArtist(String artistName){
        Artists.Artist artist = artistBO.findArtistByName(artistName);
        return (artist != null);
    }

    public String getSplitterString(){
        return splitter;
    }

    public MP3Prettifier.Word constructItem(MultiArtistConfig.Item item){
        MP3Prettifier.Word word = new MP3Prettifier().new Word();
        word.oldWord = "";
        word.oldWord += "(";
        if (!item.exactPosition) {
            // construct artist Search String
            int size = getMultiArtistMasterSize(item);
            word.oldWord = constructArtistSearch(item.artists, this.splitter, size);
        }
        else {
            word.oldWord = constructArtistSearchExact(item.artists, this.splitter);
        }
        // construct new Artist Name
        word.newWord = constructNewArtistName(item.artistSequence);

        //String tst = "Bodyrox & Luciana".replaceAll(word.oldWord, word.newWord);
        return word;
    }

    private String constructSplitter(){
        // construct the splitters
        String splitterText = "(";
        for (MultiArtistConfig.Splitter splitter : multiArtistConfig.splitters){
            if (StringUtils.isNotBlank(splitter.value1)) {
                String separator = splitter.equals(multiArtistConfig.splitters.get(0)) ? "" : "|";
                splitterText += separator + splitter.value1;
            }
        }
        splitterText += "|$)";
        return splitterText;
    }

    public List<MP3Prettifier.Word> construct(){
        List<MP3Prettifier.Word> names = new ArrayList<>();

        // construct the splitters
        String splitterText = splitter;

        for (MultiArtistConfig.Item item : multiArtistConfig.list){
            MP3Prettifier.Word word = constructItem(item);
            if (this.LOG_ARTIST_CONFIG) {
                log.info("Artist Name Old Word: " + word.oldWord);
                log.info("Artist Name New Word: " + word.newWord);
            }
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
        String artistSearch = "";
        for (MultiArtistConfig.Item.Artist artist : list){
            artistSearch += "(";
            boolean lastItem = list.indexOf(artist) == list.size()-1;
            Artists.Artist artistObj = artistBO.getArtist(artist.id);
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
            Artists.Artist artistObj = artistBO.getArtist(artist.id);
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

            Artists.Artist artistObj = artistBO.getArtist(artistSequenceItem.artistId);
            if (artistObj == null){
                throw new ApplicationException(("Artist Id not found in config:" + artistSequenceItem.artistId));
            }

            MultiArtistConfig.Splitter splitterObj = getSplitter(artistSequenceItem.splitterId);
            if (splitterObj == null){
                throw new ApplicationException(("Splitter Id not found in config:" + artistSequenceItem.splitterId));
            }
            word += (StringUtils.isBlank(artistObj.stageName) ? artistObj.name : artistObj.stageName) + splitterObj.value2;
        }
        return word;
    }

    public MultiArtistConfig.Item getMultiArtist(String id){
        MultiArtistConfig.Item multiArtist = mapItems.get(id);
        return multiArtist;
    }

    public MultiArtistConfig.Splitter getSplitter(String id){
        MultiArtistConfig.Splitter splitter = mapSplitters.get(id);
        if (splitter != null){
            splitter.value2 = splitter.id.equals(multiArtistConfig.splitterEndId) ? "": splitter.value2;
        }
        return splitter;
    }

    public MultiArtistConfig.Item findMultiArtist(String[] artists){
        // lookup if all artist exist
        List<Artists.Artist> listArtists = new ArrayList<>();
        for (String artistName : artists){
            Artists.Artist artist = artistBO.findArtistByName(artistName);
            if (artist == null){
                return null;
            }
            listArtists.add(artist);
        }

        for (MultiArtistConfig.Item item : multiArtistConfig.list) {
            if (artists.length == item.artistSequence.size() && artists.length == item.artists.size()) {
                if (item.exactPosition){
                    if (findArtistInSequenceExact(listArtists, item.artistSequence)) {
                        return item;
                    }
                }
                else {
                    if (findArtistInSequence(listArtists, item.artistSequence)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    private boolean findArtistInSequenceExact(List<Artists.Artist> listArtists, List<MultiArtistConfig.Item.ArtistSequenceItem> sequenceArtists){
        int counter = 0;
        boolean found = false;
        for (Artists.Artist artistItem : listArtists) {
            String artistId = sequenceArtists.get(counter++).getArtistId();
            if (!artistItem.getId().equals(artistId)){
                return false;
            }
            else {
                found = true;
            }
        }
        return found;
    }

    private boolean findArtistInSequence(List<Artists.Artist> listArtists, List<MultiArtistConfig.Item.ArtistSequenceItem> sequenceArtists){
        List <MultiArtistConfig.Item.ArtistSequenceItem> clone = new ArrayList(sequenceArtists);
        for (Artists.Artist artistItem : listArtists) {
            Predicate<MultiArtistConfig.Item.ArtistSequenceItem> predicate = p-> p.getArtistId().equals(artistItem.getId());
            clone.removeIf(predicate);
        }
        return clone.size() == 0;
    }

}

