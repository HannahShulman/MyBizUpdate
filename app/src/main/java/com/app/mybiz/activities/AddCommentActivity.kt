package com.app.mybiz.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.app.mybiz.Objects.Comment
import com.app.mybiz.Objects.Service
import com.app.mybiz.PreferenceKeys
import com.app.mybiz.R
import com.app.mybiz.extensions.setupToolbar
import kotlinx.android.synthetic.main.activity_add_comment.*

//frorm 189
class AddCommentActivity : AppCompatActivity() {

    val prevRating: Int by lazy {
        if (intent.getBooleanExtra("isEdit", false)) {
            intent.getIntExtra("ratingAmount", 0)
        } else 0

    }

    val mServiceRootRef = FirebaseDatabase.getInstance().getReference().child("Services");
    val service: Service by lazy {
        intent.getSerializableExtra("currentService") as Service
    }

    lateinit var serviceRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_comment)
        setupToolbar()

        vote_title.text = service.title
        toolbar.title = service.title
        serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PublicData/" + service.key)
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().currentUser?.uid
            ?: "").child("profileUrl").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value as String
                Glide.with(baseContext).load(value).into(circleImageView)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        my_comment.hint = resources.getString(R.string.describe_experience) + " " + service.title
        my_comment.setText(intent?.getStringExtra("myReview") ?: "")

        vote_name.text = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.NAME, PreferenceKeys.RANDOM_STRING)
        ratingBar.rating = 1
        ratingBar.rating = intent?.getIntExtra("ratingAmount", 0)?.minus(1) ?: 0

        vote.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("AllUsers").child("PrivateData").child(getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING)!!).child("profileUrl").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userUrl = snapshot.value as String
                    val comment = Comment().apply {
                        writer = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.NAME, PreferenceKeys.RANDOM_STRING)
                        writerUid = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING)
                        date = System.currentTimeMillis()
                        review = ratingBar.rating.plus(1)
                        comment = my_comment.text.toString()
                        url = userUrl
                    }
                    serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PublicData/" + service.getKey() + "/reviews")
                    serviceRef.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(comment)

                    val numberOfComments = service.noReviewers.toFloat()
                    var averageComments = 0.0
                    if (intent?.getBooleanExtra("isEdit", false) == true) {
                        //do not add user, but update average
                        averageComments = service.getAverageRating().toDouble()
                        val newAverage: Double = (numberOfComments * averageComments - prevRating + (ratingBar.rating + 1)) / numberOfComments
                        mServiceRootRef.child("PrivateData").child(service.getKey()).child("averageRating").setValue(newAverage)
                        mServiceRootRef.child("PublicData").child(service.getKey()).child("averageRating").setValue(newAverage)
                    } else {
                        //add user
                        averageComments = service.getAverageRating().toDouble()
                        val newAverage: Double = (numberOfComments * averageComments + (ratingBar.rating + 1)) / (numberOfComments + 1)
                        mServiceRootRef.child("PrivateData").child(service.getKey()).child("averageRating").setValue(newAverage)
                        mServiceRootRef.child("PrivateData").child(service.getKey()).child("noReviewers").setValue(numberOfComments + 1)
                        mServiceRootRef.child("PublicData").child(service.getKey()).child("averageRating").setValue(newAverage)
                        mServiceRootRef.child("PublicData").child(service.getKey()).child("noReviewers").setValue(numberOfComments + 1)
                    }
                    serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PrivateData/" + service.getKey() + "/reviews")
                    val serviceRefPublic = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PrivateData/" + service.getKey() + "/reviews")
                    serviceRef.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(comment)
                    serviceRefPublic.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(comment)
                    val intent = Intent(this@AddCommentActivity, AllServiceInfo::class.java)
                    intent.putExtra("currentService", service)
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}