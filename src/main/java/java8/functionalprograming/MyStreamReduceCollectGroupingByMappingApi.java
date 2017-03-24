package java8.functionalprograming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java8.functionalprograming.MyStreamReduceCollectGroupingByMappingApi.MyStream.myCollectMethod;

/**
 * @author Tushar Chokshi @ 3/20/17.
 */
public class MyStreamReduceCollectGroupingByMappingApi {

    public static void main(String[] args) {

        MyStream<Integer> myStream =
                new MyStream<>(1,
                        new MyStream<>(2,
                                new MyStream<>(3,
                                        new EmptyStream()
                                )
                        )
                );

        int identity = 0;

        // Major difference between the Java's internal code of Stream's reduce and collect methods are a below
        // reduce method accepts Function to operate on input stream. It means that it accepts some input and gives some output.
        // collect method accepts Consumer to operate on input stream. Consumer doesn't return anything. It means that you need to provide an object to collect method that can hold the output. collect method will just keep updating that object with output on every recursive call.

        Integer finalResult = MyStream.myReduceMethod(myStream, () -> identity, (head, identity1) -> head + identity1);
        System.out.println("Result from reduce method: " + finalResult);


        Result<Integer> identityResult = new Result<>(identity);
        myCollectMethod(myStream, () -> identityResult, (head, identity1) -> identity1.setRes(head + identity1.getRes()));
        System.out.println("Result from collect method: " + identityResult.getRes());


        List<Long> offerIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        InputPartitioner<List<Long>> listInputPartitioner = new InputPartitioner<>(offerIds, 3);


        // Real Life example of collect method. Here, build method is same as collect.
        Map<Long, List<Criterion<Long>>> identityMap = new HashMap<>();

        Map<Long, List<Criterion<Long>>> buildResult = new MyResultBuilder().build(
                listInputPartitioner,
                new MyCollector<>(
                        identityMap,
                        // In Java8, Functions are nothing but the Callback methods. Callback method is nothing but you pass the code that gets executed at later point in time.
                        new MyAccumulatorCallback<Long, Criterion<Long>>() {
                            @Override
                            public Map<Long, List<Criterion<Long>>> accumulate(List<Long> inputList) {
                                Map<Long, List<Criterion<Long>>> map = new HashMap<>();
                                for (Long input : inputList) {
                                    List<Criterion<Long>> criteria = new ArrayList<>();
                                    criteria.add(new Criterion<>(input * 10));
                                    criteria.add(new Criterion<>(input * 20));
                                    criteria.add(new Criterion<>(input * 30));
                                    map.put(input, criteria);
                                }
                                return map;
                            }
                        },
                        new MyCombiner<>()
                )
        );
        System.out.println("Build Result: " + buildResult);


        Map<Long, List<Criterion<Long>>> mappingResult = MyResultBuilder.mapping(
                input -> input * 10, // mapper that converts input value before passing to Collector's accumulator
                listInputPartitioner, // input
                new MyCollector<>( // Collector
                        identityMap,
                        // In Java8, Functions are nothing but the Callback methods. Callback method is nothing but you pass the code that gets executed at later point in time.
                        new MyAccumulatorCallback<Long, Criterion<Long>>() {
                            @Override
                            public Map<Long, List<Criterion<Long>>> accumulate(List<Long> inputList) {
                                Map<Long, List<Criterion<Long>>> map = new HashMap<>();
                                for (Long input : inputList) {
                                    List<Criterion<Long>> criteria = new ArrayList<>();
                                    criteria.add(new Criterion<>(input * 10));
                                    criteria.add(new Criterion<>(input * 20));
                                    criteria.add(new Criterion<>(input * 30));
                                    map.put(input, criteria);
                                }
                                return map;
                            }
                        },
                        new MyCombiner<>()
                )
        );

        System.out.println("Mapping Result: " + mappingResult); // {1=[10's criterion, 20's criterion, 30's criterion], 2=[20's criterion, 40's criterion, 60's criterion], 3=[30's criterion, 60's criterion, 90's criterion], 4=[40's criterion, 80's criterion, 120's criterion], 5=[50's criterion, 100's criterion, 150's criterion], 6=[60's criterion, 120's criterion, 180's criterion], 70=[700's criterion, 1400's criterion, 2100's criterion], 7=[70's criterion, 140's criterion, 210's criterion], 8=[80's criterion, 160's criterion, 240's criterion], 40=[400's criterion, 800's criterion, 1200's criterion], 9=[90's criterion, 180's criterion, 270's criterion], 10=[100's criterion, 200's criterion, 300's criterion], 80=[800's criterion, 1600's criterion, 2400's criterion], 50=[500's criterion, 1000's criterion, 1500's criterion], 20=[200's criterion, 400's criterion, 600's criterion], 90=[900's criterion, 1800's criterion, 2700's criterion], 60=[600's criterion, 1200's criterion, 1800's criterion], 30=[300's criterion, 600's criterion, 900's criterion]}
    }

    static class MyStream<I> {
        private I head;
        private MyStream<I> tail;

        protected MyStream() {
        }

