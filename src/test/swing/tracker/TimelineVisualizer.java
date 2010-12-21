package test.swing.tracker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

public class TimelineVisualizer extends JComponent {
	private List<TimelineVisualizerDot> dots;

	public TimelineVisualizer() {
		this.dots = new ArrayList<TimelineVisualizerDot>();
	}

	public void addDot(float absoluteTimelinePosition,
			float perceivedTimelinePosition) {
		synchronized (this.dots) {
			final TimelineVisualizerDot dot = new TimelineVisualizerDot();
			dot.setLocation(new Point(
					(int) (absoluteTimelinePosition * getWidth()),
					(int) (perceivedTimelinePosition * getHeight())));
			this.dots.add(dot);

			Timeline dotTimeline = new Timeline(dot);
			dotTimeline.addPropertyToInterpolate("opacity", 1.0f, 0.0f);
			dotTimeline.addCallback(new TimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(TimelineState oldState,
						TimelineState newState, float durationFraction,
						float timelinePosition) {
					if (newState == TimelineState.DONE) {
						synchronized (dots) {
							dots.remove(dot);
						}
					}
				}
			});
			dotTimeline.setDuration(2000);
			dotTimeline.play();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		synchronized (this.dots) {
			for (TimelineVisualizerDot dot : this.dots) {
				dot.paint(g2d);
			}
		}

		g2d.dispose();
	}

}
