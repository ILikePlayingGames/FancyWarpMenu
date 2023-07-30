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

import net.minecraft.client.Minecraft;

/**
 * This base class tracks a start time, end time, and the current system time's difference from the end time.
 * Subclasses are meant to extend this with GUI attributes whose values will change as time passes.
 */
public class Transition {
    protected long startTime;
    protected long endTime;
    protected long duration;
    protected long currentTime;
    protected float progress;
    protected boolean finished;

    public Transition(long duration) {
        startTime = Minecraft.getSystemTime();
        this.duration = duration;
        endTime = startTime + duration;
        finished = false;
    }

    public float getProgress() {
        return progress;
    }

    public boolean isFinished() {
        return finished;
    }

    /**
     * Recalculate the progress of the transition using the current system time
     */
    public void step() {
        if (!finished) {
            currentTime = Minecraft.getSystemTime();
            progress = (float) (currentTime - startTime) / duration;

            if (currentTime >= endTime) {
                progress = 1;
                finished = true;
            }
        }
    }
}
