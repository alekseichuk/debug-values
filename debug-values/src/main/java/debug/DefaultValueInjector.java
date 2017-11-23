package debug;

import java.util.HashMap;
import java.util.Map;

class DefaultValueInjector implements ValueInjector {
    private Map<Field, Object> mFieldToValueMap = new HashMap<>();

    @Override
    public Object getValue(Field field) {
        return mFieldToValueMap.get(field);
    }

    @Override
    public void setValue(Field field, Object value) {
        if (field != null) {
            mFieldToValueMap.put(field, value);
        }
    }
}
