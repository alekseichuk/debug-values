package debug;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static debug.Constants.DEBUG_CLASS_FULL_NAME;
import static debug.Constants.GEN_CLASS_FIELDS_MAP_NAME;
import static debug.Constants.GEN_GROUP_FIELDS_MAP_NAME;
import static debug.Constants.GEN_NAME_FIELD_MAP_NAME;

public class Debug {
    private static Map<String, Map<String, Field>> sGroupToFieldsMap;
    private static Map<String, List<Field>> sClassToFieldsMap;
    private static Map<String, Field> sFieldNameToFieldMap;
    private static Map<String, java.lang.reflect.Field> sReflectionCache;
    private static ValueInjector sValueInjector = new DefaultValueInjector();

    private Debug() { }

    @SuppressWarnings("unchecked")
    public static void init() {
        try {
            Class<?> clazz = Class.forName(DEBUG_CLASS_FULL_NAME);
            sGroupToFieldsMap = (Map) clazz.getField(GEN_GROUP_FIELDS_MAP_NAME).get(null);
            sClassToFieldsMap = (Map) clazz.getField(GEN_CLASS_FIELDS_MAP_NAME).get(null);
            sFieldNameToFieldMap = (Map) clazz.getField(GEN_NAME_FIELD_MAP_NAME).get(null);
            sReflectionCache = new HashMap<>();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Debug init failed. Can't load " + DEBUG_CLASS_FULL_NAME + " class");
        } catch (Exception e) {
            throw new RuntimeException("Debug init failed. Can't read " + DEBUG_CLASS_FULL_NAME);
        }
    }

    public static void inject(Object target) {
        checkInitialized();
        Class clazz = null;
        try {
            clazz = Class.forName(target.getClass().getName());
            while (clazz != null && !sClassToFieldsMap.containsKey(clazz.getName())) {
                clazz = clazz.getSuperclass();
            }
        } catch (ClassNotFoundException e) {
            //no op
        }
        if (clazz == null) {
            return;
        }
        for (Field f : sClassToFieldsMap.get(clazz.getName())) {
            Object value = sValueInjector.getValue(f);
            java.lang.reflect.Field classField = sReflectionCache.get(f.javaName);
            try {
                if (classField == null) {
                    classField = clazz.getDeclaredField(f.shortJavaName);
                    classField.setAccessible(true);
                    f.defaultValue = classField.get(target);
                    sReflectionCache.put(f.javaName, classField);
                }
                if (value != null) {
                    classField.set(target, value);
                } else {
                    sValueInjector.setValue(f, f.defaultValue);
                }
            } catch (Exception e) {
                throw new RuntimeException("Can't inject value " + value + " into " + f.javaName + " of " + target);
            }
        }
    }

    public static List<String> getGroups() {
        checkInitialized();
        String[] arr = new String[sGroupToFieldsMap.size()];
        return Arrays.asList(sGroupToFieldsMap.keySet().toArray(arr));
    }

    public static List<String> getFields(String group) {
        checkInitialized();
        String[] arr = new String[sGroupToFieldsMap.get(group).size()];
        return Arrays.asList(sGroupToFieldsMap.get(group).keySet().toArray(arr));
    }

    public static String getName(String fieldName) {
        String name = getField(fieldName).name;
        return name == null || name.isEmpty() ? getField(fieldName).shortJavaName : name;
    }

    public static String getComment(String fieldName) {
        return getField(fieldName).comment;
    }

    public static ValueType getType(String fieldName) {
        return getField(fieldName).type;
    }

    public static Object getDefaultValue(String fieldName) {
        return getField(fieldName).defaultValue;
    }

    public static void setValue(String fieldName, Object value) {
        checkInitialized();
        if (sFieldNameToFieldMap.containsKey(fieldName)) {
            Field field = sFieldNameToFieldMap.get(fieldName);
            sValueInjector.setValue(field, value);
        } else {
            throw new RuntimeException("Can't set value " + value + " to " + fieldName);
        }
    }

    static Field getField(String fieldName) {
        checkInitialized();
        if (sFieldNameToFieldMap.containsKey(fieldName)) {
            Field field = sFieldNameToFieldMap.get(fieldName);
            if (field != null) {
                return field;
            }
        }
        throw new RuntimeException("Can't get value for " + fieldName);
    }

    static void setValueInjector(ValueInjector valueInjector) {
        sValueInjector = valueInjector;
    }

    private static void checkInitialized() {
        if (sGroupToFieldsMap == null || sClassToFieldsMap == null || sFieldNameToFieldMap == null || sReflectionCache == null) {
            throw new RuntimeException("Debug hasn't been initialized");
        }
    }
}