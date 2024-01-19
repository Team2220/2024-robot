package frc.lib;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.motorcontrol.Talon;

public class TalonOrchestra {
 Orchestra orchestra = new Orchestra();
 public void start (){
    orchestra.clearInstruments();
    for ( TalonFX talon : TalonFXRegistry.getTalonFXs() ){
        orchestra.addInstrument(talon);
    }
 }
}
