package be.home.model;

import be.home.common.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by ghyssee on 24/03/2015.
 */
public class FileTO implements Comparable {

    private File file;
    private int level;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int compareTo(Object o) {
        FileTO fileTO = (FileTO) o;
        String[] l1 = FileUtils.getParentList(fileTO.getFile());
        String[] l2 = FileUtils.getParentList(this.file);

        for (int i=0; i < l1.length; i++){
            // one File is from a different level than the other
            if (i >= l2.length){
                return -1;
            }
            else {
                int cmp = l2[i].toUpperCase().compareTo(l1[i].toUpperCase());
                if (cmp == 0) {
                    continue;
                }
                else {
                    return cmp;
                }
            }
        }
        // one File is from a different level than the other
        if (l1.length < l2.length){
            return 1;
        }
        else {
             // same level, Sort files first, and directories after the files
            if (fileTO.getFile().isDirectory() && this.file.isFile()){
                return 1;
            }
            if (fileTO.getFile().isFile() && this.file.isDirectory()){
                return -1;
          }
        }
        return this.file.getName().compareTo(fileTO.getFile().getName());
    }
}
