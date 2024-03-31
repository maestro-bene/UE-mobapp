package fr.imt_atlantique.myfirstapplication.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import fr.imt_atlantique.myfirstapplication.R;
import fr.imt_atlantique.myfirstapplication.data.model.User;

public class UserListAdapter extends ArrayAdapter<User> {
    public interface UserEditListener {
        void onUserEdit(User user);

        void onUserEdit();
    }

    public interface UserActionListener {
        void onUserInfoDisplay(User user);
    }

    private List<User> users;
    private final UserActionListener mListener;
    private final UserEditListener mEditListener;

    public UserListAdapter(Context context, List<User> users, UserActionListener listener, UserEditListener editListener) {
        super(context, 0, users);
        this.users = users;
        this.mListener = listener;
        this.mEditListener = editListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_user, parent, false);
        }

        TextView userNameTextView = convertView.findViewById(R.id.userNameTextView);
        ImageButton editUserButton = convertView.findViewById(R.id.editUserButton);
        ImageButton seeUserButton = convertView.findViewById(R.id.seeUserButton);

        User user = getItem(position);
        if (user != null) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            userNameTextView.setText(fullName);

            userNameTextView.setOnClickListener(v -> mListener.onUserInfoDisplay(user));
            seeUserButton.setOnClickListener(v -> mListener.onUserInfoDisplay(user));
            editUserButton.setOnClickListener(v -> mEditListener.onUserEdit(user));
        }

        return convertView;
    }
}
