package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.signal.Sin;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

public class Main extends JFrame {


    public Main() {
        super("Oscilloscope");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Oscilloscope oscilloscope = new Oscilloscope();
        ElementComponent el = new ElementComponent(oscilloscope.setPos(SIZE + SIZE2, SIZE + SIZE2));
        getContentPane().add(el);
        oscilloscope.setElementComponent(el);

        oscilloscope.setSignalCh1(new Sin(1, 40.00));
        oscilloscope.setSignalCh2(new Sin(1, 51.73));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                oscilloscope.close();
            }
        });

        pack();
        setSize(SIZE * 61, SIZE * 31);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

}
