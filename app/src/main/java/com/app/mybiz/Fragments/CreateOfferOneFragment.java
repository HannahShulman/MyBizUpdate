package com.app.mybiz.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mybiz.CreateSpecial;
import com.app.mybiz.PreferenceKeys;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.app.mybiz.Interface.RequiredFields;
import com.app.mybiz.R;
import com.app.mybiz.tests.FixOrientation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CreateOfferOneFragment extends Fragment  implements RequiredFields, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;
    EditText special_title, offer_description;
    Spinner expiry_day;
    ImageView  image_button;
    TextView title_counter, text_counter;
    private File baseFile;
    private String fileName;
    Bitmap imageBitmap;
    de.hdodenhof.circleimageview.CircleImageView  image_view;

    String TAG = "CreateOfferOneFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_create_offer_one_fragment, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        title_counter = (TextView) rootView.findViewById(R.id.title_counter);
        text_counter = (TextView) rootView.findViewById(R.id.text_counter);
        special_title = (EditText) rootView.findViewById(R.id.special_title);
        expiry_day = (Spinner) rootView.findViewById(R.id.expiry_day);
        String[] days = getResources().getStringArray(R.array.days);
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_list_item_1,days);
        expiry_day.setAdapter(daysAdapter);
        expiry_day.setOnItemSelectedListener(this);
        image_button = (ImageView) rootView.findViewById(R.id.image_button);
        image_button.setOnClickListener(this);
        image_view = (de.hdodenhof.circleimageview.CircleImageView) rootView.findViewById(R.id.image_view);



        special_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=25)
                    title_counter.setText(s.length()+"/25");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        offer_description = (EditText) rootView.findViewById(R.id.offer_description);
        offer_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=135)
                    text_counter.setText(s.length()+"/135");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public boolean isComplete() {
        Log.d(TAG, "isComplete: "+ CreateSpecial.offers.getImageUrl()+"");
        if ((CreateSpecial.offers.getImageUrl().equals("")||CreateSpecial.offers.getImageUrl()==null))
            return false;
        return true;
    }

    @Override
    public boolean toSave() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case  R.id.image_button:
            Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePic.resolveActivity(getActivity().getPackageManager())!=null){
                //if granted permission
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(getContext(), "you have no granted permission for camara", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                }else {
                    File f = baseFile = new File(getActivity().getExternalFilesDir(null), "tmp");
                        if(! f.exists())
                            f.mkdir();
                        takePic.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(f, fileName = System.currentTimeMillis()+".jpg")));

                    startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            image_button.setVisibility(View.GONE);
//
//
//            //store in memory
//            saveToInternalStorage(imageBitmap);
//            loadImageFromStorage(image_button , getActivity().getFilesDir()+"/offerImage.jpg");
//            image_view.setBackgroundColor(Color.RED);
////          image_view.setImageBitmap(imageBitmap);
//            uploadFile(imageBitmap);
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
                image_view.setImageBitmap(imageBitmap);
            }

        }else{
            if(resultCode == getActivity().RESULT_CANCELED){

            }else
            if (resultCode == getActivity().RESULT_FIRST_USER){

            }
        }
        if (requestCode==MY_PERMISSIONS_REQUEST_READ_CONTACTS && resultCode == getActivity().RESULT_OK){
            //if permission granted
            image_button.performClick();

        }
    }


    private void saveToInternalStorage(Bitmap bitmapImage){

        File directory=new File(getApplicationContext().getFilesDir(), "offerImage.jpg");
        Log.d(TAG, "saveToInternalStorage1: "+directory.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(directory);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImageFromStorage(ImageView imgView, String path)
    {
        Log.d(TAG, "loadImageFromStorage: "+ path);
        File f=new File(path);
        Bitmap b1 = BitmapFactory.decodeFile(getApplicationContext().getFilesDir()+"/profile.jpg");
        imgView.setImageBitmap(b1);

    }

    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mybizz-3bbe5.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images2/" + System.currentTimeMillis() + ".png");
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
                Uri uri = taskSnapshot.getUploadSessionUri();//.getDownloadUrl();
                Log.d("downloadUrl-->sent", "" );
                //set url for offer url
                CreateSpecial.offers.setImageUrl(uri.toString());
                Glide.with(getContext()).load(uri).into(image_view);


            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        CreateSpecial.offers.setOfferTitle(special_title.getText().toString());
        CreateSpecial.offers.setDescription(offer_description.getText().toString());
        CreateSpecial.offers.setServiceKey(getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING));
        CreateSpecial.offers.setServiceUid(getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING));




    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CreateSpecial.offers.setExpiryDate(System.currentTimeMillis()+((position+1)*86400000));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        CreateSpecial.offers.setExpiryDate(System.currentTimeMillis()+((4)*86400000));

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


}
