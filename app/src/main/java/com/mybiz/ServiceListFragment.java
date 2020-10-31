package com.mybiz;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mybiz.Objects.Category;

/**
 * Created by hannashulmah on 02/02/2017.
 */

public class ServiceListFragment  extends Fragment {

    String TAG =  "tag";
    RecyclerView  serviceList;
    DatabaseReference listRef;
    public FirebaseRecyclerAdapter<Category, CategoryHolder> serviceAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getArguments().getString("ref"));
        View rootView = inflater.inflate(R.layout.service_list_fragment, container, false);

        serviceAdapter = new FirebaseRecyclerAdapter<Category, CategoryHolder>(Category.class, R.layout.category_layout, CategoryHolder.class, listRef) {
            @Override
            protected void populateViewHolder(CategoryHolder viewHolder, final Category model, int position) {
                Log.d(TAG, "populateViewHolder: " + position);


                viewHolder.text.setText(model.getTitle());
                Glide.with(getContext()).load(model.getProfileImage()).priority(Priority.IMMEDIATE).into(viewHolder.image);

                viewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getContext(), model.getTitle(), Toast.LENGTH_SHORT).show();
                        ((ServicesFragments) getActivity().getSupportFragmentManager().findFragmentByTag(TabsActivity.adapter.getItem(1).getTag())).showSingleServices(model.getTitle());
                    }
                });

                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Toast.makeText(getContext(), model.getTitle(), Toast.LENGTH_SHORT).show();
                        ((ServicesFragments) getActivity().getSupportFragmentManager().findFragmentByTag(TabsActivity.adapter.getItem(1).getTag())).showSingleServices(model.getTitle());
                    }
                });

            }
        };

        serviceList = (RecyclerView) rootView.findViewById(R.id.service_list);
        serviceList.setHasFixedSize(true);
        serviceList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        serviceList.setAdapter(serviceAdapter);
        ViewCompat.setNestedScrollingEnabled(serviceList, true);
        serviceList.setAdapter(serviceAdapter);
        return rootView;
    }




    public static class CategoryHolder extends RecyclerView.ViewHolder{

        TextView text;
        ImageView image;
        LinearLayout container;

        public CategoryHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView.findViewById(R.id.container);
            image = (ImageView) itemView.findViewById(R.id.main_category_image);
            text = (TextView) itemView.findViewById(R.id.main_category_title);
        }
    }
}
