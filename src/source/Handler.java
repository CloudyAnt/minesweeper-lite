package source;


import source.component.game.ControlPanel;
import source.component.game.GamePanel;
import source.util.Timer;

import java.awt.*;

// Main agent
public class Handler {

    private Frame main;
    private ScoreRecorder scoreRecorder;
    private int gamePanelWidth;
    private int gamePanelHeight;
    int gameTimes = 0;
    private ControlPanel controlPanel;
    private GamePanel gamePanel;

    private Compatibility compatibility;

    private GameData currentGameData = GameLevel.DEFAULT;
    private int elapsedTime = 0;
    private static Handler handler;

    static void init(Frame main) {
        if (handler == null) {
            handler = new Handler();
        }

        handler.main = main;
        handler.compatibility = Compatibility.getInstance();
        handler.scoreRecorder = ScoreRecorder.getInstance();
        handler.controlPanel = ControlPanel.getInstance();
        handler.gamePanel = GamePanel.getInstance();
    }

    public static Handler getInstance() {
        return handler;
    }

    Timer timer;

    private Handler() {
    }

    public boolean isNewRecord() {
        return scoreRecorder.isNewRecord(elapsedTime);
    }

    public int getRecord(GameLevel gameLevel) {
        return scoreRecorder.getRecord(gameLevel);
    }

    public void resetRecord(GameLevel gameLevel) {
        scoreRecorder.resetRecord(gameLevel);
    }

    public void newGame(GameData data) {
        boolean dataChanged = data.isNotSameWith(currentGameData);
        prepareGame(data, dataChanged);
    }

    void prepareGame(GameData data, boolean resize) {
        currentGameData = data;

        // reset data
        gamePanelHeight = data.getPanelHeight();
        gamePanelWidth = data.getPanelWidth();
        gamePanel.setData(data);

        prepareGame(resize);
    }

    public void prepareGame(boolean resize) {
        // initial handler data
        gameTimes++;
        elapsedTime = 0;
        finalizeTimer();

        // resize components
        if (resize) {
            compatibility.resize(gamePanelWidth, gamePanelHeight + ControlPanel.CONTROL_PANEL_HEIGHT);
            controlPanel.resize(gamePanelWidth);
        }
        // reset game panel
        gamePanel.prepare(resize);
    }

    public void startNewTimer() {
        timer = new Timer(second -> {
            elapsedTime = second;
            gamePanel.showGameTimeInfo();
        });
        timer.start();
    }

    public void finalizeTimer() {
        if (timer != null) {
            timer.end();
        }
    }

    public void enableMainPanel() {
        if (gamePanel.isGaming()) {
            timer.start();
        }
        gamePanel.setVisible(true);
        main.setEnabled(true);
    }

    public void disableMainPanel() {
        if (gamePanel.isGaming()) {
            timer.pause();
        }
        gamePanel.setVisible(false);
        main.setEnabled(false);
    }

    public Point getMainLocation() {
        return main.getLocation();
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public GameData getCurrentGameData() {
        return currentGameData;
    }
}
