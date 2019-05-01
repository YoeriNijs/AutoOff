package app.util;

public class AutoOffUtil {
    public static <T> T nullChecked(T o) {
        if(o == null) throw new IllegalStateException("Expecting that object is not null!");
        return o;
    }

    public static boolean isEmpty(String val) {
        return val == null || val.length() <= 0;
    }
}
