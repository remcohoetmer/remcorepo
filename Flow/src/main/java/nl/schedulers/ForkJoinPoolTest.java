package nl.schedulers;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import org.junit.Test;



public class ManagedBlockerDemo {
	static class QueueManagedBlocker<T> implements ManagedBlocker {
		final BlockingQueue<T> queue;
		volatile T value = null;
		QueueManagedBlocker(BlockingQueue<T> queue) {
			this.queue = queue;
		}
		public boolean block() throws InterruptedException {
			if (value == null)
				value = queue.take();
			return true;
		}
		public boolean isReleasable() {
			return value != null || (value = queue.poll()) != null;
		}
		public T getValue() {
			return value;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> bq = new ArrayBlockingQueue<String>(2);
		bq.put("A");
		bq.put("B");
		 Thread t = Thread.currentThread();
		 System.out.println( t.getClass());
		QueueManagedBlocker<String> blocker=  new QueueManagedBlocker<String>(bq);
		 System.out.println( 		(!blocker.isReleasable() &&
                !blocker.block()));
		
		ForkJoinPool.managedBlock(blocker);
		System.out.println(blocker.getValue());
	}
	
	private static class ForkableTask extends RecursiveTask<Integer> {
		private static final long serialVersionUID = 1L;
		private final CyclicBarrier barrier;

	    ForkableTask(CyclicBarrier barrier) {
	        this.barrier = barrier;
	    }

	    @Override
	    protected Integer compute() {
	        try {
	            barrier.await();
	            return 1;
	        } catch (InterruptedException | BrokenBarrierException e) {
	            throw new RuntimeException(e);
	        }
	    }
	}

	@Test
	public void testForkJoinPool() throws Exception {
	    final int parallelism = 4;
	    final ForkJoinPool pool = new ForkJoinPool(parallelism);
	    final CyclicBarrier barrier = new CyclicBarrier(parallelism);

	    final List<ForkableTask> forkableTasks = new ArrayList<>(parallelism);
	    for (int i = 0; i < parallelism; ++i) {
	        forkableTasks.add(new ForkableTask(barrier));
	    }

	    int result = pool.invoke(new RecursiveTask<Integer>() {
			private static final long serialVersionUID = 1L;

			@Override
	        protected Integer compute() {
	            for (ForkableTask task : forkableTasks) {
	                task.fork();
	            }

	            int result = 0;
	            for (ForkableTask task : forkableTasks) {
	                result += task.join();
	            }
	            return result;
	        }
	    });
	    assertEquals(result, parallelism);
	}
	private static class CallableTask implements Callable<Integer> {
	    private final CyclicBarrier barrier;

	    CallableTask(CyclicBarrier barrier) {
	        this.barrier = barrier;
	    }

	    @Override
	    public Integer call() throws Exception {
	        barrier.await();
	        return 1;
	    }
	}


	@Test
	public void testWorkStealing() throws Exception {
	    final int parallelism = 4;
	    final ExecutorService pool = new ForkJoinPool(parallelism);
	    final CyclicBarrier barrier = new CyclicBarrier(3,  ()-> System.out.println("Pass"));

	    final List<CallableTask> callableTasks = Collections.nCopies(parallelism, new CallableTask(barrier));
	    int result = pool.submit(new Callable<Integer>() {
	        @Override
	        public Integer call() throws Exception {
	            int result = 0;
	            // Deadlock in invokeAll(), rather than stealing work
	            for (Future<Integer> future : pool.invokeAll(callableTasks)) {
	                result += future.get();
	            }
	            return result;
	        }
	    }).get();
	    assertEquals(result, parallelism);
	}
	
	@Test
	public void testWorkStealing2() throws Exception {
	    final int parallelism = 4;
	    final ExecutorService pool = new ForkJoinPool(parallelism);
	    final CyclicBarrier barrier = new CyclicBarrier(parallelism);

	    final List<CallableTask> callableTasks = Collections.nCopies(parallelism, new CallableTask(barrier));
	            int result = 0;
	            // Deadlock in invokeAll(), rather than stealing work
	            for (Future<Integer> future : pool.invokeAll(callableTasks)) {
	                result += future.get();
	            }

	    assertEquals(result, parallelism);
	}
}