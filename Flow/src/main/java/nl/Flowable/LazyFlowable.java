package nl.Flowable;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LazyFlowable 
{
	public static Flowable<Integer> createGenerator(Integer i)
	{
		Flowable<Integer> generator= Flowable.generate( t ->
		{
			int ms= new Random().nextInt(10);
			try {
				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}

			t.onNext(ms);
			System.err.println( String.format( "Send %d %3d %s", i, ms, Thread.currentThread().getName()));
		});
		return generator;
	}
	
	private static Subscriber<Integer> createConsumer( CountDownLatch endSignal) {

		Subscriber<Integer> subscriber= new Subscriber<Integer>(){
			Subscription subscription;
			int count= 2;
			@Override
			public void onSubscribe(Subscription s) {
				subscription=s;
				subscription.request(2);
//				subscription.request(Long.MAX_VALUE);
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
				System.err.println( String.format( "Recv %3d %s",  n,Thread.currentThread().getName()));
				//new Exception().printStackTrace();
				if (--count==0) {
					//subscription.cancel();
					//endSignal.countDown();
				}
			}
		};
		return subscriber;
	}

	public void lazy() throws IOException, InterruptedException {
		CountDownLatch endSignal = new CountDownLatch(1);

//		publisher.onBackpressureBuffer(5);// does not work!

		Consumer<Integer> consumer = n->
			System.out.println( String.format( "Recv %3d %s",  n, Thread.currentThread().getName()));

		Flowable<Integer> generator=  createGenerator(10);
		Subscriber<Integer> subscriber= createConsumer( endSignal);

//		generator.subscribeOn(Schedulers.io()).observeOn( Schedulers.io()).subscribe( consumer);
		generator.subscribeOn(Schedulers.io()).observeOn( Schedulers.computation()).subscribe( subscriber);
		
		//Zolang de consumer niet cancelt, bijft de producer doorgaan,  indien er eenmaal gesubscribet is...
		// .... maar ook beperkt
		
		//generator.observeOn(Schedulers.io()).subscribe( subscriber);
		
		System.out.println( "Waiting...");
		endSignal.await();

	}

	public void infiniteRange() throws IOException, InterruptedException {
		CountDownLatch endSignal = new CountDownLatch(1);

		Flowable<Integer> generator=  Flowable.range(1, Integer.MAX_VALUE).map(val-> { System.out.println(""+ val); return val;});
		Subscriber<Integer> subscriber= createConsumer( endSignal);

		generator.subscribeOn(Schedulers.io()).observeOn( Schedulers.computation()).subscribe( subscriber);
		Thread.sleep(100);

		generator.subscribeOn(Schedulers.io()).observeOn( Schedulers.computation()).subscribe( subscriber);
		
		System.out.println( "Waiting...");
		// Conclusie: indien er eenmaal is gesubscribet op een observable in een andere thread, dan mag deze (waarschijnlijk volgens spec :-) 
		// deze zonder request doorproduceren. Maar het zal stoppen indien er geen noodzaak meer toe is
		endSignal.await();

	}

	public static void main( String[] args ) throws Exception
	{
//		new LazyFlowable().lazy();
		new LazyFlowable().infiniteRange();
	}
	/* Uit te zoeken:
	 * verschil cancel & dispose:
	 * Beide doen hetzelfde: resource vrijgeven en abonnement op die stroom stoppen (niet voor andere subscribers).
	 * - Observeable streamt naar Observer die dispose() kunnen aanroepen.
	 * - Flow streamt naar Subscriber die cancel() kan aanroepen.
	 * - subscribe() met een Consumer een Disposable op, waarmee hetzelfde kan worden gedaan.
	 */

}
