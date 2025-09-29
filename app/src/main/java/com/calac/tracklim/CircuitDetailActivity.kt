package com.calac.tracklim

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import com.google.android.material.appbar.MaterialToolbar

class CircuitDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circuit_detail)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Cierra la actividad al presionar el botón
        }

//        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        val circuit: Circuit? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CIRCUIT_DATA, Circuit::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CIRCUIT_DATA)
        }

        // Conectar vistas
        val nameTextView = findViewById<TextView>(R.id.circuit_name_detail)
        val trackMapImageView = findViewById<ImageView>(R.id.track_map_image)
        val locationTextView = findViewById<TextView>(R.id.value_location)
        val lengthTextView = findViewById<TextView>(R.id.value_length)
        val historyTextView = findViewById<TextView>(R.id.value_history)

        val turnsTextView = findViewById<TextView>(R.id.value_turns)
        val lapRecordTextView = findViewById<TextView>(R.id.value_lap_record)
        val capacityTextView = findViewById<TextView>(R.id.value_capacity)

        circuit?.let { currentCircuit ->
            nameTextView.text = currentCircuit.name
            trackMapImageView.setImageResource(currentCircuit.trackMapResId)
            locationTextView.text = currentCircuit.location
            lengthTextView.text = currentCircuit.length
            historyTextView.text = currentCircuit.history

            // --- LÍNEAS AÑADIDAS (ASIGNAR DATOS) ---
            turnsTextView.text = currentCircuit.turns.toString() // Convertimos el número a texto
            lapRecordTextView.text = currentCircuit.lapRecord
            capacityTextView.text = currentCircuit.capacity

            val winnersButton = findViewById<Button>(R.id.view_winners_button)
            winnersButton.setOnClickListener {
                val intent = WinnersActivity.newIntent(this, currentCircuit)
                startActivity(intent)
            }
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        finish()
//        return true
//    }

    companion object {
        private const val EXTRA_CIRCUIT_DATA = "circuit_data"
        fun newIntent(context: Context, circuit: Circuit): Intent {
            return Intent(context, CircuitDetailActivity::class.java).apply {
                putExtra(EXTRA_CIRCUIT_DATA, circuit)
            }
        }
    }
}