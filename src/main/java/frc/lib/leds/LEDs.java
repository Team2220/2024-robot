package frc.lib.leds;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDs extends SubsystemBase {
  LedSegment[] segments;
  LedSignal[] signals;

  public LEDs(LedSegment[] segments, LedSignal[] signals) {
    this.segments = segments;
    this.signals = signals;
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
