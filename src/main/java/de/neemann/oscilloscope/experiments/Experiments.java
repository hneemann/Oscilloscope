package de.neemann.oscilloscope.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Central list of available exercises
 */
public final class Experiments implements Iterable<Experiment> {

    static final Experiments INSTANCE = new Experiments();

    private final ArrayList<Experiment> experiments;

    /**
     * @return returns the singleton
     */
    public static Experiments getInstance() {
        return INSTANCE;
    }

    private Experiments() {
        experiments = new ArrayList<>();
        register(new General());
        register(new DiodeExperiment());
        register(new CapacitorExperiment());
    }

    private void register(Experiment experiment) {
        experiments.add(experiment);
    }

    /**
     * @return the list of available exercises
     */
    public List<Experiment> getList() {
        return Collections.unmodifiableList(experiments);
    }

    @Override
    public Iterator<Experiment> iterator() {
        return getList().iterator();
    }

    /**
     * Returns the given exercise
     *
     * @param name the name of the exercise
     * @return the exercise or null if not found
     */
    public Experiment get(String name) {
        for (Experiment e : experiments)
            if (e.toString().equals(name))
                return e;
        return null;
    }
}
