package frc.robot.subsystems;

public enum ArmPositions {
    SPEAKER(51.7),
    FIRSTNOTEBYSTAGE(32),
    ARMREST(0.0),
    NINETY(90),
    FORTYFIVE(45);

    private double angle;

    ArmPositions(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }
}