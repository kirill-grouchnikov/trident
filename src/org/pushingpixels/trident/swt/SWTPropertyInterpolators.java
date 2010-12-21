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

import java.util.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;
import org.pushingpixels.trident.interpolator.PropertyInterpolatorSource;

/**
 * Built-in interpolators for SWT classes.
 * 
 * @author Kirill Grouchnikov
 */
public class SWTPropertyInterpolators implements PropertyInterpolatorSource {
	private Set<PropertyInterpolator> interpolators;

	public SWTPropertyInterpolators() {
		this.interpolators = new HashSet<PropertyInterpolator>();
		this.interpolators.add(new ColorInterpolator());
		this.interpolators.add(new PointInterpolator());
		this.interpolators.add(new RectangleInterpolator());
	}

	@Override
	public Set<PropertyInterpolator> getPropertyInterpolators() {
		return Collections.unmodifiableSet(this.interpolators);
	}

	static class ColorInterpolator implements PropertyInterpolator<Color> {
		@Override
		public Class getBasePropertyClass() {
			return Color.class;
		}

		@Override
		public Color interpolate(Color from, Color to, float timelinePosition) {
			return getInterpolatedColor(from, to, 1.0f - timelinePosition);
		}

		RGB getInterpolatedRGB(Color color1, Color color2, float color1Likeness) {
			if ((color1Likeness < 0.0) || (color1Likeness > 1.0))
				throw new IllegalArgumentException(
						"Color likeness should be in 0.0-1.0 range [is "
								+ color1Likeness + "]");
			int lr = color1.getRed();
			int lg = color1.getGreen();
			int lb = color1.getBlue();
			int dr = color2.getRed();
			int dg = color2.getGreen();
			int db = color2.getBlue();

			// using some interpolation values (such as 0.29 from issue 401)
			// results in an incorrect final value without Math.round.
			int r = (lr == dr) ? lr : (int) Math.round(color1Likeness * lr
					+ (1.0 - color1Likeness) * dr);
			int g = (lg == dg) ? lg : (int) Math.round(color1Likeness * lg
					+ (1.0 - color1Likeness) * dg);
			int b = (lb == db) ? lb : (int) Math.round(color1Likeness * lb
					+ (1.0 - color1Likeness) * db);

			return new RGB(r, g, b);
		}

		Color getInterpolatedColor(Color color1, Color color2,
				float color1Likeness) {
			if (color1.equals(color2))
				return color1;
			if (color1Likeness == 1.0)
				return color1;
			if (color1Likeness == 0.0)
				return color2;
			return new Color(Display.getDefault(), getInterpolatedRGB(color1,
					color2, color1Likeness));
		}
	}

	static class PointInterpolator implements PropertyInterpolator<Point> {
		public Point interpolate(Point from, Point to, float timelinePosition) {
			int x = from.x + (int) (timelinePosition * (to.x - from.x));
			int y = from.y + (int) (timelinePosition * (to.y - from.y));
			return new Point(x, y);
		}

		@Override
		public Class getBasePropertyClass() {
			return Point.class;
		}
	}

	static class RectangleInterpolator implements
			PropertyInterpolator<Rectangle> {
		public Rectangle interpolate(Rectangle from, Rectangle to,
				float timelinePosition) {
			int x = from.x + (int) (timelinePosition * (to.x - from.x));
			int y = from.y + (int) (timelinePosition * (to.y - from.y));
			int w = from.width
					+ (int) (timelinePosition * (to.width - from.width));
			int h = from.height
					+ (int) (timelinePosition * (to.height - from.height));
			return new Rectangle(x, y, w, h);
		}

		@Override
		public Class getBasePropertyClass() {
			return Rectangle.class;
		}
	}

}
