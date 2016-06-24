package be.home.common.constants;

/**
 * Created by ghyssee on 28/09/2015.
 */
public interface Constants {

    public interface Movies {

        public enum Import {
            ID(0), TITLE(1), ALTERNATE_TITLE(2), YEAR(3), GENRE(4), STOCKPLACE(5), IMDB_ID(6);
            private int value;

            private Import(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }
        }

        public enum Status {
            NFO, SOURCE, DESTINATION, ERROR_NFO, DIFFERENT_TITLE, EXIST_NFO;
        }
    }

    public interface Path {
        public static final String MP3_PREPROCESSOR = "C:/My Data/tmp/Java/MP3Processor/Preprocess";
        public static final String MP3_PROCESSOR = "C:/My Data/tmp/Java/MP3Processor/Process";
        public static final String MP3_NEW = "C:/My Data/tmp/Java/MP3Processor/New";
    }
}
