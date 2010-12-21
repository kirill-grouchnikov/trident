package test.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.swt.SWTRepaintTimeline;

public class ShapesFrame {
	static Color COLOR_BLUE;
	static Color COLOR_GREEN;

	public static class ShapesPanel extends Canvas {
		private List<MyShape> shapes;
		private boolean toAddRectangle;

		private Color topColor;

		private Color bottomColor;

		public ShapesPanel(Composite parent) {
			super(parent, SWT.DOUBLE_BUFFERED);

			this.shapes = new ArrayList<MyShape>();
			this.topColor = COLOR_BLUE;
			this.bottomColor = COLOR_GREEN;

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					addShape(e.x, e.y);
				}
			});

			this.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					int width = e.width;
					int height = e.height;
					GC gc = e.gc;
					gc.setBackgroundPattern(new Pattern(e.display, 0, 0, 0,
							height, topColor, bottomColor));
					gc.fillRectangle(0, 0, width, height);
					gc.setAntialias(SWT.ON);

					synchronized (shapes) {
						for (MyShape shape : shapes) {
							shape.paint(gc);
						}
					}
				}
			});

			// animate the gradient endpoint colors in an infinite timeline
			Timeline colorTimeline = new SWTRepaintTimeline(this);
			colorTimeline.addPropertyToInterpolate("topColor", COLOR_BLUE,
					COLOR_GREEN);
			colorTimeline.addPropertyToInterpolate("bottomColor", COLOR_GREEN,
					COLOR_BLUE);
			colorTimeline.setDuration(1000);
			colorTimeline.playLoop(RepeatBehavior.REVERSE);
		}

		public void setTopColor(Color topColor) {
			this.topColor = topColor;
		}

		public void setBottomColor(Color bottomColor) {
			this.bottomColor = bottomColor;
		}

		public void addShape(MyShape shape) {
			synchronized (this.shapes) {
				this.shapes.add(shape);
			}
		}

		public void removeShape(MyShape shape) {
			synchronized (this.shapes) {
				this.shapes.remove(shape);
			}
		}

		private void addShape(int x, int y) {
			if (toAddRectangle) {
				final MyShape shape = new MyRectangle(x, y, 0, 0);
				addShape(shape);
				Timeline timelineRectangleFade = new Timeline(shape);
				timelineRectangleFade.addPropertyToInterpolate("x", x, x - 100);
				timelineRectangleFade.addPropertyToInterpolate("y", y, y - 100);
				timelineRectangleFade.addPropertyToInterpolate("width", 0, 200);
				timelineRectangleFade
						.addPropertyToInterpolate("height", 0, 200);
				timelineRectangleFade.addPropertyToInterpolate("rotation", 0,
						180);
				timelineRectangleFade.addPropertyToInterpolate("opacity", 1.0f,
						0.0f);
				timelineRectangleFade
						.addCallback(new TimelineCallbackAdapter() {
							@Override
							public void onTimelineStateChanged(
									TimelineState oldState,
									TimelineState newState,
									float durationFraction,
									float timelinePosition) {
								if (newState == TimelineState.DONE)
									removeShape(shape);
							}
						});
				timelineRectangleFade.setDuration(1000);
				timelineRectangleFade.play();
			} else {
				final MyShape shape = new MyCircle(x, y, 0);
				addShape(shape);
				Timeline timelineCircleFade = new Timeline(shape);
				timelineCircleFade.addPropertyToInterpolate("radius", 0, 100);
				timelineCircleFade.addPropertyToInterpolate("opacity", 1.0f,
						0.0f);
				timelineCircleFade.addCallback(new TimelineCallbackAdapter() {
					@Override
					public void onTimelineStateChanged(TimelineState oldState,
							TimelineState newState, float durationFraction,
							float timelinePosition) {
						if (newState == TimelineState.DONE)
							removeShape(shape);
					}
				});
				timelineCircleFade.setDuration(1000);
				timelineCircleFade.play();
			}
			toAddRectangle = !toAddRectangle;
		}
	}

	public interface MyShape {
		public void paint(GC gc);
	}

	public static class MyRectangle implements MyShape {
		float x;

		float y;

		float width;

		float height;

		float opacity;

		float rotation;

		public MyRectangle(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.opacity = 1.0f;
			this.rotation = 0.0f;
		}

		public void setX(float x) {
			this.x = x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		public void setHeight(float height) {
			this.height = height;
		}

		public void setOpacity(float opacity) {
			this.opacity = opacity;
		}

		public void setRotation(float rotation) {
			this.rotation = rotation;
		}

		@Override
		public void paint(GC gc) {
			gc.setAlpha((int) (255 * this.opacity));
			gc.setBackground(COLOR_GREEN);
			float xc = this.x + this.width / 2;
			float yc = this.y + this.height / 2;

			Transform transform = new Transform(gc.getDevice());
			transform.translate((int) xc, (int) yc);
			transform.rotate(this.rotation);
			gc.setTransform(transform);
			gc.fillRectangle((int) -this.width / 2, (int) -this.height / 2,
					(int) this.width, (int) this.height);
			transform.dispose();
		}
	}

	public static class MyCircle implements MyShape {
		float x;

		float y;

		float radius;

		float opacity;

		public MyCircle(float x, float y, float radius) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.opacity = 1.0f;
		}

		public void setX(float x) {
			this.x = x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public void setRadius(float radius) {
			this.radius = radius;
		}

		public void setOpacity(float opacity) {
			this.opacity = opacity;
		}

		@Override
		public void paint(GC gc) {
			gc.setAlpha((int) (255 * this.opacity));
			gc.setBackground(COLOR_GREEN);

			Transform transform = new Transform(gc.getDevice());
			gc.setTransform(transform);
			gc.fillOval((int) (this.x - this.radius),
					(int) (this.y - this.radius), (int) (2 * radius),
					(int) (2 * radius));
			transform.dispose();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("SWT Shapes");
		shell.setSize(600, 500);
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		COLOR_BLUE = new Color(display, 128, 128, 255);
		COLOR_GREEN = new Color(display, 128, 255, 128);
		new ShapesPanel(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
