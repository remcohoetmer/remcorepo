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
			
			@Override
			public void onSubscribe(Disposable d) {
				parent=d;
				done= false;
				remaining= limit;
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
					}
					if (--remaining ==0) {
						child.onComplete();
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
					child.onComplete();
					done= true;
					parent.dispose();
					parent=null;
				}
			}
		};
	}
}
