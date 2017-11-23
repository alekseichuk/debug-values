package debug;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

public class FieldDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String FIELD = "field";
    private static final int ITEM_LAYOUT = R.layout.option_layout;

    private String mField;
    private ValueType mType;
    private Object mDefaultValue;
    private Object mSelectedValue;

    private EditText mEditText;
    private ArrayAdapter<Object> mAdapter;

    static FieldDialogFragment newInstance(String field) {
        FieldDialogFragment f = new FieldDialogFragment();
        Bundle args = new Bundle();
        args.putString(FIELD, field);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mField = getArguments().getString(FIELD);
        mType = AndroidDebug.getType(mField);
        mDefaultValue =  AndroidDebug.getDefaultValue(mField);
        mSelectedValue = AndroidDebug.getValue(mField);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(AndroidDebug.getName(mField));
        builder.setPositiveButton("SET", this);
        builder.setNegativeButton("RESET", this);
        if (AndroidDebug.hasOptions(mField)) {
            List<Object> options = AndroidDebug.getOptions(mField);
            final int checkedItem = options.indexOf(mSelectedValue);
            mAdapter = new ArrayAdapter<>(getActivity(), ITEM_LAYOUT, options);
            builder.setSingleChoiceItems(mAdapter, checkedItem, this);
        } else {
            builder.setView(createEditTextView());
        }
        return builder.create();
    }

    private View createEditTextView() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_field, null);
        mEditText = (EditText) v.findViewById(R.id.edit_field);
        mEditText.setText(String.valueOf(mSelectedValue));
        mEditText.setHint(mType.toString());
        mEditText.setGravity(Gravity.LEFT | Gravity.TOP);
        if (mType == ValueType.DOUBLE || mType == ValueType.FLOAT) {
            mEditText.setKeyListener(DigitsKeyListener.getInstance(true, true));
        }
        if (mType == ValueType.SHORT || mType == ValueType.LONG || mType == ValueType.INT) {
            mEditText.setKeyListener(DigitsKeyListener.getInstance(true, false));
        }
        if (mType == ValueType.STRING) {
            mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        return v;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (mEditText != null) {
                    mSelectedValue = getValueFromEditText();
                }
                AndroidDebug.setValue(mField, mSelectedValue);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                AndroidDebug.setValue(mField, mDefaultValue);
                break;
            default:
                mSelectedValue = mAdapter.getItem(which);
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null) {
            ((Callback) getActivity()).onDismiss();
        }
    }

    private Object getValueFromEditText() {
        String s = mEditText.getText().toString();
        switch (mType) {
            case DOUBLE:
                return s.isEmpty() ? mDefaultValue : Double.parseDouble(s);
            case FLOAT:
                return s.isEmpty() ? mDefaultValue : Float.parseFloat(s);
            case SHORT:
                return s.isEmpty() ? mDefaultValue : Short.parseShort(s);
            case LONG:
                return s.isEmpty() ? mDefaultValue : Long.parseLong(s);
            case INT:
                return s.isEmpty() ? mDefaultValue : Integer.parseInt(s);
            case STRING:
                return "null".equals(s) ? null : s;
        }
        return null;
    }

    interface Callback {
        void onDismiss();
    }
}