        public MyStream(I head, MyStream<I> tail) {
            this.head = head;
            this.tail = tail;
        }

        public boolean isEmpty() {
            return this instanceof EmptyStream;
        }

        public static <I, O> O myReduceMethod(MyStream<I> myStream, Supplier<O> identitySupplier, BiFunction<I, O, O> operation) { // same as collect method that has identity and accumulator
            if (myStream.isEmpty()) return identitySupplier.get();

            O identity = identitySupplier.get();

            O newIdentity = operation.apply(myStream.head, identity);

            return myReduceMethod(myStream.tail, () -> newIdentity, operation);
        }

        public static <I, O> void myCollectMethod(MyStream<I> myStream, Supplier<Result<O>> identitySupplier, BiConsumer<I, Result<O>> operation) { // same as collect method that has identity and accumulator
            if (myStream.isEmpty()) return;

            Result<O> identity = identitySupplier.get();

            operation.accept(myStream.head, identity);

            myCollectMethod(myStream.tail, () -> identity, operation);
        }



        // You have Employee object and you want to collect emp names in the collection
        // You need a collector that can collect string to List<String>
        // you need a mapper that can convert Employee object to employee name(String)
        // Here, I = Employee, V = String, O = List<String>
        public static <I, V, O> O mapping(MyStream<I> empStream, Function<I, V> mapper, MyAnotherCollector<V, O> collector) {
            O identity = collector.getSupplier().get();
            if(empStream.isEmpty()) return identity;

            I head = empStream.head;

            V convertedValue = mapper.apply(head);

            BiConsumer<V, O> accumulator = collector.getAccumulator();
            accumulator.accept(convertedValue, identity);

            return mapping(empStream.tail, mapper, new MyAnotherCollector<V, O>(() -> identity, accumulator));
        }

        // Just like Java 8's Collectors.mapping that returns a Collector
        public static <I, V, O> MyAnotherCollector<I, O> mappingReturningDiffCollector(Function<I, V> mapper, MyAnotherCollector<V, O> collector) {
            Supplier<O> supplier = collector.getSupplier();
            O identity = supplier.get();
            BiConsumer<V, O> accumulator = collector.getAccumulator();

            BiConsumer<I, O> newAccumulator = (i, o) -> {
                V v = mapper.apply(i);
                accumulator.accept(v, identity);
            };

            return new MyAnotherCollector<I, O>(supplier, newAccumulator);
        }

        // you can think in this way
        // you need to extract a key from Employee object, So you need a Function that does it
        // you need to collect Employee objects in a Map, so you need a collector that has supplier returning a Map<EmpName, List<Employee>>
        public static Map<String, List<Employee>> groupingByTest(MyStream<Employee> empStream) {

            MyAnotherCollector<Employee, List<Employee>> collectorFromClient = new MyAnotherCollector<>( // collector that returns me a value List<Employee>
                    () -> new ArrayList<>(),
                    (emp, list) -> list.add(emp)
            );

            MyAnotherCollector<Employee, Map<String, List<Employee>>> anotherCollector = groupingByReturningDiffCollector(
                    emp -> emp.getName(), // Function to retrieve key of resulting map
                    collectorFromClient
            );

            return groupingBy(empStream, anotherCollector);
        }


        // same as Java's Collectors.groupingBy
        // I = Employee, V = List<Employee>, K = key of map
        // Client is saying I can pass you a collector that can collect Employee objects in List<Employee>,
        // but you need to return me a collector that can covert Employee to Map<Emp Name, List<Employee>>
        // Here, you can tell client that I need a function also that can convert Employee to Employee Name.
        public static <I, K, V> MyAnotherCollector<I, Map<K, V>> groupingByReturningDiffCollector(
                Function<I, String> keyRetriever,
                MyAnotherCollector<I, V> collectorFromClient) {

            Supplier supplier = () -> new HashMap<String, List<I>>();

            BiConsumer<I, V> accumulator = collectorFromClient.getAccumulator();

            BiConsumer<I, Map<String, V>> newAccumulator =
                    (emp, outputMap) -> {
                        String key = keyRetriever.apply(emp);

                        V employees = outputMap.get(key);
                        if (employees == null) {
                            employees = collectorFromClient.getSupplier().get();
                            outputMap.put(key, employees);
                        }

                        accumulator.accept(emp, employees);

                    };


            return new MyAnotherCollector(supplier, newAccumulator);
        }

        private static <EMP, K, V> Map<K, V> groupingBy(MyStream<EMP> empStream, MyAnotherCollector<EMP, Map<K, V>> anotherCollector) {
            Supplier<Map<K, V>> supplier = anotherCollector.getSupplier();
            BiConsumer<EMP, Map<K, V>> accumulator = anotherCollector.getAccumulator();

            Map<K, V> identityMap = supplier.get();

            if (empStream.isEmpty()) return identityMap; // exit condition

            EMP emp = empStream.head;

            accumulator.accept(emp, identityMap);

            return groupingBy(empStream.tail, new MyAnotherCollector<EMP, Map<K, V>>(() -> identityMap, accumulator));

        }

    }

