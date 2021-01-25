package com.heshmat.mydietwatcher.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.LoadingDialog;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.heshmat.mydietwatcher.models.User.currentUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    @BindView(R.id.weightProfileInL)
    TextInputLayout weightInL;
    @BindView(R.id.heightProfileInL)
    TextInputLayout heightInL;
    @BindView(R.id.switch1)
    Switch aSwitch;
    @BindView(R.id.saveChangesBt)
    Button saveChangesBt;
    @BindView(R.id.profile_imageView)
    ImageView profileIv;
    @BindView(R.id.img_plus)
    ImageView img_plus;
    @BindView(R.id.changeInfoLL)
    LinearLayout changeInfoLL;
    @BindView(R.id.currentWeightTv)
    TextView weightTv;
    @BindView(R.id.currentHeightTv)
    TextView heightTv;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==STORAGE_PERMISSION_REQUEST&& grantResults.length>0){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(Objects.requireNonNull(this.getActivity()));

        }
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance() {

        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        Glide.with(this.getContext()).load(User.currentUser.getImgUrl()).into(profileIv);
        setUSerInfo();

        changeInfoToggle(false);
        loadingDialog = new LoadingDialog(this.getActivity());

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    changeInfoToggle(true);

                } else {
                    changeInfoToggle(false);

                }
            }
        });


        return view;

    }


    private static final int STORAGE_PERMISSION_REQUEST = 100;

    private boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    @OnClick(R.id.img_plus)
    public void plusClick(View view) {
        if (requestPermission()) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(),this);
        }


    }

    @OnClick(R.id.saveChangesBt)
    public void saveChanges(View view) {
        boolean validateWeightAndHeight=validateFields(weightInL)&&validateFields(heightInL);
        if (validateWeightAndHeight){
            currentUser.setHeight(Double.parseDouble(heightInL.getEditText().getText().toString()));
            currentUser.setWeight(Double.parseDouble(weightInL.getEditText().getText().toString()));
            currentUser.setNeededCAL();
            setUSerInfo();
            if (muri!=null){
                uploadUserPhoto(muri,currentUser.getId());
            }
            else {
                loadingDialog.startLoadingDialog();
                DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(currentUser.getId()).set(User.currentUser, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        loadingDialog.dismissDialog();

                    }
                });
            }

        }

    }

    File file;
    Uri muri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // this.getActivity();
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                muri = result.getUri();
                try {
                    file = new File(String.valueOf(resultUri));
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this.getActivity()).getContentResolver(), resultUri);
                    file.getAbsolutePath();
                    Glide.with(this).load(bitmap).into(profileIv);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void changeInfoToggle(boolean enable) {
        if (enable) {
            changeInfoLL.setVisibility(View.VISIBLE);
            saveChangesBt.setVisibility(View.VISIBLE);
            img_plus.setVisibility(View.VISIBLE);

        } else {
            changeInfoLL.setVisibility(View.GONE);
            saveChangesBt.setVisibility(View.GONE);
            img_plus.setVisibility(View.GONE);
        }

    }
     LoadingDialog loadingDialog;

    private void uploadUserPhoto(Uri image, String id) {
        loadingDialog.startLoadingDialog();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference storageReference = storageRef.child("profileImages/" + StaticFields.CLIENTS + "/" + id + "/" + id);
        UploadTask uploadTask = storageReference.putFile(image);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {


            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                loadingDialog.dismissDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User.currentUser.setImgUrl(uri.toString());
                        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(id).set(User.currentUser, SetOptions.merge());
                    }
                });

                loadingDialog.dismissDialog();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();

            }
        });


    }
    public boolean validateFields(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.required_field));

            return false;
        }
        textInputLayout.setError(null);
        return true;
    }
    public void setUSerInfo(){
        weightTv.setText(getString(R.string.w_h, User.currentUser.getWeight(), " g"));
        heightTv.setText(getString(R.string.w_h, User.currentUser.getHeight(), " cm"));
    }
}
