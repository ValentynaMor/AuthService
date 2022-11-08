package net.absoft.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestDurationListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(result.getMethod().getMethodName() + " took " +
                (result.getEndMillis() - result.getStartMillis()) + "ms");
    }
}
