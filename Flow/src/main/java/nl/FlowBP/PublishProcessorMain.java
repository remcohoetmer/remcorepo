package nl.FlowBP;


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

public class PublishProcessorMain 
{
	Flowable<Object> createGenerator()
	{
		Flowable<Object> generator= Flowable.generate( t ->
		{
			int ms= new Random().nextInt(100);
			t.onNext(new Object[]{ new Integer(ms), new byte[1]});
			System.out.println( String.format( "Send %d %3d %s", ms, ms, Thread.currentThread().getName()));
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
				//subscription.request(5);
				subscription.request(Integer.MAX_VALUE);
			}
			@Override
			public void onComplete() {
				endSignal.countDown();
			}
			@Override
			public void onError(Throwable e) {
				//e.printStackTrace();
				System.err.println( String.format( "Recv %s %s", Thread.currentThread().getName(), e.getMessage()));
				endSignal.countDown();
			}
			@Override
			public void onNext(Object in) {
				Object[] inar= (Object[]) in;
				Integer i= (Integer) inar[0];
				System.err.println( String.format( "Recv %3d %s", i,Thread.currentThread().getName()));
				if (--count==0) {
					//subscription.cancel();
					//	endSignal.countDown();
				}
				//subscription.request(1);
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

	// 

	public void publishBackpressure() throws IOException, InterruptedException {
		CountDownLatch endSignal = new CountDownLatch(1);

		PublishProcessor<Object> publisher= PublishProcessor.create();
		publisher.onBackpressureBuffer(2, ()->{ System.err.println("overflow");}, BackpressureOverflowStrategy.DROP_LATEST );
		Flowable<Object> generator= createGenerator();
		Subscriber<Object> consumer= createConsumer( endSignal);
		generator.observeOn( Schedulers.computation()).subscribe( publisher);
		publisher.observeOn( Schedulers.io()).subscribe( consumer);

		System.out.println( "Waiting...");
		endSignal.await();
	}
	public static void main( String[] args ) throws Exception
	{
		new PublishProcessorMain().publishBackpressure();
	}

}
