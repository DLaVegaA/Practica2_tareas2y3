# Tracklim: Explorador de Circuitos de Carreras

**Tracklim** es una aplicación Android diseñada como un explorador interactivo para aficionados del automovilismo. Permite a los usuarios descubrir y aprender sobre los circuitos de carreras más icónicos del mundo, ver sus trazados, filtrar por categorías de competición y consultar datos históricos como sus ganadores.

## Capturas de Pantalla

### Modo Claro

<img width="382" height="834" alt="image" src="https://github.com/user-attachments/assets/c930ea61-8954-40a2-9443-c652405da3d5" />

<img width="382" height="840" alt="image" src="https://github.com/user-attachments/assets/4819534f-0af7-4c01-9b1b-e599500df462" />

<img width="386" height="832" alt="image" src="https://github.com/user-attachments/assets/9a19c35f-f7fa-4d3f-8259-92b2bc8512c7" />

### Modo Oscuro

<img width="386" height="836" alt="image" src="https://github.com/user-attachments/assets/6a1d59d5-1fbf-4919-ab4c-6063463c3a99" />

<img width="380" height="837" alt="image" src="https://github.com/user-attachments/assets/955d667b-d2b0-4d99-8245-c0248f962228" />

<img width="383" height="833" alt="image" src="https://github.com/user-attachments/assets/59f266dc-43eb-4fb6-bd0e-e26afe9868d8" />

## Descripción Detallada

El propósito de esta aplicación es ofrecer una experiencia inmersiva y temática para explorar circuitos de carreras. La navegación principal se centra en un mapa del mundo donde el usuario puede interactuar con diferentes puntos de interés (los circuitos). Al seleccionar un circuito, ya sea a través del mapa, un filtro de categoría o la barra de búsqueda, el usuario es transportado a una pantalla de detalle con información específica y, desde allí, puede profundizar aún más en datos históricos.

### Características Principales

  * **Mapa Interactivo:** Visualiza la ubicación de docenas de circuitos de carreras alrededor del mundo.
  * **Filtrado por Categoría:** Filtra los circuitos que aparecen en el mapa según las principales categorías del automovilismo (F1, WEC, F2, F3, IMSA, IndyCar).
  * **Búsqueda de Circuitos:** Una barra de búsqueda permite encontrar rápidamente un circuito por su nombre.
  * **Pantalla de Detalle:** Al seleccionar un circuito, se muestra una pantalla dedicada con información clave:
      * Trazado del circuito.
      * Datos técnicos: Ubicación, longitud, número de curvas, vuelta récord y capacidad.
      * Resumen histórico del circuito.
  * **Historial de Ganadores:** Desde la pantalla de detalle, se puede acceder a una lista con los ganadores de eventos importantes en ese circuito.
  * **Modo Oscuro Personalizado:** La aplicación cuenta con un interruptor para alternar entre un tema claro y uno oscuro, guardando la preferencia del usuario independientemente de la configuración del sistema.

## Características Técnicas

  * **Arquitectura:** Navegación jerárquica de tres niveles implementada con Activities (MainActivity, CircuitDetailActivity, WinnersActivity) y Fragments (ExploreFragment).
  * **Interfaz de Usuario (UI):** Diseños creados con ConstraintLayout para máxima flexibilidad y RecyclerView para mostrar eficientemente las listas de filtros y ganadores. Se utiliza MaterialToolbar y componentes personalizados para una UI consistente.
  * **Navegación:** Se implementa la navegación entre Activities mediante Intents y el paso de datos de objetos complejos (Circuit) a través de la serialización con kotlin-parcelize.
  * **Gestión de Datos:** Se utiliza una fuente de datos local en memoria para toda la información de los circuitos. La preferencia del tema oscuro se persiste localmente usando SharedPreferences.
  * **Integraciones:** Uso del **SDK de Google Maps** para la visualización del mapa y la interacción con los marcadores.

## Decisiones de Diseño

  * **UI/UX:** Se optó por un diseño minimalista y oscuro (en el modo por defecto) para crear una experiencia inmersiva y elegante. La tipografía fue seleccionada cuidadosamente: **Rajdhani** para los títulos, por su estética moderna y técnica, y una fuente estándar de alta legibilidad para el cuerpo del texto.
  * **Navegación:** La jerarquía de tres niveles (Mapa -\> Detalle -\> Historial) fue diseñada para ser intuitiva, permitiendo al usuario profundizar en la información de manera natural. Las transiciones se inician a través de acciones contextuales (clic en un marcador del mapa, búsqueda) en lugar de botones genéricos.
  * **Modo Oscuro:** Se decidió implementar un interruptor de tema dentro de la app en lugar de depender del sistema para darle al usuario control total sobre la experiencia visual, una característica común en apps de contenido.

## Implementación de Temas con SharedPreferences
Una de las características clave de la aplicación es la capacidad de cambiar entre un tema claro y uno oscuro de forma manual, independientemente de la configuración del sistema operativo. Esta funcionalidad se implementó para dar al usuario un control total sobre la experiencia visual y para asegurar que la preferencia se mantenga entre sesiones.

