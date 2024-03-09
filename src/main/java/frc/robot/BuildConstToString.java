package frc.robot;

public class BuildConstToString {
    public String toString() {
        String buildConstantsString = "MAVEN GROUP: "
                + BuildConstants.MAVEN_GROUP + "\nMAVEN NAME: "
                + BuildConstants.MAVEN_NAME + "\nVERSION: " + BuildConstants.VERSION + "\nGIT REVISION: "
                + BuildConstants.GIT_REVISION + "\nGIT SHA: " + BuildConstants.GIT_SHA + "\n GIT DATE: "
                + BuildConstants.GIT_DATE + "\nGIT BRANCH: " + BuildConstants.GIT_BRANCH + "\nBUILD DATE: "
                + BuildConstants.BUILD_DATE + "\nBUILD UNIX TIME: " + BuildConstants.BUILD_UNIX_TIME + "\nDIRTY: "
                + BuildConstants.DIRTY;
        return buildConstantsString;
    }
}
