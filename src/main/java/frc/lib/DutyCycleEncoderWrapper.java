package frc.lib;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class DutyCycleEncoderWrapper {

    private DutyCycleEncoder duty;

    public DutyCycleEncoderWrapper(int channel) {
        duty = new DutyCycleEncoder(channel);
        Fault.autoUpdating("DutyCycleEncoder Disconnected", () -> {
            var value = duty.isConnected();
            return value;
        });
    }

    public double getAngle() {
        return duty.getAbsolutePosition()
    }

    public void zero() {
        duty.reset();
    }
}