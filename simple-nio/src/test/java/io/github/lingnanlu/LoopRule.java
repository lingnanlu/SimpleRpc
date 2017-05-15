package io.github.lingnanlu;


import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by rico on 2017/5/15.
 */
public class LoopRule implements TestRule {

    private int loopCount;

    public LoopRule(int loopCount) {
        this.loopCount = loopCount;
    }

    @Override
    public Statement apply(final Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (int i = 0; i < loopCount; i++) {
                    statement.evaluate();
                }
            }
        };
    }
}
