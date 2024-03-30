package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.DriveTrain;

public class DriveCommand extends Command {
    double P = .009764387;
    double I = 0;
    double D = 0.0002;
    private BooleanSupplier slow;
    private DoubleSupplier xspeed;
    private DoubleSupplier yspeed;
    private DoubleSupplier rot;
    private DriveTrain driveTrain;
    private BooleanSupplier left;
    private BooleanSupplier right;
    private BooleanSupplier up;
    private BooleanSupplier down;
    private BooleanSupplier upLeft;
    private BooleanSupplier upRight;
    private BooleanSupplier downLeft;
    private BooleanSupplier downRight;
    PIDController controller = new PIDController(P, I, D);
    double wantedAngle = 0.0;
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
            BooleanSupplier upLeft,
            BooleanSupplier upRight,
            BooleanSupplier downLeft,
            BooleanSupplier downRight,
            DriveTrain driveTrain) {
        this.slow = slow;
        this.xspeed = xspeed;
        this.yspeed = yspeed;
        this.rot = rot;
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
        this.upLeft = upLeft;
        this.upRight = upRight;
        this.downLeft = downLeft;
        this.downRight = downRight;
        this.driveTrain = driveTrain;
        addRequirements(driveTrain);
        controller.enableContinuousInput(0, 360);
    }
    // if(m_driverController.getHID().getPOV() != -1){
    // pid = true;
    // }

    @Override
    public void execute() {
        double coefficient = slow.getAsBoolean() ? 0.5 : 1;
        if (left.getAsBoolean()) {
            pid = true;
            wantedAngle = 270;
        }
        if (right.getAsBoolean()) {
            pid = true;
            wantedAngle = 90;
        }
        if (up.getAsBoolean()) {
            pid = true;
            wantedAngle = 0;
        }
        if (down.getAsBoolean()) {
            pid = true;
            wantedAngle = 180;
        }
        if (upLeft.getAsBoolean()) {
            pid = true;
            wantedAngle = 45;
        }
        if (upRight.getAsBoolean()) {
            pid = true;
            wantedAngle = 315;
        }
        if (downLeft.getAsBoolean()) {
            pid = true;
            wantedAngle = 135;
        }
        if (downRight.getAsBoolean()) {
            pid = true;
            wantedAngle = 225;
        }
        if (rot.getAsDouble() > 0) {
            pid = false;
            wantedAngle = 0;
        }
        double rotate = this.rot.getAsDouble() * coefficient;
        if (pid) {
            rotate = controller.calculate(
                    driveTrain.getGyroscopeRotation().getDegrees() * -1,
                    wantedAngle);
        }
        this.driveTrain.drive(
                this.xspeed.getAsDouble() * coefficient,
                this.yspeed.getAsDouble() * coefficient,
                rotate,
                true);
    }
}
