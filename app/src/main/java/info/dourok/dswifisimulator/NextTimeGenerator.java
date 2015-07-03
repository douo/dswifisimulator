package info.dourok.dswifisimulator;

import java.util.Random;

/**
 * Created by Tiou on 2015/7/3.
 * f(x) = a*b^(x*c)
 * x 服从正态分布
 * 用于生成每次访问服务器的时间差
 * 模拟开发过程正常的访问行为
 * 混淆后台监管程序
 */
public class NextTimeGenerator {
    private Random mRandom;
    private double a;
    private double b;
    private double c;

    // Math.log(0xFFFFFFF/a,b)/c
    // * => 6.44935861415706
    private final static double DEFAULT_A = 150 * 1000;
    private final static double DEFAULT_B = 1.98;
    private final static double DEFAULT_C = 1.62;

    public NextTimeGenerator(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
        mRandom = new Random();
    }

    public NextTimeGenerator() {
        this(DEFAULT_A, DEFAULT_B, DEFAULT_C);
    }


    public int nextTime() {
        double x = mRandom.nextGaussian();
        System.out.println(x);
        double r = a * Math.pow(b, x * c);
        return (int) r;
    }

    public long nextMoment(long now) {
        return now + nextTime();
    }

    public long nextMoment() {
        return nextMoment(System.currentTimeMillis());
    }
}
