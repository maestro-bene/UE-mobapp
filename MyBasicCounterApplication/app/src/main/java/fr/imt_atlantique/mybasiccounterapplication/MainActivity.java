package fr.imt_atlantique.mybasiccounterapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    private int counter = 0;
    private TextView counterView;
    private ConstraintLayout baseLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterView = findViewById(R.id.Counter);
        baseLayout = findViewById(R.id.baseLayout);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Load counter value from SharedPreferences
        counter = sharedPreferences.getInt("counter", 0);
        counterView.setText(String.valueOf(counter));


        // Récupération de l'état précédent si disponible
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter");
            counterView.setText(String.valueOf(counter));
        }

        // Ajout d'un listener pour incrémenter le compteur lors d'un clic sur le layout de base
        baseLayout.setOnClickListener(v -> {
            incrementCounter();
        });
    }

    // Method to increment the counter and update UI
    private void incrementCounter() {
        counter++;
        counterView.setText(String.valueOf(counter));

        // Save the updated counter value to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("counter", counter);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Sauvegarde de l'état du compteur
        outState.putInt("counter", counter);
    }
}