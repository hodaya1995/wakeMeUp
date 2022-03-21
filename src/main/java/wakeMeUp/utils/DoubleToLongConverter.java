package me.jfenn.wakeMeUp.utils;

public class DoubleToLongConverter {

    public static double longToDouble(Long num){
        return Double.longBitsToDouble(num);
    }


    public static long doubleToLong(Double num){
        return Double.doubleToRawLongBits(num);
    }
}
