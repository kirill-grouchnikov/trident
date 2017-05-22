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
package org.pushingpixels.trident.callback;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;

/**
 * Callback for the fade tracker. Is used when the application (some UI
 * delegate) wishes to execute some code on the fade.
 * 
 * @author Kirill Grouchnikov
 */
public interface TimelineCallback {
    /**
     * Indicates that the timeline state has changed.
     * 
     * @param oldState
     *            The old timeline state.
     * @param newState
     *            The new timeline state.
     * @param durationFraction
     *            The current timeline duration fraction.Is guaranteed to be in
     *            0.0-1.0 range. The rate of change of this value is linear, and
     *            the value is proportional to
     *            {@link Timeline#setDuration(long)}.
     * @param timelinePosition
     *            The current timeline position. Is guaranteed to be in 0.0-1.0
     *            range. The rate of change of this value is not necessarily
     *            linear and is affected by the
     *            {@link Timeline#setEase(org.pushingpixels.trident.ease.TimelineEase)}
     *            .
     */
    public void onTimelineStateChanged(TimelineState oldState, TimelineState newState,
            float durationFraction, float timelinePosition);

    /**
     * Indicates that the timeline pulse has happened.
     * 
     * @param durationFraction
     *            The current timeline duration fraction.Is guaranteed to be in
     *            0.0-1.0 range. The rate of change of this value is linear, and
     *            the value is proportional to
     *            {@link Timeline#setDuration(long)}.
     * @param timelinePosition
     *            The current timeline position. Is guaranteed to be in 0.0-1.0
     *            range. The rate of change of this value is not necessarily
     *            linear and is affected by the
     *            {@link Timeline#setEase(org.pushingpixels.trident.ease.TimelineEase)}
     *            .
     */
    public void onTimelinePulse(float durationFraction, float timelinePosition);
}