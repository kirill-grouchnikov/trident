package test;

import org.pushingpixels.trident.Timeline;

public class HelloWorld {
	private float value;

	public void setValue(float newValue) {
		System.out.println(this.value + " -> " + newValue);
		this.value = newValue;
	}

	public static void main(String[] args) {
		HelloWorld helloWorld = new HelloWorld();
		Timeline timeline = new Timeline(helloWorld);
		timeline.addPropertyToInterpolate("value", 0.0f, 1.0f);
		timeline.play();

		try {
			Thread.sleep(3000);
		} catch (Exception exc) {
		}

	}
}
