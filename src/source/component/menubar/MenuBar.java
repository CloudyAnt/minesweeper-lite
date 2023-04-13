package source.component.menubar;

import javax.swing.*;
import java.awt.*;

public class MenuBar extends JMenuBar {

    private static MenuBar instance;

    public static MenuBar getInstance() {
        if (instance == null) {
            instance = new MenuBar();
        }
        return instance;
    }

    private MenuBar() {
        setBackground(Color.white);

        MenuType.GAME_LEVEL_MENU.addInto(this);
        add(RecordMenu.getInstance());
        add(Box.createHorizontalGlue());
        MenuType.EXIT_MENU.addInto(this);
    }

}
