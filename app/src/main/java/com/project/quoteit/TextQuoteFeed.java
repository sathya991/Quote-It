package com.project.quoteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextQuoteFeed extends AppCompatActivity {
    LinearLayout linearLayout;
    int limit = 50, i = 0;
    LayoutInflater vi;
    boolean like = false;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_quote_feed);
        linearLayout = findViewById(R.id.linearLayout);
        vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert vi != null;
        newPosts();
    }

    public void layoutInflator() {
        v = vi.inflate(R.layout.text_post, linearLayout, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            Intent intent = new Intent(TextQuoteFeed.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void newPosts() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Quote");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> quotes, ParseException e) {
                if (e == null) {
                    for (ParseObject object : quotes) {
                        if (i == limit) {
                            i = 0;
                            break;
                        }
                        layoutInflator();
                        TextView textView = v.findViewById(R.id.quoteView);
                        textView.setText(Objects.requireNonNull(object.get("quote")).toString());
                        ImageView imageView = v.findViewById(R.id.likeVIew);
                        imageView.setImageResource(R.drawable.heart2);
                        imageView.setOnClickListener(liked);
                        TextView usernameView = v.findViewById(R.id.usernameView);
                        usernameView.setText(Objects.requireNonNull(object.get("user")).toString());
                        TextView likeNumber = v.findViewById(R.id.likeNumber);
                        likeNumber.setText(Objects.requireNonNull(object.get("likes")).toString());
                        linearLayout.addView(v);
                        i++;
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void recentPosts(View view) {
        linearLayout.removeAllViews();
        newPosts();
    }

    public void mostLikedPosts(View view) {
    }

    public View.OnClickListener liked = new View.OnClickListener() {
        public void onClick(View view) {
            View vv = (View) view.getParent();
            final ImageView imageView = vv.findViewById(R.id.likeVIew);
            final TextView textView = vv.findViewById(R.id.quoteView);
            final TextView likeNumber = vv.findViewById(R.id.likeNumber);
            ParseQuery<ParseObject> likeOb = new ParseQuery("Quote");
            likeOb.orderByDescending("createdAt");
            likeOb.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    for (ParseObject object : objects) {
                        if ((object.get("quote").toString()).equals(textView.getText().toString())) {
                            int likesNow = Integer.parseInt(String.valueOf(object.get("likes")));
                            Log.i("likesnow before", String.valueOf(likesNow));
                            if (!like) {
                                object.put("likes", likesNow + 1);
                                likesNow++;
                                Log.i("likesnow liked", String.valueOf(likesNow));
                                imageView.setImageResource(R.drawable.heart);
                                //object.put("likedUsers", ParseUser.getCurrentUser().getUsername());
                                like = true;
                                object.saveInBackground();
                            } else {
                                object.put("likes", likesNow - 1);
                                likesNow--;
                                Log.i("likesnow removed like", String.valueOf(likesNow));
                               // object.remove("likedUsers");
                                imageView.setImageResource(R.drawable.heart2);
                                like = false;
                                object.saveInBackground();
                            }
                            likeNumber.setText(String.valueOf(likesNow));
                        }
                    }
                }
            });
        }
    };
}
