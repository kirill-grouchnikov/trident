package test.swing;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.ease.Sine;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

public class CustomPropertyInterpolatorSource extends JFrame {
	private Ellipse2D ellipse;

	private static class Ellipse2DPropertyInterpolator implements
			PropertyInterpolator<Ellipse2D> {
		public Class getBasePropertyClass() {
			return Ellipse2D.class;
		}

		@Override
		public Ellipse2D interpolate(Ellipse2D from, Ellipse2D to,
				float timelinePosition) {
			double x = from.getX() + timelinePosition
					* (to.getX() - from.getX());
			double y = from.getY() + timelinePosition
					* (to.getY() - from.getY());
			double w = from.getWidth() + timelinePosition
					* (to.getWidth() - from.getWidth());
			double h = from.getHeight() + timelinePosition
					* (to.getHeight() - from.getHeight());
			return new Ellipse2D.Double(x, y, w, h);
		}
	}

	public CustomPropertyInterpolatorSource() {
		TridentConfig.getInstance().addPropertyInterpolator(
				new Ellipse2DPropertyInterpolator());

		Ellipse2D from = new Ellipse2D.Double(10, 10, 100, 50);
		Ellipse2D to = new Ellipse2D.Double(40, 40, 200, 120);
		this.ellipse = (Ellipse2D) from.clone();
		JPanel ellipsePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.red);
				g2d.fill(ellipse);

				g2d.dispose();
			}
		};
		ellipsePanel.setBackground(Color.black);

		Timeline ellipseTimeline = new Timeline(this);
		ellipseTimeline.addPropertyToInterpolate("ellipse", from, to);
		ellipseTimeline.setEase(new Sine());
		ellipseTimeline.setDuration(2000);
		ellipseTimeline.playLoop(RepeatBehavior.REVERSE);

		new SwingRepaintTimeline(ellipsePanel).playLoop(RepeatBehavior.LOOP);

		this.add(ellipsePanel);

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setEllipse(Ellipse2D ellipse) {
		this.ellipse = ellipse;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CustomPropertyInterpolatorSource().setVisible(true);
			}
		});
	}

}
