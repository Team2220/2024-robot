package frc.lib.leds;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix.led.CANdle;

public class LEDs extends SubsystemBase {
  LedSegment[] segments;
  LedSignal[] signals;

  public LEDs(LedSegment[] segments, LedSignal[] signals) {
    this.segments = segments;
    this.signals = signals;
  }

  private static void makeCANdleSegments(int[] ids) {
    LedSegment[] segments = new LedSegment[ids.length];
    for (int id : ids) {
      for (int i = 0; i < ids.length; i++) {
        segments[i] = new LedSegment(new CANdle(id));
      }
    }
  }

  @Override
  public void periodic() {
    for (LedSegment segment : segments) {
      segment.reset();
    }
    for (LedSignal signal : signals) {
      signal.update(segments);
    }
    for (LedSegment segment : segments) {
      segment.setAnimationIfAble(null);
    }
  }
}