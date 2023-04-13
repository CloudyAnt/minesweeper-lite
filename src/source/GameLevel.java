package source;

import source.resource.Resource;

// cell width: 31 pixel
public enum GameLevel implements GameData {
    SIMPLE(Resource.getString("level.simple"), 9, 9, 10),
    MEDIUM(Resource.getString("level.medium"), 16, 16, 30),
    HARD(Resource.getString("level.hard"), 16, 30, 70);
    public static final GameLevel DEFAULT = SIMPLE;

    private String description;
    private int panelHeight;
    private int panelWidth;
    private int rowsNum;
    private int colsNum;
    private int minesNum;

    GameLevel(String description, int rowsNum, int colsNum, int minesNum) {
        setData(description, rowsNum, colsNum, minesNum);
    }

    private void setData(String description, int rowsNum, int colsNum, int minesNum) {
        this.description = description;
        this.panelHeight = GameData.getPanelHeight(rowsNum);
        this.panelWidth = GameData.getPanelWidth(colsNum);
        this.colsNum = colsNum;
        this.rowsNum = rowsNum;
        this.minesNum = minesNum;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getRowsNum() {
        return rowsNum;
    }

    @Override
    public int getColsNum() {
        return colsNum;
    }

    @Override
    public int getMinesNum() {
        return minesNum;
    }

    @Override
    public int getPanelHeight() {
        return panelHeight;
    }

    @Override
    public int getPanelWidth() {
        return panelWidth;
    }

}
