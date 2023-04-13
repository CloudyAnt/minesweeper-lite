package source.constant;

public class SystemInfo {
    private SystemInfo() {
    }

    public static final OS os;

    static {
        String osName = System.getProperty("os.name").split(" ")[0];
        switch (osName) {
            case "Windows":
                os = OS.WINDOWS;
                break;
            case "Mac":
                os = OS.MAC;
                break;
            case "Linux":
                os = OS.LINUX;
                break;
            default:
                os = OS.UNKNOWN;
        }
    }

    public enum OS {
        LINUX, MAC, WINDOWS, UNKNOWN
    }
}
