package be.home.model;

import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gebruiker on 7/10/2016.
 */
public class HTMLSettings {

    public class Exception {
        public String name;
    }

    public class Group {
        public String from;
        public String to;
        public List<Exception> exceptions;
        private String filename;
        public List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();

        public String getFrom() {
            return this.from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return this.to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

    }

    public class Export {
        public List<Group> groups;
    }

    public class Menu {
        public List<MenuItem> menuItems;
    }

    public class MenuItem {
        String description;
        String href;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    public Export export;
    public Menu menu;


}
