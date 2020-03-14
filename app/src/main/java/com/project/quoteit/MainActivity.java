package com.project.quoteit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {
    private EditText userText;
    private EditText passText;
    private TextView changeLogin;
    private EditText mailText;
    private Button loginButton;
    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SignUp/Login");
        userText = findViewById(R.id.userText);
        passText = findViewById(R.id.passText);
        changeLogin = findViewById(R.id.newClick);
        login = true;
        mailText = findViewById(R.id.mailText);
        mailText.setVisibility(View.INVISIBLE);
        loginButton = findViewById(R.id.loginButton);
        if(ParseUser.getCurrentUser() != null)
        {
            redirect();
        }
        touchOutside();
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    //Function to close the keyboard when touched outside editTexts...
    public void touchOutside() {
        passText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        userText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        mailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    //OnClick Listener for Signup/Login button
    public void loginClick(View view) {
        //Login
        if (login) {
            if (!userText.getText().toString().isEmpty() || !passText.getText().toString().isEmpty()) {
                ParseUser.logInInBackground(userText.getText().toString(), passText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                         redirect();
                        } else {
                            ParseUser.logOut();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } else {
            //Sign up
            mailText.setVisibility(View.VISIBLE);
            if (!userText.getText().toString().isEmpty() || !passText.getText().toString().isEmpty() || !mailText.getText().toString().isEmpty()) {
                ParseUser newUser = new ParseUser();
                newUser.setUsername(userText.getText().toString());
                newUser.setEmail(mailText.getText().toString());
                newUser.setPassword(passText.getText().toString());
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent newQuote = new Intent(MainActivity.this,NewQuote.class);
                            startActivity(newQuote);
                        } else {
                            ParseUser.logOut();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "One or more Fields are empty", Toast.LENGTH_SHORT).show();
            }
        }
    }
//Function to change between signup and login when textView is clicked
    public void changeWay(View view) {
        if (login) {
            loginButton.setText("Sign Up");
            mailText.setVisibility(View.VISIBLE);
            changeLogin.setText(R.string.old_user);
            login = false;
        } else {
            loginButton.setText("Login");
            mailText.setVisibility(View.INVISIBLE);
            changeLogin.setText(getString(R.string.new_signup));
            login = true;
        }
    }

    public void redirect(){
        Intent intent = new Intent(MainActivity.this,TextQuoteFeed.class);
        startActivity(intent);
    }

    //Function to hide keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
