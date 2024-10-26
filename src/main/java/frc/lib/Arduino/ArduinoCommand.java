package frc.lib.Arduino;

public enum ArduinoCommand {

    OFF(0x12),
    RED(0x13),
    ORANGE(0x14),
    YELLOW(0x15),
    GREEN(0x16),
    BLUE(0x17),
    PINK(0x18),
    PURPLE(0x19),
    POLICE(0x20);

    private byte value;

    ArduinoCommand(int value) {
        this.value = (byte) value;
    }

    public byte getvalue() {
        return value;
    }

}