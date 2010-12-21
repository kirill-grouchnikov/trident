package test.swing.tracker;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

public class BallPanel extends JComponent {
	private float ballY;

	public static final int RADIUS = 20;

	public BallPanel() {
		this.ballY = RADIUS;
	}

	public void setBallY(float ballY) {
		this.ballY = ballY;
	}

	public float getBallY() {
		return ballY;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.gray);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Shape shape = new Ellipse2D.Double(this.getWidth() / 2 - RADIUS,
				this.ballY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		g2d.setPaint(new RadialGradientPaint(this.getWidth() / 2 - RADIUS / 4,
				this.ballY - RADIUS / 5, RADIUS + RADIUS / 4, new float[] {
						0.0f, 1.0f }, new Color[] { Color.green,
						Color.green.darker().darker() }));
		g2d.fill(shape);
		g2d.setColor(new Color(0, 64, 0));
		g2d.draw(shape);

		g2d.dispose();

	}
}
