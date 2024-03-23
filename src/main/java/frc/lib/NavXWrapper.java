package frc.lib;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;

public class NavXWrapper {
    private AHRS navx = new AHRS();

    public Rotation2d getAngle() {
        return navx.getRotation2d();
    }

    public double getRate() {
        return navx.getRate();
    }

    public void zero() {
        navx.reset();
        navx.setAngleAdjustment(0);
    }
}
