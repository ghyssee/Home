package be.home.mezzmo.domain.model;

import java.util.List;

/**
 * Created by ghyssee on 7/12/2016.
 */
public class AlbumError {

        public List <Item> items;

        public class Item {

                public Long id;
                public String file;
                public String type;
                public String oldValue;
                public String newValue;

                public String getNewValue() {
                        return newValue;
                }

                public void setNewValue(String newValue) {
                        this.newValue = newValue;
                }

                public Long getId() {
                        return id;
                }

                public void setId(Long id) {
                        this.id = id;
                }

                public String getFile() {
                        return file;
                }

                public void setFile(String file) {
                        this.file = file;
                }

                public String getType() {
                        return type;
                }

                public void setType(String type) {
                        this.type = type;
                }

                public String getOldValue() {
                        return oldValue;
                }

                public void setOldValue(String oldValue) {
                        this.oldValue = oldValue;
                }
        }

}