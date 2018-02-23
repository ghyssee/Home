package be.home.main;

import be.home.common.main.BatchJobV2;
import org.apache.log4j.Logger;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Test extends BatchJobV2 {

    private static final Logger log = getMainLog(HelloWorld.class);

    public static void main(String args[]) {
        String path = "Test - 0001-0003";
        if (path.matches("(.* - )?[0-9]{3,4} ?- ?[0-9]{3,4}")){
            System.out.println("Not Main Path");
        }
        else {
            System.out.println(path);

        }

    }

    @Override
    public void run() {

    }
}
