package source.component.menubar;

import source.Compatibility;
import source.constant.Const;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders.MenuBarBorder;
import java.awt.*;

class Menu {

    protected final JMenu topMenu;
    Menu() {
        topMenu = new JMenu();
        basicSet();
    }

    private void basicSet() {
        topMenu.setFont(Const.globalFont14);
        topMenu.setForeground(Color.magenta);
        topMenu.setBorder(new MenuBarBorder(Color.magenta, Color.white));
    }

    Menu(String text) {
        this();
        topMenu.setText(text);
    }

    Menu(String text, JMenuItem[] items, Runnable clicked) {
        this(text);

        for (JMenuItem item : items) {
            topMenu.add(item);
        }

        if (clicked != null) {
            Compatibility.getInstance().listen(topMenu, clicked);
        }
    }

    JMenu value() {
        return topMenu;
    }
}
