# Aprendo IA Jugando en Santiago

Proyecto para la Evaluacion Parcial 2 de la asignatura Desarrollo de Aplicaciones Moviles (DSY1105, seccion 006D), Duoc UC Sede Padre Alonso de Ovalle.

Caso de estudio: Triskeledu SpA.

## Integrantes
- Maximiliano Dinamarca ([@kottaJS](https://github.com/kottaJS))
- Matias Carrasco ([@matiascarrasco-gif](https://github.com/matiascarrasco-gif))
- Constanza Arellano ([@coniarellano04](https://github.com/coniarellano04))

## Descripcion del Proyecto
Aplicacion movil nativa desarrollada en Android con Jetpack Compose para gamificar el aprendizaje de Inteligencia Artificial recorriendo puntos turisticos e hitos de Santiago. Permite validar visitas en terreno mediante fotos y coordenadas gps, consumiendo capsulas educativas y guardando el progreso de manera local.

## Funcionalidades Principales
- Diseno con Jetpack Compose y Material 3 (modo claro y oscuro).
- Navegacion completa con barra inferior y menu lateral drawer.
- Formularios con validacion en tiempo real (login y registro de nuevos puntos en terreno).
- Arquitectura MVVM separando la logica de negocio en ViewModel del estado de las pantallas.
- Base de datos local SQLite mediante SQLiteOpenHelper para persistir usuarios y puntos de interes de forma offline.
- Recursos nativos:
  - Camara para capturar fotos de validacion en cada mision.
  - GPS / Ubicacion para capturar latitud y longitud actuales en el formulario.
  - Lector de codigos QR con Google Play Services Code Scanner para validar JSON educativos y reproducir videos.
- Animaciones para transiciones y botones interactivos.

## Estructura del Proyecto
- `ui/screens`: Pantallas principales (Home, Login, Misiones, Escaneo QR, Descubrimientos, Formulario).
- `ui/components`: Componentes reutilizables y animaciones.
- `ui/theme`: Configuracion de colores, tipografia y tema Material 3.
- `viewmodel`: Estado y logica desacoplada de la interfaz.
- `data`: Helper de SQLite y consultas locales.
- `model`: Modelos de datos (PuntoInteres, Usuario, CapsulaIA).

## Requisitos y Ejecucion
- Android Studio Iguana, Jellyfish o Ladybug.
- Dispositivo Android fisico con depuracion USB o emulador (API 26 o superior).
- JDK 17 o superior.

### Pasos:
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/kottaJS/AprendoIA.git
   ```
2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar que Gradle descargue las dependencias y sincronice el proyecto.
4. Conectar un dispositivo fisico o iniciar un emulador.
5. Ejecutar con Run 'app'.
