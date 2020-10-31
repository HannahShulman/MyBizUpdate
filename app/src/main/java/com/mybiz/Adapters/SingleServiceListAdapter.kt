package com.mybiz.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mybiz.Constants
import com.mybiz.Objects.Service
import com.mybiz.R

//from 315
class SingleServiceListAdapter constructor(val ctx: Context, val list: ArrayList<Service>) : RecyclerView.Adapter<SingleServiceListAdapter.ViewHolder>() {

    var isAnonymous: Boolean = false

    init {
        isAnonymous = ctx.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(Constants.IS_ANONYMOUS, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.single_service_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentService = list[position]
        Glide.with(ctx).load(currentService.profileUrl).into(holder.image)

        holder.serviceTitle.text = currentService.title
        val address = "${currentService.address} ${currentService.serviceHomeNumber ?: ""} ${currentService.town}"
        //TODO("Bold town name")
        holder.serviceAddress.text = address
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.single_profile_image)
        val favoriteIcon = itemView.findViewById<ImageView>(R.id.favorite_icon)
        val shareService = itemView.findViewById<ImageView>(R.id.share)
        val sales = itemView.findViewById<ImageView>(R.id.sales)
        val ratingBar = itemView.findViewById<AppCompatRatingBar>(R.id.ratingBar)

        val chatLayout = itemView.findViewById<LinearLayout>(R.id.chat_layout)
        val additionalInfo = itemView.findViewById<LinearLayout>(R.id.additional_info)
        val offersLayout = itemView.findViewById<LinearLayout>(R.id.offers_layout)

        val topLayout = itemView.findViewById<View>(R.id.top_layout)

        val serviceTitle = itemView.findViewById<TextView>(R.id.service_title)
        val serviceAddress = itemView.findViewById<TextView>(R.id.service_address)
        val serviceDescription = itemView.findViewById<TextView>(R.id.service_description)
        val numReviews = itemView.findViewById<TextView>(R.id.num_reviews)
        val avgReviewers = itemView.findViewById<TextView>(R.id.avrg_reviewers)
        val salesText = itemView.findViewById<TextView>(R.id.sales_tv)
    }
}