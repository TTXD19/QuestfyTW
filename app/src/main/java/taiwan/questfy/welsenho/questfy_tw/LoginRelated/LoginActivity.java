package taiwan.questfy.welsenho.questfy_tw.LoginRelated;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.InternetConnectionDetect;
import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.MainActivity;
import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.OutOfConnectionActivity;
import taiwan.questfy.welsenho.questfy_tw.R;


public class LoginActivity extends AppCompatActivity {


    private String email;
    private String password;
    private String accountStatus;
    private static final int RC_SIGN_IN = 9001;

    private TextView txtWrongPassword;
    private TextView txtSignUp;
    private TextView txtSkip;
    private TextView txtFogotPassword;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignIn;
    private Button btnFacebook;
    private Button btnGoogle;
    private LoginButton loginButton;
    private SignInButton googleSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;

    private SignUpMethod signUpMethod;
    private InternetConnectionDetect internetConnectionDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        InitItem();
        InitFirebase();

        //Get email address after normal registering.
        if (getIntent().getStringExtra("Email") != null){
            editEmail.setText(getIntent().getStringExtra("Email"));
        }

        //Auto Login & check internet state
        if (firebaseUser != null) {
            if (internetConnectionDetect.isNetworkAvailable(getApplicationContext())) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(LoginActivity.this, OutOfConnectionActivity.class);
                startActivity(intent);
                finish();
            }

        }

        ItemClikc();
        InitFacebookCallBack();
        InitGoogleLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d("GSFAILEFD", e.getMessage() + e.getLocalizedMessage());
                // Google Sign In failed, update UI appropriately
            }

        }
    }

    private void InitItem(){
        txtSignUp = findViewById(R.id.new_login_txtSignUp);
        txtSkip = findViewById(R.id.new_login_txtSkip);
        txtFogotPassword = findViewById(R.id.new_login_txtForgetPassword);
        btnSignIn = findViewById(R.id.new_login_btnSignIn);
        editEmail = findViewById(R.id.new_login_editInputEmail);
        editPassword = findViewById(R.id.new_login_editInputPassword);
        txtWrongPassword = findViewById(R.id.new_login_txtWrongPassword);
        btnFacebook = findViewById(R.id.new_login_btnFacebook);
        loginButton = findViewById(R.id.new_login_facebookLogin);
        btnGoogle = findViewById(R.id.new_login_btnGoogle);
        googleSignIn = findViewById(R.id.new_login_googleLogin);

        progressDialog = new ProgressDialog(this);
        signUpMethod = new SignUpMethod();
        internetConnectionDetect = new InternetConnectionDetect();
    }

    private void ItemClikc(){

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpAcivity.class);
                startActivity(intent);
            }
        });

        txtFogotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("登入");
                progressDialog.setMessage("帳號登入中，請稍後");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signUpMethod.signInMethod(firebaseAuth, email, password, getApplicationContext(), LoginActivity.this, progressDialog, txtWrongPassword);
                }else{
                    txtWrongPassword.setText("帳號密碼不能為空");
                    txtWrongPassword.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn.performClick();
                googleSignIn();
            }
        });
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitFacebookCallBack(){
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.setTitle("帳號登入中");
                progressDialog.setMessage("請稍候，登入即將完成");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ERRORFACEBOOK", error.getMessage());
            }
        });
    }

    //For handling facebook login
    private void handleFacebookToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (isNew){
                        ProviderRegister("Facebook");
                    }else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void InitGoogleLogin(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
    }

    private void googleSignIn(){
        Toast.makeText(LoginActivity.this, "google sign in", Toast.LENGTH_SHORT).show();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            if (isNew){
                                ProviderRegister("Google");
                            }else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    private void ProviderRegister(String provider){
        EditRelatedMethod editRelatedMethod = new EditRelatedMethod();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Email", firebaseUser.getEmail());
        hashMap.put("ID", firebaseUser.getDisplayName());
        if (firebaseUser.getPhotoUrl() == null) {
            hashMap.put("User_image_uri", "https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6");
        }else {
            hashMap.put("User_image_uri", firebaseUser.getPhotoUrl().toString());
        }
        hashMap.put("createDate", editRelatedMethod.getUploadDate());
        hashMap.put("loginType", provider);
        hashMap.put("CompleteInformationCheck", "False");
        hashMap.put("userUid", firebaseUser.getUid());
        databaseReference.child("Users_profile").child(firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                }
            }
        });

    }
}
