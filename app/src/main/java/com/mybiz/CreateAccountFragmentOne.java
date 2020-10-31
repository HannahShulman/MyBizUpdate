package com.mybiz;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by hannashulmah on 09/02/2017.
 */
public class CreateAccountFragmentOne extends Fragment {
    AppCompatActivity activity;
    private FirebaseAuth mAuth;
    String TAG = "createAccountAnon";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_account_fragment_one, container, false);
        mAuth = FirebaseAuth.getInstance();
//        TextView skip = (TextView) rootView.findViewById(R.id.skip);
//        skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((CreateAccountChoiceActivity)getActivity()).pb.setVisibility(View.VISIBLE);
//                mAuth.signInAnonymously()
//                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
//
//                                // If sign in fails, display a message to the user. If sign in succeeds
//                                // the auth state listener will be notified and logic to handle the
//                                // signed in user can be handled in the listener.
//                                if (!task.isSuccessful()) {
//                                    Log.w(TAG, "signInAnonymously", task.getException());
//                                    Toast.makeText(getActivity(), "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                                else if (task.isSuccessful()){
//                                    Log.d(TAG, "onComplete: "+task.getResult().getUser().getUid());
//                                    Log.d(TAG, "onComplete: "+task.getResult().getUser().getEmail());
//                                    Log.d(TAG, "onComplete: "+task.getResult().getUser().getDisplayName());
//                                    Log.d(TAG, "onComplete: "+task.getResult().getUser().getProviderId());
//                                    getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit()
//                                            .putString(Constants.APP_ID, task.getResult().getUser().getUid())
//                                            .putString(Constants.NAME, getResources().getString(R.string.guest_account))
////                                            .putString)
//                                    .commit();
//                                }
//                                Intent intent = new Intent(getActivity(), TabsActivity.class);
//                                startActivity(intent);
//                                ((CreateAccountChoiceActivity)getActivity()).pb.setVisibility(View.GONE);
//                                getActivity().finish();
//                            }
//                        });
//            }
//        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
