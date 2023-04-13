package source.component.menubar;

import source.GameLevel;
import source.resource.Resource;

import javax.swing.*;

// This is hard merge this into MenuTypes
class RecordMenu extends Menu {
    private static RecordMenu instance;

    public static JMenu getInstance() {
        if (instance == null) {
            instance = new RecordMenu();
        }
        return instance.value();
    }

    private RecordMenu() {
        super(Resource.getString("settings.records.title"));
        RecordMenuItem[] menus = {
                new RecordMenuItem(GameLevel.SIMPLE),
                new RecordMenuItem(GameLevel.MEDIUM),
                new RecordMenuItem(GameLevel.HARD)
        };

        for (RecordMenuItem m : menus) {
            topMenu.add(m.value());
        }
    }
}
