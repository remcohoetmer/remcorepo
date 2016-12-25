package nl.Flowable;

import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public final class LimitOperator<T> implements ObservableOperator<T,T>{
	private int limit;

	public LimitOperator(int limit) {
		this.limit= limit;
	}

	@Override
	public Observer<? super T> apply(Observer<? super T> observer) throws Exception {
		return new Observer<T>() {
			final Observer<? super T>child= observer;
			Disposable parent;
			boolean done;
			int remaining;
			boolean complete;
			
			@Override
			public void onSubscribe(Disposable d) {
				parent=d;
				done= false;
				remaining= limit;
				complete=false;
				if (remaining <=0) {
					child.onComplete();
					done= true;
					parent.dispose();
					parent=null;
				}
			}

			@Override
			public void onNext(T value) {
				if (!done) {
					if (remaining>0) {
						child.onNext(value);
					} else {
						//System.out.println( "value"+ value + "ignored");
					}
					if (--remaining ==0) {
						child.onComplete();
						complete= true;
						// sneaky implementation that keeps on consuming while complete
						// in order to refuse the values to be delivered to the flatmap.
						// probably against the specs
						// leads to infinite loops when used with an infinite source
						done= true;
						parent.dispose();
						parent=null;
					}
				}
			}

			@Override
			public void onError(Throwable e) {
				if (!done) {
					child.onError(e);
					done= true;
					parent.dispose();
					parent=null;
				}
			}

			@Override
			public void onComplete() {
				if (!done) {
					if (!complete) {
						child.onComplete();
					}
					done= true;
					parent.dispose();
					parent=null;
				}
			}
		};
	}
}
