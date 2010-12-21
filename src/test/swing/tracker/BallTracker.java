package test.swing.tracker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.*;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.*;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

public class BallTracker extends JFrame {
	Timeline timelineBallFalling;

	public BallTracker() throws Exception {
		super("Ball tracker");

		this.setLayout(new GridLayout(2, 1));
		JPanel main = new JPanel(new GridLayout(1, 2));
		final TimelineVisualizer visualizer = new TimelineVisualizer();

		this.add(main);
		this.add(visualizer);

		JPanel controls = new JPanel(new FlowLayout());
		main.add(controls);

		final BallPanel ballPanel = new BallPanel();
		main.add(ballPanel);

		// final JFormattedTextField easeFactor = new JFormattedTextField(
		// new MaskFormatter("#.#"));
		// easeFactor.setText("0.8");
		// easeFactor.setColumns(5);
		// controls.add(easeFactor);

		Map<String, TimelineEase> eases = new TreeMap<String, TimelineEase>();
		eases.put("1. Linear", new Linear());
		eases.put("2. Sine", new Sine());
		eases.put("3. Spline (0.1, 0.0)-(0.9,1.0)", new Spline(0.1f, 0.0f,
				0.9f, 1.0f));
		eases.put("4. Spline (0.2, 0.0)-(0.8,1.0)", new Spline(0.2f, 0.0f,
				0.8f, 1.0f));
		eases.put("5. Spline (0.5, 0.0)-(0.5,1.0)", new Spline(0.5f, 0.0f,
				0.5f, 1.0f));
		eases.put("6. Spline (0.8, 0.0)-(0.2,1.0)", new Spline(0.8f, 0.0f,
				0.2f, 1.0f));
		eases.put("7. Spline (0.9, 0.0)-(0.1,1.0)", new Spline(0.9f, 0.0f,
				0.1f, 1.0f));
		eases.put("8. Spline (1.0, 0.0)-(0.9,1.0)", new Spline(1.0f, 0.0f,
				0.9f, 1.0f));
		ComboBoxModel easeModel = new DefaultComboBoxModel(eases.entrySet()
				.toArray());
		final JComboBox easeCombo = new JComboBox(easeModel);
		easeCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list,
						((Map.Entry<String, TimelineEase>) value).getKey(),
						index, isSelected, cellHasFocus);
			}
		});
		easeCombo.setSelectedIndex(0);
		controls.add(easeCombo);

		final JTextField msToSkip = new JTextField("0", 6);
		controls.add(new JLabel("to skip"));
		controls.add(msToSkip);

		final JTextField msInitialDelay = new JTextField("500", 6);
		controls.add(new JLabel("initial delay"));
		controls.add(msInitialDelay);

		final JTextField msCycleDelay = new JTextField("0", 6);
		controls.add(new JLabel("cycle delay"));
		controls.add(msCycleDelay);

		JButton runTimeline = new JButton("run");
		runTimeline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timelineBallFalling != null)
					timelineBallFalling.cancel();
				timelineBallFalling = new Timeline(ballPanel);
				timelineBallFalling.addPropertyToInterpolate("ballY",
						BallPanel.RADIUS, ballPanel.getHeight()
								- BallPanel.RADIUS);
				timelineBallFalling.setDuration(2000);
				timelineBallFalling.setInitialDelay(Integer
						.parseInt(msInitialDelay.getText()));
				timelineBallFalling.setCycleDelay(Integer.parseInt(msCycleDelay
						.getText()));
				timelineBallFalling
						.setEase(((Map.Entry<String, TimelineEase>) easeCombo
								.getSelectedItem()).getValue());

				timelineBallFalling.addCallback(new TimelineCallbackAdapter() {
					@Override
					public void onTimelinePulse(float durationFraction,
							float timelinePosition) {
						// add a fading dot to visualize the timeline
						// interpolation behavior
						visualizer.addDot(durationFraction, timelinePosition);
					}

					@Override
					public void onTimelineStateChanged(TimelineState oldState,
							TimelineState newState, float durationFraction,
							float timelinePosition) {
						System.out.println("State change: " + oldState.name()
								+ " -> " + newState.name());
					}
				});

				long toSkip = Long.parseLong(msToSkip.getText());
				if (toSkip > 0) {
					timelineBallFalling.playLoopSkipping(
							RepeatBehavior.REVERSE, toSkip);
				} else {
					timelineBallFalling.playLoop(RepeatBehavior.REVERSE);
				}
			}
		});

		JButton cancelTimeline = new JButton("cancel");
		cancelTimeline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timelineBallFalling != null)
					timelineBallFalling.cancelAtCycleBreak();
			}
		});

		JButton suspendTimeline = new JButton("suspend");
		suspendTimeline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timelineBallFalling != null)
					timelineBallFalling.suspend();
			}
		});

		JButton resumeTimeline = new JButton("resume");
		resumeTimeline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timelineBallFalling != null)
					timelineBallFalling.resume();
			}
		});

		controls.add(runTimeline);
		controls.add(cancelTimeline);
		controls.add(suspendTimeline);
		controls.add(resumeTimeline);

		Timeline paintTimeline = new SwingRepaintTimeline(this);
		paintTimeline.playLoop(RepeatBehavior.LOOP);

		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new BallTracker().setVisible(true);
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}

}
