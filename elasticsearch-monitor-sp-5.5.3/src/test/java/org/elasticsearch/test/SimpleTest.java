package org.elasticsearch.test;

import org.junit.Test;

import java.text.SimpleDateFormat;

public class SimpleTest {

    @Test
    public void test1(){
        String index="F";
        String s=String.valueOf(new char[]{index.charAt(0),index.charAt(index.length()-1)});
        System.out.println(s);
    }

    @Test
    public void test2(){
        String unit="mt";
        if (unit.charAt(1) != 't') {
            System.out.println("1");
        }
        String format="YYYYMMddHHmmss";
        String substring = format.substring(0, 1+format.lastIndexOf(unit.charAt(0)));
        SimpleDateFormat sdf = new SimpleDateFormat(substring);
        System.out.println(System.currentTimeMillis());
        System.out.println(1571546932595L);
        System.out.println(sdf.format(System.currentTimeMillis()));
        System.out.println(sdf.format(1571546932595L));
    }
}
