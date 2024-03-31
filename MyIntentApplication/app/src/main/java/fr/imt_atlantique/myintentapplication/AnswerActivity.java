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

import com.google.android.material.snackbar.Snackbar;

public class AnswerActivity extends AppCompatActivity {

    private EditText answerEditText;
    private String question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        answerEditText = findViewById(R.id.answerEditText);
        // Retrieve the question passed from MainActivity
        question = getIntent().getStringExtra("question");
        TextView questionTextView = findViewById(R.id.questionTextView);
        questionTextView.setText(question);

        // Assuming there's a button in your activity_answer.xml to submit the answer
        // If not, you should add it or use a menu option to trigger this.
        Button submitAnswerButton = findViewById(R.id.submitAnswerButton);
        submitAnswerButton.setOnClickListener(view -> sendAnswer());
    }

    private void displayAnswer() {
        String answer = answerEditText.getText().toString();
        if (!answer.isEmpty()) {
            String message = "Question: " + question + "\nAnswer: " + answer;
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void sendAnswer() {
        // Return the answer to MainActivity
        String answer = answerEditText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("answer", answer);
        setResult(Activity.RESULT_OK, returnIntent);
        finish(); // Close this activity
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
