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
package org.pushingpixels.trident.android;

import java.util.*;

import org.pushingpixels.trident.interpolator.PropertyInterpolator;
import org.pushingpixels.trident.interpolator.PropertyInterpolatorSource;

import android.graphics.*;

/**
 * Built-in interpolators for Android classes.
 * 
 * @author Kirill Grouchnikov
 */
public class AndroidPropertyInterpolators implements PropertyInterpolatorSource {
	private Set<PropertyInterpolator> interpolators;

	public static final PropertyInterpolator<Integer> COLOR_INTERPOLATOR = new ColorInterpolator();

	public AndroidPropertyInterpolators() {
		this.interpolators = new HashSet<PropertyInterpolator>();
		this.interpolators.add(COLOR_INTERPOLATOR);
		this.interpolators.add(new PointInterpolator());
		this.interpolators.add(new RectInterpolator());
		this.interpolators.add(new RectFInterpolator());
	}

	@Override
	public Set<PropertyInterpolator> getPropertyInterpolators() {
		return Collections.unmodifiableSet(this.interpolators);
	}

	static class ColorInterpolator implements PropertyInterpolator<Integer> {
		@Override
		public Class getBasePropertyClass() {
			return Color.class;
		}

		@Override
		public Integer interpolate(Integer from, Integer to,
				float timelinePosition) {
			return getInterpolatedRGB(from, to, 1.0f - timelinePosition);
		}

		int getInterpolatedRGB(Integer color1, Integer color2,
				float color1Likeness) {
			if ((color1Likeness < 0.0) || (color1Likeness > 1.0))
				throw new IllegalArgumentException(
						"Color likeness should be in 0.0-1.0 range [is "
								+ color1Likeness + "]");

			if (color1.equals(color2))
				return color1;
			if (color1Likeness == 1.0)
				return color1;
			if (color1Likeness == 0.0)
				return color2;

			int lr = Color.red(color1);
			int lg = Color.green(color1);
			int lb = Color.blue(color1);
			int la = Color.alpha(color1);
			int dr = Color.red(color2);
			int dg = Color.green(color2);
			int db = Color.blue(color2);
			int da = Color.alpha(color2);

			// using some interpolation values (such as 0.29 from issue 401)
			// results in an incorrect final value without Math.round.
			int r = (lr == dr) ? lr : (int) Math.round(color1Likeness * lr
					+ (1.0 - color1Likeness) * dr);
			int g = (lg == dg) ? lg : (int) Math.round(color1Likeness * lg
					+ (1.0 - color1Likeness) * dg);
			int b = (lb == db) ? lb : (int) Math.round(color1Likeness * lb
					+ (1.0 - color1Likeness) * db);
			int a = (la == da) ? la : (int) Math.round(color1Likeness * la
					+ (1.0 - color1Likeness) * da);

			return Color.argb(a, r, g, b);
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

	static class RectInterpolator implements PropertyInterpolator<Rect> {
		public Rect interpolate(Rect from, Rect to, float timelinePosition) {
			int left = from.left
					+ (int) (timelinePosition * (to.left - from.left));
			int top = from.top + (int) (timelinePosition * (to.top - from.top));
			int right = from.right
					+ (int) (timelinePosition * (to.right - from.right));
			int bottom = from.bottom
					+ (int) (timelinePosition * (to.bottom - from.bottom));
			return new Rect(left, top, right, bottom);
		}

		@Override
		public Class getBasePropertyClass() {
			return Rect.class;
		}
	}

	static class RectFInterpolator implements PropertyInterpolator<RectF> {
		public RectF interpolate(RectF from, RectF to, float timelinePosition) {
			float left = from.left
					+ (int) (timelinePosition * (to.left - from.left));
			float top = from.top
					+ (int) (timelinePosition * (to.top - from.top));
			float right = from.right
					+ (int) (timelinePosition * (to.right - from.right));
			float bottom = from.bottom
					+ (int) (timelinePosition * (to.bottom - from.bottom));
			return new RectF(left, top, right, bottom);
		}

		@Override
		public Class getBasePropertyClass() {
			return RectF.class;
		}
	}
}
