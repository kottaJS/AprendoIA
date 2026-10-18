# Aprendo IA Jugando en Santiago 📱🤖
**Evaluación Parcial 2 - Encargo (8%)**  
**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105)  
**Sección:** 006D  
**Institución:** Duoc UC - Sede Padre Alonso de Ovalle  
**Organización Caso de Estudio:** Triskeledu SpA  

---

## 👥 Integrantes del Equipo
* **Maximiliano Dinamarca** ([@kottaJS](https://github.com/kottaJS))
* **Matías Carrasco** ([@matiascarrasco-gif](https://github.com/matiascarrasco-gif))
* **Constanza Arellano** ([@coniarellano04](https://github.com/coniarellano04))

---

## 📌 1. Descripción del Proyecto y Caso de Negocio
Triskeledu SpA es una academia de educación tecnológica orientada a niños y adolescentes. Anteriormente, los coordinadores y operarios en terreno registraban visitas y coordenadas de manera manual o por mensajería instantánea, provocando desorganización y pérdidas de información.

**"Aprendo IA Jugando en Santiago"** es una aplicación móvil nativa en Android desarrollada con **Jetpack Compose**, diseñada para:
1. **Gamificar el aprendizaje de Inteligencia Artificial:** Los usuarios recorren puntos estratégicos e hitos patrimoniales de Santiago (La Moneda, Plaza de Armas, Duoc Alonso de Ovalle, Cerro Santa Lucía, etc.), consumen cápsulas educativas de IA y acumulan puntaje.
2. **Validar misiones en terreno con hardware nativo:** 
   - **Cámara:** Permite sacarse selfies turísticas de validación en cada punto (+30 pts extra) y acceder a códigos QR.
   - **GPS / Localización:** Captura las coordenadas geográficas exactas en tiempo real.
3. **Persistencia Local Offline (SQLite):** Todos los registros de puntos, progreso y datos de usuario se almacenan en una base de datos local embebida en el dispositivo móvil.

---

## 🚀 2. Características Técnicas Implementadas (Rúbrica Oficial)

### 🎨 IE 2.1.1 - Diseño Visual y Navegación
* Implementación 100% declarativa con **Jetpack Compose** y **Material 3**.
* Navegación completa mediante:
  * **Menú Lateral Hamburguesa (`ModalNavigationDrawer`)** con cabecera de perfil.
  * **Barra Superior (`TopAppBar`)** con indicador dinámico de estrellas/puntaje y botón de sincronización.
  * **Barra Inferior (`NavigationBar`)** con cambio fluido entre pantallas (`Inicio`, `Misiones`, `Nuevo Punto`).

### 📝 IE 2.1.2 - Formularios con Validación Visual en Tiempo Real
* Pantallas de **Login** y **Formulario de Registro de Terreno**.
* Todos los campos de entrada (`OutlinedTextField`) cuentan con:
  * Propiedad `isError` que resalta el borde en rojo ante datos inválidos.
  * `supportingText` con mensajes de error específicos por campo.
  * Íconos visuales de contexto (`Person`, `Edit`, `LocationOn`, `Add`).

### 🧠 IE 2.2.1 - Arquitectura MVVM y Lógica Desacoplada
* Organización estricta de carpetas:
  * `model/`: Entidades de datos (`Usuario`, `PuntoInteres`, `CapsulaIA`).
  * `data/`: Persistencia local con SQLite (`AprendoIADbHelper`).
  * `viewmodel/`: Gestión de estado centralizado (`AprendoIAViewModel`) desacoplado de las vistas.
  * `ui/`: Pantallas (`screens/`), componentes (`components/`) y estilos (`theme/`).
* Toda la validación lógica reside en el `ViewModel`, manteniendo las funciones `@Composable` puramente declarativas.

### ✨ IE 2.2.2 - Animaciones Funcionales
* Implementadas en [`Animaciones.kt`](app/src/main/java/cl/duoc/aprendoia/ui/components/Animaciones.kt) con `rememberInfiniteTransition`:
  * **Latido / Escala (`Scale`):** En botones de acción para fomentar el clic interactivo.
  * **Rebote (`Bounce / TranslationY`):** En tarjetas de misiones destacadas.
  * **Rotación (`RotationZ 360°`):** En el ícono de refrescar/sincronizar de la barra superior.
  * **Destello de Opacidad (`Alpha`):** En las notificaciones de éxito al guardar.

### 💾 IE 2.3.1 - Persistencia de Datos Local (SQLite)
* Base de datos local embebida (`aprendo_ia.db`) utilizando `SQLiteOpenHelper`.
* Tablas creadas:
  * `puntos`: Almacena el listado georreferenciado, estado de completado, selfies tomadas y códigos QR.
  * `usuario`: Guarda el perfil y puntaje acumulado.
* **Comportamiento persistente:** La base de datos puebla datos iniciales la primera vez y conserva todas las modificaciones entre sesiones, incluso tras cerrar o reiniciar la aplicación.

### 📷📍 IE 2.4.1 - Acceso a Recursos Nativos (Cámara y GPS)
1. **Cámara Nativa:** Integrada mediante `rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview())`. Al presionar *"Selfie (+30)"*, se invoca la cámara del teléfono, se captura la fotografía y se despliega una miniatura visual en la tarjeta de la misión.
2. **GPS / Localización:** Integrada mediante solicitud de permiso en tiempo de ejecución (`ACCESS_FINE_LOCATION`) y conexión al `LocationManager` del sistema para rellenar las coordenadas exactas de latitud y longitud en el formulario de terreno.

---

## 🛠️ 3. Pasos para Clonar y Ejecutar el Proyecto

### Requisitos Previos:
* Android Studio (versión Hedgehog, Iguana, Jellyfish o Ladybug).
* JDK 11 o superior configurado.
* Dispositivo móvil Android físico con **Depuración por USB habilitada** (o emulador con API 26+).

### Pasos de Ejecución:
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/kottaJS/AprendoIA.git
   ```
2. Abrir **Android Studio**.
3. Seleccionar **File > Open...** y navegar hasta la carpeta `AprendoIA`.
4. Esperar que Gradle sincronice las dependencias del proyecto.
5. Conectar el teléfono físico por USB (asegúrate de que aparezca seleccionado en la barra superior de Android Studio).
6. Presionar el botón verde **Run 'app'** (`Shift + F10`).
