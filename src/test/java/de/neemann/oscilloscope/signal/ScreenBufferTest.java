package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.graphics.Style;
import junit.framework.TestCase;

import java.awt.*;

public class ScreenBufferTest extends TestCase {

    public void testColorTable() {
        assertEquals(Style.SCREEN.getColor(), ScreenBuffer.SPEEDCOLOR[ScreenBuffer.SPEEDCOLOR.length - 1]);
        assertEquals(Color.GREEN, ScreenBuffer.SPEEDCOLOR[0]);
    }

}