package frc.lib;

import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class CommandObserver {
    private static StringLogEntry commandStateEntry = new StringLogEntry(DataLogManager.getLog(), "CommandStates");

    public static void start() {

        CommandScheduler.getInstance()
                .onCommandInitialize(
                        (Command command) -> {
                            logCommand(command, "initialize");
                        });
        CommandScheduler.getInstance()
                .onCommandFinish(
                        (Command command) -> {
                            logCommand(command, "finish");
                        });
        CommandScheduler.getInstance()
                .onCommandInterrupt(
                        (Command command) -> {
                            logCommand(command, "interrupt");
                        });
    }

    private static void logCommand(Command command, String state) {
        commandStateEntry.append(command.getName() + " " + state);

    }
}