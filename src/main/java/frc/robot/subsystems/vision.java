import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.CameraServer.VideoMode;

Camera camera = CameraServer.getInstance().getVideo();
VM videoMode = camera.getVideoMode();
Photo  photo = new Image(videoMode.pixelFormat, videoMode.width, videoMode.height);

public class Robot {

    public void robotInit() {
        try {
            CameraServer.getInstance().startAutomaticCapture();
        } catch (Exception e) {
            System.err.println("We have a bit of a problem dog: " + e.getMessage());
        }
    }
}

AprilTagDetector detector = new AprilTagDetector();
List<AprilTag> detectedTags = detector.detectTags(Photo);