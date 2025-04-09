package com.example.geobuddy
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FAQActivity : AppCompatActivity() {

    data class FAQ(
        val question: String,
        val answer: String,
        var isExpanded: Boolean = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val faqList = listOf(
            FAQ("How do I pair my tracker with the app?", "Go to Bluetooth settings and ensure the tracker is on. The app will auto-detect and pair it."),
            FAQ("Can I track multiple devices at once?", "Yes, you can add and monitor multiple devices from the Dashboard."),
            FAQ("What happens if the device goes offline?", "You’ll get a last known location and a notification about disconnection."),
            FAQ("Is my data secure?", "Yes, all user data is encrypted and securely stored on our servers."),
            FAQ("How do I reset my password?", "Go to Profile > Modify Password to reset your current password."),
            FAQ("Does the app work without internet?", "Partial features work offline, but live tracking requires an active connection."),
            FAQ("Can I share tracking access with someone else?", "Yes, use the 'Share Access' feature in settings to invite others."),
            FAQ("What is the range of the tracker?", "The range depends on GPS signal and connectivity, but typically covers a large area."),
            FAQ("How often is location updated?", "Location updates every 10 seconds by default. You can change it in Map Settings."),
            FAQ("How do I delete my account?", "Go to Profile > Delete Account to remove your profile and data.")
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FAQAdapter(faqList)
    }

    private class FAQAdapter(private val faqList: List<FAQ>) :
        RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

        inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val questionText: TextView = itemView.findViewById(R.id.questionText)
            val answerText: TextView = itemView.findViewById(R.id.answerText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_faq, parent, false)
            return FAQViewHolder(view)
        }

        override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
            val faq = faqList[position]
            holder.questionText.text = faq.question
            holder.answerText.text = faq.answer
            holder.answerText.visibility = if (faq.isExpanded) View.VISIBLE else View.GONE

            holder.itemView.setOnClickListener {
                faq.isExpanded = !faq.isExpanded
                notifyItemChanged(position)
            }
        }

        override fun getItemCount(): Int = faqList.size
    }
}

