package abs.ixi.util;

import static abs.ixi.util.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Convenience methods for Java collections
 */
public class CollectionUtils {
    public static String toString(Collection<?> list) {
	if (isNullOrEmpty(list)) {
	    return EMPTY;
	} else {
	    StringBuilder sb = new StringBuilder();
	    list.stream().forEach((v) -> {
		sb.append(v.toString());
		sb.append(",");
	    });

	    return StringUtils.removeLastChar(sb).toString();
	}
    }

    public static boolean isNullOrEmpty(Collection<?> c) {
	return c == null || c.size() == 0 ? true : false;
    }

    @SafeVarargs
    public static <T> List<T> list(T... args) {
	if (args != null && args.length > 0) {
	    return Arrays.asList(args);
	}

	return new ArrayList<T>();
    }

    @SafeVarargs
    public static <T> Set<T> set(T... args) {
	Set<T> set = new HashSet<T>();

	if (args != null && args.length > 0) {
	    set.addAll(Arrays.asList(args));
	}

	return set;
    }

    public static <T> Set<T> set(String args, Function<String, T> f) {
	Set<T> set = Arrays.asList(args.split(",")).parallelStream().map((v) -> f.apply(v)).collect(Collectors.toSet());

	return set;
    }

    public static <K, V> Map<K, V> map(K key, V val) {
	Map<K, V> map = new HashMap<K, V>();
	map.put(key, val);
	return map;
    }

}
