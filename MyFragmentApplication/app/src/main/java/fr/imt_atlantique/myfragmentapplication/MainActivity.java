package fr.imt_atlantique.myfragmentapplication;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements EditFragment.OnNameEnteredListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            // Add EditFragment only if this is the first creation
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_edit, new EditFragment())
                    .commit();
        }
    }

    @Override
    public void onNameEntered(String name) {
        DisplayFragment displayFragment = new DisplayFragment();

        // Prepare data to send to DisplayFragment
        Bundle args = new Bundle();
        args.putString("name", name);
        displayFragment.setArguments(args);

        // Check orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape mode, display fragments side by side
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_display, displayFragment)
                    .commit();
        } else {
            // In portrait mode, replace EditFragment with DisplayFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_edit, displayFragment)
                    .addToBackStack(null)  // Optional, if you want back navigation to revert to the EditFragment
                    .commit();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    hideKeyboard(v);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
