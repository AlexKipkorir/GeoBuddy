package com.example.geobuddy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geobuddy.models.Tracker
import com.example.geobuddy.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TrackersViewActivity : AppCompatActivity() {

    private lateinit var trackerRecyclerView: RecyclerView
    private val trackerList = mutableListOf<Tracker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker_view)

        trackerRecyclerView = findViewById(R.id.recyclerViewTrackers)
        trackerRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TrackerAdapter(trackerList)
        trackerRecyclerView.adapter = adapter

        loadTrackers(adapter)
    }
    private fun loadTrackers(adapter: TrackerAdapter) {
        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", "") ?: ""

        val authHeader = "Bearer $token"

        val call = RetrofitClient.retrofitService.getTrackers(authHeader)
        call.enqueue(object : Callback<List<Tracker>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<Tracker>>, response: Response<List<Tracker>>) {
                if (response.isSuccessful && response.body() != null) {
                    trackerList.clear()
                    trackerList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@TrackersViewActivity, "Failed to load trackers", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Tracker>>, t: Throwable) {
                Toast.makeText(this@TrackersViewActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class TrackerAdapter(private val trackers: List<Tracker>) :
        RecyclerView.Adapter<TrackerAdapter.TrackerViewHolder>() {

        private val expandedState = mutableMapOf<Int, Boolean>()

        inner class TrackerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById<TextView>(R.id.trackerName)
            val container: LinearLayout = view.findViewById<LinearLayout>(R.id.expandedLayout)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tracker, parent, false)
            return TrackerViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: TrackerViewHolder, position: Int) {
            val tracker = trackers[position]
            holder.name.text = tracker.trackerName

            val isExpanded = expandedState[position] == true
            holder.container.visibility = if (isExpanded) View.VISIBLE else View.GONE

            holder.itemView.setOnClickListener {
                expandedState[position] = !isExpanded
                notifyItemChanged(position)
            }

            if (isExpanded) {
                holder.container.removeAllViews()
                val context = holder.container.context
                holder.container.addView(TextView(context).apply { text = "Imei: ${tracker.imei}" })
                holder.container.addView(TextView(context).apply { text = "Name: ${tracker.trackerName}" })
                holder.container.addView(TextView(context).apply { text = "Category: ${tracker.type}" })
                holder.container.addView(TextView(context).apply { text = "Description: ${tracker.description}" })
                val age = tracker.age
                if (age != null) {
                    holder.container.addView(TextView(context).apply { text = "Age: $age" })
                }
                val color = tracker.color
                if (color != null) {
                    holder.container.addView(TextView(context).apply { text = "Color: $color" })
                }
                val breed = tracker.breed
                if (breed != null) {
                    holder.container.addView(TextView(context).apply { text = "Breed: $breed" })
                }
                holder.container.addView(TextView(context).apply { text = "Status: ${tracker.status}" })
                holder.container.addView(TextView(context).apply { text = "Battery: ${tracker.batteryCapacity}" })
                holder.container.addView(TextView(context).apply { text = "Latitude: ${tracker.latitude}" })
                holder.container.addView(TextView(context).apply { text = "Longitude: ${tracker.longitude}" })
                holder.container.addView(TextView(context).apply { text = "Date Added: ${tracker.timestamp}" })
            }
        }
        override fun getItemCount(): Int = trackers.size
    }
}