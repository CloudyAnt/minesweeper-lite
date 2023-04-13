package source.component.game;

import source.GameData;
import source.Handler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

import static source.component.game.ControlPanel.CONTROL_PANEL_HEIGHT;

public class GamePanel extends JPanel {
    public static final int GAME_BORDER_WIDTH = 2;

    private Handler handler;
    private ControlPanel controlPanel;
    private int signedCellsCount;

    private GameState gameState = GameState.IDLING;

    private int laidMinesCount;
    private int totalMines;
    private int leftCellsCount;
    private int panelWidth;
    private int panelHeight;

    private int totalCells;
    private int totalCellRows;
    private int totalCellColumns;
    private final Border gamingBorder;
    private final Border failedBorder;
    private final Border succeedBorder;

    final ArrayList<Cell> cellArray = new ArrayList<>();
    private static final Random random = new Random();

    private static GamePanel instance;

    public static GamePanel getInstance() {
        if (instance == null) {
            instance = new GamePanel();
            instance.handler = Handler.getInstance();
            instance.controlPanel = ControlPanel.getInstance();
        }
        return instance;
    }

    private GamePanel() {
        gamingBorder = new LineBorder(Color.cyan, 2);
        failedBorder = new LineBorder(Color.magenta, 2);
        succeedBorder = new LineBorder(Color.green, 2);
        setBorder(this.gamingBorder);

        setLocation(0, CONTROL_PANEL_HEIGHT);
    }

    void layMines() {
        if (laidMinesCount == totalMines) {
            return;
        } else {
            int row = random.nextInt(totalCellRows);
            int col = random.nextInt(totalCellColumns);
            Cell cell = getCell(row, col);
            if (!cell.hasMine && !cell.starter) {
                cell.hasMine = true;
                laidMinesCount++;
            }
        }
        layMines();
    }


    void prepareGame() {
        laidMinesCount = 0;
        leftCellsCount = totalCells;
        layMines();
    }

    void startGame() {
        gameState = GameState.GAMING;
        handler.startNewTimer();
    }

    void addSignedCellsCount(int add) {
        signedCellsCount += add;
        showGameTimeInfo();
    }

    public void showGameTimeInfo() {
        controlPanel.showGameTimeInfo(totalMines, signedCellsCount);
    }

    Cell getCell(int row, int col) {
        if (row < 0 || col < 0 || row >= totalCellRows || col >= totalCellColumns) {
            return null;
        }
        int index = totalCellColumns * row + col;
        return cellArray.get(index);
    }

    public void setBoardOf(BorderType borderType) {
        switch (borderType) {
            case START:
                setBorder(gamingBorder);
                break;
            case FAILED:
                setBorder(failedBorder);
                break;
            case SUCCESS:
                setBorder(succeedBorder);
        }
    }

    void reportMine() {
        digAllCell(Cell::detonate);
        gameOver(false);
    }

    void reportClear(int count) {
        leftCellsCount -= count;
        if (leftCellsCount == totalMines) {
            digAllCell(Cell::sweep);
            gameOver(true);
        }
    }

    void digAllCell(Consumer<Cell> digger) {
        for (int i = 0; i < totalCells; i++) {
            digger.accept(cellArray.get(i));
        }
    }

    private static final Color successColor = new Color(150, 255, 150);
    private static final Color failedColor = new Color(255, 150, 150);

    void gameOver(boolean success) {
        handler.finalizeTimer();
        gameState = GameState.ENDED;

        controlPanel.activeBgGradient(success ? successColor : failedColor);
        controlPanel.showGameOverInfo(success, success && handler.isNewRecord());
        setBoardOf(success ? BorderType.SUCCESS : BorderType.FAILED);
    }

    public boolean isIdling() {
        return gameState == GameState.IDLING;
    }

    public boolean isGaming() {
        return gameState == GameState.GAMING;
    }

    public void setData(GameData data) {
        panelHeight = data.getPanelHeight();
        panelWidth = data.getPanelWidth();
        totalCellRows = data.getRowsNum();
        totalCellColumns = data.getColsNum();
        totalMines = data.getMinesNum();
        totalCells = totalCellRows * totalCellColumns;
    }

    public void prepare(boolean resize) {
        showGameTimeInfo();

        if (resize) {
            setSize(panelWidth, panelHeight);

            removeAll();
            setLayout(new GridLayout(totalCellRows, totalCellColumns));

            arrangeCells((this::initCellAtIndex));
        } else {
            arrangeCells(((index, row, col) -> cellArray.get(index).setData(row, col)));
        }

        signedCellsCount = 0;
        setBoardOf(BorderType.START);
        gameState = GameState.IDLING;

        setVisible(false);
        setVisible(true);
    }

    private void arrangeCells(CellsArranger arranger) {
        int index = 0;
        for (int row = 0; row < totalCellRows; row++) {
            for (int col = 0; col < totalCellColumns; col++) {
                arranger.arrange(index, row, col);
                index++;
            }
        }
    }

    private void initCellAtIndex(int index, int row, int col) {
        Cell cell;
        if (index < cellArray.size()) {
            cell = cellArray.get(index);
            cell.setData(row, col);
        } else {
            cell = new Cell(row, col);
            cellArray.add(cell);
        }
        add(cell);
    }

    enum BorderType {
        START,
        FAILED,
        SUCCESS
    }

    enum GameState {
        IDLING,
        GAMING,
        ENDED
    }

    interface CellsArranger {
        void arrange(int index, int row, int col);
    }
}
