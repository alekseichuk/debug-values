package debug;

interface ValueInjector {
    Object getValue(Field field);

    void setValue(Field field, Object value);
}
