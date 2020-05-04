package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.Vector;

/**
 * A power switch
 */
public class PowerSwitch extends Switch<OffOn> {
    private static final Vector RAD = new Vector(SIZE2, SIZE2);
    private static final Vector POS = new Vector(SIZE * 2, SIZE);

    /**
     * Creates a new switch
     */
    public PowerSwitch() {
        super("Power");
        add(OffOn.values());
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        super.drawToOrigin(gr);
        if (getSelected()==OffOn.On)
            gr.drawCircle(POS.sub(RAD), POS.add(RAD), Style.LED);
        gr.drawCircle(POS.sub(RAD), POS.add(RAD), Style.NORMAL);
    }
}
