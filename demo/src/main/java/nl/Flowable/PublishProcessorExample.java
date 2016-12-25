package nl.Flowable;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

public class PublishProcessorExample 
{
	AtomicLong recv2Count= new AtomicLong(0);
	AtomicLong recvCount= new AtomicLong(0);
	AtomicLong sendCount= new AtomicLong(0);


	Flowable<Object> createGenerator()
	{
		Flowable<Object> generator= Flowable.generate( t ->
		{
			int ms= new Random().nextInt(100);
			try {
				//				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
			t.onNext(new Object[]{ new Integer(ms), new byte[10000]});
			sendCount.incrementAndGet();
			//System.out.println( String.format( "Send %d %s", ms, Thread.currentThread().getName()));
		});
		return generator;
	}

	Subscriber<Object> createConsumer( CountDownLatch endSignal)
	{
		return new Subscriber<Object>(){
			Subscription subscription;
			int count= 1;
			@Override
			public void onSubscribe(Subscription s) {
				subscription=s;
				//subscription.request(Long.MAX_VALUE);
				subscription.request(500);
			}
			@Override
			public void onComplete() {
				endSignal.countDown();
			}
			@Override
			public void onError(Throwable e) {
				
				System.err.println( String.format( "Recv %s %s", Thread.currentThread().getName(), e.getMessage()));
				
				endSignal.countDown();
			}
			@Override
			public void onNext(Object in) {
				Object[] inar= (Object[]) in;
				Integer i= (Integer) inar[0];

				recvCount.incrementAndGet();
				//System.err.println( String.format( "Recv %3d %s", i,Thread.currentThread().getName()));
				if (--count==0) {
					//subscription.cancel();
					//	endSignal.countDown();
				}
	//			subscription.request(1);
			}
		};
	}

	/*

		//Flowable.range(0,10).subscribe( publisher);// Flowable doet onComplete en dan sluit hij de boel af
	Consumer<Integer> consumer = n->
		System.out.println( String.format( "Recv %3d %s",  n, Thread.currentThread().getName()));
	publisher.observeOn( Schedulers.io()).subscribe(consumer);
	 */
	// Schedulers.computation() --> RxComputationThreadPool && ScheduledThreadPoolExecutor
	// Schedulers.newThread --> RxNewThreadScheduler && ScheduledThreadPoolExecutor
	// Schedulers.io --> RxCachedThreadScheduler && ScheduledThreadPoolExecutor
	// Schedulers.trampoline --> main && io.reactivex.internal.schedulers.TrampolineScheduler
	// Schedulers.single -->RxSingleScheduler && ScheduledThreadPoolExecutor

	public void publishBackpressure() throws IOException, InterruptedException {
		CountDownLatch endSignal = new CountDownLatch(1);

		PublishProcessor<Object> publisher= PublishProcessor.create();
		publisher.onBackpressureBuffer(1, ()->{ System.err.println("overflow");}, BackpressureOverflowStrategy.DROP_OLDEST);
		Flowable<Object> generator= createGenerator();
		
		//wanneer consumer op een andere scheduler wordt gezet, dan krijg je onjuiste resultaten
		publisher.subscribe( createConsumer( endSignal));
		//publisher.observeOn( Schedulers.computation()).subscribe( t -> {recv2Count.incrementAndGet();});
		generator.observeOn( Schedulers.newThread()).subscribe( publisher);
		
		System.out.println( "Waiting...");
		endSignal.await();
		System.out.println( "Send="+ sendCount.get());
		System.out.println( "Recv="+ recvCount.get());
		System.out.println( "Recv2="+ recv2Count.get());

	}
	public static void main( String[] args ) throws Exception
	{
		new PublishProcessorExample().publishBackpressure();
	}

}
