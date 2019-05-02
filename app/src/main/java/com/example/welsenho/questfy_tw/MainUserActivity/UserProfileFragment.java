package com.example.welsenho.questfy_tw.MainUserActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.MainActivityFragment.MostPopularFragment;
import com.example.welsenho.questfy_tw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_SELECT = 1;
    private final String NEW_CROP_IMAGE = "NewCropImage";

    private TextView txtChangeImage;
    private TextView txtUserName;
    private TextView txtSpeciality;
    private TextView txtStatusMessage;
    private TextView txtCreateDate;
    private TextView txtSchoolName;
    private TextView txtSchoolMajor;
    private TextView txtPopTitle;
    private TextView txtCount;
    private TextView txtSave;
    private TextView txtCancel;
    private TextView txtNotRegister;
    private TextView txtAddPhoto;
    private ImageView imgAddPhoto;
    private EditText editText;
    private ImageButton imageButtonSpeciality;
    private ImageButton imageButtonStatusMessage;
    private Dialog dialog;
    private Button btnEmailCertifiacte;
    private CircleImageView circleImageView;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private CardView cardView;
    private OnFragmentInteractionListener listener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private EditRelatedMethod editRelatedMethod;




    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        txtCreateDate = view.findViewById(R.id.user_profile_fm_txt_createDate);
        txtChangeImage = view.findViewById(R.id.user_profile_fm_txt_changeImage);
        txtUserName = view.findViewById(R.id.user_profile_fm_txt_userName);
        txtSchoolName = view.findViewById(R.id.user_profile_fm_txt_schoolName);
        txtSchoolMajor = view.findViewById(R.id.user_profile_fm_txt_schoolMajor);
        txtSpeciality = view.findViewById(R.id.user_profile_fm_txt_speciality);
        txtStatusMessage = view.findViewById(R.id.user_profile_fm_txt_customMessage);
        imageButtonSpeciality = view.findViewById(R.id.user_profile_fm_imgBtn_editSpeciality);
        imageButtonStatusMessage = view.findViewById(R.id.user_profile_fm_imgBtn_editStatusMessage);
        btnEmailCertifiacte = view.findViewById(R.id.user_profile_fm_btn_emailCertificate);
        circleImageView = view.findViewById(R.id.user_profile_fm_circleImage_userImage);
        progressBar = view.findViewById(R.id.user_profile_fm_progressBar);
        cardView = view.findViewById(R.id.user_profile_fm_cardView);
        txtNotRegister = view.findViewById(R.id.user_profile_fm_txtNotRegister);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().reload();
        }
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        progressDialog = new ProgressDialog(getContext());
        editRelatedMethod = new EditRelatedMethod();
        dialog = new Dialog(getContext());

        if (firebaseUser != null) {
            onItemClick();
            LoadUserProfile();
            getFirebaseData();
            txtNotRegister.setVisibility(View.GONE);
        }else {
            cardView.setVisibility(View.GONE);
            txtNotRegister.setVisibility(View.VISIBLE);
            txtNotRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserProfileFragment.OnFragmentInteractionListener) {
            listener = (UserProfileFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUserImageChange();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public void changeImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.d("UploadStatus", "Start Uploading");
                    starCropping(data.getData());
                    //UploadImage(data.getData());
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri cropUri = UCrop.getOutput(data);
            if (cropUri != null){
                UploadImage(cropUri);
            }
        }
        Log.d("UploadStatus", "Finish Uploading");
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void onItemClick(){
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });

        imageButtonSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMessage(getString(R.string.speciality));
            }
        });

        imageButtonStatusMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMessage(getString(R.string.status_message));
            }
        });

        txtChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });
    }

    private void showPopUpMessage(final String type){
        dialog.setContentView(R.layout.user_profile_custom_message_editing);
        txtPopTitle = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtTitle);
        txtCount = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtCount);
        txtSave = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtSave);
        txtCancel = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtCancel);
        txtAddPhoto = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtAddPicture);
        editText = dialog.findViewById(R.id.pop_up_userProfile_customMessage_editMessage);
        imgAddPhoto = dialog.findViewById(R.id.pop_up_userProfile_customMessage_addPicture);

        txtPopTitle.setText(type);
        txtAddPhoto.setVisibility(View.GONE);
        imgAddPhoto.setVisibility(View.GONE);
        dialog.show();

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtCount.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCustomMessage(type, editText.getText().toString());
            }
        });
    }

    private void uploadCustomMessage(String typeMessage, String message){
        if (typeMessage.equals(getString(R.string.speciality))){
            typeMessage = "userSpeciality";
        } else if (typeMessage.equals(getString(R.string.status_message))){
            typeMessage = "userStatusMessage";
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(typeMessage, message);
        databaseReference.child("Users_profile").child(firebaseUser.getUid()).updateChildren(hashMap);
        dialog.dismiss();
    }

    private void UploadImage(Uri uri){
        progressDialog.setMessage("Uploading Photo...");
        progressDialog.show();
        UploadUserProfileImage(uri);
    }

    private void UploadUserProfileImage(Uri userImageuri){
        final String date = editRelatedMethod.getDate();
        UploadTask uploadTask = storageReference.child("User_Profile_Image").child(firebaseUser.getUid()).child(date).putFile(userImageuri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference storageReference1 = storageReference.child("User_Profile_Image").child(firebaseUser.getUid()).child(date);
                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        Log.d("DownloadUri", uri.toString());
                        UpdateFirebaseLink(uri);
                        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                        firebaseUser.updateProfile(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Picasso.get().load(firebaseUser.getPhotoUrl()).fit().into(circleImageView);
                                listener.onUserImageChange();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });

            }
        });

    }

    private void LoadUserProfile(){

        if (firebaseUser.getPhotoUrl() != null) {
            Picasso.get().load(firebaseUser.getPhotoUrl()).fit().into(circleImageView);

        }else {
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6")
                    .fit().into(circleImageView);
        }
        txtUserName.setText(firebaseUser.getDisplayName());

        firebaseUser.reload();
        if (firebaseUser.isEmailVerified()) {
            btnEmailCertifiacte.setText("信箱已認證\n" + firebaseUser.getEmail());
        }else {
            Toast.makeText(getContext(), "如果Email已經驗證過，請關閉App在重開一次", Toast.LENGTH_SHORT).show();
            btnEmailCertifiacte.setText("信箱未認證(點擊認證)\n" + firebaseUser.getEmail());
            btnEmailCertifiacte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnEmailCertifiacte.setText("認證寄送中...");
                    if (!firebaseUser.isEmailVerified()){
                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnEmailCertifiacte.setText("已將認證Email寄送至您的郵件");
                                            Toast.makeText(getContext(), "如果Email已經驗證過，請關閉App在重開一次", Toast.LENGTH_SHORT).show();
                                        }
                                    }, 5);
                                }else {
                                    btnEmailCertifiacte.setText("信箱未認證(點擊認證)\n" + firebaseUser.getEmail());
                                    Toast.makeText(getContext(), "寄送失敗，請在試一次", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    /**
     * When userImage is upload to storage correctly then it will update in userDatabase userImageUri
     */
    private void UpdateFirebaseLink(Uri uri){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("User_image_uri", uri.toString());
        databaseReference.child("Users_profile").child(firebaseAuth.getUid()).updateChildren(hashMap);
    }

    private void getFirebaseData(){
        databaseReference.child("Users_profile").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    FirebaseDatabaseGetSet firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    if (firebaseDatabaseGetSet.getCompleteInformationCheck().equals("success")) {
                        txtSchoolName.setText(firebaseDatabaseGetSet.getSchoolName());
                        txtSchoolMajor.setText(firebaseDatabaseGetSet.getCourseName());
                        txtSpeciality.setText(firebaseDatabaseGetSet.getUserSpeciality());
                        txtStatusMessage.setText(firebaseDatabaseGetSet.getUserStatusMessage());
                        txtCreateDate.setText(firebaseDatabaseGetSet.getCreateDate());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void starCropping(Uri uri){
        String cropFileName = NEW_CROP_IMAGE;
        cropFileName += ".jpg";


        UCrop uCrop  = UCrop.of(uri, Uri.fromFile(new File(getContext().getCacheDir(), cropFileName)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(450, 450);
        uCrop.withOptions(getCropOthions());
        uCrop.start(getActivity(), this);
    }

    private UCrop.Options getCropOthions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.LightMainOrange));
        options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.LightMainOrange));

        options.setToolbarTitle("Crop Image");

        return options;
    }
}
