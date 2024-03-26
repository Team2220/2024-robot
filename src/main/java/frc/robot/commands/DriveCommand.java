package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.DriveTrain;

public class DriveCommand extends Command {
    private BooleanSupplier slow;
    private DoubleSupplier xspeed;
    private DoubleSupplier yspeed;
    private DoubleSupplier rot;
    private DriveTrain driveTrain;
    private BooleanSupplier left;
    private BooleanSupplier right;
    private BooleanSupplier up;
    private BooleanSupplier down;
    PIDController controller = new PIDController(
            DriveTrain.rotationConstants.kP,
            DriveTrain.rotationConstants.kI,
            DriveTrain.rotationConstants.kD);
    double wantedAngle = 0;
    boolean pid = false;

    public DriveCommand(
            DoubleSupplier xspeed,
            DoubleSupplier yspeed,
            DoubleSupplier rot,
            BooleanSupplier slow,
            BooleanSupplier left,
            BooleanSupplier right,
            BooleanSupplier up,
            BooleanSupplier down,
            DriveTrain driveTrain) {
        this.slow = slow;
        this.xspeed = xspeed;
        this.yspeed = yspeed;
        this.rot = rot;
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
        this.driveTrain = driveTrain;
        addRequirements(driveTrain);
    }
    // if(m_driverController.getHID().getPOV() != -1){
    // pid = true;
    // }

    @Override
    public void execute() {
        double coefficient = slow.getAsBoolean() ? 0.5 : 1;
        if (left.getAsBoolean()) {
            pid = true;
            wantedAngle = 90;
        }
        if (right.getAsBoolean()) {
            pid = true;
            wantedAngle = 270;
        }
        if (up.getAsBoolean()) {
            pid = true;
            wantedAngle = 0;
        }
        if (down.getAsBoolean()) {
            pid = true;
            wantedAngle = 180;
        }
        if (rot.getAsDouble() > 0) {
            pid = false;
            wantedAngle = 0;
        }
        double rotate = this.rot.getAsDouble() * coefficient;
        if (pid) {
            rotate = controller.calculate(
                    driveTrain.getGyroscopeRotation().getDegrees(),
                    wantedAngle);
        }
        this.driveTrain.drive(
                this.xspeed.getAsDouble() * coefficient,
                this.yspeed.getAsDouble() * coefficient,
                rotate,
                true);
    }
}
