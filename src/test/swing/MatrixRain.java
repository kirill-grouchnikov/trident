package test.swing;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelineScenario;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.callback.TimelineScenarioCallback;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

public class MatrixRain {
	private static Font font;

	public static class Letter {
		private int x;

		private int y;

		private Color color;

		private float opacity;

		private char c;

		public Letter(int x, int y, char c) {
			this.x = x;
			this.y = y;
			this.c = c;
			this.color = Color.white;
			this.opacity = 0.0f;
		}

		public void setOpacity(float opacity) {
			this.opacity = opacity;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public void paint(Graphics g) {
			if (this.opacity == 0.0f)
				return;

			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setFont(font);
			g2d.setColor(this.color);
			g2d.setComposite(AlphaComposite.SrcOver.derive(this.opacity));
			g2d.drawString("" + this.c, this.x, this.y);

			g2d.dispose();
		}
	}

	public static class Drop {
		private List<Letter> letters;

		public Drop() {
			this.letters = new ArrayList<Letter>();
		}

		public TimelineScenario getScenario(int x) {
			TimelineScenario result = new TimelineScenario.Parallel();
			Random randomizer = new Random();
			// how many letters will there be?
			int totalLetterCount = 5 + randomizer.nextInt(20);
			int initialDelay = randomizer.nextInt(1000);
			int duration = 1000 + randomizer.nextInt(100);
			for (int i = 0; i < totalLetterCount; i++) {
				int y = font.getSize() * i;
				// choose random katakana letter
				// int letterIndex = (int) (0x30A0 + Math.random()
				// * (0x30FF - 0x30A0));
				int start = 33;
				int delta = 95;
				char c = (char) (start + Math.random() * delta);
				Letter l = new Letter(x, y, c);
				this.letters.add(l);
				Timeline t = new Timeline(l);
				t.addPropertyToInterpolate("opacity", 1.0f, 0.0f);
				t.addPropertyToInterpolate("color", Color.white, Color.green);
				t.setDuration(duration);
				t.setInitialDelay(initialDelay + i * 120);
				result.addScenarioActor(t);
			}
			return result;
		}

		public void paint(Graphics g) {
			for (Letter l : this.letters)
				l.paint(g);
		}

	}

	private static class MatrixPanel extends JPanel {
		private List<Drop> drops;

		public MatrixPanel() {
			try {
				InputStream is = MatrixRain.class.getClassLoader()
						.getResourceAsStream("test/swing/katakana.ttf");
				Font kf = Font.createFont(Font.TRUETYPE_FONT, is);
				int fontSize = 14;
				font = kf.deriveFont(Font.BOLD, fontSize);
			} catch (Exception exc) {
				exc.printStackTrace();
			}

			Timeline repaint = new SwingRepaintTimeline(this);
			repaint.playLoop(RepeatBehavior.LOOP);

			this.drops = new ArrayList<Drop>();
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					while (drops.size() < 40)
						addDrop();
				}
			});
		}

		private synchronized void addDrop() {
			final Drop drop = new Drop();
			TimelineScenario scenario = drop.getScenario(new Random()
					.nextInt(getWidth()));
			scenario.addCallback(new TimelineScenarioCallback() {
				@Override
				public void onTimelineScenarioDone() {
					synchronized (MatrixPanel.this) {
						drops.remove(drop);
						addDrop();
					}
				}
			});
			this.drops.add(drop);
			scenario.play();
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());

			synchronized (this) {
				for (Drop drop : this.drops)
					drop.paint(g);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame fr = new JFrame("Matrix rain");
				fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				MatrixPanel panel = new MatrixPanel();
				panel.setPreferredSize(new Dimension(400, 300));
				fr.add(panel);
				fr.pack();
				fr.setLocationRelativeTo(null);
				fr.setVisible(true);
			}
		});
	}
}
