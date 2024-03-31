package fr.imt_atlantique.myfirstapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.imt_atlantique.myfirstapplication.R;
import fr.imt_atlantique.myfirstapplication.data.model.User;
import fr.imt_atlantique.myfirstapplication.utils.UserListAdapter;

public class UserListFragment extends Fragment {

    private static final String TAG = "UsersListFragment";

    private final List<User> userList = new ArrayList<>();

    private UserActionListener mListener;

    private UserEditListener eListener;

    public interface UserActionListener extends UserListAdapter.UserActionListener {
        void onUserInfoDisplay(User user);
    }

    public interface UserEditListener extends UserListAdapter.UserEditListener {
        void onUserEdit(User user);

        void onUserEdit();
    }

    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserActionListener) {
            mListener = (UserActionListener) context;
        } else {
            throw new RuntimeException(context + " must implement UserActionListener");
        }
        if (context instanceof UserEditListener) {
            eListener = (UserEditListener) context;
        } else {
            throw new RuntimeException(context + " must implement UserEditListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUserList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        ListView listView = view.findViewById(R.id.userListView);
        Button addUserButton = view.findViewById(R.id.addUserButton);

        // Use the custom adapter instead of ArrayAdapter
        UserListAdapter adapter = new UserListAdapter(requireContext(), userList, mListener, eListener);
        listView.setAdapter(adapter);

        addUserButton.setOnClickListener(v -> {
            eListener.onUserEdit(); // Call onEdit with no Users
        });

        return view;
    }

    private void loadUserList() {
        File directory = requireContext().getFilesDir();
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d(TAG, "There are files");
        } else
            Log.e(TAG, "No files");
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    Log.d(TAG, file.toString());
                }
                if (file.getName().startsWith("user_") && file.getName().endsWith(".json")) {
                    User user = loadUserFromFile(file.getName());
                    if (user != null) {
                        userList.add(user);
                    }
                }
            }
        }
    }

    private User loadUserFromFile(String userFileName) {
        File file = new File(requireContext().getFilesDir(), userFileName);
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: " + userFileName);
            return null;
        }
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            Gson gson = new Gson();
            return gson.fromJson(content.toString(), User.class); // This now works as photoUriString is used
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + userFileName, e);
            return null;
        }
    }
}
