package be.home.mezzmo.domain.dao.jdbc;

import java.util.*;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum PlayListEnum implements DatabaseColumn {
    ID("ID"),
    NAME("Name"),
    TYPE("Type"),
    DESCRIPTION("Description"),
    PARENTID("ParentID"),
    AUTHOR("Author"),
    ICON ("Icon"),
    FILE ("File"),
    TRAVERSEFOLDER ("TraverseFolder"),
    FOLDERPATH ("FolderPath"),
    FILTER ("Filter"),
    DYNAMICTREETOKEN ("DynamicTreeToken"),
    RUNTIME ("Runtime"),
    STREAMNUM ("StreamNum"),
    ORDERBYCOLUMN ("OrderByColumn"),
    ORDERBYDIRECTION ("OrderByDirection"),
    LIMITBY ("LimitBy"),
    COMBINEAND ("CombineAnd"),
    LIMITTYPE ("LimitType"),
    PLAYLISTORDER ("PlaylistOrder"),
    MEDIATYPE ("MediaType"),
    THUMBNAILID ("ThumbnailID"),
    THUMBNAILAUTHOR ("ThumbnailAuthor"),
    CONTENTRATINGID ("ContentRatingID"),
    BACKDROPARTWORKID ("BackdropArtworkID"),
    DISPLAYTITLEFORMAT ("DisplayTitleFormat");

    public String columnName;

    PlayListEnum(String s) {
        columnName = s;
    }

    public String getColumnName() {
        return columnName;
    }

    private static final List<String> lookup = new ArrayList();

    // Populate the lookup table on loading time
    static {
        for (PlayListEnum s : EnumSet.allOf(PlayListEnum.class))
            lookup.add(s.name() + " AS " + s.getColumnName());
    }

    // This method can be used for reverse lookup purpose
    public String getColumns() {
        String columns = "";
        for ( String tmp : lookup){
            columns += tmp + ",";
        }
        return columns;
    }

}

