package frc.lib.leds;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDs extends SubsystemBase {
  LedSegment[] segments;
  LedSignal[] signals;

  public LEDs(LedSegment[] segments, LedSignal[] signals) {
    this.segments = segments;
    this.signals = signals;
  }

  public LEDs(int[] ids, LedSignal[] signals) {
    this(makeCANdleWrapperSegments(ids), signals);
  }

  private static LedSegment[] makeCANdleWrapperSegments(int[] ids) {
    LedSegment[] segments = new LedSegment[ids.length];

    for (int i = 0; i < ids.length; i++) {
      segments[i] = new LedSegment(new CANdleWrapper(ids[i]));
    }

    return segments;
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