package com.mybiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mybiz.tests.FixOrientation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by hannashulmah on 28/12/2016.
 */

public class ServiceProfileImageFragment extends Fragment implements View.OnClickListener {
    Button takePic, choosePic;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int CHOOSE_IMAGE_GALLARY = 222;
    String TAG = "serRegFrg5";
    String imageFileName = "profile.jpg";
    Bitmap imageBitmap;
    ImageView profilePic, profileImage;
    ImageView imageView;
    private File baseFile;
    private String fileName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_registration_fragment_five, container, false);
        takePic = (Button) rootView.findViewById(R.id.take_pic_btn);
        takePic.setOnClickListener(this);
        choosePic = (Button) rootView.findViewById(R.id.gallary_pic_btn);
        choosePic.setOnClickListener(this);
        imageView = (ImageView) rootView.findViewById(R.id.image_view);
        profilePic = (ImageView) rootView.findViewById(R.id.profilePic);
        profileImage = (ImageView) rootView.findViewById(R.id.profileImage);
        ServiceRegistrationFragmentContainer.allowToSaveInfo(false);

        File myProfileImage = new File(getApplicationContext().getFilesDir()+"/"+imageFileName);
        if (myProfileImage.exists()){
            loadImageFromStorage(profileImage, imageFileName);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.take_pic_btn:
                Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePic.resolveActivity(getActivity().getPackageManager()) != null) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1121);
                    } else {
                        File f = baseFile = new File(getActivity().getExternalFilesDir(null), "tmp");
                        if(! f.exists())
                            f.mkdir();
                        takePic.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(f, fileName = System.currentTimeMillis()+".jpg")));

                        startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;

            case R.id.imageView:
                Intent takePic1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePic1.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePic1, REQUEST_IMAGE_CAPTURE);
                }
                break;

            case R.id.profilePic:
                Intent takePic2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePic2.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePic2, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case R.id.gallary_pic_btn:
                Intent choosPic = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (choosPic.resolveActivity(getActivity().getPackageManager()) != null) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1121);
                    }else
                        startActivityForResult(choosPic, CHOOSE_IMAGE_GALLARY);
                }
                break;
            default:

                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            if(fileName != null && new File(baseFile, fileName).exists()){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(new File(baseFile, fileName).getPath(), options);

                int s = FixOrientation.calculateInSampleSize(options, 512, 330);
                options.inJustDecodeBounds = false;
                options.inSampleSize = s;

                imageBitmap = BitmapFactory.decodeFile(new File(baseFile, fileName).getPath(), options);
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(new File(baseFile, fileName).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        imageBitmap = rotateImage(imageBitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        imageBitmap =  rotateImage(imageBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        imageBitmap = rotateImage(imageBitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:

                    default:
                        break;
                }
                profileImage.setImageBitmap(imageBitmap);
                ServiceRegistrationFragmentContainer.allowToSaveInfo(true);
            }
        }
        if (requestCode == CHOOSE_IMAGE_GALLARY && resultCode == getActivity().RESULT_OK) {
            Uri selectedImage = data.getData();
            //test itzik
            // Get selected gallery image
            Uri selectedPicture = data.getData();
            // Get and resize profile image
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 4;
//            options.inJustDecodeBounds = false;


            Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            int s = FixOrientation.calculateInSampleSize(options, 512, 330);
            options.inJustDecodeBounds = false;
            options.inSampleSize = s;

            imageBitmap = BitmapFactory.decodeFile(picturePath, options);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(picturePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageBitmap = rotateImage(imageBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageBitmap =  rotateImage(imageBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageBitmap = rotateImage(imageBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }
            profileImage.setImageBitmap(imageBitmap);
            ServiceRegistrationFragmentContainer.allowToSaveInfo(true);


//            Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath, options);
//            Log.d(TAG, "onActivityResult: " + loadedBitmap.getWidth());
//            Log.d(TAG, "onActivityResult: " + loadedBitmap.getHeight());
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            BitmapFactory.decodeFile(picturePath, options);
//            int samSize = FixOrientation.calculateInSampleSize(options, 512, 384);


//            try {
//                imageBitmap =  com.mybiz.tests.FixOrientation.modifyOrientation(loadedBitmap, picturePath);
//                imageBitmap = loadedBitmap;
//                profileImage.setImageBitmap(imageBitmap);
//                ServiceRegistrationFragmentContainer.allowToSaveInfo(true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            imageBitmap = null;
//            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//                return;
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            profilePic.setImageBitmap(imageBitmap);
//            ServiceRegistrationFragmentContainer.allowToSaveInfo(true);

        }

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }




    @Override
    public void onPause() {
        if (ServiceRegistrationFragmentContainer.saveVsBack==0){

        }else {
            if (imageBitmap != null) {
                saveToInternalStorage(imageBitmap);
                loadImageFromStorage(profileImage, getApplicationContext().getFilesDir()+"/profile");
                //if isEdit, need to update link to all places
            }
        }

        super.onPause();
    }

//    @Override
//    public void onStop() {
//
//        if (ServiceRegistrationFragmentContainer.saveVsBack==0){
//
//        }else {
//            if (imageBitmap != null) {
//                saveToInternalStorage(imageBitmap);
//                loadImageFromStorage(profileImage, getApplicationContext().getFilesDir()+"/"+imageFileName);
//            }
//        }
//
//        super.onStop();
//    }



    private void saveToInternalStorage(Bitmap bitmapImage){

        File directory=new File(getApplicationContext().getFilesDir(), "profile.jpg");
        Log.d(TAG, "saveToInternalStorage1: "+directory.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(directory);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            File f = new File(getApplicationContext().getFilesDir(), "profile.jpg");
            Log.d(TAG, "saveToInternalStorage: afterSave: "+f.length());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        if (getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(Constants.IS_SERVICE, false)){
//            //upload image to service
//            uploadFile(BitmapFactory.decodeFile(directory.getPath()));
//        }
    }

    private void loadImageFromStorage(ImageView imgView, String path)
    {
        Log.d(TAG, "loadImageFromStorage: "+ path);
        File f=new File(path);
        Bitmap b1 = BitmapFactory.decodeFile(getApplicationContext().getFilesDir()+"/profile.jpg");
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgView.getLayoutParams();
//        params.width =
        imgView.setImageBitmap(b1);

    }




    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mybizz-3bbe5.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                Uri downloadUrl = taskSnapshot.getUploadSessionUri();//getDownloadUrl();
                ServiceRegistrationActivityForm.newService.setProfileUrl(downloadUrl.toString());
//                sendMsg("" + downloadUrl, 2);
                Log.d(TAG , "downloadUrl-->" + downloadUrl);
                final HashMap<String, Object> updateProfileUrl = new HashMap();
                updateProfileUrl.put("profileUrl", downloadUrl.toString());
                //set the profile url ini all needed places
                //1. all users -->privateData
                //2. all users -->privateData -->services-->pictureUrl
                //3. all users -->publicData
                //4. all users -->publicData -->services-->pictureUrl
                //5. services -->privateData -->serviceKey --> profile url
                //6. services -->publicData -->serviceKey --> profile url
                String serviceUid = ServiceRegistrationActivityForm.newService.getUserUid();
                String serviceKey = ServiceRegistrationActivityForm.newService.getKey();
                final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(serviceUid);
                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(serviceUid).child("services").child(serviceKey);
                final DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(serviceUid);
                final DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(serviceUid).child("services").child(serviceKey);
                final DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData").child(serviceKey);
                final DatabaseReference ref6 = FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(serviceKey);


                //must check if service has been saved already, only if so the following fields mudt be updated, o/w, should not.
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            //if saved already in private data, update url in private data
                            ref1.updateChildren(updateProfileUrl);
                            ref2.updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                ref4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            //if saved already in private data, update url in private data
                            ref3.updateChildren(updateProfileUrl);
                            ref4.updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                ref5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                ref6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

            }
        });
    }}

