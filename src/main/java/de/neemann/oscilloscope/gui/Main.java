package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.Scaling;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.graphics.GraphicMinMax;
import de.neemann.oscilloscope.experiments.Experiment;
import de.neemann.oscilloscope.experiments.Experiments;
import de.neemann.oscilloscope.experiments.General;

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
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.prefs.Preferences;

import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE2;

/**
 * The main frame.
 */
public class Main extends JFrame {
    private static final String MESSAGE = "<h1>Oscilloscope</h1>A simple simulation of an analog oscilloscope.\n"
            + "        Written by H. Neemann in 2020.\n"
            + "\n"
            + "        Visit the project at <a href=\"https://github.com/hneemann/oscilloscope\">GitHub</a>.\n"
            + "        At Github you can also <a href=\"https://github.com/hneemann/oscilloscope/releases/latest\">download</a> "
            + "        the latest release.\n"
            + "\n"
            + "        There you also can file an <a href=\"https://github.com/hneemann/oscilloscope/issues/new?body=version:%20[[version]]&labels=bug\">issue</a> or\n"
            + "        suggest an <a href=\"https://github.com/hneemann/oscilloscope/issues/new?labels=enhancement\">enhancement</a>";


    private static final Preferences PREFS = Preferences.userRoot().node("oscilloscope");
    private final ElementComponent elementComponent;
    private Container<?> mainContainer;

    /**
     * Creates a new main window.
     *
     * @param experiment the selected experiment
     */
    public Main(String experiment) {
        super("Oscilloscope");
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
        view.add(createScaleMenu());
        if (Debug.isDebug())
            view.add(new JMenuItem(new AbstractAction("Info") {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Oscilloscope.toggleDebug();
                    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
                    long[] ids = bean.findMonitorDeadlockedThreads();
                    if (ids != null) {
                        ThreadInfo[] threadInfo = bean.getThreadInfo(ids);
                        for (ThreadInfo ti : threadInfo) {
                            System.out.println(ti.getThreadName());
                            for (StackTraceElement st : ti.getStackTrace())
                                System.out.println(st);
                        }
                    } else
                        System.out.println("no deadlock");
                }
            }));
        view.add(InfoDialog.getInstance().createMenuItem(this, MESSAGE));

        setJMenuBar(bar);


        Experiment exp = null;
        if (experiment != null)
            exp = Experiments.getInstance().get(experiment);
        if (exp == null)
            exp = Experiments.getInstance().get(General.NAME);

        setExperiment(exp);
        setLocationRelativeTo(null);
    }

    private JMenu createScaleMenu() {
        JMenu menu = new JMenu("Window Size");
        for (int i = 6; i <= 10; i++) {
            int s = i * 2;
            int x = 60 * s;
            int y = 41 * s;
            menu.add(new AbstractAction(x + "x" + y) {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Scaling.setDefault(s);
                    JOptionPane.showMessageDialog(Main.this, "Changing the window size requires a restart!");
                }
            });
        }
        return menu;
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

        System.out.println((minMax.getMax().x - minMax.getMin().x) + "x"
                + (minMax.getMax().y - minMax.getMin().y));

        elementComponent.setPreferredSize(new Dimension(minMax.getMax().x + SIZE2, minMax.getMax().y + SIZE2));
        elementComponent.setContainer(mainContainer);
        pack();
    }

    /**
     * Sets an exercise
     *
     * @param experiment the exercise to use
     */
    public void setExperiment(Experiment experiment) {
        setMain(experiment.create());
        if (Debug.isDebug())
            experiment.setup(elementComponent);
    }

    /**
     * Starts the main programm
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(SaveException.getInstance());

        String exp = null;
        for (String a : args) {
            if (a.equals("debug"))
                Debug.setDebug();
            else
                exp = a.replace('_', ' ');
        }
        String experiment = exp;

        SwingUtilities.invokeLater(() -> new Main(experiment).setVisible(true));
    }

    private final class ExerciseGenerator extends AbstractAction {
        private final Experiment experiment;

        private ExerciseGenerator(Experiment experiment) {
            super(experiment.toString());
            this.experiment = experiment;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setExperiment(experiment);
        }
    }

}
