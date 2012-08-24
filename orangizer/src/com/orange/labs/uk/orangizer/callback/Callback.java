package com.orange.labs.uk.orangizer.callback;

/**
 * Callback that can be used for asynchronous operations. Depending of the success of the operation,
 * the onSuccess or onFailure method is invoked.
 */
public interface Callback<T> {

	public void onSuccess(T result);

	public void onFailure(Exception e);

}
