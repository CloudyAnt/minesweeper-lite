package source.component.custom;

import source.GameData;
import source.Handler;
import source.component.game.GamePanel;
import source.resource.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class CustomDialog {
    private final Handler handler;
    final CustomDialogTF[] tfs = new CustomDialogTF[3];
    private static CustomDialog instance;

    private final JFrame frameRender;

    private CustomDialog() {
        frameRender = new JFrame();

        handler = Handler.getInstance();
        basicFrameSet();

        tfs[0] = new CustomDialogTF(Resource.getString("custom.rows-num"));
        tfs[1] = new CustomDialogTF(Resource.getString("custom.cols-num"));
        tfs[2] = new CustomDialogTF(Resource.getString("custom.mines-num"));
        for (CustomDialogTF tf : tfs) {
            add(tf.label);
        }
        for (CustomDialogTF tf : tfs) {
            add(tf.textField);
        }

        add(new Button(Resource.getString("custom.confirm"), this::confirmValues));
        add(new Button(Resource.getString("custom.reset"), this::clearTextFields));
        add(new Button(Resource.getString("custom.cancel"), this::closeDialog));

        frameRender.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                handler.disableMainPanel();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                closeDialog();
            }
        });

        frameRender.setVisible(true);
    }

    private void basicFrameSet() {
        Point mainLocation = handler.getMainLocation();
        frameRender.setBounds(mainLocation.x + 100, mainLocation.y + 100, 300, 185);
        frameRender.setLayout(new GridLayout(3, 3));
        frameRender.setResizable(false);
        frameRender.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frameRender.setTitle(Resource.getString("custom.title"));
        frameRender.setIconImage(Resource.appIcon);
    }

    boolean testTextFields() {
        return (tfs[0].readValue() & tfs[1].readValue()) &&
                tfs[2].readValue(v -> v > 0 && tfs[0].getValue() * tfs[1].getValue() > v);
    }

    void add(JComponent c) {
        frameRender.add(c);
    }

    void closeDialog() {
        handler.enableMainPanel();
        frameRender.setVisible(false);
    }

    private void showDialog() {
        frameRender.setVisible(true);
    }

    public static void showCustomDialog() {
        if (instance == null) {
            instance = new CustomDialog();
        }
        instance.showDialog();
    }

    void confirmValues() {
        if (testTextFields()) {
            Handler.getInstance().newGame(new GameData() {
                @Override
                public String getDescription() {
                    return Resource.getString("level.custom") + " - " +
                            tfs[0].getValue() + "×" + tfs[1].getValue() + " ";
                }

                @Override
                public int getRowsNum() {
                    return tfs[0].getValue();
                }

                @Override
                public int getColsNum() {
                    return tfs[1].getValue();
                }

                @Override
                public int getMinesNum() {
                    return tfs[2].getValue();
                }
            });
            GamePanel.getInstance().showGameTimeInfo();
            closeDialog();
        }
    }

    void clearTextFields() {
        for (CustomDialogTF tf : tfs) {
            tf.clearTextField();
        }
    }

    static class Button extends JButton {

        public Button(String description, Runnable runnable) {
            setText(description);
            setForeground(Color.magenta);
            addActionListener(e -> runnable.run());
        }
    }
}
