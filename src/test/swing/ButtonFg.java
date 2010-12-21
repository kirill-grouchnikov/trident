package test.swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.pushingpixels.trident.Timeline;

public class ButtonFg extends JFrame {
	public ButtonFg() {
		JButton button = new JButton("sample");
		button.setForeground(Color.blue);

		this.setLayout(new FlowLayout());
		this.add(button);

		final Timeline rolloverTimeline = new Timeline(button);
		rolloverTimeline.addPropertyToInterpolate("foreground", Color.blue,
				Color.red);
		rolloverTimeline.setDuration(2500);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				rolloverTimeline.play();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				rolloverTimeline.playReverse();
			}
		});

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ButtonFg().setVisible(true);
			}
		});
	}
}
