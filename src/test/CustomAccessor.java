package test;

import java.text.SimpleDateFormat;
import java.util.*;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelinePropertyBuilder.PropertyAccessor;

public class CustomAccessor {
	private Map<String, Float> values = new HashMap<String, Float>();

	public static void main(String[] args) {
		final CustomAccessor helloWorld = new CustomAccessor();
		Timeline timeline = new Timeline(helloWorld);

		PropertyAccessor<Float> propertyAccessor = new PropertyAccessor<Float>() {
			@Override
			public Float get(Object obj, String fieldName) {
				return helloWorld.values.get("value");
			}

			@Override
			public void set(Object obj, String fieldName, Float value) {
				SimpleDateFormat sdf = new SimpleDateFormat("ss.SSS");
				float oldValue = helloWorld.values.get("value");
				System.out.println(sdf.format(new Date()) + " : " + oldValue
						+ " -> " + value);
				helloWorld.values.put("value", value);
			}
		};
		helloWorld.values.put("value", 50f);

		timeline.addPropertyToInterpolate(Timeline.<Float> property("value")
				.fromCurrent().to(100.0f).accessWith(propertyAccessor));
		timeline.setDuration(300);
		timeline.play();

		try {
			Thread.sleep(1000);
		} catch (Exception exc) {
		}
	}
}
