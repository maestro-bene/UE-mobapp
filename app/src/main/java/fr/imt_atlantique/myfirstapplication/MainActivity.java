package fr.imt_atlantique.myfirstapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText birthDateEditText;
    private LinearLayout phoneNumbersContainer;

    private final Map<Integer, EditText> phoneNumberEditTexts = new HashMap<>();
    private int phoneNumberEditTextNextId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Lifecycle", "onCreate method");
        setContentView(R.layout.activity_main);
        birthDateEditText = findViewById(R.id.birth_date);
        birthDateEditText.setOnClickListener(view -> showDatePickerDialog());
        phoneNumbersContainer = findViewById(R.id.phone_numbers_container);
        addPhoneNumberField(phoneNumbersContainer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause method");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of phone number fields
        ArrayList<String> phoneNumbers = new ArrayList<>();
        for (Map.Entry<Integer, EditText> entry : phoneNumberEditTexts.entrySet()) {
            int viewId = entry.getKey();
            EditText editText = entry.getValue();
            phoneNumbers.add(editText.getText().toString());
        }
        outState.putStringArrayList("phoneNumbers", phoneNumbers);
    }

    public void validateAction(View view) {
        // Get references to the EditTexts
        EditText surnameEditText = findViewById(R.id.surname);
        EditText nameEditText = findViewById(R.id.name);
        EditText birthDateEditText = findViewById(R.id.birth_date);
        EditText cityEditText = findViewById(R.id.city);
        Spinner departmentSpinner = findViewById(R.id.department);

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

        // Create the text to display in the Snackbar
        StringBuilder textToShowBuilder = new StringBuilder();
        textToShowBuilder.append("Surname: ").append(surname).append(",\n")
                .append("Name: ").append(name).append(",\n")
                .append("Birth Date: ").append(birthDate).append(",\n")
                .append("City: ").append(city).append("\n")
                .append("Departement: ").append(department).append("\n");

        // Append phone numbers
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
            textToShowBuilder.append("Phone ").append(id).append(": ").append(phoneNumber).append("\n");
        }


        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.GreenAlertDialogTheme);
        builder.setTitle("Information validated:");
        builder.setMessage(textToShowBuilder.toString());
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
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