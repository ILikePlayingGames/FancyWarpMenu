/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.tirelesstraveler.fancywarpmenu.gui.transitions;

/**
 * Transition used to change a GUI element's size as time passes
 */
public class ScaleTransition extends Transition {
    private final float START_SCALE;
    private final float END_SCALE;
    private float currentScale;

    public ScaleTransition(long duration, float startScale, float endScale) {
        super(duration);
        this.START_SCALE = startScale;
        this.END_SCALE = endScale;
        this.currentScale = startScale;
    }

    @Override
    public void step() {
        super.step();
        currentScale = START_SCALE + (END_SCALE - START_SCALE) * progress;
    }

    public float getStartScale() {
        return START_SCALE;
    }

    public float getEndScale() {
        return END_SCALE;
    }

    public float getCurrentScale() {
        return currentScale;
    }

    public void setCurrentScale(float scale) {
        currentScale = scale;
    }
}
