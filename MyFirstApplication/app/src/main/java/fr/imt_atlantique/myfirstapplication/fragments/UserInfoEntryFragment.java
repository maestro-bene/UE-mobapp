package fr.imt_atlantique.myfirstapplication.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import fr.imt_atlantique.myfirstapplication.R;
import fr.imt_atlantique.myfirstapplication.data.model.User;
import fr.imt_atlantique.myfirstapplication.utils.DatePickerUtils;
import fr.imt_atlantique.myfirstapplication.utils.MenuUtils;
import fr.imt_atlantique.myfirstapplication.utils.ValidationUtils;


public class UserInfoEntryFragment extends Fragment {
    private static final String TAG = "UserInfoEntryFragment";
    private EditText lastNameEditText, firstNameEditText, cityEditText, birthDateEditText;
    private Spinner departmentSpinner;
    private Button birthDateButton;
    private LinearLayout phoneNumbersContainer;
    private final Map<Integer, EditText> phoneNumberEditTexts = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private Uri photoURI;
    private Bitmap imageBitmap;
    private ImageView imageView;

    public interface UserInfoEntryListener {
        void onUserInfoDisplay(User user);
    }

    private UserInfoEntryListener listener;

    // Use a factory method to create a new instance of the fragment and pass user data
    public static UserInfoEntryFragment newInstance(User user) {
        UserInfoEntryFragment fragment = new UserInfoEntryFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user); // Ensure User class implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    public static UserInfoEntryFragment newInstance() {
        return new UserInfoEntryFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserInfoEntryListener) {
            listener = (UserInfoEntryListener) context;
        } else {
            throw new RuntimeException(context + " must implement UserInfoEntryListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment view
        View view = inflater.inflate(R.layout.fragment_user_info_entry, container, false);

        // Recuperate shared preferences
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        Log.d(TAG, "onCreateView called");

        // Let the fragment know it has its own menu
        setHasOptionsMenu(true);

        // Initialize the components
        initializeViews(view);

        // Check if arguments exist and contain a User object
        if (getArguments() != null && getArguments().containsKey("user")) {
            User user = getArguments().getParcelable("user");
            // Populate your fields with User information
            if (user != null)
                populateFieldsWithUserInfo(user);
        } else if (savedInstanceState != null) {
            // Load data either from bundle, or shared preferences
            loadUserDataFromState(savedInstanceState);
        } else
            loadUserData();
        Log.i(TAG, "View created and user data loaded/restored");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.resetAction) {
            showResetConfirmationDialog();
            return true;
        } else if (id == R.id.searchWikiCityAction) {
            String cityName = cityEditText.getText().toString();
            MenuUtils.openCityWikipediaPage(getContext(), cityName);
            return true;
        } else if (id == R.id.shareCityAction) {
            String city = cityEditText.getText().toString();
            MenuUtils.shareCity(getContext(), city);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving all User information in the Bundle
        outState.putString("lastName", lastNameEditText.getText().toString());
        outState.putString("firstName", firstNameEditText.getText().toString());
        outState.putString("city", cityEditText.getText().toString());
        outState.putString("birthDate", birthDateEditText.getText().toString());
        outState.putInt("department", departmentSpinner.getSelectedItemPosition());
        outState.putString("photoURI", photoURI.toString());

        // Convert the phone numbers ArrayList to JSON
        ArrayList<String> phoneNumbersList = new ArrayList<>();
        for (EditText editText : phoneNumberEditTexts.values()) {
            String phoneNumber = editText.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                phoneNumbersList.add(phoneNumber);
            }
        }
        Gson gson = new Gson();
        String phoneNumbersJson = gson.toJson(phoneNumbersList);

        // Saving Phone Numbers as well
        outState.putString("phoneNumbers", phoneNumbersJson);

        Log.d(TAG, "User data saved to bundle");
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    // Code for an old SDK
                    // Get the Bitmap of the created File
                    imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoURI);
                } else {
                    // Up-to-date SDK
                    // Get the Bitmap of the created File
                    ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), photoURI);
                    imageBitmap = ImageDecoder.decodeBitmap(source);

                    Log.d("UserInfoEntryFragment", "ImageDecoder got the image for API >= 28");
                }

                // Set the Image to show in the component
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                Log.e("UserInfoEntryFragment", "Error on getting the image", e);
            }
        }
        Log.i(TAG, "Photo captured and ImageView updated");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If we have camera permission, launch the Picture Intent
                dispatchTakePictureIntent();
            } else {
                // If not, simply show a Toast
                Toast.makeText(getContext(), "Camera Permission was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Camera permission is necessary")
                    .setMessage("Camera permission is required to take pictures.")
                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION))
                    .create()
                    .show();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        Log.i(TAG, "Requested camera permission");
    }

    private void initializeViews(View view) {
        // Initialize Image View
        imageView = view.findViewById(R.id.photoImageView);

        // Initialize the Take Photo button
        ImageButton takePhotoButton = view.findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
            } else {
                dispatchTakePictureIntent();
            }
        });

        // Initialize other info for the form
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        cityEditText = view.findViewById(R.id.cityEditText);

        // Initialize Department spinner
        departmentSpinner = view.findViewById(R.id.departmentSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.departments, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        departmentSpinner.setAdapter(adapter);

        // Initialize birth date info
        birthDateEditText = view.findViewById(R.id.birthDateEditText);
        birthDateButton = view.findViewById(R.id.birthDateButton);

        // Initialize phone numbers and validate button
        phoneNumbersContainer = view.findViewById(R.id.phoneNumbersContainer);
        Button validateButton = view.findViewById(R.id.validateButton);


        // Initialize OnClick Listeners for the buttons
        validateButton.setOnClickListener(v -> validateAndSendData());
        view.findViewById(R.id.addPhoneNumberButton).setOnClickListener(v -> addPhoneNumberField(null));
        birthDateButton.setOnClickListener(v -> DatePickerUtils.showDatePickerDialog(getContext(), birthDateEditText));

        Log.i(TAG, "Views initialized");
    }

    private void saveUserData() {
        // Create an editor to save information to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put information in the editor
        editor.putString("lastName", lastNameEditText.getText().toString());
        editor.putString("firstName", firstNameEditText.getText().toString());
        editor.putString("city", cityEditText.getText().toString());
        editor.putString("birthDate", birthDateEditText.getText().toString());
        editor.putInt("department", departmentSpinner.getSelectedItemPosition());
        if (photoURI != null)
            editor.putString("photoURI", photoURI.toString());

        // Convert the phone numbers ArrayList to JSON
        ArrayList<String> phoneNumbersList = new ArrayList<>();
        for (EditText editText : phoneNumberEditTexts.values()) {
            String phoneNumber = editText.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                phoneNumbersList.add(phoneNumber);
            }
        }
        Gson gson = new Gson();
        String phoneNumbersJson = gson.toJson(phoneNumbersList);
        // Placing phone numbers in the editor
        editor.putString("phoneNumbers", phoneNumbersJson);

        // Applying what I set in the editor in shared preferences
        editor.apply();

        Log.d(TAG, "User data saved to SharedPreferences");
    }

    private void loadUserData() {
        // Get the photo URI for the ImageView
        String uriString = sharedPreferences.getString("photoURI", null);
        if (uriString != null) {
            photoURI = Uri.parse(uriString);
            imageView.setImageURI(photoURI);
        }

        // Load in components the information saved in shared preferences
        lastNameEditText.setText(sharedPreferences.getString("lastName", ""));
        firstNameEditText.setText(sharedPreferences.getString("firstName", ""));
        cityEditText.setText(sharedPreferences.getString("city", ""));
        departmentSpinner.setSelection(sharedPreferences.getInt("department", 0));

        // Get the birthDate as saved in shared preferences
        String birthDate = sharedPreferences.getString("birthDate", "");
        if (birthDate.isEmpty()) {
            birthDateButton.setText(R.string.set_birth_date);
        }
        birthDateEditText.setText(birthDate);

        // Deserialize the phone numbers JSON to ArrayList
        String phoneNumbersJson = sharedPreferences.getString("phoneNumbers", "");

        // Loading all phone numbers
        if (!phoneNumbersJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> phoneNumbersList = gson.fromJson(phoneNumbersJson, type);

            phoneNumberEditTexts.clear();
            phoneNumbersContainer.removeAllViews(); // Clear all views before adding them back
            for (String phoneNumber : phoneNumbersList) {
                // Create and load dynamically the information to the component
                addPhoneNumberField(phoneNumber);
            }
        }

        Log.i(TAG, "User data loaded from SharedPreferences");
    }

    private void loadUserDataFromState(Bundle savedInstanceState) {
        // Get form information from the bundle
        lastNameEditText.setText(savedInstanceState.getString("lastName"));
        firstNameEditText.setText(savedInstanceState.getString("firstName"));
        cityEditText.setText(savedInstanceState.getString("city"));
        birthDateEditText.setText(savedInstanceState.getString("birthDate"));
        departmentSpinner.setSelection(savedInstanceState.getInt("department"));

        // Load the image from the saved URI
        photoURI = Uri.parse(savedInstanceState.getString("photoURI"));
        updateImageViewWithUri(photoURI);

        // Load dynamically all saved phone numbers
        String phoneNumbersJson = savedInstanceState.getString("phoneNumbers");
        if (phoneNumbersJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> phoneNumbersList = gson.fromJson(phoneNumbersJson, type);

            phoneNumbersContainer.removeAllViews();
            phoneNumberEditTexts.clear();

            for (String phoneNumber : phoneNumbersList) {
                addPhoneNumberField(phoneNumber);
            }
        }

        Log.i(TAG, "User data restored from savedInstanceState");
    }

    private void populateFieldsWithUserInfo(User user) {
        // Get the photo URI for the ImageView
        if (user.getPhotoURI() != null && !user.getPhotoURI().isEmpty()) {
            photoURI = Uri.parse(user.getPhotoURI());
            imageView.setImageURI(photoURI);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }


        // Load in components the information saved in shared preferences
        lastNameEditText.setText(user.getLastName());
        firstNameEditText.setText(user.getFirstName());
        cityEditText.setText(user.getCity());

        // Load the spinner position
        String department = user.getDepartment();
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) departmentSpinner.getAdapter();
        // Loop through all spinner items
        for (int position = 0; position < adapter.getCount(); position++) {
            if (Objects.requireNonNull(adapter.getItem(position)).toString().equalsIgnoreCase(department)) {
                // If we find a match, select the corresponding spinner item
                departmentSpinner.setSelection(position);
                break;
            }
        }

        // Get the birthDate as saved in shared preferences
        String birthDate = user.getBirthDate();
        if (birthDate.isEmpty()) {
            birthDateButton.setText(R.string.set_birth_date);
        }
        birthDateEditText.setText(birthDate);

        for (String phoneNumber : user.getPhoneNumbers()) {
            addPhoneNumberField(phoneNumber);
        }

        Log.i(TAG, "User data loaded from selected User");
    }


    public void addPhoneNumberField(String phoneNumber) {
        // Inflate a phone number field in the phone number container
        View phoneFieldView = getLayoutInflater().inflate(R.layout.phone_number_field, phoneNumbersContainer, false);

        // Get its EditText
        EditText phoneNumberEditText = phoneFieldView.findViewById(R.id.phoneNumberEditText);

        // Attach a Listener that formats automatically when entering information
        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Set the phone number to the text
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            phoneNumberEditText.setText(phoneNumber);
        }

        // Generate a unique ID for the view
        int uniqueId = View.generateViewId();

        // Set the unique ID
        phoneNumberEditText.setId(uniqueId);

        // Use the unique ID as the key
        phoneNumberEditTexts.put(uniqueId, phoneNumberEditText);

        // Get its delete button and set the listener
        ImageButton deleteButton = phoneFieldView.findViewById(R.id.deletePhoneNumberButton);
        deleteButton.setOnClickListener(v -> {
            phoneNumbersContainer.removeView(phoneFieldView); // Remove the phone field view
            phoneNumberEditTexts.remove(uniqueId); // Remove the entry from the map using its unique ID
        });

        // Add the phone field to the container
        phoneNumbersContainer.addView(phoneFieldView);
    }

    private void dispatchTakePictureIntent() {
        // Create an intent for capturing an image
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Create the image file
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("UserInfoEntryFragment", "Error on photo file creation", ex);
            }
            if (photoFile != null) {
                // Get the file URI
                photoURI = FileProvider.getUriForFile(requireContext(),
                        requireContext().getApplicationContext().getPackageName() + ".provider",
                        photoFile);

                // Put the URI in the intent as an extra
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Start the activity with the intent
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        Log.i(TAG, "Dispatched take picture intent");
    }

    private File createImageFile() throws IOException {
        // Timestamp for the current time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Log.i(TAG, "Created the temporary file");

        // Create a temporary file in the storage directory
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void updateImageViewWithUri(Uri imageUri) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                // Code for an old SDK
                // Get the Bitmap of the created File
                imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            } else {
                // Up-to-date SDK
                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), imageUri);
                imageBitmap = ImageDecoder.decodeBitmap(source);
            }
            // Set the Image to show in the component
            imageView.setImageBitmap(imageBitmap);

            Log.d(TAG, "Photo updated in ImageView");
        } catch (IOException e) {
            Log.e("UserInfoEntryFragment", "Error on getting the Image", e);
        }
    }

    public void validateAndSendData() {
        // Create the arguments for User creation
        String lastName = lastNameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String department = departmentSpinner.getSelectedItem().toString();
        String birthDate = birthDateEditText.getText().toString();

        // Verify the name
        if (ValidationUtils.isValidName(lastName) || ValidationUtils.isValidName(firstName)) {
            Toast.makeText(getContext(), "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Validated the full name");

        // Verify the city
        if (!ValidationUtils.isValidCity(city)) {
            Toast.makeText(getContext(), "Please enter a valid city.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Validated the city");


        // Verify the phone numbers
        ArrayList<String> phoneNumbers = new ArrayList<>();
        for (EditText editText : phoneNumberEditTexts.values()) {
            String phoneNumber = editText.getText().toString().trim();
            if (ValidationUtils.isValidPhoneNumber(phoneNumber)) {
                phoneNumbers.add(phoneNumber);
            } else {
                Toast.makeText(getContext(), "Invalid phone number format detected.", Toast.LENGTH_SHORT).show();
                return; // Stop the submission if any phone number is invalid
            }
        }
        Log.i(TAG, "Validated the phone numbers");

        // Other validation, such as no empty fields in this case
        if (!lastName.isEmpty() && !firstName.isEmpty() && !city.isEmpty() && !birthDate.isEmpty() && !phoneNumbers.isEmpty()) {
            // Save user data to shared preferences
            saveUserData();

            User user;

            if (photoURI == null) {
                // Create a User with the retrieved information
                user = new User(lastName, firstName, birthDate, city, department, phoneNumbers);
            } else {
                // Create a User with the retrieved information
                user = new User(lastName, firstName, birthDate, city, department, phoneNumbers, photoURI.toString());
            }


            // Save the user to a file
            saveUserToFile(user);

            // Call the interface method to send User to Display fragment
            listener.onUserInfoDisplay(user);
        } else {
            // Simple toast to get the user to correct the inputs
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "User data validated and sent");
    }

    private void saveUserToFile(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        try {
            // Generate a unique filename for the user file
            String filename = "user_" + user.getLastName() + "_" + user.getFirstName() + ".json";
            FileOutputStream fos = requireContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(userJson.getBytes());
            fos.close();
            Log.i(TAG, "User data saved to file: " + filename);
        } catch (IOException e) {
            Log.e(TAG, "Failed to save user data to file", e);
        }
    }

    private void resetFields() {
        // Resetting all fields of the form
        lastNameEditText.setText("");
        firstNameEditText.setText("");
        cityEditText.setText("");
        departmentSpinner.setSelection(0);
        birthDateEditText.setText("");
        birthDateButton.setText(R.string.set_birth_date);
        phoneNumbersContainer.removeAllViews();
        phoneNumberEditTexts.clear();
        imageView.setImageResource(R.mipmap.ic_launcher);
        photoURI = Uri.parse("");

        Log.i(TAG, "Input fields reset");
    }

    private void showResetConfirmationDialog() {
        // Alert for added level of verification, in case the user clicked by error
        new AlertDialog.Builder(getContext())
                .setTitle("Reset Form")
                .setMessage("Are you sure you want to reset the form? This will clear all entered data.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> resetFields())
                .setNegativeButton(android.R.string.no, null) // No action needed if the user clicks "No"
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        Log.i(TAG, "Displayed reset confirmation dialog");
    }
}
