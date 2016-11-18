package java8.functionalprograming.functionalprogramminginjavabook.chapter4;

import java.util.function.Function;

/**
 * @author Tushar Chokshi @ 9/5/16.
 */
public class MemoizerDemo {
    private static Integer longCalculation(Integer x) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException ignored) {
        }
        return x * 2;
    }

    private static Function<Integer, Integer> f = MemoizerDemo::longCalculation;
    private static Function<Integer, Integer> g = Memoizer.memoize(f);

    public static void main(String[] args) {
        {
            long startTime = System.currentTimeMillis();
            Integer result1 = g.apply(1); // here it will memoize(cache) the value
            long time1 = System.currentTimeMillis() - startTime;

            System.out.println(result1); //2
            System.out.println(time1);//1003

            startTime = System.currentTimeMillis();
            Integer result2 = g.apply(1); // retrieving memoized(cached) value
            long time2 = System.currentTimeMillis() - startTime;

            System.out.println(result2); //2
            System.out.println(time2); //0
        }

        // Functions returning functions returning functions ... returning a result
        {
            Function<Integer, Function<Integer, Integer>> mhc =
                    Memoizer.memoize(x ->
                            Memoizer.memoize(y -> x + y));
            Integer curriedFunctionResult = mhc.apply(1).apply(2);

        }
        
        // A memoized function of a Tuple3
        {
            Function<Tuple3<Integer, Integer, Integer>, Integer> ft =
                    x -> longCalculation(x._1)
                            + longCalculation(x._2)
                            - longCalculation(x._3);
            Function<Tuple3<Integer, Integer, Integer>, Integer> ftm =
                    Memoizer.memoize(ft);

            long startTime = System.currentTimeMillis();
            Integer result1 = ftm.apply(new Tuple3<>(2, 3, 4)); // here it will memoize(cache) the value
            long time1 = System.currentTimeMillis() - startTime;

            System.out.println(result1); // 2
            System.out.println(time1); // 3008

            startTime = System.currentTimeMillis();
            Integer result2 = ftm.apply(new Tuple3<>(2, 3, 4)); // retrieving memoized(cached) value
            long time2 = System.currentTimeMillis() - startTime;

            System.out.println(result2); // 2
            System.out.println(time2); // 0
        }

    }
}
