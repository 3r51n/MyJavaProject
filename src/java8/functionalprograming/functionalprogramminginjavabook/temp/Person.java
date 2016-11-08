package java8.functionalprograming.functionalprogramminginjavabook.temp;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Tushar Chokshi @ 11/7/16.
 */
public class Person {
    private int id;
    private String firstName;
    private String lastName;

    public Person(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Person apply(int id, String firstName, String lastName) {
        return new Person(id, firstName, lastName);
    }

    public static Person apply(Optional<Integer> id, Optional<String> firstName, Optional<String> lastName) {
        return new Person(id.get(), firstName.get(), lastName.get());
    }

    public static void main(String[] args) {
        Person person = Person.apply(
                assertPositive(-1, "Negative id"),
                assertValidName("Tushar", "Invalid first name:"),
                assertValidName("Chokshi", "Invalid last name:"));

        // better way
        Person person1 = Person.apply(assertPositive_(-1, "Negative id").get(),
                assertValidName_("Tushar", "Invalid first name:").get(),
                assertValidName_("Chokshi", "Invalid last name:").get());// this may throw an exception




    }

    private static int assertPositive(int i, String message) {
        if (i < 0) {
            throw new IllegalStateException(message);
        } else {
            return i;
        }
    }

    private static String assertValidName(String name, String message) {
        if (name == null || name.length() == 0
                || name.charAt(0) < 65 || name.charAt(0) > 91) {
            throw new IllegalStateException(message);
        }
        return name;
    }

    private static Supplier<Optional<Integer>> assertPositive_(int i, String message) {
        if (i < 0) {
            return () -> {throw new IllegalStateException(message);};
        } else {
            return () -> Optional.ofNullable(i);
        }
    }

    private static Supplier<Optional<String>> assertValidName_(String name, String message) {
        if (name == null || name.length() == 0
                || name.charAt(0) < 65 || name.charAt(0) > 91) {
            return () -> {throw new IllegalStateException(message);};
        }
        return () -> Optional.ofNullable(name);
    }
}