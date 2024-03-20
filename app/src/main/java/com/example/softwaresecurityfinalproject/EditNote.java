package com.example.softwaresecurityfinalproject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class EditNote extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    Note note;
    String currentNoteColor; // current note color
    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);

        // Retrieve note details from intent extras
        Intent intent = getIntent();
        if (intent.hasExtra("note_object")) {
            Note recievedNote = (Note) intent.getSerializableExtra("note_object");
            note= recievedNote;
        }
        //Set the note details
        //Set up the resources
        EditText title = findViewById(R.id.editInputTitle);
        EditText description = findViewById(R.id.editInputDescription);
        //Set default color selected to be YELLOW
        title.setBackgroundColor(Color.parseColor(note.getNoteColor()));
        description.setBackgroundColor(Color.parseColor(note.getNoteColor()));
        currentNoteColor = note.getNoteColor();
        //Set text details
        title.setText(note.getTitle());
        description.setText(note.getDescription());


    }
    public void blueNoteOnClick(View v){
        //Make the title and description background color blue
        EditText title = findViewById(R.id.editInputTitle);
        EditText description = findViewById(R.id.editInputDescription);

        title.setBackgroundColor(Color.BLUE);
        description.setBackgroundColor(Color.BLUE);
        currentNoteColor="#0000FF";

    }

    public void yellowNoteOnClick(View v){
        //Make the title and description background color blue
        EditText title = findViewById(R.id.editInputTitle);
        EditText description = findViewById(R.id.editInputDescription);
        title.setBackgroundColor(Color.YELLOW);
        description.setBackgroundColor(Color.YELLOW);
        currentNoteColor ="#FFFF00";
    }

    public void orangeNoteOnClick(View v){
        //Make the title and description background color blue
        EditText title = findViewById(R.id.editInputTitle);
        EditText description = findViewById(R.id.editInputDescription);
        title.setBackgroundColor(Color.parseColor("#FFA500"));
        description.setBackgroundColor(Color.parseColor("#FFA500"));
        currentNoteColor="#FFA500";
    }

    public void editSaveNoteOnClick(View v) throws Exception {
        Context context = this; //Activity context
        //Open Database to access
        DatabaseServices dataSource = new DatabaseServices(context); // Initialize the data source
        dataSource.open(); // Open the database
        //Get Title, Description and Current Note Color
        EditText title = findViewById(R.id.editInputTitle);
        EditText description = findViewById(R.id.editInputDescription);
        String titleText = title.getText().toString();
        String descriptionText= description.getText().toString();
        //Create note
        Note newNote = new Note(titleText, descriptionText,currentNoteColor, note.getId(), note.getKey());
        //Insert note into database
        dataSource.updateNote(newNote);
        //Make a toast since note created
        Toast.makeText(getApplicationContext(),"Note Updated", Toast.LENGTH_SHORT).show();
        dataSource.close(); // Close the database when done
        //Close current activity
        finish();
        startActivity(new Intent(EditNote.this,MainActivity.class));
    }

    public void cancelOnClick(View v){
        //Close current activity
        finish();
        startActivity(new Intent(EditNote.this,MainActivity.class));
    }

    public void uploadImageOnClick(View v){
        // Open the image picker intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}
