package debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DebugFragment extends Fragment {
    static final String GROUP = "group";

    private Callback mCallback;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private String mGroup;

    static DebugFragment newFragment(String group) {
        DebugFragment f = new DebugFragment();
        Bundle args = new Bundle();
        args.putString(GROUP, group);
        f.setArguments(args);
        return f;
    }

    public DebugFragment() {   }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (Callback) activity;
        mGroup = getArguments() == null ? null : getArguments().getString(GROUP);
        getActivity().setTitle(mGroup);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setTitle("Debug");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = createAdapter();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.items_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private RecyclerView.Adapter createAdapter() {
        if (mGroup == null) {
            return new DebugAdapter(getActivity(), mOnClickListener, Debug.getGroups(), true);
        }
        return new DebugAdapter(getActivity(), mOnClickListener, Debug.getFields(mGroup), false);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            if (mGroup == null) {
                mCallback.onGroupSelected(Debug.getGroups().get(position));
            } else {
                String field = Debug.getFields(mGroup).get(position);
                mCallback.onFieldSelected(field);
            }
        }
    };

    public void reload() {
        mAdapter.notifyDataSetChanged();
    }

    interface Callback {
        void onGroupSelected(String group);
        void onFieldSelected(String field);
    }

}
