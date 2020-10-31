package com.mybiz.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mybiz.Objects.Comment
import com.mybiz.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

//from 90
class CommentRecyclerAdapter constructor(val ctx: Context, val list: ArrayList<Comment>) : RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.comment_layout, parent, false);
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = list[position]
        holder.reviewerName.text = currentComment.writer+" just to test"
        holder.reviewerDescription.text = currentComment.comment

        val date = currentComment.date
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        holder.reviewerDate.text = formatter.format(Date(date))

        holder.ratingBar.rating = currentComment.review.toFloat()
        Glide.with(ctx).load(currentComment.url).into(holder.profileImage)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage = itemView.findViewById<ImageView>(R.id.profile_image)
        val reviewerName = itemView.findViewById<TextView>(R.id.reviewer_name)
        val reviewerDate = itemView.findViewById<TextView>(R.id.review_date)
        val reviewerDescription = itemView.findViewById<TextView>(R.id.review_description)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar12)
    }
}