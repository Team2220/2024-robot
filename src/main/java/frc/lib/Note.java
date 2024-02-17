package frc.lib;

public enum Note {
    LowC(32.703),
    MiddleC(261.626),
    HighC(2093.005);

    private double frequency;

    Note(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }
}
