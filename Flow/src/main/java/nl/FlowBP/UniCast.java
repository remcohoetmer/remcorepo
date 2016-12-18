package nl.FlowBP;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.processors.UnicastProcessor;

public class UniCast {
	volatile CountDownLatch flushSignal = new CountDownLatch(1);
	volatile CountDownLatch endSignal = new CountDownLatch(1);
	Subscription subscription ;
	int previous=-1;
	int not_in_sync_count=0;
	PrintStream out= System.out;
	
	Subscriber<? super Object> observer= new Subscriber<Object>() {
		@Override
		public void onSubscribe(Subscription s) {
			s.request(1);
			subscription=s;
		}
		@Override
		public void onNext(Object t) {
			int current= ((Integer) t).intValue();
			if( current%10000==0) {			
				out.println( "Recv" + current);
			}
			if (previous+1 !=current) {
				not_in_sync_count++;
				if (previous >= current) {
					out.println("Overtaken: previous:" + previous + " current:" + current);
				}
			}
			previous=current;
			
			if (flushSignal.getCount()==0) {
				subscription.request(1);
			}
		}
		@Override
		public void onError(Throwable t) {
			System.err.println(t.getMessage());
		}
		@Override
		public void onComplete() {
			System.out.println("complete");
			endSignal.countDown();
		}
	};


	private void start() throws InterruptedException {
		UnicastProcessor<Object> processor = UnicastProcessor.create(1);
		processor.subscribe(observer);
		processor.onBackpressureDrop();


		for( int i=0; i<10000000;i++) {			
			processor.onNext( i);
			if( i%10000==0) {			
				out.println( "Send " + i);
			}
		}
		flushSignal.countDown();
		processor.onComplete();
		
		subscription.request(1);
		endSignal.await();
		System.out.println( "Not in sync: "+ not_in_sync_count);
	}

	public static void main(String[] args) throws InterruptedException {
		new UniCast().start();
	}
}
