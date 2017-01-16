package io.github.lingnanlu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rico on 2017/1/13.
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {

        final List<Integer> list = new ArrayList<Integer>();

        boolean endflag = false;
        Object lock = new Object();

        int middle = 100;
        for(int i = 0; i < middle; i++) {
            list.add(i);
        }

        new Thread(new Runnable() {
            public void run() {

                for (int i : list) {
                    System.out.print (i + " ");
                }
            }
        }).start();


        Thread.yield();
        int end = middle * 3;

        for(int i = middle; i < end; i++) {
            System.out.println("add " + i);
            list.add(i);
        }

    }
}
