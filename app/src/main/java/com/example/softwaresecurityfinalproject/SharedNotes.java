package com.example.softwaresecurityfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SharedNotes extends AppCompatActivity {
    User user;
    //Get All shared notes
    List<Note> sharedNotesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharednotes);

        //Get user details from Login...
        Intent intent = getIntent();
        if (intent.hasExtra("user_object")) {
            User recievedUser = (User) intent.getSerializableExtra("user_object");
            user= recievedUser;
        }
        TextView welcome = findViewById(R.id.textViewToolbarTitle);
        welcome.setText("Shared Notes");

        //Set Up Bottom Navigation:
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.action_shared);
        //Initialize TextView
        TextView noItemsTextView = findViewById(R.id.noItemsTextView);
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSharedNoteItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Check if there are no  items
        if (sharedNotesList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noItemsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noItemsTextView.setVisibility(View.GONE);
            // Initialize Adapter
            Adapter adapter = new Adapter(SharedNotes.this,sharedNotesList, user);
            recyclerView.setAdapter(adapter);
        }

    }
    //Logout Function
    public void logOutOnClick(){
        finish();
        Intent intent = new Intent(SharedNotes.this, LoginActivity.class);
        startActivity(intent);
    }
    //New Note OnClick Function
    public void newSharedFABOnClick(View view) {
        //Launch Modal
        AlertDialog.Builder builder = new AlertDialog.Builder(SharedNotes.this);
        View enterTokenModal = LayoutInflater.from(SharedNotes.this).inflate(R.layout.enterkeymodal, null);
        // Find views in ModalView
        Button btnSubmit = enterTokenModal.findViewById(R.id.btnTokenSubmit);
        Button btnCancel= enterTokenModal.findViewById(R.id.btnTokenClose);
        //Create Dialog
        builder.setView(enterTokenModal);
        AlertDialog dialog = builder.create(); // Create the dialog instance
        dialog.show();
        //onClick Functions
        btnSubmit.setOnClickListener(v -> {
            // Get the ID of the note
            try{
                //Get entered token
                EditText tokenInput = enterTokenModal.findViewById(R.id.tokenInput);
                String tokenText=  tokenInput.getText().toString().trim();
                //Open Database to access
                try {
                    //Add items to list
                    Context context = this; //Activity context
                    //Open Database to access
                    DatabaseServices dataSource = new DatabaseServices(context); // Initialize the data source
                    dataSource.open();
                    // Assume you have a list of AuctionItems from your database
                    sharedNotesList = dataSource.getNotesByKey(tokenText);
                    dataSource.close();
                } catch (SQLException e) {
                    Log.e("YourTag", "Error message", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //Refresh list
                //Initialize TextView
                TextView noItemsTextView = findViewById(R.id.noItemsTextView);
                // Initialize RecyclerView
                RecyclerView recyclerView = findViewById(R.id.recyclerViewSharedNoteItems);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                if (sharedNotesList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noItemsTextView.setVisibility(View.VISIBLE);
                    TextView errorText2 = enterTokenModal.findViewById(R.id.tokenError);
                    errorText2.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noItemsTextView.setVisibility(View.GONE);
                    // Initialize Adapter
                    SharedNotesAdapter adapter = new SharedNotesAdapter(SharedNotes.this,sharedNotesList, user);
                    recyclerView.setAdapter(adapter);
                    // Dismiss the dialog
                    dialog.dismiss();
                }
                //Make a toast that Bid Created
                //Toast.makeText(context,"Note has been deleted", Toast.LENGTH_SHORT).show();
            }catch (SQLException e) {
                Log.e("YourTag", "Error message", e);
            }
        });
        btnCancel.setOnClickListener(v -> {
            // Dismiss the dialog
            dialog.dismiss();
        });
    }
    //Function for navbar
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    if (item.getItemId() == R.id.action_home) {
                        finish();
                        intent = new Intent(SharedNotes.this, MainActivity.class);
                        intent.putExtra("user_object", user);
                        startActivity(intent);
                        return true;
                    } else if (item.getItemId() == R.id.action_shared) {
                        return true;
                    }
                    return false;
                }
    };
}
