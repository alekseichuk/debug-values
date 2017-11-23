package debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class DebugActivity extends AppCompatActivity implements DebugFragment.Callback, FieldDialogFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Debug");
    }

    @Override
    public void onGroupSelected(String group) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.pop_up, 0, 0, R.anim.pop_down)
                .add(R.id.group_content, DebugFragment.newFragment(group))
                .addToBackStack(group)
                .commit();
    }

    @Override
    public void onFieldSelected(String field) {
        FieldDialogFragment.newInstance(field).show(getSupportFragmentManager(), field);
    }

    @Override
    public void onDismiss() {
        ((DebugFragment) getSupportFragmentManager().findFragmentById(R.id.group_content)).reload();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}
