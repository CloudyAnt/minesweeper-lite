package source.component.menubar;

import source.GameLevel;
import source.Handler;
import source.ScoreRecorder;
import source.resource.Resource;

import javax.swing.*;

// Show record of each level, contains one cleaner button
class RecordMenuItem extends Menu {
    GameLevel gameLevel;

    RecordMenuItem(GameLevel gameLevel) {
        super();
        this.gameLevel = gameLevel;
        resetRecordText();

        JMenuItem cleaner = new MenuItem(Resource.getString("settings.records.reset"), () -> {
            Handler.getInstance().resetRecord(gameLevel);
            resetRecordText();
        });
        topMenu.add(cleaner);
        ScoreRecorder.getInstance().addScoreRefresher((level) -> {
            if (level == this.gameLevel) {
                resetRecordText();
            }
        });
    }

    void resetRecordText() {
        int recordedTime = Handler.getInstance().getRecord(gameLevel);
        String recordText = recordedTime == -1 ? "" : recordedTime + "";
        String text = gameLevel.getDescription() + "︰" + recordText + "       ";
        topMenu.setText(text);
    }
}
