package frc.lib.leds;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;

public class CANdleWrapper {
    private CANdle candle;
    private int leds;

    public CANdleWrapper(int id, int numLed) {

        candle = new CANdle(id);
        leds = numLed;

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

    public int getNumLed() {
        return leds;
    }

}
