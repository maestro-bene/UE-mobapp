package fr.imt_atlantique.myfirstapplication.activities;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import fr.imt_atlantique.myfirstapplication.R;
import fr.imt_atlantique.myfirstapplication.data.model.User;
import fr.imt_atlantique.myfirstapplication.fragments.UserInfoDisplayFragment;
import fr.imt_atlantique.myfirstapplication.fragments.UserInfoEntryFragment;
import fr.imt_atlantique.myfirstapplication.fragments.UserListFragment;

public class MainActivity extends AppCompatActivity implements UserInfoEntryFragment.UserInfoEntryListener, UserListFragment.UserEditListener, UserListFragment.UserActionListener, UserInfoDisplayFragment.OnBackToListListener {

    private static final String TAG = "MainActivityLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate called");

        if (savedInstanceState == null) { // Ensure fragment isn't added multiple times
            // Create a UserInfoEntryFragment
            UserListFragment userListFragment = new UserListFragment();

            // Add the fragment in the fragmentContainer
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, userListFragment);
            transaction.commit();

            Log.d(TAG, "UserListFragment added");
        }
    }

    @Override
    public void onUserInfoDisplay(User user) {
        // Create a displayFragment with the transmitted user
        UserInfoDisplayFragment displayFragment = UserInfoDisplayFragment.newInstance(user);

        Log.d(TAG, "Created the displayFragment using User:\n" + user.toString());

        // Replace the current fragment with the newly created fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, displayFragment)
                .addToBackStack(null)
                .commit();

        Log.i(TAG, "Display fragment replaced the old fragment");
    }

    @Override
    public void onUserEdit(User user) {
        // Create a displayFragment with the transmitted user
        UserInfoEntryFragment userInfoEntryFragment = UserInfoEntryFragment.newInstance(user);

        Log.d(TAG, "Created the userInfoEntryFragment using User:\n" + user.toString());

        // Replace the current fragment with the newly created fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, userInfoEntryFragment)
                .addToBackStack(null)
                .commit();

        Log.i(TAG, "User Info Entry fragment replaced the old fragment");
    }

    @Override
    public void onUserEdit() {
        // Create a displayFragment with the transmitted user
        UserInfoEntryFragment userInfoEntryFragment = UserInfoEntryFragment.newInstance();

        Log.d(TAG, "Created the userInfoEntryFragment using no Users\n");

        // Replace the current fragment with the newly created fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, userInfoEntryFragment)
                .addToBackStack(null)
                .commit();

        Log.i(TAG, "User Info Entry fragment replaced the old fragment");
    }

    @Override
    public void onBackToList() {
        UserListFragment userListFragment = UserListFragment.newInstance();

        // Replace fragment with UserListFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, userListFragment)
                .addToBackStack(null) // Optional: Add transaction to back stack
                .commit();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Detect a touch outside an clickable or selectable component
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            Log.d(TAG, "Touched to hide keyboard");
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
        // Hide the Keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Log.d(TAG, "Keyboard hidden");
    }

}
