package com.example.softwaresecurityfinalproject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class EditNote extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    byte[] imageByteArray; // variable to capture the image to save in database if needed
    Note note;
    User user;
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
        if (intent.hasExtra("user_object")) {
            User recievedUser = (User) intent.getSerializableExtra("user_object");
            user= recievedUser;
        }
        //Set the note details
        //Set up the resources
        EditText title = findViewById(R.id.editInputTitle);
        EditText description = findViewById(R.id.editInputDescription);
        ImageView imageView = findViewById(R.id.imageDisplay);
        //Set default color selected to be YELLOW
        title.setBackgroundColor(Color.parseColor(note.getNoteColor()));
        description.setBackgroundColor(Color.parseColor(note.getNoteColor()));
        currentNoteColor = note.getNoteColor();
        //Set text details
        title.setText(note.getTitle());
        description.setText(note.getDescription());
        //Set imageview
        if(note.getImage() != null){
            ByteArrayInputStream stream = new ByteArrayInputStream(note.getImage());
            // Decode byte array stream into a bitmap
            Bitmap bm = BitmapFactory.decodeStream(stream);
            imageView.setImageBitmap(bm);
        }
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
        ImageView imageView = findViewById(R.id.imageDisplay);
        //Get Image if there is one
        byte[] bm = null;
        // Encrypt image
        if (imageView.getDrawable() != null) {
            // Retrieve image and convert to byte array
            // bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            bm = imageViewToByteArray(imageView);
        }
        //Create note
        Note newNote = new Note(titleText, descriptionText,currentNoteColor,note.getCreatedUser(), note.getId(), note.getKey(),bm);
        //Insert note into database
        dataSource.updateNote(newNote);
        //Make a toast since note created
        Toast.makeText(getApplicationContext(),"Note Updated", Toast.LENGTH_SHORT).show();
        dataSource.close(); // Close the database when done
        //Close current activity
        finish();
        Intent intent = new Intent(EditNote.this, MainActivity.class);
        intent.putExtra("user_object", user);
        startActivity(intent);
    }

    public void cancelOnClick(View v){
        //Close current activity
        finish();
        Intent intent = new Intent(EditNote.this, MainActivity.class);
        intent.putExtra("user_object", user);
        startActivity(intent);
    }

    public void uploadImageOnClick(View v){
        // Open the image picker intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    public void capturePhotoOnClick(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Call the superclass method
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Handle the result from the camera capture
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Rest of your code
            // Convert the image to a byte array (e.g., using ByteArrayOutputStream) and save to temp array to use when needed
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();
            //Change the displayed ImageView to the image taken
            ImageView imageView = findViewById(R.id.imageDisplay);
            imageView.setImageBitmap(imageBitmap);
        }
    }
    public byte[] imageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
