package test.swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

public class ButtonFgLoop extends JFrame {
	public ButtonFgLoop() {
		this.setLayout(new FlowLayout());

		JButton buttonWithCancel = createButton("button w/cancel");
		final Timeline timelineWithCancel = createTimeline(buttonWithCancel);
		buttonWithCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				timelineWithCancel.playLoop(RepeatBehavior.REVERSE);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				timelineWithCancel.cancelAtCycleBreak();
			}
		});
		this.add(buttonWithCancel);

		JButton buttonWithRevert = createButton("button w/revert");
		final Timeline timelineWithRevert = createTimeline(buttonWithRevert);
		buttonWithRevert.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				timelineWithRevert.playLoop(RepeatBehavior.REVERSE);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				timelineWithRevert.playReverse();
			}
		});
		this.add(buttonWithRevert);

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JButton createButton(String label) {
		JButton button = new JButton(label);
		button.setForeground(Color.blue);
		return button;
	}

	private Timeline createTimeline(JButton button) {
		Timeline timeline = new Timeline(button);
		timeline.setDuration(1500);
		timeline.addPropertyToInterpolate("foreground", button.getForeground(),
				Color.red);
		return timeline;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ButtonFgLoop().setVisible(true);
			}
		});
	}
}
