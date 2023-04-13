package source.component.game;

import source.constant.Const;
import source.resource.Resource;
import source.util.ColorFadeData;
import source.util.ColorFader;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Cell extends JLabel {
    public static final int CELL_WIDTH;
    public static final int CELL_HEIGHT = CELL_WIDTH = 31;

    boolean hasMine;
    boolean signed;
    boolean starter;
    private int row;
    private int col;
    private int nearbyMinesCount;
    private CellState state;

    private final ColorFader colorFader;
    private final GamePanel host;

    private static final Border initBorder = new BevelBorder(EtchedBorder.RAISED, Color.gray, Color.darkGray);
    private static final Border actBorder = new BevelBorder(EtchedBorder.LOWERED, Color.lightGray, Color.darkGray);

    private static final Border safeAreaBorder = new LineBorder(Color.cyan, 1);
    private static final Border ruinsBorder = new LineBorder(Color.magenta, 1);

    private static final ImageIcon detonationIcon = Resource.getIcon("detonation.png");
    private static final ImageIcon explosionIcon = Resource.getIcon("explosion.png");
    private static final ImageIcon mineIcon = Resource.getIcon("bomb.png");

    private static final Color[] indicatorColors = {Color.blue, Color.green, Color.red, Color.magenta,
            Color.yellow, Color.cyan, new Color(100, 175, 255), new Color(255, 175, 100)};
    private static final Color bg = Color.lightGray;

    private static final ArrayList<Cell> chosenCells = new ArrayList<>();

    private static final ColorFadeData bgFadeData = new ColorFadeData(300, 25, bg, Color.white);
    private static final ColorFadeData signFadeData =
            new ColorFadeData(200, 25, bg, Color.magenta);
    private static final ColorFadeData unSignFadeData =
            new ColorFadeData(200, 25, Color.magenta, bg);

    private static final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            Cell cell = (Cell) e.getSource();
            cell.afterReleased(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Cell cell = (Cell) e.getSource();
            cell.afterPressed(e);
        }
    };

    Cell(Integer row, Integer col) {
        setData(row, col);
        basicSet();

        host = GamePanel.getInstance();
        colorFader = ColorFader.getInstance();

        addMouseListener(mouseListener);
    }

    private void afterReleased(MouseEvent e) {
        // during the game
        if (host.isGaming()) {
            mouseAct(e);
        }
        // start the game
        else if (host.isIdling()) {
            host.startGame();
            showDiggingResult();
        }
    }

    private void afterPressed(MouseEvent e) {
        if (host.isGaming() && e.getButton() == MouseEvent.BUTTON1 && isState(CellState.VIRGIN)) {
            dig();
        } else if (host.isIdling()) {
            // prepare the game
            starter = true;
            host.prepareGame();
            dig();
        }
    }

    private void mouseAct(MouseEvent e) {
        int button = e.getButton();
        if (button == MouseEvent.BUTTON1 && isState(CellState.DUG)) {
            showDiggingResult();
        } else if (isState(CellState.VIRGIN)) {
            switchSignState();
        }
    }

    private void showDiggingResult() {
        colorFader.fadeBG(chosenCells, bgFadeData);
        chosenCells.forEach(cell -> {
            cell.setIndicatorNumber();
            cell.state = CellState.INDICATED;
        });
        host.reportClear(chosenCells.size());
    }

    private void basicSet() {
        setOpaque(true);
        setFont(Const.globalFont16);
        setHorizontalAlignment(CENTER);
    }

    void setData(int row, int col) {
        this.col = col;
        this.row = row;
        nearbyMinesCount = 0;

        hasMine = false;
        starter = false;
        signed = false;
        state = CellState.VIRGIN;

        setIcon(null);
        setText("");
        setBackground(bg);
        setBorder(initBorder);
    }

    void switchSignState() {
        signed = !signed;
        colorFader.fadeBG(this, signed ? signFadeData : unSignFadeData);
        host.addSignedCellsCount(signed ? 1 : -1);
    }

    void removeSignState() {
        if (signed) {
            signed = false;
            host.addSignedCellsCount(-1);
        }
    }

    private void dig() {
        if (isState(CellState.DUG)) {
            return;
        }
        if (hasMine) {
            explode();
            return;
        }

        setBorder(actBorder);
        chosenCells.clear();
        chainEffect();
    }

    private void chainEffect() {
        if (isState(CellState.VIRGIN)) {
            // current cell settings
            state = CellState.DUG;
            chosenCells.add(this);
            removeSignState();
            setBorder(safeAreaBorder);

            // around cells check
            checkAroundCells();
        }
    }

    private void checkAroundCells() {
        countNearbyMines();
        // open nearby lands
        if (nearbyMinesCount == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    Cell cell = host.getCell(row + i, col + j);
                    if (cell != null) {
                        cell.chainEffect();
                    }
                }
            }
        }
    }

    private void countNearbyMines() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Cell cell = host.getCell(row + i, col + j);
                if (cell != null && cell.hasMine) {
                    nearbyMinesCount++;
                }
            }
        }
    }

    //Set the number color depends on nearbyMinesCount
    void setIndicatorNumber() {
        if (nearbyMinesCount == 0) {
            return;
        }
        setText(String.valueOf(nearbyMinesCount));
        setForeground(indicatorColors[nearbyMinesCount - 1]);
    }

    private void explode() {
        setIcon(explosionIcon);
        hasMine = false;
        host.reportMine();
    }

    void detonate() {
        if (hasMine) {
            setIcon(detonationIcon);
            setBorder(ruinsBorder);
        }
    }

    void sweep() {
        if (hasMine) {
            setIcon(mineIcon);
            setBorder(safeAreaBorder);
        }
    }


    private boolean isState(CellState state) {
        return this.state == state;
    }

    private enum CellState {
        VIRGIN,
        DUG,
        INDICATED
    }
}