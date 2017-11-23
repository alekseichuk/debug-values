package debug;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsInjector implements ValueInjector {
    private SharedPreferences mSharedPreferences;

    public PrefsInjector(Context context) {
        mSharedPreferences = context.getSharedPreferences("debug_values", 0);
    }

    @Override
    public Object getValue(Field field) {
        if (mSharedPreferences.contains(field.javaName)) {
            switch (field.type) {
                case BOOLEAN:
                    return mSharedPreferences.getBoolean(field.javaName, false);
                case INT:
                case SHORT:
                    return mSharedPreferences.getInt(field.javaName, 0);
                case LONG:
                    return mSharedPreferences.getLong(field.javaName, 0L);
                case FLOAT:
                    return mSharedPreferences.getFloat(field.javaName, 0f);
                case DOUBLE:
                    return getDouble(mSharedPreferences, field.javaName, 0d);
                case STRING:
                    return mSharedPreferences.getString(field.javaName, null);
            }
        }
        return null;
    }

    @Override
    public void setValue(Field field, Object value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (value == null) {
            editor.remove(field.javaName);
        } else {
            switch (field.type) {
                case BOOLEAN:
                    editor.putBoolean(field.javaName, (Boolean) value);
                    break;
                case INT:
                case SHORT:
                    editor.putInt(field.javaName, (Integer) value);
                    break;
                case LONG:
                    editor.putLong(field.javaName, (Long) value);
                    break;
                case FLOAT:
                    editor.putFloat(field.javaName, (Float) value);
                    break;
                case DOUBLE:
                    putDouble(editor, field.javaName, (Double) value);
                    break;
                case STRING:
                    editor.putString(field.javaName, (String) value);
                    break;
            }
        }
        editor.commit();
    }

    private static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
