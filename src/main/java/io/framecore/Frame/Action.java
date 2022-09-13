package io.framecore.Frame;


@FunctionalInterface
public interface Action <T> {
	
	 T doWork();
}
