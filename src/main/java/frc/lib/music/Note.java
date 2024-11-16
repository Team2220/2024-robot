package frc.lib.music;

//  website for note freqency www.liutaiomottola.com
public enum Note {
    LowC(32.703),
    MiddleC(261.626),
    HighC(2093.005),
    HigherC(3000.69420);
    private double frequency;

    Note(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }
}
