package nl.Flowable;
import io.reactivex.Observable;


public class CustomOperator {


	public void take5()
	{
		Observable.range(1, 40)
		.lift(new LimitOperator<Integer>(11))
		.forEach(System.out::println);
	}


	public static void main(String args[]) {
		new CustomOperator().take5();

	}
}
