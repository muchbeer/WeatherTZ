package com.muchbeer.king;

/**
 * Created by muchbeer on 11/30/2014.
 */

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class fullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(fullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public fullTestSuite() {
        super();
    }
}
