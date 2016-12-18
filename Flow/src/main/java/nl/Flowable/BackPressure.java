package nl.Flowable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.schedulers.Schedulers;

public class BackPressure {
	CountDownLatch endSignal = new CountDownLatch(1);

	public static Flowable<Object> createGenerator(Integer i)
	{
		Flowable<Object> generator= Flowable.generate( t ->
		{
			int ms= new Random().nextInt(1000);
			try {
				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}

			t.onNext(new Object[]{ new Integer(i), new byte[100000]});
			System.err.println( String.format( "Send %d %3d %s", i, ms, Thread.currentThread().getName()));
		});
		return generator;
	}

	class SlowConsumer implements Subscriber<Object>
	{
		Subscription subscription;
		int count= 3;
		@Override
		public void onSubscribe(Subscription s) {
			subscription=s;
			subscription.request(4);
			//subscription.request(Long.MAX_VALUE);
		}
		@Override
		public void onComplete() {
			System.err.println( String.format( "Recv complete"));
			endSignal.countDown();
		}
		@Override
		public void onError(Throwable e) {
			System.out.println( String.format( "Recv error"+ e.getMessage()));
			endSignal.countDown();
		}
		@Override
		public void onNext(Object input) {
			Object[]ar= (Object[]) input;
			System.out.println( String.format( "Recv %3d %s", ar[0], Thread.currentThread().getName()));

			if (--count==0) {
				//subscription.cancel();
				//endSignal.countDown();
			}
			//subscription.request(1);
		}

	}

	void initiateBPStrategiesHotObservable() throws InterruptedException
	{
		Flowable<Object> flowable= Flowable.create((FlowableEmitter<Object> emitter) -> {
			for (int i=0;i<100000/* 100000*/;i++) {
				emitter.onNext(new Object[]{ new Integer(i), new byte[1000000]});
				System.err.println( String.format( "Send %d %s", i, Thread.currentThread().getName()));
			}
			emitter.onComplete();
		}, BackpressureStrategy.LATEST);

		// BackpressureStrategy:
		// MISSING --> houdt in geheel geen rekening met request
		// BUFFER --> unlimited buffering until OOME
		// ERROR --> MBPE
		// DROP: gooit alles weg
		// LATEST: houdt er nog eentje vast

		//flowable.subscribeOn( Schedulers.io());
		//Disposable d= flowable.subscribe();
		//d.dispose();

		SlowConsumer subscriber= new SlowConsumer();

		flowable.subscribe(subscriber);
		subscriber.subscription.request(1);//--> om de laatste eruit te trekken bij LATEST

		System.out.println("Waiting");
		endSignal.await();
	}

	void initiateEscalateBPStrategy() throws InterruptedException
	{
		Flowable<? extends Object> flowable= createGenerator(4);//.subscribeOn( Schedulers.io());		
		SlowConsumer subscriber= new SlowConsumer();

		// Merkwaardig --> er is geen observer die om meer items vraagt en toch gaat de generator door
		// als de observatie via een scheduler gebeurt
		flowable.observeOn(Schedulers.io()).subscribe(new SlowConsumer());
		// Maar zonder dat blijkt er wel escalatie plaats te vinden!!!
		//flowable.subscribe(subscriber);

		System.out.println("Waiting a short time");
		Thread.sleep(2000);
		subscriber.subscription.request(1);		
		System.out.println("Waiting");
		endSignal.await();
	}

	List<Object> generateList()
	{
		List<Object> l= new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			l.add( new Object[]{ new Integer(i), new byte[1000000]});
		}
		return l;
	}
	Object map( Object in)
	{
		Object[] out=(Object[]) in;
		Integer out0= (Integer) out[0];
		System.out.println( "process "+ out0);//out0 + 1
		return new Object[]{ new Long( System.nanoTime() ), out[1]};
	}

	void initiateBlockBPStrategy() throws InterruptedException
	{
		Flowable<? extends Object> flowable= Flowable.fromIterable(generateList()).map(this::map);

		SlowConsumer subscriber= new SlowConsumer();
		flowable.onBackpressureBuffer(2, ()->{ System.err.println("overflow");}, BackpressureOverflowStrategy.ERROR );// does not work!

		// ook met een cold observable (berekening) gaat de bron door met zenden (in ee )
		// --> voor iedere consumer.
		flowable.observeOn(Schedulers.io()).subscribe(subscriber);
		//flowable.observeOn(Schedulers.io()).subscribe(subscriber);
		// het lijkt erop alsof backpressure niet zozer bedoeld is (wordt gespecd) om de resources bij de bron te
		// beperken maar meer om de consumptie te beheersen
		System.out.println("Waiting a short time");
		Thread.sleep(2000);
		subscriber.subscription.request(6);		
		System.out.println("Waiting");
		endSignal.await();
	}

	void initiateUnicastStrategy() throws InterruptedException
	{
		Flowable<? extends Object> source= Flowable.fromIterable(generateList()).map(this::map);
		UnicastProcessor<? super Object> processor= UnicastProcessor.create();
		SlowConsumer subscriber= new SlowConsumer();
		source.subscribe( processor);

		processor.observeOn(Schedulers.io()).subscribe(subscriber);
		source.subscribe( processor);
		System.out.println("Waiting a short time");
		Thread.sleep(2000);
		subscriber.subscription.request(1);		
		System.out.println("Waiting");
		endSignal.await();
	}	

	void initiateReplayStrategy() throws InterruptedException
	{
		Flowable<Object> source= Flowable.fromIterable(generateList()).map(this::map);
		ReplayProcessor<Object> processor= ReplayProcessor.create();

		SlowConsumer subscriber= new SlowConsumer();

		processor.observeOn(Schedulers.io()).subscribe(subscriber);
		processor.observeOn(Schedulers.io()).subscribe(subscriber);
		source.subscribe( processor);
		System.out.println("Waiting a short time");
		Thread.sleep(2000);
		subscriber.subscription.request(1);		
		System.out.println("Waiting");
		endSignal.await();
	}



	public static void main(String[] args) throws InterruptedException {
		//new BackPressure().initiateBPStrategiesHotObservable();
		new BackPressure().initiateEscalateBPStrategy();
		//new BackPressure().initiateBlockBPStrategy();
		//new BackPressure().initiateUnicastStrategy();
		//new BackPressure().initiateReplayStrategy();
	}

}
