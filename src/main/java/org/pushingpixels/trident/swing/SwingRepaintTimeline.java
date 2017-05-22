/*
 * Copyright (c) 2005-2017 Trident Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Trident Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.pushingpixels.trident.swing;

import java.awt.Component;
import java.awt.Rectangle;

import org.pushingpixels.trident.Timeline;

public class SwingRepaintTimeline extends Timeline {
    private SwingRepaintCallback repaintCallback;

    public SwingRepaintTimeline(Component mainTimelineComp) {
        this(mainTimelineComp, null);
    }

    public SwingRepaintTimeline(Component mainTimelineComp, Rectangle toRepaint) {
        super(mainTimelineComp);
        this.repaintCallback = new SwingRepaintCallback(mainTimelineComp, toRepaint);
        this.addCallback(this.repaintCallback);
    }

    public void forceRepaintOnNextPulse() {
        this.repaintCallback.forceRepaintOnNextPulse();
    }

    public void setAutoRepaintMode(boolean autoRepaintMode) {
        this.repaintCallback.setAutoRepaintMode(autoRepaintMode);
    }

    public void setRepaintRectangle(Rectangle rect) {
        this.repaintCallback.setRepaintRectangle(rect);
    }

    @Override
    public void play() {
        throw new UnsupportedOperationException("Only infinite looping is supported");
    }

    @Override
    public void playReverse() {
        throw new UnsupportedOperationException("Only infinite looping is supported");
    }

    @Override
    public void replay() {
        throw new UnsupportedOperationException("Only infinite looping is supported");
    }

    @Override
    public void replayReverse() {
        throw new UnsupportedOperationException("Only infinite looping is supported");
    }

    @Override
    public void playLoop(int loopCount, RepeatBehavior repeatBehavior) {
        if (loopCount >= 0) {
            throw new UnsupportedOperationException("Only infinite looping is supported");
        }
        super.playLoop(loopCount, repeatBehavior);
    }
}