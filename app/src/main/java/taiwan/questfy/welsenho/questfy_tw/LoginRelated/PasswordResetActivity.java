package taiwan.questfy.welsenho.questfy_tw.LoginRelated;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taiwan.questfy.welsenho.questfy_tw.R;

public class PasswordResetActivity extends AppCompatActivity {


    private TextView txtEmail;
    private Button btnReset;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.reset_password_editInputEmail);
        btnReset = findViewById(R.id.reset_password_btnReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }



    private void resetPassword(){
        String Email = txtEmail.getText().toString();
        if (!Email.isEmpty()) {
            if (isEmailValid(Email)) {
                btnReset.setText("寄送中");
                firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnReset.setText("已成功寄出郵件");
                                    btnReset.setClickable(false);
                                }
                            }, 3000);
                        } else if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                            btnReset.setText("傳送重設密碼郵件");
                            Toast.makeText(PasswordResetActivity.this, "無此郵件，請在試一次", Toast.LENGTH_SHORT).show();
                        }else {
                            btnReset.setText("傳送重設密碼郵件");
                            Toast.makeText(PasswordResetActivity.this, "寄送失敗，請再試一次", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "信箱格式不符合", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "請輸入信箱", Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
