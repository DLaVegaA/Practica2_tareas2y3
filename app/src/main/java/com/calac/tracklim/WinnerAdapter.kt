// Archivo: WinnerAdapter.kt
package com.calac.tracklim

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WinnerAdapter(private val winners: List<Winner>) : RecyclerView.Adapter<WinnerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val yearTextView: TextView = view.findViewById(R.id.winner_year)
        val driverTextView: TextView = view.findViewById(R.id.winner_driver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_winner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val winner = winners[position]
        holder.yearTextView.text = winner.year.toString()
        holder.driverTextView.text = winner.driver
    }

    override fun getItemCount() = winners.size
}