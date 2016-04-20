package be.home.common.archiving;

/**
 * Created by ghyssee on 18/11/2015.
 */
public abstract class Archiver {

    private String inputDir;
    private String outputFile;
    private int counter = 0;

    Archiver(String inputDir, String outputFile) {
        this.inputDir = inputDir; //.replace(" ", "%20");;
        this.outputFile = outputFile; //.replace(" ", "%20");
    }

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void incrementCount(){
        counter++;
    }

    public abstract void run();
}
