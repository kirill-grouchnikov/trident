package test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TridentConfig;

public class CustomPulseSource {
	private float value;

	public void setValue(float newValue) {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:SSS");
		System.out.println(sdf.format(new Date()) + " : " + this.value + " -> "
				+ newValue);
		this.value = newValue;
	}

	public static void main(String[] args) {
		TridentConfig.getInstance().setPulseSource(
				new TridentConfig.PulseSource() {
					@Override
					public void waitUntilNextPulse() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
					}
				});
		CustomPulseSource helloWorld = new CustomPulseSource();
		Timeline timeline = new Timeline(helloWorld);
		timeline.addPropertyToInterpolate("value", 0.0f, 1.0f);
		timeline.play();

		try {
			Thread.sleep(3000);
		} catch (Exception exc) {
		}
	}
}
