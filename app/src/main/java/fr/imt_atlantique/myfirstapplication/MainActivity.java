package fr.imt_atlantique.myfirstapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout phoneNumbersContainer;

    private Map<Integer, EditText> phoneNumberEditTexts = new HashMap<>();
    private int phoneNumberEditTextNextId = 1;

    private SharedPreferences sharedPreferences;

    private EditText surnameEditText;
    private EditText nameEditText;
    private Spinner departmentSpinner;
    private EditText birthDateEditText;
    private EditText cityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Lifecycle", "onCreate method");
        setContentView(R.layout.activity_main);

        surnameEditText = findViewById(R.id.surname);
        nameEditText = findViewById(R.id.name);
        birthDateEditText = findViewById(R.id.birth_date);
        cityEditText = findViewById(R.id.city);
        departmentSpinner = findViewById(R.id.department);
        phoneNumbersContainer = findViewById(R.id.phone_numbers_container);

        birthDateEditText.setOnClickListener(view -> showDatePickerDialog());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        Log.d("SharedPreferences", "SharedPreferences object: " + sharedPreferences);

        // Restore state from savedInstanceState Bundle if available
        if (savedInstanceState != null) {
            // Restore the state of phone number fields
            for (Map.Entry<Integer, EditText> entry : phoneNumberEditTexts.entrySet()) {
                int viewId = entry.getKey();
                EditText editText = entry.getValue();
                String phoneNumber = savedInstanceState.getString("phoneNumber" + viewId);
                if (phoneNumber != null) {
                    editText.setText(phoneNumber);
                }
            }
            // Restore the number of phone number fields
            int numPhoneNumbers = savedInstanceState.getInt("numPhoneNumbers", 0);
            // Add additional phone number fields if needed
            for (int i = phoneNumberEditTextNextId; i <= numPhoneNumbers; i++) {
                addPhoneNumberField(phoneNumbersContainer);
                EditText editText = phoneNumberEditTexts.get(i);
                String phoneNumber = savedInstanceState.getString("phoneNumber" + i);
                if (editText != null && phoneNumber != null) {
                    editText.setText(phoneNumber);
                }
            }

        }
        // Load data from SharedPreferences and fill in the EditText fields
        loadUserData();
    }

    // Method to load user data from SharedPreferences and fill in EditText fields
    private void loadUserData() {

        // Clear any existing phone number fields
        phoneNumbersContainer.removeAllViews();
        phoneNumberEditTexts.clear();

        surnameEditText.setText(sharedPreferences.getString("surname", ""));
        nameEditText.setText(sharedPreferences.getString("name", ""));
        birthDateEditText.setText(sharedPreferences.getString("birthDate", ""));
        cityEditText.setText(sharedPreferences.getString("city", ""));
        //  department from SharedPreferences
        String department = sharedPreferences.getString("department", "");
        if (!department.isEmpty()) {
            // Find the position of the department in the spinner's array
            int position = ((ArrayAdapter<String>) departmentSpinner.getAdapter()).getPosition(department);
            if (position != -1) {
                departmentSpinner.setSelection(position);
            }
        }

        // Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("phoneNumbers", "");

        Log.d("SharedPreferences", "Retrieved JSON string: " + json);
        // Create a new map to store EditTexts
        phoneNumberEditTexts = new HashMap<>();

        // If the JSON string is not empty, parse it and add EditTexts to the map
        if (!json.isEmpty()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    Integer key = Integer.parseInt(entry.getKey());
                    String phoneNumber = entry.getValue().getAsString();
                    addPhoneNumberFieldWithText(phoneNumbersContainer, phoneNumber); // Add EditText with pre-filled phone number
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause method");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of phone number fields
        for (Map.Entry<Integer, EditText> entry : phoneNumberEditTexts.entrySet()) {
            int viewId = entry.getKey();
            EditText editText = entry.getValue();
            outState.putString("phoneNumber" + viewId, editText.getText().toString());
        }
        // Save the number of phone number fields
        outState.putInt("numPhoneNumbers", phoneNumberEditTexts.size());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of phone number fields
        for (Map.Entry<Integer, EditText> entry : phoneNumberEditTexts.entrySet()) {
            int viewId = entry.getKey();
            EditText editText = entry.getValue();
            String phoneNumber = savedInstanceState.getString("phoneNumber" + viewId);
            if (phoneNumber != null) {
                editText.setText(phoneNumber);
            }
        }
    }

    public void validateAction(View view) {

        // Get the text from the EditTexts
        String surname = surnameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String department = departmentSpinner.getSelectedItem().toString();

        // Check if any of the EditTexts is empty
        if (surname.isEmpty() || name.isEmpty() || birthDate.isEmpty() || city.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method without proceeding further
        }
        // Check if there are any phone numbers entered
        if (phoneNumberEditTexts.isEmpty()) {
            // Show a red toast message to notify the user to enter at least one phone number
            Toast.makeText(this, "Please enter at least one phone number", Toast.LENGTH_SHORT).show();
            return; // Exit the method without proceeding further
        }

        // Save user data in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("surname", surname);
        editor.putString("name", name);
        editor.putString("birthDate", birthDate);
        editor.putString("city", city);
        editor.putString("department", department);

        // Create an array to store phone numbers for the User class
        ArrayList<String> phoneNumbersList = new ArrayList<>();

        // Append phone numbers to StringBuilder and add them to the array list
        StringBuilder phoneNumbersBuilder = new StringBuilder();
        for (Map.Entry<Integer, EditText> entry : phoneNumberEditTexts.entrySet()) {
            int id = entry.getKey();
            EditText editText = entry.getValue();
            String phoneNumber = editText.getText().toString();
            if (phoneNumber.isEmpty()) {
                Log.e("Validation Error", "One or more phone number EditTexts is null");
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Phone number " + id + " is not valid", Toast.LENGTH_SHORT).show();
                return;
            }
            phoneNumbersBuilder.append(phoneNumber);
            phoneNumbersList.add(phoneNumber);
        }

        // Save the JSON string to SharedPreferences
        String json = serializeMap(phoneNumberEditTexts);
        editor.putString("phoneNumbers", json);

        // Apply to preferences
        editor.apply();

        Log.d("SharedPreferences", "Data saved to SharedPreferences");

        // Convert the array list to an array of strings
        String[] phoneNumbersArray = phoneNumbersList.toArray(new String[0]);

        // Create user object
        User user = new User(surname, name, birthDate, city, department, phoneNumbersArray);

        // Create Intent to start DisplayActivity
        Intent intent = new Intent(this, DisplayActivity.class);
        // Add user object to Intent
        intent.putExtra("user", user);
        // Start DisplayActivity
        startActivity(intent);
    }

    // Custom method to serialize the map to JSON
    private String serializeMap(Map<Integer, EditText> map) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<Integer, EditText> entry : map.entrySet()) {
            Integer key = entry.getKey();
            EditText editText = entry.getValue();
            // Extract relevant data from EditText and add to JsonObject
            jsonObject.addProperty(key.toString(), editText.getText().toString());
        }
        return jsonObject.toString();
    }

    public void addPhoneNumberFieldWithText(View view, String phoneNumber) {
        // Call addPhoneNumberField method and pass the view
        addPhoneNumberField(view);

        // Retrieve the EditText corresponding to the newly added phone number field
        EditText editText = phoneNumberEditTexts.get(phoneNumberEditTextNextId - 1);

        // Set the provided phone number to the EditText
        if (editText != null) {
            editText.setText(phoneNumber);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            // Parse the phone number
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, "FR");
            // Check if the parsed phone number is valid
            return phoneNumberUtil.isValidNumber(parsedPhoneNumber);
        } catch (NumberParseException e) {
            Log.e("Validation Error", "Error parsing phone number", e);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void resetAction(MenuItem menu) {
        EditText surnameEditText = findViewById(R.id.surname);
        EditText nameEditText = findViewById(R.id.name);
        EditText birthDateEditText = findViewById(R.id.birth_date);
        EditText cityEditText = findViewById(R.id.city);

        // Get the text from the EditTexts
        surnameEditText.setText("");
        nameEditText.setText("");
        birthDateEditText.setText("");
        cityEditText.setText("");

        // Clear phone number fields
        phoneNumbersContainer.removeAllViews();
        phoneNumberEditTexts.clear();

        // Clear shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, year1, month1, day1) -> {
                    // Handle the selected date here, for example, update the EditText
                    String selectedDate = String.format("%02d/%02d/%d", day1, month1 + 1, year1);
                    birthDateEditText.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }


    public void addPhoneNumberField(View v) {

        // Define margin values in dp
        int startMarginInDp = 16; // for example, 16dp

        // Convert dp to pixels
        float scale = getResources().getDisplayMetrics().density;
        int startMarginInPx = (int) (startMarginInDp * scale + 0.5f);

        // Create a horizontal LinearLayout for the EditText and the delete button
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create EditText
        EditText editText = new EditText(this);
        editText.setHint(getString(R.string.phone_number_hint));
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        editText.setLayoutParams(editTextParams);
        editTextParams.setMargins(startMarginInPx, 0, 0, 0);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        int phoneNumberId = phoneNumberEditTextNextId;
        phoneNumberEditTexts.put(phoneNumberId, editText);
        phoneNumberEditTextNextId++;
        layout.addView(editText);

        // Create Delete Button
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(android.R.drawable.ic_delete);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setOnClickListener(view -> {
            // Remove the layout from the container
            phoneNumbersContainer.removeView(layout);
            phoneNumberEditTexts.remove(phoneNumberId);
        });
        layout.addView(deleteButton);

        // Add the layout to the container
        phoneNumbersContainer.addView(layout);
    }


    public void searchWikiCityAction(MenuItem item) {
        // Retrieve city name from the EditText field
        String cityName = cityEditText.getText().toString();
        // Check if the city name is not empty
        if (!cityName.isEmpty()) {
            // Construct the search URL
            String url = "http://fr.wikipedia.org/?search=" + Uri.encode(cityName);
            // Create an Intent to view the URL
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            // Verify that the Intent can be resolved to an Activity
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // No Activity found that can handle the Intent
                Toast.makeText(this, "No application found to open Wikipedia", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareCityAction(MenuItem item) {
        // Retrieve city name from the EditText field
        String cityName = cityEditText.getText().toString();

        // Check if the city name is not empty
        if (!cityName.isEmpty()) {
            // Create the text to share
            String shareText = "Check out this city: " + cityName + "!";

            // Create a SHARE Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "City Information");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            // Start the chooser Intent
            startActivity(Intent.createChooser(shareIntent, "Share city via"));
        } else {
            Toast.makeText(this, "City name is empty. Please enter a city name to share.", Toast.LENGTH_SHORT).show();
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