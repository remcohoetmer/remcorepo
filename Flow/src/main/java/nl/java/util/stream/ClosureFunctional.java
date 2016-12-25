package nl.java.util.stream;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Int {
	public final int value;
	public Int(int value) {
		this.value = value;
	}
}
public class ClosureFunctional {
	private final Integer b = 2;
	private Integer getB() {
		return this.b;
	}

	private Stream<Integer> calculate(Stream<Integer> stream, final Int a) {
		return stream.map(new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				return t * a.value + getB();
			}
		});
	}

	public static void main(String[] args) throws Exception
	{
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
		System.out.println(new ClosureFunctional().calculate(list.stream(), new Int(3)).collect(Collectors.toList()));
	}
}
