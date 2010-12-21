package test.swing;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

public class Snake {
	public static class SnakePanelRectangle {
		private Color backgroundColor;

		private boolean isRollover;

		private Timeline rolloverTimeline;

		private SwingRepaintTimeline repaintTimeline;

		public SnakePanelRectangle(SwingRepaintTimeline repaintTimeline) {
			this.backgroundColor = Color.black;
			this.isRollover = false;

			this.rolloverTimeline = new Timeline(this);
			this.rolloverTimeline.addPropertyToInterpolate("backgroundColor",
					Color.yellow, Color.black);
			this.rolloverTimeline.setDuration(2500);

			this.repaintTimeline = repaintTimeline;
		}

		public void setRollover(boolean isRollover) {
			if (this.isRollover == isRollover)
				return;
			this.isRollover = isRollover;
			if (this.isRollover) {
				this.rolloverTimeline.replay();
			}
		}

		public void setBackgroundColor(Color backgroundColor) {
			this.backgroundColor = backgroundColor;
			this.repaintTimeline.forceRepaintOnNextPulse();
		}

		public Color getBackgroundColor() {
			return backgroundColor;
		}
	}

	private static class SnakePanel extends JPanel {

		private SnakePanelRectangle[][] grid;

		private int ROWS = 10;

		private int COLUMNS = 20;

		private int DIM = 20;

		public SnakePanel() {
			SwingRepaintTimeline repaintTimeline = new SwingRepaintTimeline(
					this);
			repaintTimeline.setAutoRepaintMode(false);

			this.grid = new SnakePanelRectangle[COLUMNS][ROWS];
			for (int i = 0; i < COLUMNS; i++) {
				for (int j = 0; j < ROWS; j++) {
					this.grid[i][j] = new SnakePanelRectangle(repaintTimeline);
				}
			}
			this.setPreferredSize(new Dimension(COLUMNS * (DIM + 1), ROWS
					* (DIM + 1)));

			repaintTimeline.playLoop(RepeatBehavior.LOOP);

			this.addMouseMotionListener(new MouseMotionAdapter() {
				int rowOld = -1;
				int colOld = -1;

				@Override
				public void mouseMoved(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();

					int column = x / (DIM + 1);
					int row = y / (DIM + 1);

					if ((column != colOld) || (row != rowOld)) {
						if ((colOld >= 0) && (rowOld >= 0))
							grid[colOld][rowOld].setRollover(false);
						grid[column][row].setRollover(true);
					}
					colOld = column;
					rowOld = row;
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();

			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			for (int i = 0; i < COLUMNS; i++) {
				for (int j = 0; j < ROWS; j++) {
					SnakePanelRectangle rect = this.grid[i][j];
					Color backgr = rect.getBackgroundColor();

					if (!Color.black.equals(backgr)) {
						g2d.setColor(backgr);
						g2d.fillRect(i * (DIM + 1), j * (DIM + 1), DIM, DIM);
					}
				}
			}

			g2d.dispose();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Snake");
				frame.add(new SnakePanel());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
