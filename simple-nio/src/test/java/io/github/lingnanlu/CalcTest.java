package io.github.lingnanlu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by rico on 2017/2/14.
 */
public class CalcTest {

    Calc calc;


    @Before
    public void before() {
        calc = new Calc();
    }

    @Test
    public void add() {

        int result  = 0;
        try {
            result = calc.add(1, 0);
            Assert.fail();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}