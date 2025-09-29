package com.calac.tracklim // Asegúrate que sea tu paquete

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SavedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Por ahora, solo inflaremos un layout simple que no hemos creado aún
        // O podemos devolver null si no queremos que muestre nada por ahora.
        // Para evitar errores, es mejor crear un layout simple.
        // Pero para avanzar rápido, vamos a crear una vista de texto programáticamente.
        return TextView(activity).apply {
            text = "Pantalla de Guardados"
            gravity = Gravity.CENTER
        }
    }
}
