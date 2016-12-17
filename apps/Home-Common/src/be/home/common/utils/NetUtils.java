package be.home.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Gebruiker on 25/06/2016.
 */
public class NetUtils {

    private static String hostName = null;

    public static String getHostName() {

        String host = null;
        if (hostName != null){
            host = hostName;
        }
        else {
            try {
                InetAddress addr;
                addr = InetAddress.getLocalHost();
                host = addr.getHostName();
            } catch (UnknownHostException ex) {
                throw new RuntimeException("Hostname can not be resolved", ex);
            }
        }
        return host;
    }


}
