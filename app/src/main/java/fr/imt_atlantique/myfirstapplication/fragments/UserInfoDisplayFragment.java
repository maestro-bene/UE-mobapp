package fr.imt_atlantique.myfirstapplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.imt_atlantique.myfirstapplication.R;
import fr.imt_atlantique.myfirstapplication.data.model.User;

public class UserInfoDisplayFragment extends Fragment {

    private static final String TAG = "UserInfoDisplayFragment";
    private TextView lastNameTextView;
    private TextView firstNameTextView;
    private TextView cityTextView;
    private TextView birthDateTextView;
    private TextView departmentTextView;
    private TextView phoneNumbersTextView;
    private ImageView imageView;

    private OnBackToListListener mListener;

    public interface OnBackToListListener {
        void onBackToList();
    }


    // Use a factory method to create a new instance of the fragment and pass user data
    public static UserInfoDisplayFragment newInstance(User user) {
        UserInfoDisplayFragment fragment = new UserInfoDisplayFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user); // Ensure User class implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBackToListListener) {
            mListener = (OnBackToListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBackToListListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info_display, container, false);

        Log.d(TAG, "onCreateView called");

        // Initialize TextView objects
        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        birthDateTextView = view.findViewById(R.id.birthDateTextView);
        departmentTextView = view.findViewById(R.id.departmentTextView);
        phoneNumbersTextView = view.findViewById(R.id.phoneNumbersTextView);
        imageView = view.findViewById(R.id.photoImageView);

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        Button backToListButton = view.findViewById(R.id.backToListButton);
        backToListButton.setOnClickListener(v -> {
            mListener.onBackToList();
        });

        // Display user data
        displayUserData();

        return view;
    }

    private void displayUserData() {
        // Retrieve the user object from the arguments
        Bundle args = getArguments();
        if (args != null) {
            User user = args.getParcelable("user");
            if (user != null) {
                // Extract user information and display it
                if (user.getPhotoURI() != null && !user.getPhotoURI().isEmpty()) {
                    imageView.setImageURI(Uri.parse(user.getPhotoURI()));
                } else {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }

                lastNameTextView.setText(user.getLastName());
                firstNameTextView.setText(user.getFirstName());
                cityTextView.setText(user.getCity());
                birthDateTextView.setText(user.getBirthDate());
                departmentTextView.setText(user.getDepartment());


                // Display phone numbers
                StringBuilder phoneNumbersText = new StringBuilder();
                for (String phoneNumber : user.getPhoneNumbers()) {
                    phoneNumbersText.append(phoneNumber).append("\n");
                }
                phoneNumbersTextView.setText(phoneNumbersText.toString());

                Log.i(TAG, "Displayed the user");
            }
        }
    }
}
