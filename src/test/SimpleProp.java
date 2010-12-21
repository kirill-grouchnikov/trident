package test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class SimpleProp {
	public void setValue(float value) {
		System.out.println("Cool " + value);
	}
	
	public static void main(String[] args) throws Exception {
		SimpleProp prop = new SimpleProp();
		PropertyDescriptor desc = new PropertyDescriptor("value", prop.getClass(),
				null, "setValue");
		Method writer = desc.getWriteMethod();
		writer.invoke(prop, new Float(2.0));
		System.out.println(Float.class.isAssignableFrom(float.class));
	}
}