La implementación se basa en tres componentes principales:

  1. **Almacenamiento de la Preferencia:** Se utiliza SharedPreferences como un mecanismo de almacenamiento ligero para guardar la elección del usuario. Se guarda un único valor booleano con la clave isNightMode. Esto garantiza que, si el usuario cierra y vuelve a abrir la aplicación, esta se inicie con el último tema seleccionado.

  2. **Lógica de Cambio y Aplicación:** Toda la lógica reside en MainActivity.kt.

     * Una función toggleTheme() se encarga de leer el estado actual desde SharedPreferences, invertirlo (de claro a oscuro o viceversa), guardar el nuevo estado y, finalmente, llamar a AppCompatDelegate.setDefaultNightMode().

     * La llamada a setDefaultNightMode() es la que le indica al sistema que debe recrear la Activity actual, aplicando los recursos correspondientes al nuevo tema. El "parpadeo" que se observa durante el cambio es el resultado de este proceso de recreación.

     * Para asegurar que la app siempre se inicie con el tema correcto, se llama a una función applySavedTheme() en el método onCreate() de la MainActivity, justo antes de setContentView(). Esto lee la preferencia guardada y establece el tema antes de que cualquier elemento de la interfaz sea dibujado, evitando un cambio de tema visualmente brusco al arrancar.

  3. **Adaptación de la Interfaz de Usuario (UI):** La adaptación visual a cada tema se logra a través del sistema de recursos de Android. Se utilizan directorios con el calificador -night (por ejemplo, values-night y color-night).

     * Recursos como los selectores de color para la barra de navegación (bottom_nav_item_color.xml) existen en dos versiones: una en res/color/ para el tema claro y otra en res/color-night/ para el oscuro.

     * De esta manera, cuando AppCompatDelegate cambia el modo de la aplicación, el sistema operativo selecciona automáticamente el conjunto de recursos correcto sin necesidad de escribir código condicional en los layouts o en las clases de las Activities.

## Instrucciones para Ejecutar el Proyecto

### Prerrequisitos

  * Android Studio (versión Iguana o superior recomendada).
  * Un emulador o dispositivo físico con Google Play Services.

### Configuración

El proyecto utiliza la API de Google Maps, la cual requiere una clave de API para funcionar.

1.  **Obtén una Clave de API:** Sigue la guía oficial de Google para crear un proyecto en Google Cloud Console, habilitar la API "Maps SDK for Android" y obtener tu clave.
2.  **Añade la Clave al Proyecto:**
      * En la carpeta raíz de tu proyecto, busca o crea un archivo llamado local.properties.
      * Añade tu clave en una nueva línea con el siguiente formato:
        
        MAPS_API_KEY=AQUI_PEGA_TU_CLAVE_DE_API
        
      * El proyecto ya está configurado para leer esta clave de forma segura. No necesitas modificar el AndroidManifest.xml.

### Ejecución

1.  Clona o descarga este repositorio.
2.  Abre el proyecto con Android Studio.
3.  Espera a que Gradle sincronice todas las dependencias.
4.  Selecciona un dispositivo y presiona el botón "Run".

## Retos y Soluciones

Durante el desarrollo, surgieron varios desafíos técnicos que requirieron investigación y soluciones específicas:

  * **Reto: Authorization Failure en la API de Google Maps.**

      * **Problema:** A pesar de tener una clave de API correcta, el mapa no se cargaba debido a un fallo de autorización.
      * **Solución:** El problema se localizó en las restricciones de la clave de API en Google Cloud Console. La huella digital **SHA-1** del certificado de depuración de la app no coincidía con la registrada. La solución definitiva fue generar la clave SHA-1 directamente desde Android Studio usando la tarea de Gradle signingReport y asegurarse de que tanto esa clave como el nombre del paquete (com.calac.tracklim) estuvieran correctamente añadidos a las restricciones de la clave.

  * **Reto: Conflictos con el Plugin kotlin-parcelize.**

      * **Problema:** Al intentar hacer los objetos Circuit transportables, surgieron errores contradictorios: Unresolved reference 'Parcelize' y, al añadir el plugin, plugin is already on the classpath.
      * **Solución:** Se descubrió que las versiones modernas del plugin de Kotlin para Android ya incluyen la funcionalidad de parcelize. La solución fue utilizar la sintaxis específica kotlin("parcelize") en el archivo build.gradle.kts, lo que resolvió el conflicto y activó la anotación correctamente.

  * **Reto: Implementar un Modo Oscuro Persistente e Independiente del Sistema.**

      * **Problema:** Se requería que la app gestionara su propio estado de tema (claro/oscuro) y lo recordara entre sesiones.
      * **Solución:** Se utilizó SharedPreferences para guardar la elección del usuario (un simple booleano isNightMode). En el onCreate de la MainActivity, se lee esta preferencia y se aplica el tema correspondiente usando AppCompatDelegate.setDefaultNightMode() **antes** de que la vista sea creada con setContentView(). El botón de cambio simplemente invierte el valor guardado en SharedPreferences y llama a la misma función para recrear la Activity con el nuevo tema.