    static class EmptyStream extends MyStream {

        public EmptyStream() {

        }
    }

    static class Result<O> {

        private O res;

        public Result(O res) {
            this.res = res;
        }

        public O getRes() {
            return res;
        }

        public void setRes(O res) {
            this.res = res;
        }
    }

    static class MyCollector<I, O> {
        private Map<I, List<O>> identity;
        private MyAccumulatorCallback<I, O> accumulator;
        private MyCombiner<I, O> combiner;

        public MyCollector(Map<I, List<O>> identity, MyAccumulatorCallback<I, O> accumulator, MyCombiner<I, O> combiner) {
            this.identity = identity;
            this.accumulator = accumulator;
            this.combiner = combiner;
        }

        public Map<I, List<O>> getIdentity() {
            return identity;
        }

        public MyAccumulatorCallback<I, O> getAccumulator() {
            return accumulator;
        }

        public MyCombiner<I, O> getCombiner() {
            return combiner;
        }
    }


    static class MyAnotherCollector<I, O> { // you want to collect objects of I in O. e.g. I can be Employee and O can be List,Set etc.

        private Supplier<O> supplier;
        private BiConsumer<I, O> accumulator;


        public MyAnotherCollector(Supplier<O> supplier, BiConsumer<I, O> accumulator) {
            this.supplier = supplier;
            this.accumulator = accumulator;
        }

        public Supplier<O> getSupplier() {
            return supplier;
        }

        public BiConsumer<I, O> getAccumulator() {
            return accumulator;
        }
    }

    static class MyResultBuilder {

        // When do you decide whether something should be passed as Callback (anonymous class object) or a concrete class object
        // e.g. if combiner operation is same (it always creates the same type of output e.g. map), then I would just write a concrete class and pass its object in this method.
        public static <I, O> Map<I, List<O>> build(
                InputPartitioner<List<I>> inputPartitioner, // it has input list and partition size
                MyCollector<I, O> myCollector) { // combiner/reducer/aggregator combines the results from two accumulators

            Map<I, List<O>> identity = myCollector.getIdentity();
            MyAccumulatorCallback<I, O> accumulator = myCollector.getAccumulator();
            MyCombiner<I, O> combiner = myCollector.getCombiner();

            InputPartitioner<List<I>>.PartitionResult<List<I>> partition = inputPartitioner.partition();

            while (partition != null) {
                Map<I, List<O>> accumulatedResult = accumulator.accumulate(partition.getPartition());
                identity = combiner.combine(identity, accumulatedResult);
                partition = inputPartitioner.partition(partition.getNextFrom());
                //return build(inputPartitioner, identity, accumulator, combiner);
            }

            return identity;
        }

        // before calling accumulator, mapper transforms the input to some other output that you want
        public static <I, U, O> Map<U, List<O>> mapping(
                Function<I, U> mapper,
                InputPartitioner<List<I>> inputPartitioner, // it has input list and partition size
                MyCollector<U, O> myCollector) {

            Map<U, List<O>> identity = myCollector.getIdentity();
            MyAccumulatorCallback<U, O> accumulator = myCollector.getAccumulator();
            MyCombiner<U, O> combiner = myCollector.getCombiner();

            InputPartitioner<List<I>>.PartitionResult<List<I>> partition = inputPartitioner.partition();

            while (partition != null) {
                Map<U, List<O>> accumulatedResult = accumulator.accumulate(partition.getPartition().stream().map(mapper).collect(Collectors.toList()));

                identity = combiner.combine(identity, accumulatedResult);
                partition = inputPartitioner.partition(partition.getNextFrom());

            }
            return identity;
        }

    }


    interface MyAccumulatorCallback<I, O> {
        Map<I, List<O>> accumulate(List<I> input);
    }

    static class MyCombiner<I, O> {
        public Map<I, List<O>> combine(Map<I, List<O>> o1, Map<I, List<O>> o2) {
            o1.putAll(o2);
            return o1;
        }
    }

    static class InputPartitioner<T extends List> {

        private T input;
        private int size;

        public InputPartitioner(T input, int size) {
            this.input = input;
            this.size = size;
        }

        public PartitionResult partition() {
            return partition(0);
        }

        public PartitionResult partition(int from) {

            if (from >= input.size()) return null;
            if ((input.size() - from) < size)
                return new PartitionResult(input.subList(from, input.size()), size + from);

            return new PartitionResult(input.subList(from, from + size), size + from);
        }

        class PartitionResult<T extends List> {
            private int nextFrom;
            private T partition;

            public PartitionResult(T partition, int nextFrom) {
                this.nextFrom = nextFrom;
                this.partition = partition;
            }

            public int getNextFrom() {
                return nextFrom;
            }

            public T getPartition() {
                return partition;
            }
        }
    }

    static class Criterion<I> {
        private I input;

        public Criterion(I input) {
            this.input = input;
        }

        public String getOutput() {
            return input.toString() + "'s criterion";
        }

        @Override
        public String toString() {
            return getOutput();
        }
    }

}


