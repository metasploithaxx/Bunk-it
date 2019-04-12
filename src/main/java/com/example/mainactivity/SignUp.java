package com.example.mainactivity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SignUp extends AppCompatActivity {
    private ConstraintLayout layout;
    private static final int RC_SIGN_IN =123 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            findViewById(R.id.signOutButton).setEnabled(true);
            findViewById(R.id.chat).setEnabled(true);
            findViewById(R.id.signInButton).setEnabled(false);
            String t1=auth.getCurrentUser().getProviders().get(0);
            String t2=auth.getCurrentUser().getDisplayName();
            TextView t=findViewById(R.id.Display);
            t.setText("Hi, "+t2);

        } else {
            // not signed in
            findViewById(R.id.signOutButton).setEnabled(false);
            findViewById(R.id.signInButton).setEnabled(true);
            findViewById(R.id.chat).setEnabled(false);
        }
    }
    public void createSignInIntent(View view){

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);



    }
    public void signOutIntent(View view){
        layout=findViewById(R.id.mainLayout);
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(layout,"Signed out!",Snackbar.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                Button myButton = findViewById(R.id.signInButton);
                if(response==null){
                Snackbar.make(layout,"Sign in cancelled!",Snackbar.LENGTH_SHORT).show();
                return;}
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(layout,"Network Error!",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Snackbar.make(myButton,"Unknown Error!",Snackbar.LENGTH_SHORT);
                Log.e("Err", "Sign-in error: ", response.getError());
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("d","OnResume()");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            findViewById(R.id.signOutButton).setEnabled(true);
            findViewById(R.id.signInButton).setEnabled(false);
            findViewById(R.id.chat).setEnabled(true);
            String t2=auth.getCurrentUser().getDisplayName();
            TextView t=findViewById(R.id.Display);
            t.setText("Hi, "+t2);
        }
        else {
            // not signed in
            findViewById(R.id.signOutButton).setEnabled(false);
            findViewById(R.id.signInButton).setEnabled(true);
            findViewById(R.id.chat).setEnabled(false);

        }

    }
    public void startChat(View v){
        Intent chat=new Intent(this,FirestoreChatActivity.class);
        startActivity(chat);

    }
}
