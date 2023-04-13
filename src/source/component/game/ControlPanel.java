package source.component.game;

import source.Handler;
import source.constant.Const;
import source.resource.Resource;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.SwingConstants.CENTER;

public class ControlPanel extends JPanel {
    public static final int CONTROL_PANEL_HEIGHT = 80;

    private Handler handler;
    private final JLabel infoLabel;
    private final JLabel resetButton;

    private static final Color bgc = Color.lightGray;
    private final Font titleFont = new Font("YouYuan", Font.BOLD, 14);
    private final Border outerBorder = new EtchedBorder(EtchedBorder.RAISED, Color.cyan, Color.darkGray);
    private final TitledBorder titledBorder = new TitledBorder(outerBorder,
            Handler.getInstance().getCurrentGameData().getDescription(),
            TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, titleFont, Color.cyan);

    private static ControlPanel instance;

    public static ControlPanel getInstance() {
        if (instance == null) {
            instance = new ControlPanel();
            instance.handler = Handler.getInstance();
        }
        return instance;
    }

    private ControlPanel() {
        basicSet();

        ImageIcon resetIcon = Resource.getIcon("reset.png");
        resetButton = new JLabel(resetIcon);
        resetButton.setBounds(14, 27, 40, 40);
        resetButton.setOpaque(true);
        resetButton.setBackground(bgc);
        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setControlPanelBackground(bgc);
                resetGame();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setControlPanelBackground(Color.darkGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setControlPanelBackground(bgc);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setControlPanelBackground(Color.gray);
            }
        });
        add(resetButton);

        infoLabel = new JLabel(Handler.getInstance().getCurrentGameData().getDescription());
        infoLabel.setFont(Const.globalFont16);
        infoLabel.setHorizontalAlignment(CENTER);
        infoLabel.setLocation(57, 28);

        add(infoLabel);
    }

    private void basicSet() {
        setLocation(0, 0);

        Border innerBorder = new LineBorder(Color.cyan, 1, true);
        Border compoundBorder = new CompoundBorder(titledBorder, innerBorder);

        setBorder(compoundBorder);
        setLayout(null);
        setBackground(bgc);
    }

    void showGameOverInfo(boolean success, boolean newRecord) {
        String indication;
        if (success) {
            infoLabel.setForeground(Color.green);
            indication = Resource.getString(newRecord ? "gamed.new-record" : "gamed.succeeded");
        } else {
            infoLabel.setForeground(Color.magenta);
            indication = Resource.getString("gamed.failed");
        }
        infoLabel.setText(indication + " " + Resource.getString("gamed.elapsed-time") +
                handler.getElapsedTime() + "s");
    }

    void showGameTimeInfo(int minesAmount, int signed) {
        infoLabel.setForeground(Color.magenta);
        infoLabel.setText("<html>" + Resource.getString("gaming.total-mines") + minesAmount + "/" +
                Resource.getString("gaming.planted-flags") + signed + "/" +
                Resource.getString("gaming.elapsed-time") + handler.getElapsedTime() + "s</html>");
    }

    private void setControlPanelBackground(Color c) {
        resetButton.setBackground(c);
        setBackground(c);
    }

    private void resetGame() {
        if (!GamePanel.getInstance().isIdling()) {
            handler.prepareGame(false);
        }
        GamePanel.getInstance().showGameTimeInfo();
    }

    public void resize(int gamePanelWidth) {
        setSize(gamePanelWidth, CONTROL_PANEL_HEIGHT);
        // To adapt the reset icon
        infoLabel.setSize(gamePanelWidth - 84, 40);
        titledBorder.setTitle(Handler.getInstance().getCurrentGameData().getDescription());
    }
}
