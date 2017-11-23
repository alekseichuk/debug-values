package debug;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class DebugAdapter extends RecyclerView.Adapter<DebugAdapter.ViewHolder> {
    private static final int VIEW_TEXT = 0;

    private final boolean mIsGroups;
    private final List<String> mSource;
    private final View.OnClickListener mOnClickListener;
    private final int mBackground;

    DebugAdapter(Context context, View.OnClickListener onClickListener, List<String> source, boolean groups) {
        mOnClickListener = onClickListener;
        mIsGroups = groups;
        mSource = source;
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        mBackground = typedValue.resourceId;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TEXT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = mSource.get(position);
        if (mIsGroups) {
            holder.mFieldNameTextView.setText(text);
            holder.mFieldValueTextView.setVisibility(View.GONE);
        } else {
            holder.mFieldNameTextView.setText(AndroidDebug.getName(text));
            holder.mFieldValueTextView.setText(getValue(text));
            holder.mFieldValueTextView.setVisibility(View.VISIBLE);
        }
    }

    private String getValue(String text) {
        Object value = AndroidDebug.getValue(text);
        Object defaultValue = AndroidDebug.getDefaultValue(text);
        return equals(value, defaultValue) ? "Default(" + defaultValue + ")"  : String.valueOf(value);
    }

    private static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    @Override
    public int getItemCount() {
        return mSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mFieldNameTextView;
        final TextView mFieldValueTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(mOnClickListener);
            view.setBackgroundResource(mBackground);
            mFieldNameTextView = (TextView) view.findViewById(R.id.field_name_text_view);
            mFieldValueTextView = (TextView) view.findViewById(R.id.field_value_text_view);
        }
    }
}
