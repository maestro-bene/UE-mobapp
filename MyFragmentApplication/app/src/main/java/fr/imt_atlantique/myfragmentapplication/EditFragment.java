package fr.imt_atlantique.myfragmentapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class EditFragment extends Fragment {

    private OnNameEnteredListener listener;

    public interface OnNameEnteredListener {
        void onNameEntered(String name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNameEnteredListener) {
            listener = (OnNameEnteredListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameEnteredListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        Button submitButton = view.findViewById(R.id.submit_button);
        EditText nameInput = view.findViewById(R.id.name_input);

        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            listener.onNameEntered(name); // Use the interface to send the name to MainActivity
        });

        return view;
    }
}

