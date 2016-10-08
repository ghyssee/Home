package be.home.model;

import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gebruiker on 7/10/2016.
 */
public class HTMLSettings {
    public class Group {
        public String from;
        public String to;
        public List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
    }

    public class Export {
        public List<Group> groups;
    }
    public Export export;


}
