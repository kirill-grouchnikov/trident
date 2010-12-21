package test.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

public class ButtonFgLoop {
	private static Timeline createTimeline(Button button) {
		Timeline timeline = new Timeline(button);
		timeline.setDuration(1500);
		timeline.addPropertyToInterpolate("foreground", button.getForeground(),
				Display.getDefault().getSystemColor(SWT.COLOR_RED));
		return timeline;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(300, 200);
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		Button buttonWithCancel = new Button(shell, SWT.RADIO);
		GridData gridData = new GridData(GridData.CENTER, GridData.CENTER,
				true, false);
		buttonWithCancel.setLayoutData(gridData);

		buttonWithCancel.setText("sample w/cancel");
		final Timeline timelineWithCancel = createTimeline(buttonWithCancel);
		buttonWithCancel.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				timelineWithCancel.playLoop(RepeatBehavior.REVERSE);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				timelineWithCancel.cancelAtCycleBreak();
			}
		});

		Button buttonWithRevert = new Button(shell, SWT.RADIO);
		GridData gridDataRevert = new GridData(GridData.CENTER,
				GridData.CENTER, true, false);
		buttonWithRevert.setLayoutData(gridDataRevert);

		buttonWithRevert.setText("sample w/cancel");
		final Timeline timelineWithRevert = createTimeline(buttonWithRevert);
		buttonWithRevert.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				timelineWithRevert.playLoop(RepeatBehavior.REVERSE);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				timelineWithRevert.playReverse();
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
