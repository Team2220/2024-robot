package frc.robot;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
// import frc.twilight.swerve.subsystems.Swerve;
// import frc.twilight.swerve.vectors.DriveVector;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.tunables.TunableDouble;
import frc.robot.LimelightHelpers.LimelightTarget_Detector;

public class ObjectTracker extends Command {
  private PIDController turningPid = new PIDController(0, 0, 0);
  private PIDController movingPid = new PIDController(0, 0, 0);

   private DriveTrain driveTrain;
   private DoubleSupplier fwd;
   private DoubleSupplier str;

  private static TunableDouble p = new TunableDouble("P", .01, true, "limelight");
  private static TunableDouble i = new TunableDouble("I", 0, true, "limelight");
  private static TunableDouble d = new TunableDouble("D", 0, true, "limelight");

  private static TunableDouble pM = new TunableDouble("PM", .00001, true, "limelight");
  private static TunableDouble iM = new TunableDouble("IM", 0, true, "limelight");
  private static TunableDouble dM = new TunableDouble("DM", 0, true, "limelight");

  public ObjectTracker(DriveTrain driveTrain,  DoubleSupplier fwd, DoubleSupplier str) {
    addRequirements(driveTrain);
    turningPid.setTolerance(0.5);
    turningPid.setSetpoint(0);
    movingPid.setTolerance(0.5);
    movingPid.setSetpoint(0);
    this.driveTrain = driveTrain;
    this.fwd = fwd;
    this.str = str;
  }
  

  @Override
  public void initialize() {
    // System.out.println("init");
    // var results = LimelightHelpers.getJSONDump("limelight-right");
    // System.out.println("results=" + results);
    // System.out.println("done");
  }

  @Override
  public void execute() {
    turningPid.setPID(p.getValue(), i.getValue(), d.getValue());
    movingPid.setPID(pM.getValue(), iM.getValue(), dM.getValue());

    double out = 0;
    double outA = 0;
    boolean foundNote = false;

    var results = LimelightHelpers.getLatestResults("limelight-right");
    for (LimelightTarget_Detector target : results.targets_Detector) {
      if (target.className.equals("note")) {
        out = target.tx;
        outA = target.ta;
        foundNote = true;
      } else {
        System.out.println("unknown: " + target.className);
      }
    }

    out = turningPid.calculate(out);
    outA = movingPid.calculate(outA);


    if (foundNote) {
      // System.out.println("out: "+ out);
      // var result = results.targets_Detector[0];
      
      out = MathUtil.clamp(out, -1, 1);
      driveTrain.drive(-outA, str.getAsDouble(), -out, true);
      // driveTrain.setDrive(new DriveVector(fwd.getAsDouble(), str.getAsDouble(), -out),
      // true);
    } else {
      // System.out.println("nothing found");
     driveTrain.drive(0, str.getAsDouble(), 0, true);
    }

  }

  @Override
  public void end(boolean interupted) {
    // swerve.setDrive(0, 0, 0);

  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
