package frc.lib.leds;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;

public class CANdleWrapper {
    private CANdle candle;

    public CANdleWrapper(int id) {

        candle = new CANdle(id);

    };

    public void animate(Animation animation) {
        candle.animate(animation);

    };

    public void setLEDs(int r, int g, int b, int startIdx, int count) {
        candle.setLEDs(r, g, b, 0, startIdx, count);
    }

    public void setLEDs(int r, int g, int b) {
        setLEDs(r, g, b, 0, 512);
    }
}
