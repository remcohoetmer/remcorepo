package nl.Flowable;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LazyObservable 
{
	Observable<Integer> createGenerator(Integer i)
	{
		Observable<Integer> generator= Observable.generate( t ->
		{
			int ms= new Random().nextInt(1000);
			try {
				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
			t.onNext(ms);
			System.out.println( String.format( "Send %d %3d %s", i, ms, Thread.currentThread().getName()));
		});
		return generator;
	}

	private static void observeLazy( Observable<Integer> observable) throws InterruptedException  {

		Observer<Integer> observer= new Observer<Integer>(){
			Disposable disposable;
			int count= 5;
			@Override
			public void onSubscribe(Disposable s) {
				disposable=s;
			}
			@Override
			public void onComplete() {
			}
			@Override
			public void onError(Throwable e) {
				e.printStackTrace(System.err);
			}
			@Override
			public void onNext(Integer n) {
				System.err.println( String.format( "Recv %3d %s %s",  n,Thread.currentThread().getClass(),  Thread.currentThread().getName()));
				//new Exception().printStackTrace();
				if (--count==0) {
					disposable.dispose();
				}
			}

		};
		observable.observeOn( Schedulers.computation()).subscribe( observer);
	}

	public void lazy() throws IOException, InterruptedException {
		CountDownLatch endSignal = new CountDownLatch(1);
		Consumer<Integer> consumer = n->
			System.out.println( String.format( "Recv %3d %s",  n, Thread.currentThread().getName()));

		Observable<Integer> generator= createGenerator(4);
		observeLazy( generator);
		
		System.out.println( "Waiting...");
		endSignal.await();

	}

	public static void main( String[] args ) throws Exception
	{
		new LazyObservable().lazy();
	}

}
