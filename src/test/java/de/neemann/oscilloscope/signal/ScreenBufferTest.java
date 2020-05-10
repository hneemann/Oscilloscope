package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.graphics.Style;
import junit.framework.TestCase;

import java.awt.*;

import static de.neemann.oscilloscope.signal.ScreenBuffer.MIN_TRACE_BRIGHT;

public class ScreenBufferTest extends TestCase {

    public void testColorTable() {
        assertEquals(Style.SCREEN.getColor().getGreen() + MIN_TRACE_BRIGHT, ScreenBuffer.SPEEDCOLOR[ScreenBuffer.SPEEDCOLOR.length - 1].getGreen());
        assertEquals(Color.GREEN, ScreenBuffer.SPEEDCOLOR[0]);
    }

}