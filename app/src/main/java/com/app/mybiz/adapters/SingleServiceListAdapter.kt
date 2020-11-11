package com.app.mybiz.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.mybiz.ChatActivity
import com.app.mybiz.CreateAccountChoiceActivity
import com.app.mybiz.Managers.FavoriteServiceManager
import com.app.mybiz.objects.Service
import com.app.mybiz.objects.User
import com.app.mybiz.PreferenceKeys
import com.app.mybiz.R
import com.app.mybiz.activities.AllServiceInfo
import com.app.mybiz.activities.ServiceOffersList
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat

//from 315
class SingleServiceListAdapter constructor(val ctx: Context, val list: ArrayList<Service>) : RecyclerView.Adapter<SingleServiceListAdapter.ViewHolder>() {

    var isAnonymous: Boolean = false

    init {
        isAnonymous = ctx.getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PreferenceKeys.IS_ANONYMOUS, false)
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
        holder.serviceAddress.text = address
        holder.serviceDescription.text = currentService.shortDescription

        if (currentService.getAverageRating() > 0) {
            holder.numReviews.text = "(${currentService.getAverageRating()})"
            holder.avgReviewers.text = DecimalFormat("##.#").format(currentService.getAverageRating())
        }
        holder.numReviews.isVisible = currentService.getAverageRating() > 0
        holder.avgReviewers.isVisible = currentService.getAverageRating() > 0

        if (currentService.isUserService(holder.itemView.context)) {
            holder.chatLayout.alpha = .12f
            holder.chatLayout.isEnabled = false
            holder.favoriteIcon.visibility = View.GONE
        } else {
            holder.chatLayout.alpha = .80f
            holder.chatLayout.isEnabled = true
            holder.favoriteIcon.visibility = View.VISIBLE
        }

        if (currentService.getOffers().size == 0) {
            holder.offersLayout.isEnabled = false
            holder.offersLayout.alpha = .20f
        } else {
            holder.sales.setColorFilter(Color.RED)
            holder.salesText.setTextColor(Color.RED)
            holder.offersLayout.isEnabled = true
            holder.offersLayout.alpha = 1.0f
        }

        holder.chatLayout.setOnClickListener {
            val context = holder.itemView.context
            if (!currentService.isUserService(context)) {
                goToChat(context, currentService)
            }
        }

        holder.additionalInfo.setOnClickListener { showAllInfo(currentService) }
        holder.offersLayout.setOnClickListener { showOffers(currentService) }


        //adding to favorite list
        holder.favoriteIcon.setOnClickListener {
            if (!isAnonymous) {
                val myID = ctx.getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING)
                val serviceKey = list[position].getKey()
                //1.
                FirebaseDatabase.getInstance().reference.child("AllUsers").child("PublicData")
                        .child(myID!!).child("favorites").child(serviceKey).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    FavoriteServiceManager.removeToFavoriteList(serviceKey, myID)
                                    dataSnapshot.ref.removeEventListener(this)
                                } else {
                                    val service = list[position]
                                    FavoriteServiceManager.addToFavoriteList(service, serviceKey, myID)
                                    dataSnapshot.ref.removeEventListener(this)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
            } else {
                if (isAnonymous || ctx.getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING) == PreferenceKeys.RANDOM_STRING) {
                    val builder = AlertDialog.Builder(ctx)
                    builder.setMessage(ctx.getString(R.string.register_for_favorites))
                    builder.setCancelable(true)
                    builder.setPositiveButton(ctx.getString(R.string.register)) { dialog, which ->
                        val intent = Intent(ctx, CreateAccountChoiceActivity::class.java)
                        ctx.startActivity(intent)
                    }.setNegativeButton(ctx.getString(R.string.cancel)) { dialog, which -> }
                    builder.setCancelable(false)
                    builder.create().show()
                }
            }
        }

        holder.shareService.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, ctx.resources.getString(R.string.get_my_service) + "https://mybizz.application.to/allInfo_" + list[position].getKey() + "  "
                    + ctx.resources.getString(R.string.download_app) + " https://play.google.com/store/apps/details?id=com.app.mybiz"
            )
            //                intent.putExtra(Intent.EXTRA_TEXT, "https://mybizz.application.to/allInfo_" + list.get(position).getKey());
            intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!")
            ctx.startActivity(Intent.createChooser(intent, "Share"))
            //                MyApplication.shareService( list.get(position).getKey());
        }

        val averageRating: Float = currentService.getAverageRating()
        holder.ratingBar.stepSize = 0.5f
        holder.ratingBar.rating = averageRating


        if (!isAnonymous) {
            val selfRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + FirebaseAuth.getInstance().currentUser!!.uid)
            //decide which color to use for icon
            selfRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val u = dataSnapshot.getValue(User::class.java)
                    val map = u!!.getFavorites()
                    if (map[currentService.getKey()] != null) {
                        holder.favoriteIcon.isSelected = true
                        holder.favoriteIcon.setColorFilter(Color.RED) //
                    } else {
                        holder.favoriteIcon.isSelected = false
                        holder.favoriteIcon.setColorFilter(Color.BLACK)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    private fun goToChat(ctx: Context, service: Service) {
        Intent(ctx, ChatActivity::class.java).apply {
            putExtra("otherID", service.getUserUid())
            putExtra("otherName", service.getTitle())
            putExtra("isService", true);
            putExtra("currentService", service);
            flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            ctx.startActivity(this);
        }
    }

    fun showAllInfo(service: Service?) {
        val allInfo = Intent(ctx, AllServiceInfo::class.java)
        allInfo.putExtra("currentService", service)
        allInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(allInfo)
    }

    fun showOffers(service: Service) {
        val i = Intent(ctx, ServiceOffersList::class.java)
        i.putExtra("currentService", service)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    }

    private fun Service.isUserService(ctx: Context): Boolean {
        return getUserUid() == ctx.getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE)
                .getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING)
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