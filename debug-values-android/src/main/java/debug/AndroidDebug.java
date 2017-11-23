package debug;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AndroidDebug {
    private static Context sContext;
    private static PrefsInjector sValueInjector;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
        sValueInjector = new PrefsInjector(sContext);
        Debug.init();
        Debug.setValueInjector(sValueInjector);
    }

    public static void inject(Object target) {
        Debug.inject(target);
    }

    public static List<String> getGroups() {
        return Debug.getGroups();
    }

    public static List<String> getFields(String group) {
        return Debug.getFields(group);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String fieldName) {
        return (T)sValueInjector.getValue(Debug.getField(fieldName));
    }

    public static void setValue(String fieldName, Object value) {
        Debug.setValue(fieldName, value);
    }

    public static String getName(String fieldName) {
        return Debug.getName(fieldName);
    }

    public static String getComment(String fieldName) {
        return Debug.getComment(fieldName);
    }

    public static ValueType getType(String fieldName) {
        return Debug.getType(fieldName);
    }

    public static Object getDefaultValue(String fieldName) {
        return Debug.getDefaultValue(fieldName);
    }

    public static boolean hasOptions(String fieldName) {
        Field field = Debug.getField(fieldName);
        int[] values = field.values;
        return field.type == ValueType.BOOLEAN ||
                (values.length > 0 && !(values.length == 1 && values[0] == 0));
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getOptions(String fieldName) {
        Field field = Debug.getField(fieldName);

        if (field.type == ValueType.BOOLEAN) {
            return (List<T>) Arrays.asList(Boolean.FALSE, Boolean.TRUE);
        }

        int[] optionRes = field.values;
        int length = optionRes.length;

        if (length == 0) {
            return Collections.emptyList();
        }

        Object[] arr = null;

        switch (field.type) {
            case STRING:
                if (length == 1) {
                    String[] strArr = sContext.getResources().getStringArray(optionRes[0]);
                    arr = new Object[strArr.length];
                    System.arraycopy(strArr, 0, arr, 0, strArr.length);
                } else {
                    arr = new Object[length];
                    for (int i = 0; i < length; i++) {
                        arr[i] = sContext.getResources().getString(optionRes[i]);
                    }
                }
                break;
            case INT:
                if (length == 1) {
                    int[] intArr = sContext.getResources().getIntArray(optionRes[0]);
                    arr = new Object[intArr.length];
                    for (int i = 0; i < intArr.length; i++) {
                        arr[i] = intArr[i];
                    }
                } else {
                    arr = new Object[length];
                    for (int i = 0; i < length; i++) {
                        arr[i] = sContext.getResources().getInteger(optionRes[i]);
                    }
                }
                break;
        }

        return arr == null ? Collections.<T>emptyList() : (List<T>) Arrays.asList(arr);
    }
}
