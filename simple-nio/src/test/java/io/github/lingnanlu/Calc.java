package io.github.lingnanlu;

/**
 * Created by rico on 2017/2/14.
 */
public class Calc {

    public int add(int a, int b) throws Exception {

        if (b == 0) {
            throw new Exception("hehe");
        } else {
            return a + b;
        }
    }
}
