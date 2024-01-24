package frc.lib;

import java.net.*;
import java.util.*;

public class GetMACAddress {
    public static void main() throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
        byte[] mac = networkInterface.getHardwareAddress();
        System.out.print("MAC address  : ");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));

            System.out.println(stringBuilder.toString());
        }
    }
}
