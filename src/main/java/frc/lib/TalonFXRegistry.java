package frc.lib;

import java.util.ArrayList;

import com.ctre.phoenix6.hardware.TalonFX;

public class TalonFXRegistry {
    private static ArrayList<TalonFX> talonFXs = new ArrayList<>();

    public static void register(TalonFX talonFX) {
        talonFXs.add(talonFX);
    }

    public static ArrayList<TalonFX> getTalonFXs() {
        return talonFXs;
    }
}
