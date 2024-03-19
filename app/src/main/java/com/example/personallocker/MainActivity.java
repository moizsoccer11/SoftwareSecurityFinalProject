package com.example.personallocker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    int SELECT_IMAGE = 100;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views
        EditText userInput = findViewById(R.id.edittext_pt);
        Button saveButton = findViewById(R.id.button_save);
        TextView encryptedText = findViewById(R.id.text_cipher);
        Button decryptButton = findViewById(R.id.button_decrypt);
        TextView decryptedText = findViewById(R.id.text_decryptedText);
        Button imageButton = findViewById(R.id.button_image);
        imageView = findViewById(R.id.imageView);
        ImageView encryptedImageView = findViewById(R.id.imageView_encrypted);

        // Save user image
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE);
        });
        try {
            // Encryption
            SecretKey key = generateKey();
            byte[][] encryptedImage = new byte[1][];

            saveButton.setOnClickListener(v -> {
                try {
                    // Encrypt text
                    String plaintext = userInput.getText().toString();
                    byte[] ciphertext = encrypt(plaintext, key);
                    encryptedText.setText(Base64.encodeToString(ciphertext, Base64.NO_WRAP));

                    // Encrypt image
                    if (imageView.getDrawable() != null) {
                        // Retrieve image and convert to byte array
                        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        // Encrypt
                        byte[] imageBytes = bitmapToByteArray(bm);
                        encryptedImage[0] = imgEnc(imageBytes, key);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // Decryption
            decryptButton.setOnClickListener(v -> {
                try {
                    // Decrypt text
                    String ciphertext = encryptedText.getText().toString();
                    byte[] plaintext = decrypt(ciphertext, key);
                    decryptedText.setText(new String(plaintext, StandardCharsets.UTF_8));

                    // Decrypt image
                    if (encryptedImage[0] != null) {
                        // Decrypt image
                        byte[] imageBytes = imgDec(encryptedImage[0], key);
                        ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
                        // Decode byte array stream into a bitmap
                        Bitmap bm = BitmapFactory.decodeStream(stream);
                        encryptedImageView.setImageBitmap(bm);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Encryption Failed", Toast.LENGTH_LONG).show();
        }
    }

    // Generate 256 bit key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public byte[] imgEnc(byte[] image, SecretKey key) throws Exception {
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(image);

        // Combine IV and ciphertext arrays
        byte[] encryption = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryption, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryption, iv.length, ciphertext.length);
        return encryption;
    }
    public byte[] imgDec(byte[] image, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        // Get IV and ciphertext from combined array
        byte[] iv = Arrays.copyOfRange(image, 0, 16);
        byte[] ct = Arrays.copyOfRange(image, 16, image.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ct);
    }

    // Encrypt using CBC and PKCS5Padding
    public byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine IV and ciphertext arrays
        byte[] encryption = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryption, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryption, iv.length, ciphertext.length);

        return encryption;
    }

    // Decrypt using the inputs given from encryption
    public byte[] decrypt(String ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        // Get IV and ciphertext from combined array
        byte[] encryption = Base64.decode(ciphertext, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(encryption, 0, 16);
        byte[] ct = Arrays.copyOfRange(encryption, 16, encryption.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ct);
    }

    public byte[] bitmapToByteArray(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                Uri imageURI = data.getData();
                if (null != imageURI) {
                    imageView.setImageURI(imageURI);
                }
            }
        }
    }
}