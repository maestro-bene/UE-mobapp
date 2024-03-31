package fr.imt_atlantique.myfragmentapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DisplayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);
        TextView nameTextView = view.findViewById(R.id.name_text_view);

        // Get the name passed from EditFragment
        if (getArguments() != null && getArguments().containsKey("name")) {
            String name = getArguments().getString("name");
            nameTextView.setText(name); // Set the name to the TextView
        }

        return view;
    }
}
