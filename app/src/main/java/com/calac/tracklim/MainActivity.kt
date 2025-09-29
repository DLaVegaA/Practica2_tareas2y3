package com.calac.tracklim

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applySavedTheme()

        setContentView(R.layout.activity_main)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        // Cargar el fragment inicial ("Explorar")
        if (savedInstanceState == null) {
            loadFragment(ExploreFragment())
            // Marcar el item "Explorar" como seleccionado por defecto
            bottomNavView.selectedItemId = R.id.nav_explore
        }

        // Listener para los clics en la barra de navegación
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    loadFragment(ExploreFragment())
                    true
                }
                R.id.action_dark_mode -> {
                    toggleTheme()
                    false
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun applySavedTheme() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isNightMode = sharedPrefs.getBoolean(KEY_THEME, false) // false = Modo Claro por defecto

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun toggleTheme() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isNightMode = sharedPrefs.getBoolean(KEY_THEME, false)

        // Cambiamos al modo opuesto
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefs.edit().putBoolean(KEY_THEME, false).apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefs.edit().putBoolean(KEY_THEME, true).apply()
        }
        // La actividad se recreará automáticamente para aplicar el nuevo tema
    }

    companion object {
        private const val PREFS_NAME = "ThemePrefs"
        private const val KEY_THEME = "isNightMode"
    }
}