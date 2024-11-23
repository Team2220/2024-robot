package frc.lib.drivetrain;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.controllers.CommandJoystickWrapper;
import frc.lib.controllers.CommandXBoxWrapper;
import frc.lib.limeLight.LimelightHelpers;
import frc.lib.limeLight.LimelightHelpers.LimelightTarget_Detector;

public class DriveCommand extends Command {
    double P = .005;
    double I = 0;
    double D = 0.000;
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
    private BooleanSupplier pointAtGoal;
    PIDController controller = new PIDController(P, I, D);
    double wantedAngle = 0.0;
    boolean pid = false;
    boolean goal = false;

    public DriveCommand(CommandXBoxWrapper xbox, CommandJoystickWrapper joystick, DriveTrain driveTrain) {
        this(
            ()-> (xbox.getLeftX())+(joystick.getX()),
            ()-> (xbox.getLeftY())+(joystick.getY()),
            ()-> (xbox.getRightX())+(joystick.getZ()),
            xbox.leftBumper().or(joystick.button(4)),
            xbox.povLeft().or(joystick.povLeft()),
            xbox.povRight().or(joystick.povRight()),
            xbox.povUp().or(joystick.povUp()),
            xbox.povDown().or(joystick.povDown()),
            xbox.povUpLeft().or(joystick.povUpLeft()),
            xbox.povUpRight().or(joystick.povUpRight()),
            xbox.povDownLeft().or(joystick.povDownLeft()),
            xbox.povDownRight().or(joystick.povDownRight()),
            xbox.b(),
            driveTrain
        );
    }

    public DriveCommand(CommandXBoxWrapper xbox, DriveTrain driveTrain) {
        this(
            xbox::getLeftX,
            xbox::getLeftY,
            xbox::getRightX,
            xbox.leftBumper(),
            xbox.povLeft(),
            xbox.povRight(),
            xbox.povUp(),
            xbox.povDown(),
            xbox.povUpLeft(),
            xbox.povUpRight(),
            xbox.povDownLeft(),
            xbox.povDownRight(),
            xbox.b(),
            driveTrain
        );
    }

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
            BooleanSupplier pointAtGoal,
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
        this.pointAtGoal = pointAtGoal;
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
            goal = false;
            wantedAngle = 270;
        }
        if (right.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 90;
        }
        if (up.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 0;
        }
        if (down.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 180;
        }
        if (upLeft.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 45;
        }
        if (upRight.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 315;
        }
        if (downLeft.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 135;
        }
        if (downRight.getAsBoolean()) {
            pid = true;
            goal = false;
            wantedAngle = 225;
        }
        if (rot.getAsDouble() > 0) {
            pid = false;
            goal = false;
            wantedAngle = 0;
        }
        if (pointAtGoal.getAsBoolean()) {
            pid = true;
            goal = true;
          
            
        }
        if (goal){
            AprilTagFieldLayout layout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
            Pose3d RED = layout.getTagPose(4).get();
            Pose3d BLUE = layout.getTagPose(7).get();
            var goal = DriverStation.getAlliance().get() == DriverStation.Alliance.Red ? RED : BLUE;
            var Botpose = driveTrain.getPose().getTranslation().minus((goal).toPose2d().getTranslation());
            
            wantedAngle = Math.toDegrees(Math.atan2(Botpose.getY(), Botpose.getX()));
        }
        double rotate = this.rot.getAsDouble() * coefficient;
        if (pid) {
            rotate = controller.calculate(
                    driveTrain.getGyroscopeRotation().getDegrees() * -1,
                    wantedAngle);
        }
        this.driveTrain.drive(
                this.xspeed.getAsDouble() * coefficient * -1,
                this.yspeed.getAsDouble() * coefficient * -1,
                rotate * -1,
                true);
    }
}
