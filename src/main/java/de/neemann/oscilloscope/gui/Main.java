package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.graphics.GraphicMinMax;
import de.neemann.oscilloscope.experiments.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The main frame.
 */
public class Main extends JFrame {
    private static final Preferences PREFS = Preferences.userRoot().node("oscilloscope");
    private final ElementComponent elementComponent;
    private final boolean preset;
    private Container<?> mainContainer;
    private File lastExportFile;

    /**
     * Creates a new main window.
     *
     * @param preset if true, the system is setup properly
     */
    public Main(boolean preset) {
        super("Oscilloscope");
        this.preset = preset;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        elementComponent = new ElementComponent();
        getContentPane().add(elementComponent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                elementComponent.close();
            }
        });

        JMenuBar bar = new JMenuBar();

        JMenu exercises = new JMenu("Experiments");
        bar.add(exercises);
        for (Experiment e : Experiments.getInstance())
            exercises.add(new JMenuItem(new ExerciseGenerator(e)));


        JMenu view = new JMenu("View");
        bar.add(view);
        view.add(new JMenuItem(new AbstractAction("Screenshot") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BufferedImage ss = elementComponent.createScreenShot();
                if (ss != null) {
                    JFileChooser fc = new JFileChooser();
                    fc.setCurrentDirectory(getUserFolder());
                    fc.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphic", "png"));
                    if (fc.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        if (!f.getName().endsWith(".png"))
                            f = new File(f.getParent(), f.getName() + ".png");
                        try {
                            setUserFolder(f.getParent());
                            ImageIO.write(ss, "png", f);
                        } catch (IOException e) {
                            showError(e);
                        }
                    }
                }
            }
        }));

        setJMenuBar(bar);

        Experiment general = Experiments.getInstance().get(General.NAME);
        if (general != null)
            setExercise(general);
        else {
            setSize(800, 600);
        }

        setLocationRelativeTo(null);
    }

    private File getUserFolder() {
        String f = PREFS.get("folder", null);
        if (f == null)
            return null;
        return new File(f);
    }

    private void setUserFolder(String folder) {
        PREFS.put("folder", folder);
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Sets the main container
     *
     * @param main the main container
     */
    public void setMain(Container<?> main) {
        if (mainContainer != null)
            mainContainer.close();

        mainContainer = main;

        GraphicMinMax minMax = new GraphicMinMax();
        mainContainer.draw(minMax);

        elementComponent.setPreferredSize(new Dimension(minMax.getMax().x + SIZE2, minMax.getMax().y + SIZE2));
        elementComponent.setContainer(mainContainer);
        pack();
    }

    /**
     * Sets an exercise
     *
     * @param experiment the exercise to use
     */
    public void setExercise(Experiment experiment) {
        setMain(experiment.create());
        if (preset)
            experiment.setup(elementComponent);
    }

    /**
     * Starts the main programm
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(args.length == 1 && args[0].equals("preset")).setVisible(true));
    }

    private final class ExerciseGenerator extends AbstractAction {
        private final Experiment experiment;

        private ExerciseGenerator(Experiment experiment) {
            super(experiment.toString());
            this.experiment = experiment;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setExercise(experiment);
        }
    }

}
