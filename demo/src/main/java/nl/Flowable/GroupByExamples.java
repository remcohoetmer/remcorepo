package nl.Flowable;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.internal.operators.maybe.MaybeToObservable;
import io.reactivex.internal.operators.single.SingleToObservable;
import io.reactivex.observables.GroupedObservable;


public class GroupByExamples {

	public void take10()
	{
		Observable.range(1, 40)
		.groupBy(n -> n % 2 == 0)
		.flatMap(g -> getSublist(g))
		.forEach(System.out::println);
	}
	
	/* Probleem: 
	 * Je wilt van de 2 lijsten de eerste 11 elementem. De rest wil je negeren. Oplossing de flatmap.
	 * Maar: nadat de flatMap de waarden van de 1e observable heeft verwerkt (take()),
	 * staat deze nog open dwz is in staat om te leveren. De map wordt nogmaals toegepast, zolang de producer niet uitgeput is.
	 * De vraag is nu hoe je een flatMap kan maken die enkel de eerste 11 elementen van de observable leest, zonder de rest te verwerken.
	 * Wat je hiervan leert is het pull-mechanisme van RxJava: je kan de stroom niet controleren op een impreatieve wijze. Je bent overgeleverd aan de werking van de componenten (flatMap).
	 * Eenvoudige oplossing: je weet dat je maar 2 lijsten terug moet krijgen --> je kan dus een take doen op de elementen. Daarna zal de observable de stream (beter gezegd: de subscription) cancelen.
	 * 
	 */
	public void take11()
	{
		Observable.range(1, 40)
		.groupBy(n -> n % 2 )
		.flatMap(g -> Observable.just( g.take(11)))
		.take(2)
		.flatMap(g -> new SingleToObservable<List<Integer>>( g.toList()))
		.forEach(System.out::println);
	}

	private static Observable<List<String>> getSublist(GroupedObservable<Boolean, Integer> g) {
		return new SingleToObservable<List<String>>( Observable.concat( Arrays.asList( g.take(10).map( i-> i.toString()), Observable.just( g.getKey().toString()))).toList());
	}

	@SuppressWarnings("unchecked")
	public static void all() {
		// odd/even into 2 lists
		Observable<GroupedObservable<Boolean,Integer>> observable= Observable.range(1, 100).groupBy(n -> n % 2 == 0);

		Observable<List<Integer>> observable2= observable.flatMap( g -> new SingleToObservable<List<Integer>>( g.toList()));
		observable2.forEach(System.out::println);
		Observable<Boolean> observable3= observable.flatMap( g -> Observable.just( g.getKey()) );
		observable3.forEach(System.out::println);


		System.out.println("2--------------------------------------------------------------------------------------------------------");

		// odd/even into lists of 10


		Observable.range(1, 100)
		.groupBy(n -> n % 2 == 0)
		.flatMap(g ->  getSublist(g)).forEach(System.out::println);

		System.out.println("3--------------------------------------------------------------------------------------------------------");

		//odd/even into lists of 10
		Observable.range(1, 100)
		.groupBy(n -> n % 2 == 0)
		.flatMap(g -> new SingleToObservable( g.filter(i -> i <= 20).toList()))
		.forEach(System.out::println);

		System.out.println("4--------------------------------------------------------------------------------------------------------");

		//odd/even into lists of 20 but only take the first 2 groups
		Observable.range(1, 100)
		.groupBy(n -> n % 2 == 0)
		.flatMap(g -> {
			return  new SingleToObservable( g.take(20).toList());
		}).take(2).forEach(System.out::println);

		System.out.println("5--------------------------------------------------------------------------------------------------------");

		//odd/even into 2 lists with numbers less than 30
		Observable.range(1, 100)
		.groupBy(n -> n % 2 == 0)
		.flatMap(g -> {
			return new SingleToObservable<List<Integer>>(g.takeWhile(i -> i < 30).toList());
		})
		.filter(l -> !l.isEmpty())
		.forEach(System.out::println);

		System.out.println("6--------------------------------------------------------------------------------------------------------");

		String[] ar= new String[]{ "a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c"};
		Observable.fromArray( ar)
		.groupBy(n -> n)
		.flatMap(g -> {
			return new MaybeToObservable(g.take(3).reduce((s, s2) -> s + s2));
		}).forEach(System.out::println);

		System.out.println("7--------------------------------------------------------------------------------------------------------");
		/*
		 *
        Observable.timer(0, 1, TimeUnit.MILLISECONDS)
                .groupBy(n -> n % 2 == 0)
                .flatMap(g -> {
                    return g.take(10).toList();
                }).take(2).toBlocking().forEach(System.out::println);

        System.out.println("8--------------------------------------------------------------------------------------------------------");

        Observable.timer(0, 1, TimeUnit.MILLISECONDS)
                .take(20)
                .groupBy(n -> n % 2 == 0)
                .map(g ->  g.toList()).toBlocking().forEach(System.out::println);
		 */
	}
	public static void main(String args[]) {
		new GroupByExamples().take11();

	}
}
