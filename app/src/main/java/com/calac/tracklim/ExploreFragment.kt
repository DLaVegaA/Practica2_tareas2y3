package com.calac.tracklim

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.Marker

class ExploreFragment : Fragment(), OnMapReadyCallback, CategoryFilterAdapter.OnCategoryFilterClickListener {

    private var mMap: GoogleMap? = null
    private val allCircuits by lazy { loadCircuits() }
    private var currentMarkers: MutableList<Marker> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configuramos los filtros
        setupCategoryFilters(view)

        val searchBar = view.findViewById<android.widget.EditText>(R.id.search_bar)
        searchBar.setOnEditorActionListener { textView, actionId, _ ->
            // Comprueba si la acción es "buscar"
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                    // Ocultar el teclado para una mejor experiencia
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(textView.windowToken, 0)
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.setOnMarkerClickListener { marker ->
            // 1. Obtenemos el objeto Circuit desde el tag del marcador
            val circuit = marker.tag as? Circuit

            // 2. Si existe, lanzamos la Activity de detalle
            circuit?.let {
                val intent = CircuitDetailActivity.newIntent(requireContext(), it)
                startActivity(intent)
            }

            // 3. Devolvemos 'true' para indicar que hemos manejado el evento
            return@setOnMarkerClickListener true
        }

        filterAndShowCircuits(null) // Pasamos null para mostrarlos todos

        val europe = LatLng(48.8566, 2.3522)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(europe, 4f))
    }

    private fun setupCategoryFilters(view: View) {
        val filters = listOf(
            CategoryFilter(Category.WEC, R.drawable.ic_wec_logo),
            CategoryFilter(Category.F1, R.drawable.ic_f1_logo),
            CategoryFilter(Category.F2, R.drawable.ic_f2_logo),
            CategoryFilter(Category.F3, R.drawable.ic_f3_logo),
            CategoryFilter(Category.IMSA, R.drawable.ic_imsa_logo),
            CategoryFilter(Category.INDYCAR, R.drawable.ic_indy_logo)
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.category_filters)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = CategoryFilterAdapter(filters, this)
        recyclerView.adapter = adapter
    }

    override fun onCategoryClick(category: Category) {
        filterAndShowCircuits(category)
    }

    private fun filterAndShowCircuits(selectedCategory: Category?) {
        currentMarkers.forEach { it.remove() }
        currentMarkers.clear()

        val filteredCircuits = allCircuits.filter { circuit ->
            val gradeMatch = circuit.grade == 1 || circuit.grade == 2
            val categoryMatch = selectedCategory == null || circuit.categories.contains(selectedCategory)
            gradeMatch && categoryMatch
        }

        filteredCircuits.forEach { circuit ->
            val position = LatLng(circuit.latitude, circuit.longitude)
            mMap?.addMarker(MarkerOptions().position(position).title(circuit.name))?.let { marker ->
                marker.tag = circuit // Asociamos el objeto Circuit completo al marcador
                currentMarkers.add(marker)
            }
        }
    }

    private fun performSearch(query: String) {
        val foundCircuit = allCircuits.firstOrNull { circuit ->
            circuit.name.contains(query, ignoreCase = true)
        }

        if (foundCircuit != null) {
            // Usamos el nuevo método para crear y lanzar el Intent
            val intent = CircuitDetailActivity.newIntent(requireContext(), foundCircuit)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Circuito no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCircuits(): List<Circuit> {
        return listOf(
            Circuit(
                name = "Albert Park Circuit",
                latitude = -37.8497,
                longitude = 144.9680,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Melbourne, Australia",
                length = "5.278 km",
                turns = 14,
                lapRecord = "1:19.813",
                capacity = "~125,000",
                history = "Un circuito urbano alrededor del lago Albert Park, que acogió carreras por primera vez en 1953. Fue renovado para albergar el Gran Premio de Australia a partir de 1996. El trazado fue modificado en 2021 para mejorar las carreras.",
                trackMapResId = R.drawable.albert_park_circuit,
                winners = listOf(
                    Winner(2025, "Lando Norris"),
                    Winner(2024, "Carlos Sainz Jr."),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Charles Leclerc"),
                    Winner(2019, "Valtteri Bottas"),
                    Winner(2018, "Sebastian Vettel"),
                    Winner(2017, "Sebastian Vettel"),
                    Winner(2016, "Nico Rosberg"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2014, "Nico Rosberg"),
                    Winner(2013, "Kimi Raikkonen"),
                    Winner(2012, "Jenson Button"),
                    Winner(2011, "Sebastian Vettel"),
                    Winner(2010, "Jenson Button"),
                    Winner(2009, "Jenson Button")
                )
            ),
            Circuit(
                name = "Autódromo Hermanos Rodríguez",
                latitude = 19.4042,
                longitude = -99.0907,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Ciudad de México, México",
                length = "4.304 km",
                turns = 17,
                lapRecord = "1:17.774",
                capacity = "110,000",
                history = "Construido en 1959 y propiedad del Gobierno de la Ciudad de México. Nombrado en honor a los pilotos Ricardo y Pedro Rodríguez. Ha albergado el Gran Premio de México de forma intermitente desde 1963.",
                trackMapResId = R.drawable.autodromo_hermanos_rodriguez,
                winners = listOf(
                    Winner(2024, "Carlos Sainz Jr."),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Max Verstappen"),
                    Winner(2017, "Max Verstappen"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Nico Rosberg"),
                    Winner(1992, "Nigel Mansell"),
                    Winner(1991, "Riccardo Patrese"),
                    Winner(1990, "Alain Prost"),
                    Winner(1989, "Ayrton Senna"),
                    Winner(1988, "Alain Prost"),
                    Winner(1987, "Nigel Mansell")
                )
            ),
            Circuit(
                name = "Autódromo José Carlos Pace",
                latitude = -23.7036,
                longitude = -46.6997,
                grade = 1,
                categories = listOf(Category.F1, Category.WEC),
                location = "São Paulo, Brasil",
                length = "4.309 km",
                turns = 15,
                lapRecord = "1:10.540",
                capacity = "60,000",
                history = "Inaugurado en 1940, es conocido como Interlagos. Nombrado en honor al piloto brasileño Carlos Pace tras su muerte en 1977. Es famoso por su trazado antihorario y su ambiente de carnaval.",
                trackMapResId = R.drawable.autodromo_jose_carlos_pace,
                winners = listOf(
                    Winner(2025, "Alex Lynn"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2024, "Sébastien Buemi"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "George Russell"),
                    Winner(2021, "Lewis Hamilton"),
                    Winner(2019, "Max Verstappen"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Sebastian Vettel"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Nico Rosberg"),
                    Winner(2014, "Nico Rosberg"),
                    Winner(2014, "Alexander Wurz"),
                    Winner(2013, "Sebastian Vettel"),
                    Winner(2013, "André Lotterer")
                )
            ),
            Circuit(
                name = "Autodromo Internazionale Enzo e Dino Ferrari",
                latitude = 44.3439,
                longitude = 11.7167,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3, Category.WEC),
                location = "Imola, Italia",
                length = "4.909 km",
                turns = 19,
                lapRecord = "1:15.484",
                capacity = "78,000",
                history = "Inaugurado en 1953, es un circuito antihorario que lleva el nombre del fundador de Ferrari y su hijo. Famoso por albergar el Gran Premio de San Marino y recordado por los trágicos eventos de 1994.",
                trackMapResId = R.drawable.autodromo_internazionale_enzo_e_dino_ferrari,
                winners = listOf(
                    Winner(2025, "Max Verstappen"),
                    Winner(2025, "James Calado"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2024, "Alessandro Pier Guidi"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2011, "Anthony Davidson"),
                    Winner(2006, "Michael Schumacher"),
                    Winner(2005, "Fernando Alonso"),
                    Winner(2004, "Michael Schumacher"),
                    Winner(2003, "Michael Schumacher"),
                    Winner(2002, "Michael Schumacher"),
                    Winner(2001, "Ralf Schumacher"),
                    Winner(2000, "Michael Schumacher")
                )
            ),
            Circuit(
                name = "Autodromo Nazionale di Monza",
                latitude = 45.6156,
                longitude = 9.2811,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Monza, Italia",
                length = "5.793 km",
                turns = 11,
                lapRecord = "1:21.046",
                capacity = "118,865",
                history = "Conocido como el 'Templo de la Velocidad', fue construido en 1922. Es uno de los circuitos permanentes más antiguos del mundo y ha albergado el Gran Premio de Italia casi ininterrumpidamente desde 1950.",
                trackMapResId = R.drawable.autodromo_nazionale_di_monza,
                winners = listOf(
                    Winner(2025, "Max Verstappen"),
                    Winner(2024, "Charles Leclerc"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Daniel Ricciardo"),
                    Winner(2020, "Pierre Gasly"),
                    Winner(2019, "Charles Leclerc"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Nico Rosberg"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Sebastian Vettel"),
                    Winner(2012, "Lewis Hamilton"),
                    Winner(2011, "Sebastian Vettel")
                )
            ),
            Circuit(
                name = "Bahrain International Circuit",
                latitude = 26.0325,
                longitude = 50.5106,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3, Category.WEC),
                location = "Sakhir, Bahréin",
                length = "5.412 km",
                turns = 15,
                lapRecord = "1:31.447",
                capacity = "70,000",
                history = "Inaugurado en 2004, fue el primer circuito de F1 en Oriente Medio. Diseñado por Hermann Tilke, es conocido por sus amplias escapatorias y por albergar carreras nocturnas bajo potentes focos.",
                trackMapResId = R.drawable.bahrain_international_circuit,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2024, "Sébastien Buemi"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2023, "Sébastien Buemi"),
                    Winner(2022, "Charles Leclerc"),
                    Winner(2022, "Mike Conway"),
                    Winner(2021, "Lewis Hamilton"),
                    Winner(2021, "Sébastien Buemi"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2020, "Mike Conway"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2019, "Mike Conway"),
                    Winner(2018, "Sebastian Vettel"),
                    Winner(2017, "Sebastian Vettel")
                )
            ),
            Circuit(
                name = "Baku City Circuit",
                latitude = 40.3725,
                longitude = 49.8533,
                grade = 1,
                categories = listOf(Category.F1, Category.F2),
                location = "Bakú, Azerbaiyán",
                length = "6.003 km",
                turns = 20,
                lapRecord = "1:43.009",
                capacity = "18,500",
                history = "Un circuito urbano que debutó en 2016 como el Gran Premio de Europa. Es conocido por su larguísima recta y su sección extremadamente estrecha alrededor de la ciudad vieja, combinando altas velocidades con desafíos técnicos.",
                trackMapResId = R.drawable.baku_city_circuit,
                winners = listOf(
                    Winner(2025, "Max Verstappen"),
                    Winner(2024, "Oscar Piastri"),
                    Winner(2023, "Sergio Perez"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Sergio Perez"),
                    Winner(2019, "Valtteri Bottas"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Daniel Ricciardo"),
                    Winner(2016, "Nico Rosberg")
                )
            ),
            Circuit(
                name = "Canadian Tire Motorsport Park",
                latitude = 44.0503,
                longitude = -78.6769,
                grade = 2,
                categories = listOf(Category.IMSA),
                location = "Clarington, Canadá",
                length = "3.957 km",
                turns = 10,
                lapRecord = "1:05.823",
                capacity = "Asientos abiertos sin límite de capacidad",
                history = "Inaugurado en 1961 como Mosport Park, es uno de los circuitos más antiguos de Canadá. Ha albergado la F1, Can-Am e IMSA. Es un trazado de alta velocidad con cambios de elevación significativos.",
                trackMapResId = R.drawable.canadian_tire_motorsport_park,
                winners = listOf(
                    Winner(2025, "PJ Hyett"),
                    Winner(2022, "Renger van der Zande"),
                    Winner(2019, "Oliver Jarvis"),
                    Winner(2018, "Colin Braun"),
                    Winner(2017, "Dane Cameron"),
                    Winner(2016, "Dane Cameron"),
                    Winner(2015, "Jordan Taylor"),
                    Winner(2014, "Olivier Pla")
                )
            ),
            Circuit(
                name = "Circuit de Barcelona-Catalunya",
                latitude = 41.5700,
                longitude = 2.2611,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Montmeló, España",
                length = "4.657 km",
                turns = 14,
                lapRecord = "1:16.330",
                capacity = "140,700",
                history = "Construido en 1991, es un circuito muy completo utilizado para pruebas de F1 por su mezcla de curvas de alta y baja velocidad. Fue sede de eventos ciclistas en los Juegos Olímpicos de 1992.",
                trackMapResId = R.drawable.circuit_de_barcelona_catalunya,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Lewis Hamilton"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Max Verstappen"),
                    Winner(2015, "Nico Rosberg"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Fernando Alonso"),
                    Winner(2012, "Pastor Maldonado"),
                    Winner(2011, "Sebastian Vettel")
                )
            ),
            Circuit(
                name = "Circuit de Monaco",
                latitude = 43.7347,
                longitude = 7.4206,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Montecarlo, Mónaco",
                length = "3.337 km",
                turns = 19,
                lapRecord = "1:12.909",
                capacity = "37,000",
                history = "El circuito urbano más famoso del mundo, inaugurado en 1929. Es conocido por su glamour y su trazado estrecho y exigente que no perdona errores. Ha sido parte del campeonato de F1 desde 1950.",
                trackMapResId = R.drawable.circuit_de_monaco,
                winners = listOf(
                    Winner(2025, "Lando Norris"),
                    Winner(2024, "Charles Leclerc"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Sergio Pérez"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Daniel Ricciardo"),
                    Winner(2017, "Sebastian Vettel"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Nico Rosberg"),
                    Winner(2014, "Nico Rosberg"),
                    Winner(2013, "Nico Rosberg"),
                    Winner(2012, "Mark Webber"),
                    Winner(2011, "Sebastian Vettel"),
                    Winner(2010, "Mark Webber")
                )
            ),
            Circuit(
                name = "Circuit de Spa-Francorchamps",
                latitude = 50.4372,
                longitude = 5.9714,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3, Category.WEC),
                location = "Stavelot, Bélgica",
                length = "7.004 km",
                turns = 19,
                lapRecord = "1:46.286",
                capacity = "70,000",
                history = "Uno de los circuitos más históricos y queridos, inaugurado en 1921. Originalmente usaba carreteras públicas. Famoso por su trazado rápido y ondulado en el bosque de las Ardenas, y la icónica curva de Eau Rouge.",
                trackMapResId = R.drawable.circuit_de_spa_francorchamps,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2025, "James Calado"),
                    Winner(2024, "Lewis Hamilton"),
                    Winner(2024, "Mike Conway"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2023, "Mike Conway"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2022, "Mike Conway"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2021, "Sébastien Buemi"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2020, "Mike Conway"),
                    Winner(2019, "Charles Leclerc"),
                    Winner(2019, "Fernando Alonso"),
                    Winner(2018, "Sebastian Vettel")
                )
            ),
            Circuit(
                name = "Circuit Gilles Villeneuve",
                latitude = 45.5000,
                longitude = -73.5225,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Montreal, Canadá",
                length = "4.361 km",
                turns = 14,
                lapRecord = "1:13.078",
                capacity = "100,000",
                history = "Ubicado en la isla artificial de Notre Dame, fue inaugurado en 1978. Renombrado en honor al piloto canadiense Gilles Villeneuve. Es un circuito semipermanente conocido por el 'Muro de los Campeones'.",
                trackMapResId = R.drawable.circuit_gilles_villeneuve,
                winners = listOf(
                    Winner(2025, "George Russell"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Sebastian Vettel"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2014, "Daniel Ricciardo"),
                    Winner(2013, "Sebastian Vettel"),
                    Winner(2012, "Lewis Hamilton"),
                    Winner(2011, "Jenson Button"),
                    Winner(2010, "Lewis Hamilton"),
                    Winner(2008, "Robert Kubica")
                )
            ),
            Circuit(
                name = "Circuit of the Americas",
                latitude = 30.1328,
                longitude = -97.6411,
                grade = 1,
                categories = listOf(Category.F1, Category.WEC),
                location = "Austin, Estados Unidos",
                length = "5.513 km",
                turns = 20,
                lapRecord = "1:36.169",
                capacity = "120,000",
                history = "El primer circuito construido específicamente para la F1 en EE. UU., inaugurado en 2012. Diseñado por Hermann Tilke, es conocido por su pronunciada subida hacia la curva 1 y una mezcla de curvas inspiradas en otros circuitos famosos.",
                trackMapResId = R.drawable.circuit_of_the_americas,
                winners = listOf(
                    Winner(2025, "Matt Campbell"),
                    Winner(2024, "Charles Leclerc"),
                    Winner(2024, "Robert Kubica"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2020, "Gustavo Menezes"),
                    Winner(2019, "Valtteri Bottas"),
                    Winner(2018, "Kimi Raikkonen"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2017, "Earl Bamber"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2016, "Mark Webber"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2015, "Mark Webber")
                )
            ),
            Circuit(
                name = "Circuit Zandvoort",
                latitude = 52.3888,
                longitude = 4.5409,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Zandvoort, Países Bajos",
                length = "4.259 km",
                turns = 14,
                lapRecord = "1:11.097",
                capacity = "105,000",
                history = "Inaugurado en 1948 entre las dunas de la costa. Un circuito de la 'vieja escuela' que regresó al calendario de F1 en 2021 tras una modernización que incluyó la adición de curvas peraltadas.",
                trackMapResId = R.drawable.circuit_zandvoort,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2024, "Lando Norris"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Max Verstappen"),
                    Winner(1985, "Niki Lauda"),
                    Winner(1984, "Alain Prost"),
                    Winner(1983, "René Arnoux"),
                    Winner(1982, "Didier Pironi"),
                    Winner(1981, "Alain Prost"),
                    Winner(1980, "Nelson Piquet"),
                    Winner(1979, "Alan Jones"),
                    Winner(1978, "Mario Andretti"),
                    Winner(1977, "Niki Lauda"),
                    Winner(1976, "James Hunt")
                )
            ),
            Circuit(
                name = "Detroit Street Circuit",
                latitude = 42.3299,
                longitude = -83.0403,
                grade = 2,
                categories = listOf(Category.IMSA, Category.INDYCAR),
                location = "Detroit, Estados Unidos",
                length = "2.647 km",
                turns = 9,
                lapRecord = "1:01.941",
                capacity = "Información no disponible",
                history = "Detroit ha albergado carreras urbanas desde 1982, primero para la F1 y luego para CART/IndyCar. El trazado ha cambiado varias veces, pasando del Renaissance Center a Belle Isle y regresando al centro en 2023 con un nuevo diseño.",
                trackMapResId = R.drawable.detroit_street_circuit,
                winners = listOf(
                    Winner(2025, "Kyle Kirkwood"),
                    Winner(2025, "Renger van der Zande"),
                    Winner(2024, "Scott Dixon"),
                    Winner(2024, "Ricky Taylor"),
                    Winner(2023, "Alex Palou"),
                    Winner(2023, "Daniel Morad"),
                    Winner(2022, "Will Power"),
                    Winner(2022, "Renger van der Zande"),
                    Winner(2021, "Patricio O'Ward"),
                    Winner(2021, "Marcus Ericsson"),
                    Winner(2021, "Kevin Magnussen"),
                    Winner(2019, "Scott Dixon"),
                    Winner(2019, "Josef Newgarden"),
                    Winner(2018, "Ryan Hunter-Reay"),
                    Winner(2018, "Scott Dixon")
                )
            ),
            Circuit(
                name = "Fuji Speedway",
                latitude = 35.3717,
                longitude = 138.9272,
                grade = 1,
                categories = listOf(Category.WEC),
                location = "Oyama, Japón",
                length = "4.563 km",
                turns = 16,
                lapRecord = "1:18.426",
                capacity = "110,000",
                history = "Inaugurado en 1965 a los pies del Monte Fuji. Originalmente tenía una peligrosa curva peraltada. Propiedad de Toyota, ha albergado la F1 y es una cita fija del WEC. Famoso por su recta de 1.5 km.",
                trackMapResId = R.drawable.fuji_speedway,
                winners = listOf(
                    Winner(2025, "Paul-Loup Chatin"),
                    Winner(2024, "Mike Conway"),
                    Winner(2023, "Mike Conway"),
                    Winner(2022, "Sébastien Buemi"),
                    Winner(2019, "Sébastien Buemi"),
                    Winner(2018, "Mike Conway"),
                    Winner(2017, "Sébastien Buemi"),
                    Winner(2016, "Stéphane Sarrazin"),
                    Winner(2015, "Timo Bernhard"),
                    Winner(2014, "Sébastien Buemi"),
                    Winner(2013, "Alexander Wurz"),
                    Winner(2012, "Alexander Wurz")
                )
            ),
            Circuit(
                name = "Hungaroring",
                latitude = 47.5789,
                longitude = 19.2486,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Mogyoród, Hungría",
                length = "4.381 km",
                turns = 14,
                lapRecord = "1:16.627",
                capacity = "70,000",
                history = "Construido en 1986, fue el primer Gran Premio de F1 detrás del Telón de Acero. Es un circuito estrecho y revirado, a menudo comparado con un 'karting sin muros', donde los adelantamientos son difíciles.",
                trackMapResId = R.drawable.hungaroring,
                winners = listOf(
                    Winner(2025, "Lando Norris"),
                    Winner(2024, "Oscar Piastri"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Esteban Ocon"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Sebastian Vettel"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Sebastian Vettel"),
                    Winner(2014, "Daniel Ricciardo"),
                    Winner(2013, "Lewis Hamilton"),
                    Winner(2012, "Lewis Hamilton"),
                    Winner(2011, "Jenson Button")
                )
            ),
            Circuit(
                name = "Indianapolis Motor Speedway",
                latitude = 39.7950,
                longitude = -86.2344,
                grade = 1,
                categories = listOf(Category.IMSA, Category.INDYCAR),
                location = "Indianapolis, Estados Unidos",
                length = "3.925 km",
                turns = 14,
                lapRecord = "Información no disponible",
                capacity = "400,000",
                history = "Conocido como 'The Brickyard', es el recinto deportivo de mayor capacidad del mundo. Inaugurado en 1909, es famoso por las 500 Millas de Indianápolis. También tiene un trazado interior que ha albergado F1 e IMSA.",
                trackMapResId = R.drawable.indianapolis_motor_speedway,
                winners = listOf(
                    Winner(2025, "Alex Palou"),
                    Winner(2025, "Jack Aitken"),
                    Winner(2024, "Josef Newgarden"),
                    Winner(2024, "Philipp Eng"),
                    Winner(2023, "Josef Newgarden"),
                    Winner(2023, "Mathieu Jaminet"),
                    Winner(2022, "Marcus Ericsson"),
                    Winner(2021, "Helio Castroneves"),
                    Winner(2020, "Takuma Sato"),
                    Winner(2019, "Simon Pagenaud"),
                    Winner(2018, "Will Power"),
                    Winner(2017, "Takuma Sato"),
                    Winner(2016, "Alexander Rossi"),
                    Winner(2015, "Juan Pablo Montoya"),
                    Winner(2014, "Ryan Hunter-Reay")
                )
            ),
            Circuit(
                name = "Jeddah Corniche Circuit",
                latitude = 21.6319,
                longitude = 39.1044,
                grade = 1,
                categories = listOf(Category.F1, Category.F2),
                location = "Yeda, Arabia Saudita",
                length = "6.174 km",
                turns = 27,
                lapRecord = "1:30.734",
                capacity = "50,000",
                history = "Debutó en 2021 como el circuito urbano más rápido de la F1. Ubicado en la costa del Mar Rojo, es un trazado largo y fluido con muchas curvas de alta velocidad y muros cercanos.",
                trackMapResId = R.drawable.jeddah_corniche_circuit,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2023, "Sergio Perez"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Lewis Hamilton")
                )
            ),
            Circuit(
                name = "Las Vegas Strip Circuit",
                latitude = 36.1147,
                longitude = -115.1728,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Las Vegas, Estados Unidos",
                length = "6.201 km",
                turns = 17,
                lapRecord = "1:34.876",
                capacity = "100,000",
                history = "Debutó en 2023, llevando la F1 al famoso Las Vegas Strip. Es una carrera nocturna que pasa por lugares emblemáticos. La recta principal de 1.9 km es una de las más largas del calendario.",
                trackMapResId = R.drawable.las_vegas_strip_circuit,
                winners = listOf(
                    Winner(2024, "George Russell"),
                    Winner(2023, "Max Verstappen")
                )
            ),
            Circuit(
                name = "Long Beach Street Circuit",
                latitude = 33.7661,
                longitude = -118.1922,
                grade = 2,
                categories = listOf(Category.IMSA, Category.INDYCAR),
                location = "Long Beach, Estados Unidos",
                length = "3.167 km",
                turns = 11,
                lapRecord = "Información no disponible",
                capacity = "194,000+",
                history = "La carrera urbana más antigua de Norteamérica, celebrada desde 1975. Albergó la F1 (como GP del Oeste de EE. UU.) de 1976 a 1983 antes de convertirse en un evento principal de IndyCar.",
                trackMapResId = R.drawable.long_beach_street_circuit,
                winners = listOf(
                    Winner(2025, "Kyle Kirkwood"),
                    Winner(2025, "Felipe Nasr"),
                    Winner(2024, "Scott Dixon"),
                    Winner(2024, "Sebastien Bourdais"),
                    Winner(2023, "Kyle Kirkwood"),
                    Winner(2023, "Mathieu Jaminet"),
                    Winner(2022, "Josef Newgarden"),
                    Winner(2021, "Colton Herta"),
                    Winner(2019, "Alexander Rossi"),
                    Winner(2018, "Alexander Rossi"),
                    Winner(2017, "James Hinchcliffe"),
                    Winner(2016, "Simon Pagenaud"),
                    Winner(2015, "Scott Dixon"),
                    Winner(2014, "Mike Conway"),
                    Winner(2013, "Takuma Sato")
                )
            ),
            Circuit(
                name = "Lusail International Circuit",
                latitude = 25.4900,
                longitude = 51.4542,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.WEC),
                location = "Lusail, Catar",
                length = "5.419 km",
                turns = 16,
                lapRecord = "1:22.384",
                capacity = "52,000",
                history = "Inaugurado en 2004, principalmente para MotoGP. Famoso por ser el primer circuito en albergar una carrera nocturna de MotoGP en 2008. Debutó en F1 en 2021 y fue renovado extensamente para 2023.",
                trackMapResId = R.drawable.lusail_international_circuit,
                winners = listOf(
                    Winner(2025, "Antonio Fuoco"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2024, "Kévin Estre"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2021, "Lewis Hamilton")
                )
            ),
            Circuit(
                name = "Marina Bay Street Circuit",
                latitude = 1.2914,
                longitude = 103.8640,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Singapur, Singapur",
                length = "4.940 km",
                turns = 19,
                lapRecord = "1:34.486",
                capacity = ">90,000",
                history = "Albergó la primera carrera nocturna de la historia de la F1 en 2008. Es un circuito urbano exigente y bacheado que rodea la bahía de Singapur, conocido por su clima húmedo y su duración cercana a las dos horas.",
                trackMapResId = R.drawable.marina_bay_street_circuit,
                winners = listOf(
                    Winner(2024, "Lando Norris"),
                    Winner(2023, "Carlos Sainz"),
                    Winner(2022, "Sergio Perez"),
                    Winner(2019, "Sebastian Vettel"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Nico Rosberg"),
                    Winner(2015, "Sebastian Vettel"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Sebastian Vettel"),
                    Winner(2012, "Sebastian Vettel"),
                    Winner(2011, "Sebastian Vettel"),
                    Winner(2010, "Fernando Alonso"),
                    Winner(2009, "Lewis Hamilton"),
                    Winner(2008, "Fernando Alonso")
                )
            ),
            Circuit(
                name = "Miami International Autodrome",
                latitude = 25.9581,
                longitude = -80.2389,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Miami Gardens, Estados Unidos",
                length = "5.412 km",
                turns = 19,
                lapRecord = "1:29.708",
                capacity = "65,000",
                history = "Un circuito temporal construido alrededor del Hard Rock Stadium, que debutó en 2022. Diseñado para crear un ambiente de festival, combina secciones rápidas y fluidas con una parte más lenta y técnica.",
                trackMapResId = R.drawable.miami_international_autodrome,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2024, "Lando Norris"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen")
                )
            ),
            Circuit(
                name = "Red Bull Ring",
                latitude = 47.2197,
                longitude = 14.7647,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Spielberg, Austria",
                length = "4.326 km",
                turns = 10,
                lapRecord = "1:05.619",
                capacity = "105,000",
                history = "Originalmente conocido como Österreichring, es un circuito rápido con grandes cambios de elevación en las montañas de Estiria. Fue acortado y rebautizado como A1-Ring, y posteriormente comprado y renovado por Red Bull para regresar a la F1 en 2014.",
                trackMapResId = R.drawable.red_bull_ring,
                winners = listOf(
                    Winner(2025, "Lando Norris"),
                    Winner(2024, "George Russell"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Charles Leclerc"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2020, "Valtteri Bottas"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2019, "Max Verstappen"),
                    Winner(2018, "Max Verstappen"),
                    Winner(2017, "Valtteri Bottas"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Nico Rosberg"),
                    Winner(2014, "Nico Rosberg"),
                    Winner(2003, "Michael Schumacher"),
                    Winner(2002, "Michael Schumacher")
                )
            ),
            Circuit(
                name = "Sebring International Raceway",
                latitude = 27.4546,
                longitude = -81.3483,
                grade = 2,
                categories = listOf(Category.IMSA),
                location = "Sebring, Estados Unidos",
                length = "6.019 km",
                turns = 17,
                lapRecord = "1:41.800",
                capacity = "~170,000",
                history = "El circuito de carreras en carretera más antiguo de EE. UU., inaugurado en 1950 en un antiguo aeródromo de la Segunda Guerra Mundial. Es famoso por su superficie bacheada de hormigón y asfalto y por albergar las 12 Horas de Sebring.",
                trackMapResId = R.drawable.sebring_international_raceway,
                winners = listOf(
                    Winner(2025, "Felipe Nasr"),
                    Winner(2024, "Louis Deletraz"),
                    Winner(2023, "Pipo Derani"),
                    Winner(2022, "Earl Bamber"),
                    Winner(2021, "Sebastien Bourdais"),
                    Winner(2020, "Jonathan Bomarito"),
                    Winner(2019, "Pipo Derani"),
                    Winner(2018, "Pipo Derani"),
                    Winner(2017, "Ricky Taylor"),
                    Winner(2016, "Pipo Derani"),
                    Winner(2015, "Joao Barbosa"),
                    Winner(2014, "Marino Franchitti"),
                    Winner(2013, "Marcel Fässler"),
                    Winner(2012, "Dindo Capello"),
                    Winner(2011, "Loïc Duval")
                )
            ),
            Circuit(
                name = "Shanghai International Circuit",
                latitude = 31.3389,
                longitude = 121.2206,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Shanghái, China",
                length = "5.451 km",
                turns = 16,
                lapRecord = "1:32.238",
                capacity = "200,000",
                history = "Inaugurado en 2004, su diseño se inspira en el carácter chino 'shang' (上). Es conocido por su combinación de largas rectas y curvas técnicas, como la exigente curva 1 de radio decreciente.",
                trackMapResId = R.drawable.shanghai_international_circuit,
                winners = listOf(
                    Winner(2025, "Oscar Piastri"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Daniel Ricciardo"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Nico Rosberg"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Fernando Alonso"),
                    Winner(2012, "Nico Rosberg"),
                    Winner(2011, "Lewis Hamilton"),
                    Winner(2010, "Jenson Button"),
                    Winner(2009, "Sebastian Vettel"),
                    Winner(2008, "Lewis Hamilton"),
                    Winner(2007, "Kimi Raikkonen")
                )
            ),
            Circuit(
                name = "Silverstone Circuit",
                latitude = 52.0786,
                longitude = -1.0169,
                grade = 1,
                categories = listOf(Category.F1, Category.F2, Category.F3),
                location = "Silverstone, Reino Unido",
                length = "5.891 km",
                turns = 18,
                lapRecord = "1:27.097",
                capacity = "164,000",
                history = "Conocido como la 'casa del automovilismo británico', está construido sobre una antigua base aérea de la Segunda Guerra Mundial. Albergó el primer Gran Premio del Campeonato Mundial de F1 en 1950. Famoso por sus curvas de alta velocidad como Maggots y Becketts.",
                trackMapResId = R.drawable.silverstone_circuit,
                winners = listOf(
                    Winner(2025, "Lando Norris"),
                    Winner(2024, "Lewis Hamilton"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Carlos Sainz"),
                    Winner(2021, "Lewis Hamilton"),
                    Winner(2020, "Lewis Hamilton"),
                    Winner(2020, "Max Verstappen"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Sebastian Vettel"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Nico Rosberg"),
                    Winner(2012, "Mark Webber")
                )
            ),
            Circuit(
                name = "Suzuka International Racing Course",
                latitude = 34.8431,
                longitude = 136.5411,
                grade = 1,
                categories = listOf(Category.F1),
                location = "Suzuka, Japón",
                length = "5.807 km",
                turns = 18,
                lapRecord = "1:30.983",
                capacity = "155,000",
                history = "Diseñado como pista de pruebas para Honda en 1962. Es uno de los favoritos de los pilotos por su desafiante trazado en forma de '8', único en el calendario de F1. Ha sido escenario de muchas definiciones de campeonatos.",
                trackMapResId = R.drawable.suzuka_international_racing_course,
                winners = listOf(
                    Winner(2025, "Max Verstappen"),
                    Winner(2024, "Max Verstappen"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2019, "Valtteri Bottas"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Lewis Hamilton"),
                    Winner(2016, "Nico Rosberg"),
                    Winner(2015, "Lewis Hamilton"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Sebastian Vettel"),
                    Winner(2012, "Sebastian Vettel"),
                    Winner(2011, "Jenson Button"),
                    Winner(2010, "Sebastian Vettel"),
                    Winner(2009, "Sebastian Vettel")
                )
            ),
            Circuit(
                name = "Yas Marina Circuit",
                latitude = 24.4672,
                longitude = 54.6031,
                grade = 1,
                categories = listOf(Category.F1, Category.F2),
                location = "Abu Dabi, EAU",
                length = "5.281 km",
                turns = 16,
                lapRecord = "1:25.637",
                capacity = "60,000",
                history = "Inaugurado en 2009 en la isla de Yas, es conocido por ser una carrera crepuscular que empieza de día y termina de noche. El trazado fue modificado en 2021 para mejorar las oportunidades de adelantamiento.",
                trackMapResId = R.drawable.yas_marina_circuit,
                winners = listOf(
                    Winner(2024, "Lando Norris"),
                    Winner(2023, "Max Verstappen"),
                    Winner(2022, "Max Verstappen"),
                    Winner(2021, "Max Verstappen"),
                    Winner(2020, "Max Verstappen"),
                    Winner(2019, "Lewis Hamilton"),
                    Winner(2018, "Lewis Hamilton"),
                    Winner(2017, "Valtteri Bottas"),
                    Winner(2016, "Lewis Hamilton"),
                    Winner(2015, "Nico Rosberg"),
                    Winner(2014, "Lewis Hamilton"),
                    Winner(2013, "Sebastian Vettel"),
                    Winner(2012, "Kimi Raikkonen"),
                    Winner(2011, "Lewis Hamilton"),
                    Winner(2010, "Sebastian Vettel")
                )
            )
        )
    }
}
