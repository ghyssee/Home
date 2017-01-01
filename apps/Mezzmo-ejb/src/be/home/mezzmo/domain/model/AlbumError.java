package be.home.mezzmo.domain.model;

import java.util.List;

/**
 * Created by ghyssee on 7/12/2016.
 */
public class AlbumError {

    public List<Item> items;

    public class Item {

        public long uniqueId;

        public Long id;

        public Long fileId;
        public String file;
        public String type;
        public String oldValue;
        public String newValue;
        public String basePath;
        public boolean done;

        public boolean process;

        public Long getFileId() {
            return fileId;
        }

        public void setFileId(Long fileId) {
            this.fileId = fileId;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }


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

        public boolean isProcess() {
            return process;
        }

        public void setProcess(boolean process) {
            this.process = process;
        }
        public long getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(long uniqueId) {
            this.uniqueId = uniqueId;
        }


    }

}
