package com.example.welsenho.questfy_tw.MainUserActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
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
    private TextView txtCreateDate;
    private TextView txtSchoolName;
    private TextView txtSchoolMajor;
    private Button btnEmailCertifiacte;
    private CircleImageView circleImageView;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private EditRelatedMethod editRelatedMethod;


    private OnFragmentInteractionListener mListener;

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
        txtChangeImage = view.findViewById(R.id.user_profile_fm_txt_changeImage);
        txtUserName = view.findViewById(R.id.user_profile_fm_txt_userName);
        txtSchoolName = view.findViewById(R.id.user_profile_fm_txt_schoolName);
        txtSchoolMajor = view.findViewById(R.id.user_profile_fm_txt_schoolMajor);
        btnEmailCertifiacte = view.findViewById(R.id.user_profile_fm_btn_emailCertificate);
        circleImageView = view.findViewById(R.id.user_profile_fm_circleImage_userImage);
        progressBar = view.findViewById(R.id.user_profile_fm_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        progressDialog = new ProgressDialog(getContext());
        editRelatedMethod = new EditRelatedMethod();

        txtChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });

        LoadUserProfile();
        getFirebaseData();


        return view;
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
        void onFragmentInteraction(Uri uri);
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
                    public void onSuccess(Uri uri) {
                        Log.d("DownloadUri", uri.toString());
                        UpdateFirebaseLink(uri);
                        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                        firebaseUser.updateProfile(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Picasso.get().load(firebaseUser.getPhotoUrl()).fit().into(circleImageView);
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
            Picasso.get().load(firebaseUser.getPhotoUrl()).resize(128, 128).into(circleImageView);

        }else {
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6")
                    .resize(128, 128).into(circleImageView);
        }
        txtUserName.setText(firebaseUser.getDisplayName());
        if (firebaseUser.isEmailVerified()) {
            btnEmailCertifiacte.setText("信箱已認證\n" + firebaseUser.getEmail());
        }else {
            btnEmailCertifiacte.setText("信箱未認證(點擊認證)\n" + firebaseUser.getEmail());
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
