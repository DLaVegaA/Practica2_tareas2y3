// Archivo: CategoryFilterAdapter.kt
package com.calac.tracklim // Asegúrate que sea tu paquete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

// Un data class simple para guardar la información de cada filtro
data class CategoryFilter(val category: Category, val logoResId: Int)

class CategoryFilterAdapter(
    private val filterList: List<CategoryFilter>,
    private val listener: OnCategoryFilterClickListener
) : RecyclerView.Adapter<CategoryFilterAdapter.FilterViewHolder>() {

    interface OnCategoryFilterClickListener {
        fun onCategoryClick(category: Category)
    }

    // Esta clase interna representa la vista de un solo item (nuestro CardView)
    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logoImage: ImageView = itemView.findViewById(R.id.category_logo)
    }

    // Crea una nueva vista para un item (llamado por el RecyclerView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_filter, parent, false)
        return FilterViewHolder(view)
    }

    // Devuelve el número total de items en la lista
    override fun getItemCount(): Int {
        return filterList.size
    }

    // Conecta los datos de un item con su vista (llamado por el RecyclerView)
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filterList[position]
        holder.logoImage.setImageResource(filter.logoResId)

        // Aquí podrías añadir un OnClickListener si quieres que los filtros hagan algo
        holder.itemView.setOnClickListener {
            listener.onCategoryClick(filter.category)
        }
    }
}