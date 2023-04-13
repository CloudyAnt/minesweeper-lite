package source.util;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @version 1.0 16/09/02
 */
public class Dragger {
    // c stands component, m stands for mouse
    private Point cStart;
    private Point mPre;
    private Point mStart;

    private Dragger(Component c, Component listen) {
        listen.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cStart = c.getLocation();
                mStart = e.getLocationOnScreen();
            }
        });
        listen.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mPre = e.getLocationOnScreen();
                c.setLocation(new Point(mPre.x - mStart.x + cStart.x, mPre.y - mStart.y + cStart.y));
            }
        });
    }

    public static void drag(Component c, Component listen) {
        new Dragger(c, listen);
    }
}
