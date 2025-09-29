package com.calac.tracklim

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WinnersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_winners)

        // --- CÓDIGO ACTUALIZADO ---
        // Controlamos el nuevo botón de regreso
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Cierra la actividad
        }

        // Buscamos el nuevo TextView del título
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)

        val circuit: Circuit? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("circuit_data", Circuit::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("circuit_data")
        }

        val recyclerView = findViewById<RecyclerView>(R.id.winners_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        circuit?.let {
            // Asignamos el nombre del circuito al título de la barra
            toolbarTitle.text = it.name
            recyclerView.adapter = WinnerAdapter(it.winners)
        }
    }

    // La función onSupportNavigateUp() ya no es necesaria, la puedes borrar.

    companion object {
        fun newIntent(context: Context, circuit: Circuit): Intent {
            return Intent(context, WinnersActivity::class.java).apply {
                putExtra("circuit_data", circuit)
            }
        }
    }
}