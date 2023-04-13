package source;

import source.component.game.ControlPanel;
import source.component.game.GamePanel;
import source.component.menubar.MenuBar;
import source.resource.ConfigResolver;
import source.resource.Resource;
import source.util.Dragger;
import source.util.TaskExecutor;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * 扫雷 | Minesweeper
 */
public class MineSweeper {
    private final JFrame frame;

    private MineSweeper() {
        frame = new JFrame();

        appConfigure();
        frameSettings();

        // handlers
        Compatibility.init(frame);
        Handler.init(frame);

        // menus
        MenuBar menus = MenuBar.getInstance();
        Dragger.drag(frame, menus);
        frame.setJMenuBar(menus);

        // panels
        ControlPanel controlPanel = ControlPanel.getInstance();
        GamePanel gamePanel = GamePanel.getInstance();
        Dragger.drag(frame, controlPanel);

        frame.add(controlPanel);
        frame.add(gamePanel);

        afterPreparation();

        // shutdown tasks
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                TaskExecutor.shutdown();
            }
        });
    }

    private void appConfigure() {
        // UI settings
        Map<String, Object> uiConfig = ConfigResolver.resolveOneLevelMap("ui");
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.putAll(uiConfig);
        uiConfig.clear();

        // System settings
        Map<String, Object> systemConfig = ConfigResolver.resolveMap("system");
        systemConfig.forEach((k, v) -> System.setProperty(k, v.toString()));
        systemConfig.clear();
    }

    private void frameSettings() {
        frame.setLocation(70, 80);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setTitle(Resource.getString("title"));
        frame.setResizable(false);
    }

    private void afterPreparation() {
        Handler.getInstance().prepareGame(GameLevel.DEFAULT, true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MineSweeper();
    }
}
