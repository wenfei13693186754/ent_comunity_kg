package com.kg.platform.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MathUtils   {
    public static BigDecimal getBigDecimal(Object value){
        BigDecimal ret = null;
        if(value != null){
            if(value instanceof BigDecimal){
                ret = (BigDecimal)value;
            }else if(value instanceof String){
                ret = new BigDecimal((String)value);
            }else if(value instanceof BigInteger){
                ret = new BigDecimal((BigInteger)value);
            }else if(value instanceof Number){
                ret = new BigDecimal(((Number)value).doubleValue());
            }else{
                throw new ClassCastException("Not possible to coerce ["+value+"] from class +"+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        String str1 = "aabbzgrhhh";
        String str2 = "zgr";
        int startIndex = str1.indexOf(str2);
        int endIndex = startIndex+str2.length();
        System.out.println();
        System.out.println(str1.substring(startIndex,endIndex));
    }
}
