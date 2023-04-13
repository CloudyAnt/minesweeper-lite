package source;

import source.component.game.Cell;
import source.component.game.GamePanel;

public interface GameData {

    String getDescription();

    int getRowsNum();

    int getColsNum();

    int getMinesNum();

    default int getPanelWidth() {
        return getPanelWidth(getColsNum());
    }

    default int getPanelHeight() {
        return getPanelHeight(getRowsNum());
    }

    static int getPanelWidth(int cellColumnsAmount) {
        return cellColumnsAmount * Cell.CELL_WIDTH + GamePanel.GAME_BORDER_WIDTH * 2;
    }

    static int getPanelHeight(int cellRowsAmount) {
        return cellRowsAmount * Cell.CELL_HEIGHT + GamePanel.GAME_BORDER_WIDTH * 2;
    }

    default boolean isNotSameWith(GameData data) {
        if (data == null) {
            return true;
        }
        if (this == data) {
            return false;
        }
        return getRowsNum() != data.getRowsNum() || getColsNum() != data.getColsNum()
                || getMinesNum() != data.getMinesNum();
    }

}
