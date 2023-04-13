package source.resource;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Resource {
    public static final Image appIcon = getIcon("icon.png").getImage();
    private static final ResourceBundle contentBundle = ResourceBundle
            .getBundle("source.resource.locales.content");

    private Resource() {
    }

    public static ImageIcon getIcon(String iconName) {
        return new ImageIcon(urlOf("img/" + iconName));
    }

    public static URL urlOf(String resourceName) {
        return Resource.class.getResource(resourceName);
    }

    public static InputStream getInputStream(String fileName) {
        return Resource.class.getResourceAsStream(fileName);
    }

    public static String getString(String stringId) {
        return contentBundle.getString(stringId);
    }
}
