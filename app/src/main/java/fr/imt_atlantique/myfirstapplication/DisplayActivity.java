package fr.imt_atlantique.myfirstapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {

    private TextView surnameTextView;
    private TextView nameTextView;
    private TextView cityTextView;
    private TextView birthDateTextView;
    private TextView departmentTextView;
    private TextView phoneNumbersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Initialize TextView objects
        surnameTextView = findViewById(R.id.surnameTextView);
        nameTextView = findViewById(R.id.nameTextView);
        cityTextView = findViewById(R.id.cityTextView);
        birthDateTextView = findViewById(R.id.birthDateTextView);
        departmentTextView = findViewById(R.id.departmentTextView);
        phoneNumbersTextView = findViewById(R.id.phoneNumbersTextView);

        // Display user data
        displayUserData();
    }

    private void displayUserData() {
        // Retrieve the user object from the Intent
        User user = getIntent().getParcelableExtra("user");
        if (user != null) {
            // Extract user information
            String surname = user.getSurname();
            String name = user.getName();
            String city = user.getCity();
            String birthDate = user.getBirthDate();
            String department = user.getDepartment();
            String[] phoneNumbers = user.getPhoneNumbers();

            // Display the user information in the TextViews
            surnameTextView.setText(surname);
            nameTextView.setText(name);
            cityTextView.setText(city);
            birthDateTextView.setText(birthDate);
            departmentTextView.setText(department);

            // Display phone numbers
            StringBuilder phoneNumbersText = new StringBuilder();
            for (String phoneNumber : phoneNumbers) {
                phoneNumbersText.append(phoneNumber).append("\n");
            }
            phoneNumbersTextView.setText(phoneNumbersText.toString());
        }
    }

    // Method to close the activity and return to the previous one
    public void closeActivity(View view) {
        finish(); // Close the activity
    }
}
