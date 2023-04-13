package source.component.custom;

import source.constant.Const;
import source.util.ColorFadeData;
import source.util.ColorFader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.util.function.IntPredicate;

import static javax.swing.SwingConstants.CENTER;

class CustomDialogTF {

    public static final ColorFadeData WRONG_DATA_ALERT =
            new ColorFadeData(200, 25, Color.CYAN, Color.RED, Color.CYAN);
    final JTextField textField;
    final JLabel label;
    private final IntPredicate defaultPredict;

    private int value;

    CustomDialogTF(String title) {
        label = new JLabel();
        label.setText(title);
        label.setHorizontalAlignment(CENTER);
        label.setFont(Const.globalFont14);
        label.setForeground(Color.magenta);

        textField = new JTextField();
        textField.setHorizontalAlignment(CENTER);
        textField.setBackground(Color.lightGray);
        textField.setForeground(Color.CYAN);
        textField.setFont(Const.globalFont14);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c < '0' || c > '9') {
                    e.consume();
                }
            }
        });
        defaultPredict = v -> v > 0;
    }

    boolean readValue(IntPredicate predicate) {
        boolean test = false;
        if (textField.getText().length() != 0) {
            BigInteger bi = new BigInteger(textField.getText());
            value = bi.intValue();
            test = predicate.test(value);
        }
        if (!test) {
            ColorFader.getInstance().fadeFG(textField, WRONG_DATA_ALERT);
        }
        return test;
    }

    boolean readValue() {
        return readValue(defaultPredict);
    }

    int getValue() {
        return value;
    }

    void clearTextField() {
        textField.setText("");
    }
}
