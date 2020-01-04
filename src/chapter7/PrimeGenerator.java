package chapter7;

import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

/*
Генератор простых чисел
 */

@ThreadSafe
public class PrimeGenerator implements Runnable{
    private final List<BigInteger> primes = new ArrayList<>();
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while(!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    private void cancel() {cancelled = true;}

    private synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }

    private static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();
        try {
            SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }
    public static void main(String[] args) {
        try {
            System.out.println(aSecondOfPrimes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
