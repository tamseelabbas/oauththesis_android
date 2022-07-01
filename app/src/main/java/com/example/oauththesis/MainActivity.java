package com.example.oauththesis;

import static android.content.ContentValues.TAG;

import androidx.biometric.BiometricPrompt;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.security.keystore.KeyInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    private BiometricPrompt.CryptoObject myCrypto;

    private TextView step_1;
    private TextView step_2;
    private TextView step_3;
    private TextView step_4;
    private TextView step_5;
    private TextView step_6;
    private TextView step_7;
    private TextView step_8;
    private TextView step_9;
    private TextView LogedIn;
    private TextView EncryptAccessToken;




    private String encrypted_email = "";
    private String encrypted_name = "";
    private String encrypted_given_name = "";
    private String encrypted_family_name = "";
    private String encrypted_pid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        step_1 = findViewById(R.id.step_1);
        step_1.setText("");
        step_2 = findViewById(R.id.step_2);
        step_2.setText("");
        step_3 = findViewById(R.id.step_3);
        step_3.setText("");
        step_4 = findViewById(R.id.step_4);
        step_4.setText("");
        step_5 = findViewById(R.id.step_5);
        step_5.setText("");
        step_6 = findViewById(R.id.step_6);
        step_6.setText("");
        step_7 = findViewById(R.id.step_7);
        step_7.setText("");
        step_8 = findViewById(R.id.step_8);
        step_8.setText("");
        step_9 = findViewById(R.id.step_9);
        step_9.setText("");
        LogedIn = findViewById(R.id.LogedIn);
        LogedIn.setText("");
        EncryptAccessToken = findViewById(R.id.EncryptAccessToken);
        EncryptAccessToken.setText("");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        Button decryptButton = findViewById(R.id.decrypt);
        BioSecurity bioSecurity = new BioSecurity(MainActivity.this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bioSecurity.encrypt(new CryptoObjectListener() {
                    @Override
                    public void available(BiometricPrompt.CryptoObject cryptoObject) {
                        myCrypto=cryptoObject;
                        signIn();
                    }
                });
            }
        });

//        decryptButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//
//                bioSecurity.decrypt(new CryptoObjectListener() {
//                    @Override
//                    public void available(BiometricPrompt.CryptoObject cryptoObject) {
//
//                        String decryptedInfo = null;
//                        try {
//                            decryptedInfo = new String(cryptoObject.getCipher().doFinal(encryptedInfo));
//                        } catch (BadPaddingException e) {
//                            e.printStackTrace();
//                        } catch (IllegalBlockSizeException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d("MY_APP_TAG", "Descrypted information: " +
//                                Arrays.toString(new String[]{decryptedInfo}));
//                    }
//                });
//            }
//        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    Runnable updater = null;

    private void updateGUI(){
        final Handler timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                boolean rerun = false;
                if(step_1.getText().toString() == ""){
                    step_1.setText("Authenticate using trusty");
                    rerun = true;
                }else if(step_2.getText().toString() == ""){
                    step_2.setText("Invoke trusty API");
                    rerun = true;
                }
                else if(EncryptAccessToken.getText().toString() == ""){
                    EncryptAccessToken.setText("Encrypting access token");
                    rerun = true;

                }
                else if(step_3.getText().toString() == ""){
                    step_3.setText("Send data to encrypt");
                    rerun = true;

                }else if(step_4.getText().toString() == ""){
                    step_4.setText("Encrypted Email : "+ encrypted_email);
                    rerun = true;

                }else if(step_5.getText().toString() == ""){
                    step_5.setText("Encrypted Name : "+ encrypted_name);
                    rerun = true;

                }else if(step_6.getText().toString() == ""){
                    step_6.setText("Encrypted Family Name : "+ encrypted_family_name);
                    rerun = true;

                }else if(step_7.getText().toString() == ""){
                    step_7.setText("Encrypted Given Name : "+ encrypted_given_name);
                    rerun = true;

                }else if(step_8.getText().toString() == ""){
                    step_8.setText("Encrypted Id : "+ encrypted_pid);
                    rerun = true;
                }
                else if(step_9.getText().toString() == ""){
                    step_9.setText("Send Encrypted data to server");
                    rerun = true;
                }else if(LogedIn.getText().toString() == ""){
                    LogedIn.setText("User LogedIn successfully");


                }

                if(rerun){
                    timerHandler.postDelayed(updater,100);
                }
            }
        };

        timerHandler.post(updater);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                String idToken = account.getIdToken();

                byte[] pEmail = new byte[0];
                byte[] pName = new byte[0];
                byte[] pGivenName = new byte[0];
                byte[] pFamilyName = new byte[0];
                byte[] pID = new byte[0];

                try {
                    pEmail = myCrypto.getCipher().doFinal(
                            personEmail.getBytes(Charset.defaultCharset()));
                    pName = myCrypto.getCipher().doFinal(
                            personName.getBytes(Charset.defaultCharset()));
                    pGivenName = myCrypto.getCipher().doFinal(
                            personGivenName.getBytes(Charset.defaultCharset()));
                    pFamilyName = myCrypto.getCipher().doFinal(
                            personFamilyName.getBytes(Charset.defaultCharset()));
                    pID = myCrypto.getCipher().doFinal(
                            personId.getBytes(Charset.defaultCharset()));




                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }





                encrypted_email = pEmail.toString();
                encrypted_name = pName.toString();
                encrypted_given_name = pGivenName.toString();
                encrypted_family_name = pFamilyName.toString();
                encrypted_pid = pID.toString();

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().putString("id", pID.toString())
                        .apply();


                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().putString("email", pEmail.toString())
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().putString("displayName", pName.toString())
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().putString("givenName", pGivenName.toString())
                        .apply();

                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().putString("familyName", pFamilyName.toString())
                        .apply();

                updateGUI();

                Users users=new Users(pName.toString(), pGivenName.toString(), pFamilyName.toString(), pEmail.toString(), pID.toString(),personPhoto);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Add a new document with a generated ID
                CollectionReference cities = db.collection("users");
                cities.document(users.personId).set(users);
                DocumentReference docRef = db.collection("users").document(personId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("Data", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("No document", "No such document");
                            }
                        } else {
                            Log.d("failed to get", "get failed with ", task.getException());
                        }
                    }
                });

                //   abc =myRef.child(personId).get();
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }
//            startActivity(new Intent(MainActivity.this, SecondActivity.class));
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("tamseelerror", e.toString());
        }
    }
}
