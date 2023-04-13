package source.component.custom;

import source.constant.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.util.function.IntPredicate;

import static javax.swing.SwingConstants.CENTER;

class CustomDialogTF {

    final JTextField textField;
    final JLabel label;
    private final IntPredicate defaultPredict;

    private int value;

    private static Color defaultForeground;

    CustomDialogTF(String title) {
        label = new JLabel();
        label.setText(title);
        label.setHorizontalAlignment(CENTER);
        label.setFont(Const.globalFont14);
        label.setForeground(Color.magenta);

        textField = new JTextField();
        defaultForeground = textField.getForeground();
        textField.setHorizontalAlignment(CENTER);
        textField.setBackground(Color.lightGray);
        textField.setForeground(Color.CYAN);
        textField.setFont(Const.globalFont14);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                textField.setForeground(defaultForeground);
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
            textField.setForeground(Color.RED);
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
