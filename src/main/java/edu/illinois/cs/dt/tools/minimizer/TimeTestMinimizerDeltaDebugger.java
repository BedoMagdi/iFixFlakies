package edu.illinois.cs.dt.tools.minimizer;

import edu.illinois.cs.testrunner.data.results.Result;
import edu.illinois.cs.testrunner.data.results.TestRunResult;
import edu.illinois.cs.testrunner.runner.SmartRunner;

import java.util.ArrayList;
import java.util.List;

public class TimeTestMinimizerDeltaDebugger extends TestMinimizerDeltaDebugger{


    private final SmartRunner runner;
    private final String dependentTest;
    private final Result expected;
    private double curTime;
    private final double origTime;

    public TimeTestMinimizerDeltaDebugger(SmartRunner runner, String dependentTest, Result expected,TestRunResult expectedRun) {
        super(runner, dependentTest, expected);
        this.runner = runner;
        this.dependentTest = dependentTest;
        this.expected = expected;
        origTime = expectedRun.results().get(this.dependentTest).time();  //to get the time before rerun
    }


    @Override
    public boolean checkValid(List<String> tests) {
        final double threshold;
        boolean check = this.expected == result(tests);
        final double min = Math.min(origTime, curTime);
        threshold = Math.min((1)/(Math.log(1+min)), 50);

        return  check && (Math.abs(origTime - curTime) <= min * threshold) ;
    }

    private Result result(final List<String> tests) {
        try {
            return runResult(tests).results().get(this.dependentTest).result();
        } catch (java.lang.IllegalThreadStateException e) {
            // indicates timeout
            return Result.SKIPPED;
        }
    }

    private TestRunResult runResult(final List<String> tests) {
        final List<String> actualOrder = new ArrayList<>(tests);

        if (!actualOrder.contains(this.dependentTest)) {
            actualOrder.add(this.dependentTest);
        }
        TestRunResult res =  this.runner.runList(actualOrder).get();
        curTime = res.results().get(this.dependentTest).time();     //added this to get the newTime
        return res;
    }



}
