package source;

import source.constant.SystemInfo;
import source.exception.GameException;
import source.resource.Resource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Compatibility {

    private static Compatibility instance;
    private final Frame frame;
    private final Compat compat;

    private Compatibility(Frame frame) {
        this.frame = frame;
        SystemInfo.OS os = SystemInfo.os;
        switch (os) {
            case WINDOWS:
                compat = new WindowsCompat();
                break;
            case MAC:
                compat = new MacCompat();
                break;
            case LINUX:
                compat = new LinuxCompat();
                break;
            default:
                compat = new DefaultCompat();
        }
    }

    public static Compatibility getInstance() {
        return instance;
    }

    static void init(Frame main) {
        instance = new Compatibility(main);
        instance.compat.initialize();
    }

    public void listen(JMenu menu, Runnable action) {
        compat.listen(menu, action);
    }

    public void listen(JMenuItem item, Runnable action) {
        compat.listen(item, action);
    }

    public void resize(int contentWidth, int contentHeight) {
        compat.resize(contentWidth, contentHeight);
    }

    private abstract class Compat {

        void initialize() {
            frame.setUndecorated(true);
            frame.setIconImage(Resource.appIcon);
            frame.setBackground(new Color(255, 255, 255, 200));
        }

        abstract void resize(int contentWidth, int contentHeight);

        void listen(JMenu menu, Runnable action) {
            menu.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
            });
        }

        void listen(JMenuItem item, Runnable action) {
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    action.run();
                }
            });
        }
    }

    private class WindowsCompat extends Compat {

        @Override
        public void resize(int contentWidth, int contentHeight) {
            frame.setSize(contentWidth, contentHeight + 23);
        }
    }

    private class MacCompat extends Compat {

        @Override
        public void initialize() {
            frame.setUndecorated(false);

            InputStream iconStream = Objects.requireNonNull(Resource.class.getResourceAsStream("img/icon.png"));
            Image iconImage;
            try {
                iconImage = ImageIO.read(iconStream);
                Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
                Object application = applicationClass.getMethod("getApplication").invoke(null);
                applicationClass.getMethod("setDockIconImage", Image.class).invoke(application, iconImage);
            } catch (IOException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new GameException(e);
            } catch (ClassNotFoundException ignored) {
                // ignored
            }
        }

        @Override
        public void resize(int contentWidth, int contentHeight) {
            frame.setSize(contentWidth, contentHeight + 28);
        }

        @Override
        void listen(JMenu menu, Runnable action) {
            menu.addActionListener(e -> action.run());
        }

        @Override
        void listen(JMenuItem item, Runnable action) {
            item.addActionListener(e -> action.run());
        }
    }

    private class LinuxCompat extends Compat {

        @Override
        public void resize(int contentWidth, int contentHeight) {
            frame.setSize(contentWidth, contentHeight + 21);
        }
    }

    private class DefaultCompat extends Compat {

        @Override
        public void resize(int contentWidth, int contentHeight) {
            frame.setSize(contentWidth, contentHeight + 23);
        }
    }
}
