package frc.lib.eventLoops;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.event.EventLoop;

public class IsolatedEventLoop {
    private EventLoop eventLoop;

    public IsolatedEventLoop() {
        eventLoop = new EventLoop();
    }

    public void bind(Runnable action) {
        eventLoop.bind(() -> {
            try {
                action.run();
            } catch (Exception e) {
                DriverStation.reportError("ISOLATED EVENT LOOP ERROR", e.getStackTrace());
            }
        });
    }

    public void poll() {
        eventLoop.poll();
    }

    public void clear() {
        eventLoop.clear();
    }
}
