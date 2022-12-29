package story;

import java.util.ArrayList;

public final class ArrayUtil {
    public static <T> T getRandomObjectFromArray(T[] ARR) {
        return ARR[(int) (Math.round(Math.random() * (ARR.length - 1)))];
    }

    public static <T> T getRandomObjectFromArray(ArrayList<T> LIST) {
        return LIST.get((int) (Math.round(Math.random() * (LIST.size() - 1))));
    }
}
