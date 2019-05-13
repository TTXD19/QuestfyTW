package taiwan.questfy.welsenho.questfy_tw.LoginRelated;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.R;

public class SignUpAcivity extends AppCompatActivity {


    private String Uid;
    private String sex;
    private String email;
    private String password;
    private String confirmPassword;
    private String ID;
    private String loginType;


    private TextView txtMissing;
    private TextView txtPrivactPolicy;
    private TextView txtUserAgreements;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editID;
    private Button btnRegister;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private CheckBox checkPrivacyPolicy;
    private CheckBox checkUserAgreements;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ScrollView scrollView;

    private EditRelatedMethod editRelatedMethod;
    private SignUpMethod signUpMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_acivity);

        txtMissing = findViewById(R.id.txtMissing);
        txtPrivactPolicy = findViewById(R.id.signUp_txtReviewPrvacyPolicy);
        txtUserAgreements = findViewById(R.id.signUp_txtReviewUserAgreement);
        editEmail = findViewById(R.id.editEmailRegis);
        editPassword = findViewById(R.id.editPasswordRegis);
        editConfirmPassword = findViewById(R.id.editConfirmPasswordRegis);
        editID = findViewById(R.id.editIDRegis);
        btnRegister = findViewById(R.id.btnRegister);
        scrollView = findViewById(R.id.scrollRegister);
        radioGroup = findViewById(R.id.radioGpSignUp);
        checkPrivacyPolicy = findViewById(R.id.checkPrivacy);
        checkUserAgreements = findViewById(R.id.checkTermService);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("帳號註冊");
        progressDialog.setMessage("正在註冊您的帳號中，請稍候");

        editRelatedMethod = new EditRelatedMethod();
        signUpMethod = new SignUpMethod();
        firebaseAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSex() == null) {
                    txtMissing.setText("請選擇您的性別");
                    txtMissing.setVisibility(View.VISIBLE);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                } else {
                    progressDialog.setMessage("正在註冊您的帳號中，請稍候");
                    progressDialog.setTitle("帳號註冊");
                    progressDialog.show();
                    register();
                }
            }
        });

        txtPrivactPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://welsen9595.wixsite.com/questfyprivacypolicy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        txtUserAgreements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://welsen9595.wixsite.com/questfyprivacypolicy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }


    private void register() {

        getUserInput();
        final String createDate = editRelatedMethod.getUploadDate();
        if (checkPrivacyPolicy.isChecked() && checkUserAgreements.isChecked()) {
            if (!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !ID.isEmpty()) {
                if (password.equals(confirmPassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpAcivity.this, R.string.regis_type_success, Toast.LENGTH_SHORT).show();
                                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            databaseReference = FirebaseDatabase.getInstance().getReference();
                                            firebaseAuth = FirebaseAuth.getInstance();
                                            firebaseUser = firebaseAuth.getCurrentUser();
                                            signUpMethod.firebaseProfileSignUp(databaseReference, firebaseAuth.getUid(), email, ID, checkSex(), loginType, getApplicationContext(), createDate, firebaseUser.getUid());
                                            signUpMethod.setUpFirebaseProfile(firebaseUser, ID);
                                            firebaseAuth.signOut();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(SignUpAcivity.this, LoginActivity.class);
                                            intent.putExtra("Email", email);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            } else {
                                if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                    progressDialog.dismiss();
                                    txtMissing.setVisibility(View.VISIBLE);
                                    txtMissing.setText("此帳號已有人註冊");
                                } else if (task.getException().getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]")) {
                                    progressDialog.dismiss();
                                    txtMissing.setVisibility(View.VISIBLE);
                                    txtMissing.setText("密碼字元需大於6位數");
                                } else if (task.getException().getMessage().equals("The email address is badly formatted.")) {
                                    progressDialog.dismiss();
                                    txtMissing.setVisibility(View.VISIBLE);
                                    txtMissing.setText("信箱格式錯誤");
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpAcivity.this, R.string.regis_type_failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    txtMissing.setText(R.string.regis_type_password_not_equal);
                    txtMissing.setVisibility(View.VISIBLE);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            } else {
                progressDialog.dismiss();
                txtMissing.setText(R.string.regis_missing);
                txtMissing.setVisibility(View.VISIBLE);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "您必須同意隱私權政策及使用者協議內容才能繼續", Toast.LENGTH_SHORT).show();
        }
    }

    private String checkSex() {
        //-1代表radioGroup.getCheckedRadioButtonId()未被選擇
        if (radioGroup.getCheckedRadioButtonId() != -1) {
            int checkSex = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(checkSex);
            sex = radioButton.getText().toString();
        } else {
            sex = null;
        }
        return sex;
    }

    private void getUserInput(){
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        confirmPassword = editConfirmPassword.getText().toString();
        ID = editID.getText().toString();
        loginType = "EmailPassword";
    }


}
