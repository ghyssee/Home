package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 27/06/2016.
 */
public class MGOPlaylistTO {


    private int ID;
    private String Name;
    private int Type;
    private String Description;
    private int ParentID;
    private int Author;
    private int Icon;
    private String File;
    private String TraverseFolder;
    private String FolderPath;
    private String Filter;
    private String DynamicTreeToken;
    private String RunTime;
    private String StreamNum;
    private int OrderByColumn;
    private int OrderByDirection;
    private int LimitBy;
    private int CombineAnd;
    private int LimitType;
    private int PlaylistOrder;
    private int MediaType;
    private int ThumbnailID;
    private int ThumbnailAuthor;
    private int ContentRatingID;
    private int BackdropArtworkID;
    private String DisplayTitleFormat;

    private String parentName;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public int getAuthor() {
        return Author;
    }

    public void setAuthor(int author) {
        Author = author;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public String getFile() {
        return File;
    }

    public void setFile(String file) {
        File = file;
    }

    public String getTraverseFolder() {
        return TraverseFolder;
    }

    public void setTraverseFolder(String traverseFolder) {
        TraverseFolder = traverseFolder;
    }

    public String getFolderPath() {
        return FolderPath;
    }

    public void setFolderPath(String folderPath) {
        FolderPath = folderPath;
    }

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String filter) {
        Filter = filter;
    }

    public String getDynamicTreeToken() {
        return DynamicTreeToken;
    }

    public void setDynamicTreeToken(String dynamicTreeToken) {
        DynamicTreeToken = dynamicTreeToken;
    }

    public String getRunTime() {
        return RunTime;
    }

    public void setRunTime(String runTime) {
        RunTime = runTime;
    }

    public String getStreamNum() {
        return StreamNum;
    }

    public void setStreamNum(String streamNum) {
        StreamNum = streamNum;
    }

    public int getOrderByColumn() {
        return OrderByColumn;
    }

    public void setOrderByColumn(int orderByColumn) {
        OrderByColumn = orderByColumn;
    }

    public int getOrderByDirection() {
        return OrderByDirection;
    }

    public void setOrderByDirection(int orderByDirection) {
        OrderByDirection = orderByDirection;
    }

    public int getLimitBy() {
        return LimitBy;
    }

    public void setLimitBy(int limitBy) {
        LimitBy = limitBy;
    }

    public int getCombineAnd() {
        return CombineAnd;
    }

    public void setCombineAnd(int combineAnd) {
        CombineAnd = combineAnd;
    }

    public int getLimitType() {
        return LimitType;
    }

    public void setLimitType(int limitType) {
        LimitType = limitType;
    }

    public int getPlaylistOrder() {
        return PlaylistOrder;
    }

    public void setPlaylistOrder(int playlistOrder) {
        PlaylistOrder = playlistOrder;
    }

    public int getMediaType() {
        return MediaType;
    }

    public void setMediaType(int mediaType) {
        MediaType = mediaType;
    }

    public int getThumbnailID() {
        return ThumbnailID;
    }

    public void setThumbnailID(int thumbnailID) {
        ThumbnailID = thumbnailID;
    }

    public int getThumbnailAuthor() {
        return ThumbnailAuthor;
    }

    public void setThumbnailAuthor(int thumbnailAuthor) {
        ThumbnailAuthor = thumbnailAuthor;
    }

    public int getContentRatingID() {
        return ContentRatingID;
    }

    public void setContentRatingID(int contentRatingID) {
        ContentRatingID = contentRatingID;
    }

    public int getBackdropArtworkID() {
        return BackdropArtworkID;
    }

    public void setBackdropArtworkID(int backdropArtworkID) {
        BackdropArtworkID = backdropArtworkID;
    }

    public String getDisplayTitleFormat() {
        return DisplayTitleFormat;
    }

    public void setDisplayTitleFormat(String displayTitleFormat) {
        DisplayTitleFormat = displayTitleFormat;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }


}
