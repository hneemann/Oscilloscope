/*
 * Copyright (c) 2016 Helmut Neemann
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.oscilloscope.draw.graphics;

import java.awt.*;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;

/**
 * Defines the styles (color, line thickness, font size and style) which are used to draw the circuit.
 */
public final class Style {
    /**
     * maximal line thickness
     */
    public static final int MAXLINETHICK = 4;

    private static final int LINETHICK = MAXLINETHICK;
    private static final int LINEDASH = 1;

    /**
     * used for all lines to draw the shapes itself
     */
    public static final Style NORMAL = new Builder().build();

    /**
     * Used for the power LED
     */
    public static final Style LED = new Builder(NORMAL).setFilled(true).setColor(Color.GREEN).build();

    /**
     * Style used for interactive elements
     */
    public static final Style SWITCH = new Builder().setFilled(true).setColor(Color.GRAY).build();

    /**
     * Style used for front panel print
     */
    public static final Style PRINT = new Builder().setColor(Color.GRAY).build();

    /**
     * Style used for front panel print
     */
    public static final Style PRINT_FILLED = new Builder(PRINT).setFilled(true).build();

    /**
     * Style used for the grid
     */
    public static final Style GRID = new Builder(PRINT).setColor(new Color(158, 158, 158, 128)).setThickness(1).build();

    /**
     * Style used for the screen
     */
    public static final Style SCREEN = new Builder().setColor(Color.GREEN.darker().darker().darker()).setFilled(true).build();

    /**
     * Used to draw the grid in the graph
     */
    public static final Style DASH = new Builder()
            .setThickness(LINEDASH)
            .setDash(new float[]{4, 4})
            .build();

    private final int thickness;
    private final boolean filled;
    private final Color color;
    private final int fontSize;
    private final float[] dash;
    private final BasicStroke stroke;
    private final Font font;
    private final boolean mattersForSize;
    private final int fontStyle;

    /**
     * Creates a new style
     *
     * @param builder the builder
     */
    private Style(Builder builder) {
        this.thickness = builder.thickness;
        this.filled = builder.filled;
        this.color = builder.color;
        this.fontSize = builder.fontSize;
        this.fontStyle = builder.fontStyle;
        this.dash = builder.dash;
        this.mattersForSize = builder.mattersForSize;

        stroke = new BasicStroke(thickness, builder.endCap, BasicStroke.JOIN_MITER, 10f, dash, 0f);
        font = new Font(null, fontStyle, fontSize);
    }

    /**
     * @return the lines thickness
     */
    public int getThickness() {
        return thickness;
    }

    /**
     * @return true if polygons and circles are filled
     */
    boolean isFilled() {
        return filled;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the Swing stroke which represents this style
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * @return the font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @return the font style
     */
    public int getFontStyle() {
        return fontStyle;
    }

    /**
     * @return the font to use
     */
    public Font getFont() {
        return font;
    }

    /**
     * @return the dash style
     */
    float[] getDash() {
        return dash;
    }

    /**
     * If this flag is set, the text is always to include in size estimation.
     *
     * @return the mattersForSize flag
     */
    boolean mattersAlwaysForSize() {
        return mattersForSize;
    }

    /**
     * Creates a new style, based on this style.
     *
     * @param fontSize       the new font size
     * @param mattersForSize the mattersForSize flag
     * @return Style the derived style with the given font size and mattersForSize flag.
     */
    public Style deriveFontStyle(int fontSize, boolean mattersForSize) {
        return new Builder(this)
                .setFontSize(fontSize)
                .setMattersForSize(mattersForSize)
                .build();
    }

    /**
     * Creates a new style, based on this style.
     *
     * @param color the new color
     * @return Style the derived style with the given color set.
     */
    public Style deriveColor(Color color) {
        return new Builder(this)
                .setColor(color)
                .build();
    }

    /**
     * Creates a new style, based on this style.
     *
     * @param thickness the line thickness
     * @param filled    filled flag for polygons
     * @param color     the color
     * @return the new style
     */
    public Style deriveStyle(int thickness, boolean filled, Color color) {
        return new Builder(this)
                .setThickness(thickness)
                .setFilled(filled)
                .setColor(color)
                .build();
    }

    /**
     * Creates a new style suited for filling polygons, based on this style.
     *
     * @param color the fill color
     * @return the nes style
     */
    public Style deriveFillStyle(Color color) {
        return new Builder(this)
                .setThickness(0)
                .setFilled(true)
                .setColor(color)
                .build();
    }

    private static final class Builder {
        private int thickness = LINETHICK;
        private boolean filled = false;
        private Color color = Color.BLACK;
        private int fontSize = SIZE * 15 / 20;
        private float[] dash = null;
        private boolean mattersForSize = false;
        private int endCap = BasicStroke.CAP_SQUARE;
        private int fontStyle = Font.PLAIN;

        private Builder() {
        }

        private Builder(Style style) {
            thickness = style.thickness;
            filled = style.filled;
            color = style.color;
            fontSize = style.fontSize;
            dash = style.getDash();
            mattersForSize = style.mattersForSize;
            endCap = style.stroke.getEndCap();
        }

        private Builder setThickness(int thickness) {
            this.thickness = thickness;
            return this;
        }

        private Builder setFilled(boolean filled) {
            this.filled = filled;
            return this;
        }

        private Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        private Builder setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        private Builder setFontStyle(int fontStyle) {
            this.fontStyle = fontStyle;
            return this;
        }

        private Builder setDash(float[] dash) {
            this.dash = dash;
            return this;
        }

        private Builder setMattersForSize(boolean mattersForSize) {
            this.mattersForSize = mattersForSize;
            return this;
        }

        private Builder setEndCap(int endCap) {
            this.endCap = endCap;
            return this;
        }

        private Style build() {
            return new Style(this);
        }

    }

}
