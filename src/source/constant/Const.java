package source.constant;

import source.resource.Resource;

import java.awt.*;

public class Const {
    private Const() {
    }

    public static final String BASE_PATH;
    static {
        SystemInfo.OS os = SystemInfo.os;
        String prefix;
        switch (os) {
            case WINDOWS:
                prefix = System.getenv("Appdata");
                break;
            case MAC:
                prefix = System.getenv("HOME") + "/Library/Application Support";
                break;
            default:
                prefix = System.getProperty("user.home");
        }
        BASE_PATH = prefix + "/.MineSweeper";
    }
    public static final Font globalFont14 = new Font("Microsoft YaHei", Font.BOLD, 14);
    public static final Font globalFont16 = new Font("Microsoft YaHei", Font.BOLD, 16);
}
