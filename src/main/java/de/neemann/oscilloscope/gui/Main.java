package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The main frame.
 */
public class Main extends JFrame {

    /**
     * Creates a new main window.
     */
    public Main() {
        super("Oscilloscope");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        Oscilloscope oscilloscope = new Oscilloscope();
        Generator gen1 = new Generator();
        Generator gen2 = new Generator();

        Container<?> main = new Container<>()
                .add(oscilloscope.setPos(SIZE + SIZE2, SIZE + SIZE2))
                .add(gen1.setPos(SIZE + SIZE2, SIZE * 31+SIZE2))
                .add(gen2.setPos(SIZE * 32 + SIZE2, SIZE * 31+SIZE2));


        ElementComponent el = new ElementComponent(main);
        getContentPane().add(el);
        oscilloscope.setElementComponent(el);

        oscilloscope.setSignalCh1(gen1);
        oscilloscope.setSignalCh2(gen2);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                oscilloscope.close();
            }
        });

        pack();
        setSize(SIZE * 61, SIZE * 45);
        setLocationRelativeTo(null);
    }

    /**
     * Starts the main programm
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

}
