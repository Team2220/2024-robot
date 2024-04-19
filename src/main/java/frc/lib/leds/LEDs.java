package frc.lib.leds;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDs extends SubsystemBase {
  LedSegment[] segments;
  LedSignal[] signals;

  public LEDs(LedSegment[] segments, LedSignal[] signals) {
    this.segments = segments;
    this.signals = signals;
  }

  public LEDs(CANdleWrapper[] candles, LedSignal[] signals) {
    this(makeCANdleWrapperSegments(candles), signals);
  }

  private static LedSegment[] makeCANdleWrapperSegments(CANdleWrapper candle[]) {
    LedSegment[] segments = new LedSegment[candle.length];

    for (int i = 0; i < candle.length; i++) {
      segments[i] = new LedSegment(candle[i]);
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
      segment.setAnimationIfAble(LedAnimation.off());
    }
  }
}