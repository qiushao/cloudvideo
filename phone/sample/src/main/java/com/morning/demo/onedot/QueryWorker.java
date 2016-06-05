package com.morning.demo.onedot;


import com.morning.demo.tools.DefaultThreadFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public abstract class QueryWorker<T> implements Runnable {
	public interface IWorkerCallback<T> {
		void success(T item);
	}
	
	public abstract static class Task<T> {
		public T item;
		public IWorkerCallback<T> callback;
		
		public Task(T item, IWorkerCallback<T> callback) {
			this.item = item;
			this.callback = callback;
		}
	}
	
	private BlockingQueue<Task<T>> mQueue;
	private ExecutorService mProducer;
	private ExecutorService mConsumer;
	private AtomicInteger mRemainingTasks;
	private AtomicBoolean isShutDown;
	
	public QueryWorker() {
		this(getNumCores());
	}

	public QueryWorker(int threads) {
		isShutDown = new AtomicBoolean(false);
		mQueue = new ArrayBlockingQueue<>(threads * 20);
		int procducerCount = threads / 2;
		if (2 >= procducerCount) {
			procducerCount = 1;
		}
		int consumerCount = threads;
		if (2 >= consumerCount) {
			consumerCount = 1;
		}
		mProducer = Executors.newFixedThreadPool(procducerCount, new DefaultThreadFactory(
				"OndotProducerThread"));
		mConsumer = Executors.newFixedThreadPool(consumerCount, new DefaultThreadFactory(
				"OndotConsumerThread"));
		mRemainingTasks = new AtomicInteger(0);
		for (int i = 0; i < consumerCount; i ++) {
			mConsumer.execute(this);
		}
	}
	
	public void shutdown() {
		if (!isShutDown.getAndSet(true)) {
			mProducer.shutdownNow();
			mProducer = null;
			mConsumer.shutdownNow();
			mConsumer = null;
		}
	}
	
	public void submit(final Task<T> task) {
		if (isShutDown.get()) {
			return;
		}
		mRemainingTasks.incrementAndGet();
		mProducer.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					mQueue.put(task);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void run() {
		while (!isShutDown.get() && null != mConsumer) {
			final Task<T> task = mQueue.poll();
			if (null == task) {
				continue;
			}
			final T item = task.item;
			final IWorkerCallback<T> callback = task.callback;
			final boolean isDone = doWork(task);
			mRemainingTasks.decrementAndGet();
			if (isDone) {
				if (null != callback) {
					callback.success(item);
				}
			} else {
				submit(task);
			}
			if (0 == mRemainingTasks.get()) {
				remainWorkEmpty();
			}
		}
	}
	
	protected abstract boolean doWork(Task<T> task);
	protected abstract void remainWorkEmpty();

	
	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	private static int getNumCores() {
	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
		            //Check if filename is "cpu", followed by a single digit number
		            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
		                return true;
		            }
		            return false;
				}
			});
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	        //Default to return 1 core
	        return 1;
	    }
	}
	
}
