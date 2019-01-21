package com.example.welsenho.questfy_tw.MainUserActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_SELECT = 1;

    private TextView txtChangeImage;
    private TextView txtUserName;
    private TextView txtCreateDate;
    private TextView txtSchoolName;
    private TextView txtSchoolMajor;
    private Button btnEmailCertifiacte;
    private CircleImageView circleImageView;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


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
        btnEmailCertifiacte = view.findViewById(R.id.user_profile_fm_btn_emailCertificate);
        circleImageView = view.findViewById(R.id.user_profile_fm_circleImage_userImage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        progressDialog = new ProgressDialog(getContext());

        txtChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });

        LoadUserProfile();


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
                    UploadImage(data.getData());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadImage(Uri uri){
        progressDialog.setMessage("Uploading Photo...");
        progressDialog.show();
        UploadUserProfileImage(uri);
    }

    private void UploadUserProfileImage(Uri userImageuri){
        UploadTask uploadTask = storageReference.child("User_Profile_Image").child(firebaseUser.getUid()).putFile(userImageuri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference storageReference1 = storageReference.child("User_Profile_Image").child(firebaseUser.getUid());
                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("DownloadUri", uri.toString());
                        LoadUserProfile();
                        UpdateFirebaseLink(uri);
                        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                        firebaseUser.updateProfile(userProfile);
                        LoadUserProfile();
                        progressDialog.dismiss();
                    }
                });

            }
        });

    }

    private void LoadUserProfile(){
        Picasso.get().load(firebaseUser.getPhotoUrl()).resize(128, 128).into(circleImageView);
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

}
