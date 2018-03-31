package no.larsvidar.quizshow;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /*****************
     *** VARIABLES ***
     *****************/
    private int questionNumber = 0;                     //Keeps tab on what question is next.
    private int score = 0;                              //How many questions has been answered right.
    private String[][] questions = new String[9][6];    //Array of questions and answers
    private String gameProgress = "start";              //Variable to tell if a game is in progress.
    private TextView questionView;                      //Variable for the TextView with the question.
    private TextView scoreView;                         //Variable for TextView which shows the score at the bottom of the screen.
    private TextView resultView;                        //Variable for TextView which shows the result at the end screen.
    private RadioGroup radioGroupAnswer;                //Variable for the RadioGroup of radio buttons.
    private LinearLayout card;                          //Variable for the whole card.
    private LinearLayout cardface;                      //Variable for the content of the card.
    private LinearLayout welcomeLayout;                 //Variable for the view that contains the Welcome Screen.
    private LinearLayout endLayout;                     //Variable for the view that contains the end screen.
    private LinearLayout textFieldLayout;               //Variable for the Linearlayout with the EditText view for text field questions.
    private RelativeLayout checkBoxLayout;              //Variable for the Relativelayout with the answer alternatives for checkbox questions.
    private RelativeLayout radioButtonLayout;           //Variable for the Relativelayout with the answer alternatives for radio button questions.
    private RelativeLayout wholeCard;                   //Variable for the whole card view (for rotation).


    /**
     * Method for populating questions array.
     * @return array of arrays.
     */
    private String[][] getQuestions() {
        return new String[][]{
                {getString(R.string.question1), getString(R.string.question1_alt1), getString(R.string.question1_alt2), getString(R.string.question1_alt3), getString(R.string.question1_alt4), getString(R.string.question1_answer), getString(R.string.question1_type)},
                {getString(R.string.question2), getString(R.string.question2_alt1), getString(R.string.question2_alt2), getString(R.string.question2_alt3), getString(R.string.question2_alt4), getString(R.string.question2_answer), getString(R.string.question2_type)},
                {getString(R.string.question3), "", "", "", "", getString(R.string.question3_answer), getString(R.string.question3_type)},
                {getString(R.string.question4), getString(R.string.question4_alt1), getString(R.string.question4_alt2), getString(R.string.question4_alt3), getString(R.string.question4_alt4), getString(R.string.question4_answer), getString(R.string.question4_type)},
                {getString(R.string.question5), getString(R.string.question5_alt1), getString(R.string.question5_alt2), getString(R.string.question5_alt3), getString(R.string.question5_alt4), getString(R.string.question5_answer), getString(R.string.question5_type)},
                {getString(R.string.question6), getString(R.string.question6_alt1), getString(R.string.question6_alt2), getString(R.string.question6_alt3), getString(R.string.question6_alt4), getString(R.string.question6_answer), getString(R.string.question6_type)},
                {getString(R.string.question7), getString(R.string.question7_alt1), getString(R.string.question7_alt2), getString(R.string.question7_alt3), getString(R.string.question7_alt4), getString(R.string.question7_answer), getString(R.string.question7_type)},
                {getString(R.string.question8), getString(R.string.question8_alt1), getString(R.string.question8_alt2), getString(R.string.question8_alt3), getString(R.string.question8_alt4), getString(R.string.question8_answer), getString(R.string.question8_type)},
                {getString(R.string.question9), getString(R.string.question9_alt1), getString(R.string.question9_alt2), getString(R.string.question9_alt3), getString(R.string.question9_alt4), getString(R.string.question9_answer), getString(R.string.question9_type)},
                {getString(R.string.question10), getString(R.string.question10_alt1), getString(R.string.question10_alt2), getString(R.string.question10_alt3), getString(R.string.question10_alt4), getString(R.string.question10_answer), getString(R.string.question10_type)}};
    }

    /********** onCreate method **********/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //****** This code runs when the app starts ********

        //Gets values from savedInstanceState, if any exists.
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("savedScore");
            questionNumber = savedInstanceState.getInt("savedQuestionNumber");
            gameProgress = savedInstanceState.getString("savedGameProgress");
        }

        //Initializes all variables and views.
        initializeQuiz();
        //Show welcome screen
        showWelcome();

        //Checking game progress
        if(gameProgress.equals("playing")) {
            updateScoreView();
            startQuiz(null);
        } else if (gameProgress.equals("end")) {
            //Hides the welcome view.
            hideView(welcomeLayout);
            //Shows the cardface view.
            showView(cardface);
            gameEnd();
        }
    }

    /**
     * Method for preparing to start the quiz.
     */
    private void initializeQuiz() {
        //Load all views from XML-files
        loadViews();
        hideView(endLayout);

        //Populate questions array.
        questions = getQuestions();
        //Initializing score.
        updateScoreView();
    }

    /**
     * Method for loading and attaching all views.
     */
    private void loadViews() {
        //Load views from xml-files.
        welcomeLayout = (LinearLayout) View.inflate(this, R.layout.start, null);                //Loads the welcome screen.
        endLayout = (LinearLayout) View.inflate(this, R.layout.end, null);                      //Loads the end screen.
        radioButtonLayout = (RelativeLayout) View.inflate(this, R.layout.radiobuttons, null);   //Loads layout for radio buttons.
        textFieldLayout = (LinearLayout) View.inflate(this, R.layout.textfield, null);          //Loads layout for text field.
        checkBoxLayout = (RelativeLayout) View.inflate(this, R.layout.checkboxes, null);        //Loads layout for checkboxes.

        //Saves views to variables.
        card = findViewById(R.id.card);                 //View for the inner car, for attaching other views.
        cardface = findViewById(R.id.cardface);         //View for the content on the card.

        //Attaching views to parent views.
        card.addView(welcomeLayout);            //Attaches welcome screen layout to the card view.
        card.addView(endLayout);                //Attaches end screen layout to the card view.
        cardface.addView(radioButtonLayout);    //Attaches radio button layout to the cardface view.
        cardface.addView(textFieldLayout);      //Attaches text field layout to the cardface view.
        cardface.addView(checkBoxLayout);       //Attaches checkbox layout to the cardface view.

        //Saves remaining views to variables.
        wholeCard = findViewById(R.id.quizcard);        //View for the whole card, for rotation.
        resultView = findViewById(R.id.result);         //View for the end screen.
        questionView = findViewById(R.id.question);     //View for displaying the question.
        scoreView = findViewById(R.id.score);           //View for displaying the current score at the bottom of the screen.
        radioGroupAnswer = findViewById(R.id.radio_group);        //Saving radio button group to a variable.
    }

    /**
     * Method for showing welcome screen
     */
    private void showWelcome() {
        //Hides the layout of the cardface
        hideView(cardface);
    }

    /**
     * Method for updating the score message at the bottom of the screen.
     */
    private void updateScoreView() {
        //Initializing score.
        scoreView.setText(getString(R.string.current_score, score));
    }

    /**
     * Method for hiding views.
     */
    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    /**
     * Method for showing hidden views
     */
    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Method for starting the quiz.
     */
    public void startQuiz(View view) {
        //Updating the game progress.
        gameProgress = "playing";
        //Hides the welcome view.
        hideView(welcomeLayout);
        //Shows the cardface view.
        showView(cardface);
        //Asks next question in line.
        nextQuestion();
    }

    /**
     * Method for asking the next question in line
     */
    private void nextQuestion() {
        //Checks what kind of question is about to be asked
        switch (questions[questionNumber][6]) {
            case "radio":
                setRadioQuestions();
                break;
            case "check":
                setCheckQuestions();
                break;
            case "text":
                setTextQuestion();
                break;
        }
    }

    /**
     * Method for showing and populating radiobutton questions
     */
    private void setRadioQuestions() {
        //Hides all other answer views and shows the RadioButton layout.
        hideView(checkBoxLayout);
        hideView(textFieldLayout);
        showView(radioButtonLayout);

        //Populating the question
        questionView.setText(questions[questionNumber][0]);

        //Populating the answer alternatives.
        TextView textView;
        for (int i = 1; i < 5; i++) {
            textView = findViewById(getResources().getIdentifier("alt" + i, "id", this.getPackageName()));
            //Getting the answer alternative form the questions array.
            textView.setText(questions[questionNumber][i]);
        }
    }

    /**
     * Method for showing and populating checkbox questions
     */
    private void setCheckQuestions() {
        //Hides all other answer views and shows the CheckBox layout.
        showView(checkBoxLayout);
        hideView(textFieldLayout);
        hideView(radioButtonLayout);

        //Populating the question
        questionView.setText(questions[questionNumber][0]);

        //Populating the checkboxes.
        TextView textView;
        for (int i = 1; i < 5; i++) {
            textView = findViewById(getResources().getIdentifier("check" + i, "id", this.getPackageName()));
            //Getting answer alternative from questions array.
            textView.setText(questions[questionNumber][i]);
        }
    }

    /**
     * Method for showing and populating textfield questions
     */
    private void setTextQuestion() {
        //Hides all other answer views and shows the TextEdit layout.
        hideView(checkBoxLayout);
        showView(textFieldLayout);
        hideView(radioButtonLayout);

        //Populating the question
        questionView.setText(questions[questionNumber][0]);
    }

    /**
     * Method to be called when submitting RadioButton answers
     */
    public void submitRadioAnswer(View view) {
        //Verifying that the user has chosen an answer.
        if (checkRadioAnswer().equals("0")) {
            makeToast(getString(R.string.null_answer));
        } else {
            //Checking if the answer is right and awarding points.
            if (checkRadioAnswer().equals(questions[questionNumber][5])) {
                setScore();
            } else {
                //Letting the user know that the answer was wrong
                makeToast(getString(R.string.wrong_answer));
            }

            //Checking if the final question has been asked.
            if (questionNumber < questions.length - 1) {
                //Increasing the question number.
                questionNumber++;
                //Clearing the RadioButtons.
                radioGroupAnswer.clearCheck();
                //Getting the next question
                nextQuestion();
            } else {
                //Executes when all the questions has been asked.
                radioGroupAnswer.clearCheck();
                gameEnd();
            }
        }
    }

    /**
     * Method for checking if the RadioButton answers are right.
     * @return which radio button is checked (number in string format).
     */
    private String checkRadioAnswer() {
        //Making variables for radio buttons and answer.
        RadioButton radioButton;
        String buttonNumber = "0";

        //Checking which button is checked.
        for (int i = 1; i < 5; i++) {
            radioButton = findViewById(getResources().getIdentifier("alt" + i, "id", this.getPackageName()));
            if (radioButton.isChecked()) {
                buttonNumber = String.valueOf(i);
            }
        }
        return buttonNumber;
    }

    /**
     * Method for submitting CheckBox answers
     */
    public void submitCheckAnswer(View view) {
        //Verifying that the user has chosen one or more answers
        if (checkCheckAnswer().equals("0")) {
            makeToast(getString(R.string.null_answer));
        } else {
            //Checking if the answer is right and awarding points
            if (checkCheckAnswer().equals(questions[questionNumber][5])) {
                setScore();
            } else {
                //Letting the user know that the answer was wrong
                makeToast(getString(R.string.wrong_answer));
            }

            //Checking if the last question has been asked.
            if (questionNumber < questions.length - 1) {
                //Increasing the question number.
                questionNumber++;
                //Clearing checkboxes
                clearCheckBoxes();
                //Getting the next question
                nextQuestion();
            } else {
                //Executes when all the questions has been asked.
                clearCheckBoxes();
                gameEnd();
            }
        }
    }

    /**
     * Method for checking if the CheckBox answers is right
     * @return an answer code that tells which alternatives has been checked.
     */
    private String checkCheckAnswer() {
        int answerCode = 0; //Initializing variable for the answer code.
        int codeIndex = 1;  //Number for generating answer code.
        CheckBox checkbox;  //Variable for the checkboxes.

        //Iterating over the checkboxes and verifying if they are checked.
        for (int i = 4; i > 0; i--) {
            checkbox = findViewById(getResources().getIdentifier("check" + i, "id", this.getPackageName()));
            if (checkbox.isChecked()) {
                answerCode += codeIndex; //If the checkbox is checked, add it to the answer code.
            }
            codeIndex *= 10; //Moving the code index one place to the left.
        }
        return String.valueOf(answerCode);
    }

    /**
     * Method for clearing checkboxes after use.
     */
    private void clearCheckBoxes() {
        CheckBox checkbox;
        for (int i = 1; i < 5; i++) {
            checkbox = findViewById(getResources().getIdentifier("check" + i, "id", this.getPackageName()));
            checkbox.setChecked(false);
        }
    }

    /**
     * Method for submitting EditText answers
     */
    public void submitTextAnswer(View view) {
        //Closing software keyboard.
        InputMethodManager imm = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //Saving the EditText view to a variable
        EditText answer = findViewById(R.id.textanswer);
        //Checking if the EditText field is empty, and gives zero points if it is.
        if (TextUtils.isEmpty(answer.getText())) {
            makeToast(getString(R.string.nothing_answer));
            //Checking if the answer typed in is correct and awarding points.
        } else if (String.valueOf(answer.getText()).toLowerCase().equals(questions[questionNumber][5].toLowerCase())) {
            setScore();
        } else {
            //Letting the user know that the answer was wrong
            makeToast(getString(R.string.wrong_answer));
        }

        //Clears the answer view.
        answer.setText("");

        //Checking if the last question has been asked.
        if (questionNumber < 9) {
            //Increasing the question number.
            questionNumber++;
            //Getting the next question
            nextQuestion();
        } else {
            //Executes when all the questions has been asked.
            gameEnd();
        }
    }

    /**
     * Method for increasing score and displaying "Right Answer" message.
     */
    private void setScore() {
        //Increases the score.
        score++;
        //Displays victory message
        makeToast(getString(R.string.right_answer));
        //Updates the score on screen
        scoreView.setText(getString(R.string.current_score, score));
    }

    /**
     * Method for making a toast message
     * @param text to be displayed
     */
    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method for end game activity
     */
    private void gameEnd() {
        gameProgress = "end";

        //Toast to let user know there are no more questions left
        makeToast(getString(R.string.game_end));

        hideView(cardface);
        showView(endLayout);

        //Shows result
        resultView.setText(getString(R.string.final_result, score, questions.length));
    }

    /**
     * Method for restarting the game.
     */
    public void restart(View view) {
        //Removes results layout.
        hideView(endLayout);

        //Resetting question and score variables.
        questionNumber = 0;
        score = 0;
        gameProgress = "start";

        //Shows the default layout.
        showView(cardface);

        //Gets next question in line.
        nextQuestion();
    }

    /********** Methods for restoring progress on changed screen orientation **********/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("savedScore", score);
        outState.putInt("savedQuestionNumber", questionNumber);
        outState.putString("savedGameProgress", gameProgress);
        super.onSaveInstanceState(outState);
    }
}
