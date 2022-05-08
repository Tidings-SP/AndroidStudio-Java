# Code snippets 

---
## Auth with Google

```java
package com.example.prototype;

// Login Activity

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private Button mSignInBtn;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        createRequest();

    }


    private void init(){
        mSignInBtn = findViewById(R.id.googleSignIn);
        mAuth = FirebaseAuth.getInstance();
    }

    private void createRequest() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(googleSignInAccount != null) {
            // If user already signIn forward to HomeActivity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            forwardUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            forwardUser(null);
                        }
                    }
                });
    }

    private void forwardUser(FirebaseUser user) {

        if (user != null) {
            Toast.makeText(MainActivity.this,"Log in Successful!", Toast.LENGTH_LONG).show();

            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            // Set start activity to Home Activity.
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            addSound(R.raw.forward);
            // Forward user to Home Activity.
            startActivity(homeIntent);
        } else {

            Toast.makeText(MainActivity.this, "Login Failed.", Toast.LENGTH_LONG).show();
        }

    }

    // optional but sounds make great user experience
    private void addSound(int sound) {
        MediaPlayer ring= MediaPlayer.create(MainActivity.this, sound);
        ring.start();
    }

}

// Authored by Surya Prakash
```

## Setup NavDrawer

```java
// Check whether any items pressed in navigation drawer.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);

                switch (id) {

                    case R.id.navHome:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.navAdd:
                        mDialog.show();
                        break;
                    case R.id.navLogOut:
                        logOut();
                        break;
                }

                // Close navigation drawer after a item selection or on a empty click.
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();
    }
        
    private void logOut() {

        // Sign Out
        mGoogleSignInClient.signOut();
        Toast.makeText(this, "Log out Successful!", Toast.LENGTH_SHORT).show();
        sendUserToLoginPage();
    }
 ```
 
 ## Firebase Read and Write
 
 ```java
 
 private void setValueOnChange(DatabaseReference databaseReference, TextView textView) {
        // Look for the data change(read)
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textView.setText(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
}

DatabaseReference.setValue(//Enter the value to be written in firebase database);
```

## Demo Dialog

```java
private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add, null);

        builder.setView(view);
        builder.setTitle("Enter Name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        mDialog = builder.create();
    }
```
