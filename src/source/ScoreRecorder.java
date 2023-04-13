package source;

import source.constant.Const;
import source.exception.GameException;
import source.util.TaskExecutor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ScoreRecorder {
    // Highest score recording file
    private final File scoreSaver;

    private String recordString;
    private final int[] records = new int[3];
    private final ArrayList<Consumer<GameLevel>> scoreRefreshers = new ArrayList<>();

    private static ScoreRecorder instance;

    public static ScoreRecorder getInstance() {
        if (instance == null) {
            instance = new ScoreRecorder();
        }
        return instance;
    }

    private ScoreRecorder() {
        String saverName = "/record.rec";
        String saverPath = Const.BASE_PATH;

        File scoreSaverFolder = new File(saverPath);
        scoreSaver = new File(saverPath + saverName);

        if (!scoreSaverFolder.exists() && !scoreSaverFolder.mkdirs()) {
            throwException("Cannot create record file directory");
        }

        try {
            initRecorderFile();
        } catch (IOException e) {
            throwException(e.getMessage());
        }
    }

    private void initRecorderFile() throws IOException {
        if (scoreSaver.exists()) {
            try (FileReader in = new FileReader(scoreSaver)) {
                char[] ch = new char[100];
                int len = in.read(ch);
                recordString = new String(ch, 0, len);
            }
        } else {
            if (!scoreSaver.createNewFile()) {
                throwException("Cannot create record file");
            }
            try (FileWriter out = new FileWriter(scoreSaver)) {
                recordString = "|||";
                out.write(recordString);
            }
        }
        resolveRecord();
    }

    private void resolveRecord() {
        char[] ch = recordString.toCharArray();
        int ordinal = 0;
        StringBuilder s = new StringBuilder();
        for (char c : ch) {
            if (c != '|') {
                s.append(c);
            } else {
                String recordStr = s.toString();
                records[ordinal] = "".equals(recordStr) ? -1 : Integer.parseInt(recordStr);
                ordinal++;
                s = new StringBuilder();
            }
        }
    }

    private void writeNewRecords() {
        TaskExecutor.execute(() -> {
            try {
                Files.delete(scoreSaver.toPath());
            } catch (IOException e) {
                throwException("Cannot delete record file");
            }

            try {
                if (!scoreSaver.createNewFile()) {
                    throwException("Cannot create record file");
                }
                FileWriter out = new FileWriter(scoreSaver);
                recordString = records[0] + "|" + records[1] + "|" + records[2] + "|";
                out.write(recordString);
                out.close();
            } catch (IOException e) {
                throwException(e.getMessage());
            }
        });
    }

    boolean isNewRecord(int newRecord) {
        GameData currentGameData = Handler.getInstance().getCurrentGameData();
        if (!(currentGameData instanceof GameLevel)) {
            return false;
        }
        GameLevel gameLevel = (GameLevel) currentGameData;
        int ordinal = gameLevel.ordinal();
        if (ordinal >= records.length) {
            return false;
        }
        if (records[ordinal] == -1 || records[ordinal] > newRecord) {
            records[ordinal] = newRecord;
            scoreRefreshers.forEach(r -> r.accept(gameLevel));
            writeNewRecords();
            return true;
        }
        return false;
    }

    public int getRecord(GameLevel gameLevel) {
        return records[gameLevel.ordinal()];
    }

    public void resetRecord(GameLevel gameLevel) {
        int ordinal = gameLevel.ordinal();
        records[ordinal] = -1;
        writeNewRecords();
    }

    private void throwException(String message) {
        throw new GameException(message);
    }

    public void addScoreRefresher(Consumer<GameLevel> scoreRefresher) {
        scoreRefreshers.add(scoreRefresher);
    }
}
