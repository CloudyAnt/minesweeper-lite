package source.component.menubar;

import source.GameLevel;
import source.component.custom.CustomDialog;
import source.resource.Resource;
import source.util.TaskExecutor;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

enum MenuType {
    EXIT_MENU(Resource.getString("settings.exit"), () -> {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.put("Menu.selectionBackground", Color.red);
    }, null, () -> {
        TaskExecutor.shutdown();
        System.exit(0);
    }),
    GAME_LEVEL_MENU(Resource.getString("settings.level-set.title"), null, () -> new JMenuItem[]{
            new MenuItem(GameLevel.SIMPLE),
            new MenuItem(GameLevel.MEDIUM),
            new MenuItem(GameLevel.HARD),
            new MenuItem(Resource.getString("level.custom"), CustomDialog::showCustomDialog)}, null);

    private final Runnable before;
    private final String description;
    private final Supplier<JMenuItem[]> getItems;
    private final Runnable action;

    MenuType(String description, Runnable before, Supplier<JMenuItem[]> getItems, Runnable action) {
        this.description = description;
        this.before = before;
        this.getItems = getItems;
        this.action = action;
    }


    void addInto(MenuBar menuBar) {
        if (before != null) {
            before.run();
        }

        JMenuItem[] items = new JMenuItem[0];
        if (getItems != null) {
            items = getItems.get();
        }

        menuBar.add(new Menu(description, items, action).value());
    }
}
