package debug;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

enum ValueType {
    SHORT, INT, LONG, FLOAT, DOUBLE, STRING, BOOLEAN;

    private static final Map<String, ValueType> TYPES_MAPPING;

    static {
        TYPES_MAPPING = new HashMap<>();

        for(String s : Arrays.asList("short", "Short", "java.lang.Short")) {
            TYPES_MAPPING.put(s, SHORT);
        }
        for (String s : Arrays.asList("int", "Integer", "java.lang.Integer")) {
            TYPES_MAPPING.put(s, INT);
        }
        for (String s : Arrays.asList("long", "Long", "java.lang.Long")) {
            TYPES_MAPPING.put(s, LONG);
        }
        for (String s : Arrays.asList("float", "Float", "java.lang.Float")) {
            TYPES_MAPPING.put(s, FLOAT);
        }
        for (String s : Arrays.asList("double", "Double", "java.lang.Double")) {
            TYPES_MAPPING.put(s, DOUBLE);
        }
        for (String s : Arrays.asList("String", "java.lang.String")) {
            TYPES_MAPPING.put(s, STRING);
        }
        for (String s : Arrays.asList("boolean", "Boolean", "java.lang.Boolean")) {
            TYPES_MAPPING.put(s, BOOLEAN);
        }
    }

    public static ValueType from(String text) {
        return TYPES_MAPPING.get(text);
    }
}
