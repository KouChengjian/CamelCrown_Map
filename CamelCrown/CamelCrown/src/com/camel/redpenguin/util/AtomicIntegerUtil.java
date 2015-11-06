package com.camel.redpenguin.util;

/**
 * 
 */

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Administrator
 *
 */
/**
 * @ClassName: AtomicIntegerUtil
 * @Description: socket 非阻塞
 * @author kcj
 * @date
 */
public final class AtomicIntegerUtil {

	private static final AtomicInteger mAtomicInteger = new AtomicInteger();

	public static int getIncrementID() {
		return mAtomicInteger.getAndIncrement();
	}
}
