package io.gsonfire.gson;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.gson.Gson;

import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.Exclude;
import io.gsonfire.annotations.ExposeMethodParam;

public class ExposeMethodParamTest {

	@Test
	public void testCall() {
		Gson gson = new GsonFireBuilder()
				.enableExposeMethodParam()
				.createGson();
		A a = gson.fromJson("{value: \"initialized\", field: \"correct\"}", A.class);
		assertEquals("correct", a.value);
	}

	private static class A {
		String value = "unitialized";

		@ExposeMethodParam("field")
		void setField(String value) {
			this.value = value;
		}
	}

	@Test
	public void testData() {
		Gson gson = new GsonFireBuilder()
				.enableExposeMethodParam()
				.enableExcludeByAnnotation()
				.createGson();
		List<String> data = Arrays.asList("hello", "world", "1234");
		B b1 = new B();
		b1.setData(data);
		B b2 = gson.fromJson("{map:" + gson.toJson(data) + "}", B.class);
		assertEquals(b1, b2);
	}

	private static class B {

		@Exclude
		Map<Integer, String> map;

		@ExposeMethodParam("map")
		void setData(List<String> data) {
			// TODO Compact this once Java 8 is enabled
			map = data.stream().collect(Collectors.toMap(new Function<String, Integer>() {

				@Override
				public Integer apply(String t) {
					return t.hashCode();
				}
			}, new Function<String, String>() {

				@Override
				public String apply(String t) {
					return t;
				}
			}));
		}

		@Override
		public String toString() {
			return "A [map=" + map + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			B other = (B) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			return true;
		}
	}

	@Test
	public void testGeneric() {
		Gson gson = new GsonFireBuilder()
				.enableExposeMethodParam()
				.enableExcludeByAnnotation()
				.createGson();
		List<Integer> list = Arrays.asList(1, 5, 4, 3, 10, 50, 20, 1, 3, 4);
		String data = gson.toJson(list);
		C c = gson.fromJson("{map:" + data + "}", C.class);
		assertEquals(101, c.sum);
	}

	private static class C {
		@Exclude
		int sum;

		@ExposeMethodParam("map")
		public void sum(Collection<Byte> data) {
			sum = 0;
			for (int i : data)
				sum += i;
		}
	}
}
