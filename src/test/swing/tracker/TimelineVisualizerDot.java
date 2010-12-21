package test.swing.tracker;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class TimelineVisualizerDot {
	private float opacity;

	private Point location;

	public TimelineVisualizerDot() {
		this.opacity = 1.0f;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public void paint(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setComposite(AlphaComposite.SrcOver.derive(this.opacity));
		Shape dotShape = new Ellipse2D.Double(this.location.x - 3,
				this.location.y - 3, 6, 6);
		g2d.setColor(Color.green.darker());
		g2d.fill(dotShape);
		g2d.setColor(Color.black);
		g2d.draw(dotShape);

		g2d.dispose();
	}
}