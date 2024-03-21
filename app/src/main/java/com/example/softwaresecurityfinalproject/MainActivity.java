package com.example.softwaresecurityfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Get user details from Login...
        Intent intent = getIntent();
        if (intent.hasExtra("user_object")) {
            User recievedUser = (User) intent.getSerializableExtra("user_object");
            user= recievedUser;
        }
        TextView welcome = findViewById(R.id.textViewToolbarTitle);
        welcome.setText("Welcome " + user.getUsername());

        //Set Up Bottom Navigation:
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.action_home);


        //Get All Auction Items associated with current user
        List<Note> noteItemList = new ArrayList<>();
        try {
            //Add items to list
            Context context = this; //Activity context
            //Open Database to access
            DatabaseServices dataSource = new DatabaseServices(context); // Initialize the data source
            dataSource.open();
            // Assume you have a list of AuctionItems from your database
            noteItemList = dataSource.getAllNotesForAssociatedUser(user.getUsername());
            System.out.println(noteItemList);
            dataSource.close();
        } catch (SQLException e) {
            Log.e("YourTag", "Error message", e);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        //Initialize TextView
        TextView noItemsTextView = findViewById(R.id.noItemsTextView);
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNoteItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Check if there are no auction items
        if (noteItemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noItemsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noItemsTextView.setVisibility(View.GONE);
            // Initialize Adapter
            Adapter adapter = new Adapter(MainActivity.this,noteItemList, user);
            recyclerView.setAdapter(adapter);
        }

    }
    //New Note OnClick Function
    public void newItemFABOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, NewNote.class);
        intent.putExtra("user_object", user);
        startActivity(intent);
    }
    //Logout Function
    public void logOutOnClick(){
        finish();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    //Function for navbar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    if (item.getItemId() == R.id.action_home) {
                        // Do Nothing since already on this page

                        return true;
                    } else if (item.getItemId() == R.id.action_shared) {
                        finish();
                        intent = new Intent(MainActivity.this, SharedNotes.class);
                        intent.putExtra("user_object", user);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            };

}