package be.home.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Gebruiker on 25/06/2016.
 */
public class NetUtils {

    public static String getHostName() {

        String hostname = null;
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Hostname can not be resolved", ex);
        }
        return hostname;
    }


}
