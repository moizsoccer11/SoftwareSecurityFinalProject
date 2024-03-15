package com.example.personallocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

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

        // Encryption
        try {
            SecretKey key = generateKey();
            saveButton.setOnClickListener(v -> {
                try {
                    String plaintext = userInput.getText().toString();
                    byte[] ciphertext = encrypt(plaintext, key);
                    encryptedText.setText(Base64.encodeToString(ciphertext, Base64.NO_WRAP));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            decryptButton.setOnClickListener(v -> {
                try {
                    String ciphertext = encryptedText.getText().toString();
                    byte[] plaintext = decrypt(ciphertext, key);
                    decryptedText.setText(new String(plaintext, StandardCharsets.UTF_8));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Encryption Failed", Toast.LENGTH_LONG).show();
        }


        // Decryption
    }

    // Generate 256 bit key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    // Encrypt using CBC and PKCS5Padding
    public byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine iv and ciphertext arrays
        byte[] encryption = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryption, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryption, iv.length, ciphertext.length);

        return encryption;
    }

    public byte[] decrypt(String ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        // Get IV and ciphertext from combined array
        byte[] encryption = Base64.decode(ciphertext, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(encryption, 0, 16);
        byte[] ct = Arrays.copyOfRange(encryption, 16, encryption.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ct);
    }

}