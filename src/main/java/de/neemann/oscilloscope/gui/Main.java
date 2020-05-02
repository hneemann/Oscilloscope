package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.signal.FastModel;

import javax.swing.*;

import java.awt.event.ActionEvent;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

public class Main extends JFrame {


    public Main() {
        super("Oscilloscope");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Oscilloscope oscilloscope = new Oscilloscope();
        ElementComponent el = new ElementComponent(oscilloscope.setPos(SIZE + SIZE2, SIZE + SIZE2));
        getContentPane().add(el);

        el.setModel(new FastModel(t -> Math.sin(t * 40 * Math.PI), t -> Math.cos(t * 51 * Math.PI), oscilloscope));

        Timer timer = new Timer(10, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                el.repaint();
            }
        });
        timer.start();

        pack();
        setSize(SIZE * 61, SIZE * 31);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

}
