package test.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.pushingpixels.trident.Timeline;

public class ButtonFg {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(300, 200);
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		Button button = new Button(shell, SWT.RADIO);
		GridData gridData = new GridData(GridData.CENTER, GridData.CENTER,
				true, false);
		button.setLayoutData(gridData);

		button.setText("sample");

		Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		Color red = display.getSystemColor(SWT.COLOR_RED);
		button.setForeground(blue);

		final Timeline rolloverTimeline = new Timeline(button);
		rolloverTimeline.addPropertyToInterpolate("foreground", blue, red);
		rolloverTimeline.setDuration(2500);
		button.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				rolloverTimeline.play();
			}

			@Override
			public void mouseExit(MouseEvent e) {
				rolloverTimeline.playReverse();
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
