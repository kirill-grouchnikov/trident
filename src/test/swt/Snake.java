package test.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.swt.SWTRepaintTimeline;

public class Snake {
	public static class SnakePanelRectangle {
		private Color backgroundColor;

		private boolean isRollover;

		private Timeline rolloverTimeline;

		private SWTRepaintTimeline repaintTimeline;

		public SnakePanelRectangle(SWTRepaintTimeline repaintTimeline) {
			this.backgroundColor = Display.getDefault().getSystemColor(
					SWT.COLOR_BLACK);
			this.isRollover = false;

			this.rolloverTimeline = new Timeline(this);
			this.rolloverTimeline.addPropertyToInterpolate("backgroundColor",
					Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
					Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
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

	private static class SnakePanel extends Canvas {

		private SnakePanelRectangle[][] grid;

		private int ROWS = 10;

		private int COLUMNS = 20;

		private int DIM = 20;

		public SnakePanel(Composite parent) {
			super(parent, SWT.DOUBLE_BUFFERED);

			SWTRepaintTimeline repaintTimeline = new SWTRepaintTimeline(this);
			repaintTimeline.setAutoRepaintMode(false);

			this.grid = new SnakePanelRectangle[COLUMNS][ROWS];
			for (int i = 0; i < COLUMNS; i++) {
				for (int j = 0; j < ROWS; j++) {
					this.grid[i][j] = new SnakePanelRectangle(repaintTimeline);
				}
			}

			repaintTimeline.playLoop(RepeatBehavior.LOOP);

			this.addMouseMoveListener(new MouseMoveListener() {
				int rowOld = -1;
				int colOld = -1;

				@Override
				public void mouseMove(MouseEvent e) {
					int x = e.x;
					int y = e.y;

					int column = x / (DIM + 1);
					int row = y / (DIM + 1);

					if ((column >= COLUMNS) || (row >= ROWS))
						return;

					if ((column != colOld) || (row != rowOld)) {
						if ((colOld >= 0) && (rowOld >= 0))
							grid[colOld][rowOld].setRollover(false);
						grid[column][row].setRollover(true);
					}
					colOld = column;
					rowOld = row;
				}
			});

			this.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					GC gc = e.gc;
					gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
					gc.fillRectangle(e.x, e.y, e.width, e.height);

					for (int i = 0; i < COLUMNS; i++) {
						for (int j = 0; j < ROWS; j++) {
							SnakePanelRectangle rect = grid[i][j];
							Color backgr = rect.getBackgroundColor();
							gc.setBackground(backgr);
							gc.fillRectangle(i * (DIM + 1), j * (DIM + 1), DIM,
									DIM);
						}
					}
				}
			});
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(430, 240);
		shell.setText("SWT Snake");
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		new SnakePanel(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
