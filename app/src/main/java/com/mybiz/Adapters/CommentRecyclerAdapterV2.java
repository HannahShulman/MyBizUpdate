//package com.mybiz.Adapters;
//
//import android.content.Context;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.mybiz.Objects.Comment;
//import com.mybiz.R;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.TimeZone;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
///**
// * Created by hannashulmah on 05/03/2017.
// */
//
//public class CommentRecyclerAdapterV2 extends RecyclerView.Adapter<CommentRecyclerAdapterV2.ViewHolder> {
//
//    String TAG = "lll adapter";
//    Context ctx;
//    ArrayList<Comment> list;
//
//
//    public CommentRecyclerAdapterV2() {
//    }
//
//    public CommentRecyclerAdapterV2(Context ctx, ArrayList<Comment> list) {
//        this.ctx = ctx;
//        this.list = list;
//
//    };
//
//    @Override
//    public CommentRecyclerAdapterV2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        CommentRecyclerAdapterV2.ViewHolder holder = null;
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View v1 = inflater.inflate(R.layout.comment_layout, parent, false);
//        holder = new CommentRecyclerAdapterV2.ViewHolder(v1);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        Comment comment = list.get(position);
//        holder.reviewerName.setText(comment.getWriter());
//        holder.reviewDescription.setText(comment.getComment());
//        long date = comment.getDate();
//
//        SimpleDateFormat formatterTime = new SimpleDateFormat("dd/MM/yyyy");
//        formatterTime.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String timeString = formatterTime.format(new Date(date));
//
//        holder.reviewDate.setText(timeString);
//        holder.ratingBar.setRating(comment.getReview());
//        Glide.with(ctx).load(comment.getUrl()).into(holder.profileImage);
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        CircleImageView profileImage;
//        TextView reviewerName, reviewDate, reviewDescription;
//        RatingBar ratingBar;
//
//        public ViewHolder(View convertView) {
//            super(convertView);
//            reviewerName = (TextView) convertView.findViewById(R.id.reviewer_name);
//            profileImage = (CircleImageView) convertView.findViewById(R.id.profile);
//            reviewDate = (TextView) convertView.findViewById(R.id.review_date);
//            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar12);
//            reviewDescription = (TextView) convertView.findViewById(R.id.review_description);
//
//        }
//    }
//}
