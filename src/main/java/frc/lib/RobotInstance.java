package frc.lib;
/* https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.function.Function;

import frc.lib.faults.Fault;

public enum RobotInstance {
    Robot23("00-80-2F-17-60-67"),
    Robot24("00-80-2F-36-FD-D6");

    private String address;
    private static RobotInstance current = getMacAddress();
    private static Fault fault;

    RobotInstance(String text) {
        this.address = text;
    }

    public String getText() {
        return this.address;
    }

    public static RobotInstance fromString(String text) {
        for (RobotInstance b : RobotInstance.values()) {
            if (b.address.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static String getMacAddressStr() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            byte[] mac = networkInterface.getHardwareAddress();
            // System.out.print("MAC address : ");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Error";
        }
    }

    public static RobotInstance getMacAddress() {
        var check = fromString(getMacAddressStr());
        if (check == null) {
            if (fault == null) {
                fault = new Fault("Unknown Robot MAC Address: " + getMacAddressStr());
                fault.setIsActive(true);
                System.out.println("Unknown Robot MAC Address: " + getMacAddressStr());
            }
            return Robot24;
        } else {
            return check;
        }
    }

    public static <T> T config(Function<RobotInstance, T> config) {
        return config.apply(current);
    }
}
