package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Orientation;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.VectorInterface;

import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE;
import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE2;

/**
 * A selector knob which shows the unit of the selected values
 *
 * @param <T> the type of the item
 */
public class SelectorKnobUnit<T extends Magnify> extends SelectorKnob<T> {
    private final int radius;
    private final String unit;

    /**
     * Creates a new selector knob
     *
     * @param name   the name
     * @param radius the size
     * @param unit   the unit of the value
     */
    public SelectorKnobUnit(String name, int radius, String unit) {
        super(name, radius);
        this.radius = radius;
        this.unit = unit;
    }

    @Override
    public String getStringFor(T t) {
        String s = t.getValStr();
        if (s == null)
            return t.toString();
        else
            return s;
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        super.drawToOrigin(gr);


        float lastLine = 0;
        ArrayList<T> items = getItems();
        for (int i = 1; i < items.size(); i++) {
            String pr0 = items.get(i - 1).getPrefix();
            String pr1 = items.get(i).getPrefix();
            if (pr0 != null && pr1 != null && !pr0.equals(pr1)) {
                float line = i - 0.5f;
                VectorInterface p1 = getOffset(line, radius + SIZE * 2 + Style.MAXLINETHICK);
                VectorInterface p2 = getOffset(line, radius + SIZE * 2 + SIZE2);
                gr.drawLine(p1, p2, Style.PRINT);

                drawUnitText(gr, pr0 + unit, (lastLine + line) / 2);

                lastLine = line;
            }
        }
        drawUnitText(gr, items.get(items.size() - 1).getPrefix() + unit, (lastLine + items.size()) / 2);

    }

    private void drawUnitText(Graphic gr, String text, float textAngle) {
        VectorInterface textPos = getOffset(textAngle, radius + SIZE * 2);
        gr.drawText(textPos, text, Orientation.from(textPos), Style.PRINT);
    }

    @Override
    int getNameOfs() {
        return SIZE2 * 3 / 2;
    }
}
