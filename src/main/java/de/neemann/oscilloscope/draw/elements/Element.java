package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.*;

public abstract class Element<T extends Element<?>> {
    private Transform transform = Transform.IDENTITY;
    private GraphicMinMax minMax;

    abstract void drawToOrigin(Graphic gr);

    public final void draw(Graphic gr) {
        drawToOrigin(new GraphicTransform(gr, transform));
    }

    public T setTransform(Transform transform) {
        this.transform = transform;
        minMax = null;
        return (T) this;
    }

    public Transform getTransform() {
        return transform;
    }

    public T setPos(int x, int y) {
        return setTransform(new TransformTranslate(x, y));
    }

    public GraphicMinMax getBoundingBox() {
        if (minMax == null) {
            minMax = new GraphicMinMax(false);
            draw(minMax);
        }
        return minMax;
    }

    public void up() {
    }

    public void down() {
    }
}
