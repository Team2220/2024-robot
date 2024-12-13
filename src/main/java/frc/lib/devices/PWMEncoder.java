 package frc.lib.devices;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.AnalogInput;

public class PWMEncoder {
    private AnalogInput encoder;
    private final int oversampling = 4; // Set to 1 to disable oversampling

    public PWMEncoder(int analogPort) {
        encoder = new AnalogInput(analogPort);
        encoder.setOversampleBits(oversampling);
    }

    public Measure<Angle> getPosition() {
        // return encoder.getAverageValue() * 360 / 4096.0 / Math.pow(2, oversampling);
        return Units.Degrees.of(encoder.getValue() * 360 / 4096.);
    }
}