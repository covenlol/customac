package dev.phoenixhaven.customac.utils.java;

import java.util.List;
import java.util.function.Predicate;

public class StreamUtil {
    public static <T> boolean anyMatch(final List<T> objects, final Predicate<T> condition) {
        if (condition == null) return false;

        for (final T object : objects) {

            if (condition.test(object)) return true;
        }

        return false;
    }
}
