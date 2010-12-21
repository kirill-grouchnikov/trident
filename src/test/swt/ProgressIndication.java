package test.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.interpolator.*;
import org.pushingpixels.trident.swt.SWTRepaintTimeline;

public class ProgressIndication {
	public static class ProgressPanel extends Canvas {
		private static final int INNER_HEIGHT = 60;

		private static final int INNER_WIDTH = 300;

		private static final int HIGHLIGHTER_HEIGHT = 2;

		private static final int HIGHLIGHTER_WIDTH = 58;

		private int xPosition;

		private float alpha;

		private boolean started;

		private Timeline progressTimeline;

		public ProgressPanel(Composite parent) {
			super(parent, SWT.DOUBLE_BUFFERED);

			this.xPosition = 0;
			this.alpha = 0;

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if (started)
						return;

					start();
					started = true;
				}
			});

			this.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					if (started) {
						progressTimeline.cancel();
						start();
					}
				}
			});

			new SWTRepaintTimeline(this).playLoop(RepeatBehavior.LOOP);

			this.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					GC gc = e.gc;
					gc.setAntialias(SWT.ON);

					int w = e.width;
					int h = e.height;

					// Full background
					Color bg = new Color(e.display, 12, 12, 12);
					gc.setBackground(bg);
					gc.fillRectangle(0, 0, w, h);
					bg.dispose();

					if (!started) {
						gc.setForeground(e.display
								.getSystemColor(SWT.COLOR_WHITE));
						Font font = new Font(e.display, "Tahoma", 13,
								SWT.NORMAL);
						gc.setFont(font);
						String caption = "Click to start";
						Point stringExtent = gc.stringExtent(caption);
						gc.drawString(caption, (w - stringExtent.x) / 2, h / 3
								- stringExtent.y);
						font.dispose();
					}

					// Inner gradient fill
					Color pattern1 = new Color(e.display, 47, 47, 47);
					Pattern innerPattern = new Pattern(e.display,
							(w - INNER_WIDTH) / 2.0f,
							(h - INNER_HEIGHT) / 2.0f,
							(w - INNER_WIDTH) / 2.0f,
							(h + INNER_HEIGHT) / 2.0f, pattern1, e.display
									.getSystemColor(SWT.COLOR_BLACK));
					gc.setBackgroundPattern(innerPattern);
					gc.fillRoundRectangle((w - INNER_WIDTH) / 2,
							(h - INNER_HEIGHT) / 2, INNER_WIDTH, INNER_HEIGHT,
							10, 10);
					gc.setBackgroundPattern(null);
					pattern1.dispose();
					innerPattern.dispose();

					// Inner contour
					Color inner = new Color(e.display, 67, 67, 67);
					gc.setForeground(inner);
					gc.drawRoundRectangle((w - INNER_WIDTH) / 2,
							(h - INNER_HEIGHT) / 2, INNER_WIDTH, INNER_HEIGHT,
							10, 10);
					inner.dispose();

					// Progress track
					int trackWidth = INNER_WIDTH - 36;
					int trackHeight = 1;
					Color track = new Color(e.display, 91, 91, 91);
					gc.setForeground(track);
					for (int i = 2; i >= 0; i--) {
						float trackAlpha = 1.0f;
						if (i == 1)
							trackAlpha = 0.3f;
						if (i == 2)
							trackAlpha = 0.1f;
						gc.setAlpha((int) (255 * trackAlpha));
						gc.drawRoundRectangle((w - INNER_WIDTH) / 2 + 18 - i, h
								/ 2 - i - 1, trackWidth + i * 2, trackHeight
								+ i * 2, 2 * i, 2 * i);
					}
					track.dispose();

					// Highlighter
					Color highlight = new Color(e.display, 13, 106, 206);
					gc.setBackground(highlight);
					gc.setAlpha((int) (255 * 0.1f * alpha));
					for (int i = 6; i >= 0; i--) {
						gc.fillOval(xPosition - HIGHLIGHTER_WIDTH / 2 - i, h
								/ 2 - HIGHLIGHTER_HEIGHT / 2 - i,
								HIGHLIGHTER_WIDTH + 2 * i, HIGHLIGHTER_HEIGHT
										+ 2 * i);
					}
					highlight.dispose();

					// Highlighter on track
					gc.setAlpha((int) (255 * alpha));
					Color trackHighlight = new Color(e.display, 136, 182, 231);
					gc.setBackground(trackHighlight);
					gc.fillRectangle(xPosition - 28, h / 2 - 1, 56, 2);
					trackHighlight.dispose();

					gc.setAlpha(255);
				}
			});
		}

		public void start() {
			progressTimeline = new Timeline(this);

			int startX = (this.getBounds().width - INNER_WIDTH) / 2 + 18
					+ HIGHLIGHTER_WIDTH / 2;
			int endX = (this.getBounds().width + INNER_WIDTH) / 2 - 18
					- HIGHLIGHTER_WIDTH / 2;
			progressTimeline
					.addPropertyToInterpolate("xPosition", startX, endX);

			KeyValues<Float> alphaValues = KeyValues.create(0.0f, 1.0f, 1.0f,
					0.0f);
			KeyTimes alphaTimes = new KeyTimes(0.0f, 0.3f, 0.7f, 1.0f);
			progressTimeline.addPropertyToInterpolate("alpha",
					new KeyFrames<Float>(alphaValues, alphaTimes));

			progressTimeline.setDuration(1500);
			progressTimeline.playLoop(RepeatBehavior.LOOP);
		}

		public void setXPosition(int position) {
			xPosition = position;
		}

		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("SWT progress");
		shell.setSize(400, 300);
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		new ProgressPanel(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
