package com.example.geobuddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class FAQ(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)

class FAQActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var faqAdapter: FAQAdapter
    private lateinit var faqList: List<FAQ>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        faqList = listOf(
            FAQ(
                "How do I pair my tracker with the app?",
                "Enter the imei number found on the tracker in the app and it will automatically connect."
            ),
            FAQ(
                "Can I track multiple devices at once?",
                "Yes, you can add and monitor multiple devices from the Dashboard."
            ),
            FAQ(
                "What happens if the device goes offline?",
                "You’ll get a last known location and a notification about disconnection."
            ),
            FAQ(
                "Is my data secure?",
                "Yes, all user data is encrypted and securely stored on our servers."
            ),
            FAQ(
                "How do I reset my password?",
                "Go to Profile > Modify Password to reset your current password."
            ),
            FAQ(
                "Does the app work without internet?",
                "Partial features work offline, but live tracking requires an active connection."
            ),
            FAQ(
                "Can I share tracking access with someone else?",
                "Yes, use the 'Share Access' feature in settings to invite others."
            ),
            FAQ(
                "What is the range of the tracker?",
                "The range depends on GPS signal and connectivity, but typically covers a large area."
            ),
            FAQ(
                "How often is location updated?",
                "Location updates every 10 seconds by default. You can change it in Map Settings."
            ),
            FAQ(
                "How do I delete my account?",
                "Go to Profile > Delete Account to remove your profile and data."
            )
        )

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        faqAdapter = FAQAdapter(faqList)
        recyclerView.adapter = faqAdapter
    }

    class FAQAdapter(private val faqList: List<FAQ>) :
        RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

        inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val questionTextView: TextView = itemView.findViewById(R.id.questionText)
            val answerText: TextView = itemView.findViewById(R.id.answerText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
            return FAQViewHolder(view)
        }

        override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
            val item = faqList[position]
            holder.questionTextView.text = item.question
            holder.answerText.text = item.answer
            holder.answerText.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            holder.questionTextView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(position)
            }
        }

        override fun getItemCount() = faqList.size
    }

}
