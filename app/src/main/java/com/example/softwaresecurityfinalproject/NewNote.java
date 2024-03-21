package com.example.softwaresecurityfinalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class NewNote extends AppCompatActivity {
    User user;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentNoteColor; //current note color
    boolean imageCaptured = false; //variable to capture if user has added a photo to the note or not
    byte[] imageByteArray; // variable to capture the image to save in database if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        //Get user details
        Intent intent = getIntent();
        if (intent.hasExtra("user_object")) {
            User recievedUser = (User) intent.getSerializableExtra("user_object");
            user= recievedUser;
        }
        //Set up the resources
        EditText title = findViewById(R.id.inputTitle);
        EditText description = findViewById(R.id.inputDescription);
        //Set default color selected to be YELLOW
        title.setBackgroundColor(Color.YELLOW);
        description.setBackgroundColor(Color.YELLOW);
        currentNoteColor = "#FFFF00";



        ImageView yellowRect = findViewById(R.id.yellowNote);
        ImageView blueRect = findViewById(R.id.blueNote);
        ImageView orangeRect = findViewById(R.id.orangeNote);
    }
    public void blueNoteOnClick(View v){
        //Make the title and description background color blue
        EditText title = findViewById(R.id.inputTitle);
        EditText description = findViewById(R.id.inputDescription);

        title.setBackgroundColor(Color.BLUE);
        description.setBackgroundColor(Color.BLUE);
        currentNoteColor="#0000FF";

    }

    public void yellowNoteOnClick(View v){
        //Make the title and description background color blue
        EditText title = findViewById(R.id.inputTitle);
        EditText description = findViewById(R.id.inputDescription);
        title.setBackgroundColor(Color.YELLOW);
        description.setBackgroundColor(Color.YELLOW);
        currentNoteColor ="#FFFF00";
    }

    public void orangeNoteOnClick(View v){
        //Make the title and description background color blue
        EditText title = findViewById(R.id.inputTitle);
        EditText description = findViewById(R.id.inputDescription);
        title.setBackgroundColor(Color.parseColor("#FFA500"));
        description.setBackgroundColor(Color.parseColor("#FFA500"));
        currentNoteColor="#FFA500";
    }

    public void saveNoteOnClick(View v) throws Exception {
        Context context = this; //Activity context
        //Open Database to access
        DatabaseServices dataSource = new DatabaseServices(context); // Initialize the data source
        dataSource.open(); // Open the database
        //Get Title, Description and Current Note Color
        EditText title = findViewById(R.id.inputTitle);
        EditText description = findViewById(R.id.inputDescription);
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
        //Get Associated username
        String userName = user.getUsername();
        //Create note
        Note newNote = new Note(titleText, descriptionText,currentNoteColor, userName, bm);
        //Insert note into database
        long noteId = dataSource.insertNote(newNote);
        //Make a toast since note created
        Toast.makeText(getApplicationContext(),"Note Saved", Toast.LENGTH_SHORT).show();
        dataSource.close(); // Close the database when done
        //Close current activity
        finish();
        Intent intent = new Intent(NewNote.this, MainActivity.class);
        intent.putExtra("user_object", user);
        startActivity(intent);
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
    public void cancelOnClick(View v){
        //Close current activity
        finish();
        startActivity(new Intent(NewNote.this,MainActivity.class));
    }

    public void uploadImageOnClick(View v){
        // Open the image picker intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    public byte[] imageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
