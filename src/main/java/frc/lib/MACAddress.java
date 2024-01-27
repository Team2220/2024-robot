package frc.lib;
/* https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java */

public enum MACAddress {
    Robot23("00-80-2F-17-60-67"),
    Robot24("");

    private String address;

    MACAddress(String text) {
        this.address = text;
    }

    public String getText() {
        return this.address;
    }

    public static MACAddress fromString(String text) {
        for (MACAddress b : MACAddress.values()) {
            if (b.address.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
