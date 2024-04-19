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

    public void setLEDs(Color color, int startIdx, int count) {
        candle.setLEDs(color.getR(), color.getG(), color.getB(), 0, startIdx, count);
    }

    public void setLEDs(Color color) {
        setLEDs(color, 0, 512);
    }

    public int getNumLed() {
        return leds;
    }

    public int e = 3;
    //hehehe

}
