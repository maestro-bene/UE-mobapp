package fr.imt_atlantique.myintentapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    private EditText questionEditText;
    private static final int REQUEST_CODE_ANSWER = 1;
    private String lastAskedQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionEditText = findViewById(R.id.questionEditText);
        Button askButton = findViewById(R.id.askButton);

        askButton.setOnClickListener(v -> {
            String question = questionEditText.getText().toString();
            if (!question.isEmpty()) {
                lastAskedQuestion = question;
                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
                intent.putExtra("question", questionEditText.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_ANSWER);
            }
        });
    }

    // Override onActivityResult to handle the result from AnswerActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ANSWER && resultCode == RESULT_OK && data != null) {
            String answer = data.getStringExtra("answer");
            // Display the question and the answer using a Scrollable Snackbar
            showScrollableSnackbar("Q: " + lastAskedQuestion + "\nA: " + answer);
        }
    }

    private void showScrollableSnackbar(String text) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog); // Example theme
        View bottomSheetView = getLayoutInflater().inflate(R.layout.scrollable_snackbar, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        TextView textView = bottomSheetView.findViewById(R.id.scrollable_textview);
        textView.setText(text);
        bottomSheetDialog.show();
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