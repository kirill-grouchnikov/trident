package test.swt;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelineScenario;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.callback.TimelineScenarioCallback;
import org.pushingpixels.trident.ease.Spline;
import org.pushingpixels.trident.swt.SWTRepaintTimeline;

public class Fireworks extends Canvas {
	private Set<VolleyExplosion> volleys;

	private Map<VolleyExplosion, TimelineScenario> volleyScenarios;

	private boolean firstVolleyInitiated;

	public class SingleExplosion {
		float x;

		float y;

		float radius;

		float opacity;

		Color color;

		public SingleExplosion(Color color, float x, float y, float radius) {
			this.color = color;
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

		public void paint(GC gc) {
			gc.setAlpha((int) (255 * this.opacity));
			gc.setBackground(this.color);

			gc.fillOval((int) (this.x - this.radius),
					(int) (this.y - this.radius), (int) (2 * radius),
					(int) (2 * radius));
		}
	}

	public class VolleyExplosion {
		private int x;

		private int y;

		private Color color;

		private Set<SingleExplosion> circles;

		public VolleyExplosion(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			this.color = color;
			this.circles = new HashSet<SingleExplosion>();
		}

		public TimelineScenario getExplosionScenario() {
			TimelineScenario scenario = new TimelineScenario.Parallel();

			Random randomizer = new Random();
			int duration = 1000 + randomizer.nextInt(1000);
			for (int i = 0; i < 18; i++) {
				float dist = (float) (50 + 10 * Math.random());
				float radius = (float) (2 + 2 * Math.random());
				for (float delta = 0.6f; delta <= 1.0f; delta += 0.2f) {
					float circleRadius = radius * delta;

					double degrees = 20.0 * (i + Math.random());
					float radians = (float) (2.0 * Math.PI * degrees / 360.0);

					float initDist = delta * dist / 10.0f;
					float finalDist = delta * dist;
					float initX = (float) (this.x + initDist
							* Math.cos(radians));
					float initY = (float) (this.y + initDist
							* Math.sin(radians));
					float finalX = (float) (this.x + finalDist
							* Math.cos(radians));
					float finalY = (float) (this.y + finalDist
							* Math.sin(radians));

					SingleExplosion circle = new SingleExplosion(this.color,
							initX, initY, circleRadius);
					Timeline timeline = new Timeline(circle);
					timeline.addPropertyToInterpolate("x", initX, finalX);
					timeline.addPropertyToInterpolate("y", initY, finalY);
					timeline.addPropertyToInterpolate("opacity", 1.0f, 0.0f);
					timeline.setDuration(duration - 200
							+ randomizer.nextInt(400));
					timeline.setEase(new Spline(0.4f));

					synchronized (this.circles) {
						circles.add(circle);
					}
					scenario.addScenarioActor(timeline);
				}
			}

			return scenario;
		}

		public void paint(GC gc) {
			gc.setAntialias(SWT.ON);
			synchronized (this.circles) {
				for (SingleExplosion circle : this.circles) {
					circle.paint(gc);
				}
			}
		}
	}

	public Fireworks(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.fillRectangle(e.x, e.y, e.width, e.height);
				synchronized (volleys) {
					for (VolleyExplosion exp : volleys)
						exp.paint(gc);
				}
			}
		});

		Timeline repaint = new SWTRepaintTimeline(this);
		repaint.playLoop(RepeatBehavior.LOOP);

		this.volleys = new HashSet<VolleyExplosion>();
		this.volleyScenarios = new HashMap<VolleyExplosion, TimelineScenario>();

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				synchronized (volleys) {
					for (TimelineScenario scenario : volleyScenarios.values())
						scenario.suspend();
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				synchronized (volleys) {
					for (TimelineScenario scenario : volleyScenarios.values())
						scenario.resume();
				}
			}
		});

		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (!firstVolleyInitiated) {
					firstVolleyInitiated = true;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							final int width = Fireworks.this.getBounds().width;
							final int height = Fireworks.this.getBounds().height;
							new Thread() {
								@Override
								public void run() {
									while (true) {
										addExplosions(5, width, height);
									}
								}
							}.start();
						}
					});
				}
			}
		});
	}

	private void addExplosions(int count, int width, int height) {
		final CountDownLatch latch = new CountDownLatch(count);

		Random randomizer = new Random();
		for (int i = 0; i < count; i++) {
			int r = randomizer.nextInt(255);
			int g = 100 + randomizer.nextInt(155);
			int b = 50 + randomizer.nextInt(205);
			Color color = new Color(Display.getDefault(), r, g, b);

			int x = 60 + randomizer.nextInt(width - 120);
			int y = 60 + randomizer.nextInt(height - 120);
			final VolleyExplosion exp = new VolleyExplosion(x, y, color);
			synchronized (volleys) {
				volleys.add(exp);
				TimelineScenario scenario = exp.getExplosionScenario();
				scenario.addCallback(new TimelineScenarioCallback() {
					@Override
					public void onTimelineScenarioDone() {
						synchronized (volleys) {
							volleys.remove(exp);
							volleyScenarios.remove(exp);
							latch.countDown();
						}
					}
				});
				volleyScenarios.put(exp, scenario);
				scenario.play();
			}
		}

		try {
			latch.await();
		} catch (Exception exc) {
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(480, 320);
		shell.setText("SWT Fireworks");
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		new Fireworks(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
