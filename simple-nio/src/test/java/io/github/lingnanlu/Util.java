package io.github.lingnanlu;

/**
 * Created by rico on 2017/2/17.
 */
public class Util {


    public static String bytesToStr(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append((char)b);
        }

        return sb.toString();
    }

    public static String strToBytes(String s) {

        StringBuilder sb = new StringBuilder();

        byte[] bytes = s.getBytes();

        sb.append("[");
        for (byte b : bytes) {
            sb.append(b + " ");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        byte[] bytes = {72, 69, 76, 76, 79, 10};
        System.out.println(Util.bytesToStr(bytes));

        System.out.println(strToBytes("HELLO\n"));
    }
}
