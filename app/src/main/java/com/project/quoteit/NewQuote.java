package com.project.quoteit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewQuote extends AppCompatActivity {
    protected EditText quoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quote);
        quoteText = findViewById(R.id.quoteText);
    }

    public void postQuote(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Post?");
        builder.setMessage("Do you want to post this quote?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseObject quoteObject = new ParseObject("Quote");
                        quoteObject.put("quote", quoteText.getText().toString());
                        quoteObject.put("user", ParseUser.getCurrentUser().getUsername());
                        quoteObject.put("likes",0);
                        quoteObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(NewQuote.this, "Quote Posted!!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(NewQuote.this, TextQuoteFeed.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create();
        builder.show();
    }

    public void skipPage(View view) {
        Intent intent = new Intent(NewQuote.this,TextQuoteFeed.class);
        startActivity(intent);
    }
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
