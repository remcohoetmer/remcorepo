package nl.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.single.SingleToObservable;

import java.util.List;
import java.util.stream.Stream;


public class FlatMapLimitResearch {

	private Observer<Integer> createObserver2() {
		return new Observer<Integer>(){
			@Override
			public void onSubscribe(Disposable s) {
				System.out.print( "{");
			}
			@Override
			public void onNext(Integer t) {
				System.out.print( t+ ",");
			}
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				System.out.print( "}");
			}
		};
	}

	private Observer<Observable<Integer>> createObserver()
	{
		return new Observer<Observable<Integer>>(){
			private Disposable dis;
			@Override
			public void onSubscribe(Disposable d) {
				this.dis=d;
			}
			@Override
			public void onNext(Observable<Integer> t) {
				System.out.println( "["+t);
				t.subscribe( createObserver2());				
				System.out.println( "]");
			}
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
			@Override
			public void onComplete() {
				dis.dispose();
			}
		};
	}
	public void flapmapthatshit()
	{
		Observable.range(1, 40)
		.groupBy(n -> n % 2 )
		.flatMap(g -> Observable.just( g.lift ( new LimitOperator<Integer>(11))))
		//		.flatMap(g -> Observable.just( g.take(11)))
		.flatMap(g -> new SingleToObservable<List<Integer>>( g.toList()))
		.subscribe( System.out::println);
	}

	public void takeGrouped()
	{
		Observable.fromArray(new Integer[]{1,2,3})
		.groupBy(n -> "all" )
		.flatMap(g -> Observable.just( g.take (2)))
		//		.flatMap(g -> Observable.just( g.lift ( new LimitOperator<Integer>(3))))
		//.take(1)
		.flatMap(g -> new SingleToObservable<List<Integer>>( g.toList()))
		.forEach(System.out::println);
	}

	public void takeNonGrouped()
	{
		Observable.just( Observable.fromArray(new Integer[]{1,2,3}))
				.flatMap(g -> g.take (2))
				//		.flatMap(g -> Observable.just( g.lift ( new LimitOperator<Integer>(3))))
				//.take(1)
//				.flatMap(g -> new SingleToObservable<List<Integer>>( g.toList()))
				.forEach(System.out::println);
	}

	public void take5()
	{
		Observable.just( Observable.fromArray(new Integer[]{1,2,3}))
		.flatMap(g -> Observable.just( g.take ( 2)))
		//		.flatMap(g -> Observable.just( g.lift ( new LimitOperator<Integer>(3))))
		.flatMap(g -> new SingleToObservable<List<Integer>>( g.toList()))
		.forEach(System.out::println);
	}

	public void takeSimple()
	{
		Observable.fromArray(new Integer[]{1,2,3})
		.groupBy(n -> "all" )
		.subscribe( createObserver());
		System.err.println("------");

		Observable.fromArray(new Integer[]{1,2,3})
		.groupBy(n -> "all" )
		.flatMap(g -> Observable.just( g.take ( 2)))
		.subscribe( createObserver());
		System.err.println("------");

		Observable.just( Observable.fromArray(new Integer[]{1,2,3}))
		.flatMap(g -> Observable.just( g.take ( 2)))
		.subscribe( createObserver());
		System.err.println("------");

		//Java
		Stream.of( Stream.of(1,2,3),Stream.of(4,5,6))
		.flatMap( s-> Stream.of( s.limit ( 2)))
		.forEach( s-> { System.out.print("["); s.forEach( System.out::print);System.out.println("]");});


	}

	public static void main(String args[]) {
		//new CustomOperator().takeSimple();
		new FlatMapLimitResearch().takeGrouped();
		new FlatMapLimitResearch().takeNonGrouped();
		//		new CustomOperator().take5();
		//		new CustomOperator().flapmapthatshit();

	}
}
