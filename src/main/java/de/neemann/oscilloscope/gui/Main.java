package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.diode.Diode;
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
     *
     * @param preset if true, the system is setup properly
     */
    public Main(boolean preset) {
        super("Oscilloscope");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Oscilloscope oscilloscope = new Oscilloscope();
        Generator gen1 = new Generator();
        Generator gen2 = new Generator();

        Container<?> main = new Container<>()
                .add(oscilloscope.setPos(SIZE + SIZE2, SIZE + SIZE2))
                .add(gen1.setPos(SIZE + SIZE2, SIZE * 31 + SIZE2))
//                .add(gen2.setPos(SIZE * 32 + SIZE2, SIZE * 31 + SIZE2));
                .add(new Diode().setPos(SIZE*32, SIZE*31+SIZE2));


        ElementComponent el = new ElementComponent(main);
        getContentPane().add(el);
        oscilloscope.setElementComponent(el);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                oscilloscope.close();
            }
        });

        pack();
        setSize(SIZE * 61, SIZE * 45);
        setLocationRelativeTo(null);

        if (preset) {
            oscilloscope.getHorizontal().getPosPoti().set(0.5);
            oscilloscope.getCh1().getPosPoti().set(0.5);
            oscilloscope.getCh1().getCouplingSwitch().set(2);
            oscilloscope.getCh1().getAmplitudeSwitch().down(false);
            oscilloscope.getCh2().getPosPoti().set(0.5);
            oscilloscope.getCh2().getCouplingSwitch().set(2);
            oscilloscope.getCh2().getAmplitudeSwitch().down(false);
            oscilloscope.getPowerSwitch().set(1);
            oscilloscope.getTrigger().getLevelPoti().set(0.5);

            gen1.getPowerSwitch().set(1);
            gen1.getAmplitude().set(0.1);
            gen1.setFrequencySwitch().down(false);
            gen1.setFrequencySwitch().down(false);
            gen2.getPowerSwitch().set(1);
            gen2.getAmplitude().set(0.1);
            gen2.setFrequencySwitch().down(false);
            gen2.setFrequencySwitch().down(false);
            gen2.setFrequencyFinePoti().set(Math.log(3) / Math.log(10));

            el.add(new Wire(gen1.getOutput(), oscilloscope.getCh1().getInput()));
            el.add(new Wire(gen2.getOutput(), oscilloscope.getCh2().getInput()));

        }

    }

    /**
     * Starts the main programm
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(args.length > 0).setVisible(true));
    }

}
