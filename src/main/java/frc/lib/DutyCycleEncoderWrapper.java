package frc.lib;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class DutyCycleEncoderWrapper {

    private DutyCycleEncoder duty;

    public DutyCycleEncoderWrapper(int channel) {
        duty = new DutyCycleEncoder(channel);
        Fault.autoUpdating("DutyCycleEncoder Disconnected", () -> {
            var value = !duty.isConnected();
            return value;
        });
    }

    public Rotation2d getAngle() {
        return Rotation2d.fromRotations(duty.getAbsolutePosition());
    }

    public void zero() {
        duty.reset();
    }
}