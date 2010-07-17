package com.hoydaa.bookdroid.test;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Executes all tests under this package: com.hoydaa
 * 
 * @author Umut Utkan
 */
public class AllTests extends TestSuite {

	public static Test suite() {
		return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
	}

}