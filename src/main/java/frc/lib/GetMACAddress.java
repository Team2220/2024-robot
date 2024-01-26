package frc.lib;

import java.net.*;

/* got code from here: https://www.tutorialspoint.com/java-program-to-get-system-mac-address-of-windows-and-linux-machine#:~:text=Get%20NetworkInterface%20instance%20by%20using,store%20in%20a%20byte%20array. */

public class GetMACAddress {
    public static void main() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            byte[] mac = networkInterface.getHardwareAddress();
            System.out.print("MAC address  : ");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));

                System.out.println(stringBuilder.toString());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
