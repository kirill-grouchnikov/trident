package test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelinePropertyBuilder.PropertySetter;

public class CustomSetter {
	private float value;

	public static void main(String[] args) {
		final CustomSetter helloWorld = new CustomSetter();
		Timeline timeline = new Timeline(helloWorld);
		PropertySetter<Float> propertySetter = new PropertySetter<Float>() {
			@Override
			public void set(Object obj, String fieldName, Float value) {
				SimpleDateFormat sdf = new SimpleDateFormat("ss.SSS");
				float oldValue = helloWorld.value;
				System.out.println(sdf.format(new Date()) + " : " + oldValue
						+ " -> " + value);
				helloWorld.value = value;
			}
		};
		timeline.addPropertyToInterpolate(Timeline.<Float> property("value")
				.from(0.0f).to(1.0f).setWith(propertySetter));
		timeline.setDuration(300);
		timeline.play();

		try {
			Thread.sleep(1000);
		} catch (Exception exc) {
		}
	}
}
