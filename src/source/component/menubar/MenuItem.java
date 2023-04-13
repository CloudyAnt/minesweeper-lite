package source.component.menubar;

import source.Compatibility;
import source.GameLevel;
import source.Handler;
import source.constant.Const;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class MenuItem extends JMenuItem {
    private static final MatteBorder matteBorder =
            new MatteBorder(0, 0, 1, 0, Color.cyan);

    MenuItem(GameLevel gameLevel) {
        this(gameLevel.getDescription(), () -> Handler.getInstance().newGame(gameLevel));
    }

    MenuItem(String description, Runnable action) {
        setText(description);
        setBackground(Color.white);
        setSize(50, 20);
        setFont(Const.globalFont14);
        setForeground(Color.cyan);
        setBorder(matteBorder);
        Compatibility.getInstance().listen(this, action);
    }
}
