/*
 * Copyright (c) 2005-2010 Trident Kirill Grouchnikov. All Rights Reserved.
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
package org.pushingpixels.trident.swt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.RunOnUIThread;
import org.pushingpixels.trident.callback.TimelineCallback;

@RunOnUIThread
public class SWTRepaintCallback implements TimelineCallback {
	private Control control;

	private Rectangle rect;

	private AtomicBoolean repaintGuard;

	public SWTRepaintCallback(Control control) {
		this(control, null);
	}

	public SWTRepaintCallback(Control control, Rectangle rect) {
		if (control == null) {
			throw new NullPointerException("Control must be non-null");
		}
		this.control = control;
		if (rect != null) {
			this.rect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
		}
	}

	public synchronized void setAutoRepaintMode(boolean autoRepaintMode) {
		if (autoRepaintMode) {
			this.repaintGuard = null;
		} else {
			this.repaintGuard = new AtomicBoolean(false);
		}
	}

	public synchronized void forceRepaintOnNextPulse() {
		if (this.repaintGuard == null) {
			throw new IllegalArgumentException(
					"This method cannot be called on auto-repaint callback");
		}
		this.repaintGuard.set(true);
	}

	public synchronized void setRepaintRectangle(Rectangle rect) {
		if (rect == null) {
			this.rect = null;
		} else {
			this.rect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
		}
	}

	@Override
	public void onTimelinePulse(float durationFraction, float timelinePosition) {
		redrawAsNecessary();
	}

	@Override
	public void onTimelineStateChanged(TimelineState oldState,
			TimelineState newState, float durationFraction,
			float timelinePosition) {
		redrawAsNecessary();
	}

	private void redrawAsNecessary() {
		if (this.control.isDisposed())
			return;

		if (this.repaintGuard != null) {
			if (!this.repaintGuard.compareAndSet(true, false)) {
				// no need to repaint
				return;
			}
		}

		if (this.rect == null)
			this.control.redraw();
		else
			this.control.redraw(this.rect.x, this.rect.y, this.rect.width,
					this.rect.height, true);
	}
}
