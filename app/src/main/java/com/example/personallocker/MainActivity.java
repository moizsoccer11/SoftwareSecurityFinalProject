package com.example.personallocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class MainActivity extends AppCompatActivity {

    byte[] iv = null;
    byte[] ciphertext = null;
    SecretKey key = null;
    TextView cipherView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views
        Button save = findViewById(R.id.button_save);
        Button buttonEnc = findViewById(R.id.button_enc);
        EditText text = findViewById(R.id.edit_save);
        TextView decryption = findViewById(R.id.text_pt);
        cipherView = findViewById(R.id.text_cipher);

        // Encryption
        String plaintext = text.getText().toString();
        save.setOnClickListener(v -> {
            try {
                byte[] encryptedText = encrypt(plaintext);
                cipherView.setText(new String(encryptedText, StandardCharsets.UTF_8));
                ciphertext = encryptedText;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
        // Decryption
        buttonEnc.setOnClickListener(v -> {
            try {
                String decryptedText = decrypt(ciphertext);
                decryption.setText(decryptedText);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public byte[] encrypt(String plaintext) throws Exception {
        byte[] pt = plaintext.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        key = keyGen.generateKey();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        ciphertext = cipher.doFinal(pt);
        iv = cipher.getIV();
        return ciphertext;
    }

    public String decrypt(byte[] ct) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(ct);
        System.out.println(new String(decryptedBytes, StandardCharsets.UTF_8));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}