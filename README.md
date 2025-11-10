<!-- Encabezado -->
<div align="center">

# Widget Inventory
### Sistema de Gestión de Inventarios Móviles

Aplicación <strong>Android profesional</strong> para <strong>gestión de inventarios</strong> con <strong>widget interactivo</strong>, <strong>autenticación biométrica</strong> y <strong>base de datos local</strong>.

---

<!-- Badges -->
<img alt="Android" src="https://img.shields.io/badge/Android-Studio-green?style=for-the-badge&logo=android" />
<img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-1.8%2B-purple?style=for-the-badge&logo=kotlin" />
<img alt="Supabase" src="https://img.shields.io/badge/Supabase-3FCF8E?style=for-the-badge&logo=supabase&logoColor=white" />
<img alt="License" src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" />

---

 <strong>Versión actual:</strong> 1.0.0  

##  Autores y Colaboradores

| Rol | Nombre | Descripción |
|------|---------|-------------|
| 💡 <strong>Desarrollador</strong> | <strong>Daniel Márquez</strong> | Desarrollo y testing |
| 💡 <strong>Desarrollador</strong> | <strong>Juan Manuel Moreno</strong> | Desarrollo principal|
| 💡 <strong>Desarrollador</strong> | <strong>Daniel Rueda</strong> | Base de datos y lógica de negocio |
| 💡 <strong>Desarrollador</strong> | <strong>Brayan Fernandez</strong> | UI/UX y diseño de interfaces |
| 💡 <strong>Desarrollador</strong> | <strong>Juan José Alvarez</strong> | Autenticación y seguridad |

**Institución:** Facultad de Ingeniería de Sistemas y Computación  
**Curso:** Desarrollo de Aplicaciones para Dispositivos Móviles  
**Miniproyecto:** 1 - Sprint 1  
**Docente:** Ing. Walter Medina  
**Fecha:** Octubre 26 de 2025

</div>

---

## Descripción General
Widget Inventory es una aplicación móvil robusta para la gestión completa de inventarios, diseñada con arquitectura moderna y mejores prácticas de desarrollo Android. El sistema permite:

- Visualizar inventario mediante widget interactivo
- Autenticación segura con biometría dactilar
- Gestión completa de productos (CRUD)
- Cálculos automáticos de valores de inventario
- Persistencia local con Room Database
- Interfaz de usuario moderna y responsive

## Diagrama de Flujo
[Widget] → [Login Biometrico] → [Home Inventario] →
├─ [Agregar Producto] → Room Database
├─ [Detalle Producto] → Editar/Eliminar
└─ [Cálculos Automáticos] → Widget Update

text

## Estructura de Directorios
```bash
/WidgetInventory/
├── app/
│   ├── src/main/
│   │   ├── java/com/widget/inventory/
│   │   │   ├── ui/                    # Activities y Fragments
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   ├── HomeActivity.kt
│   │   │   │   ├── AddProductActivity.kt
│   │   │   │   ├── DetailActivity.kt
│   │   │   │   └── EditActivity.kt
│   │   │   ├── data/                  # Capa de datos
│   │   │   │   ├── database/
│   │   │   │   │   ├── InventoryDatabase.kt
│   │   │   │   │   ├── entities/
│   │   │   │   │   └── dao/
│   │   │   │   └── repository/
│   │   │   ├── widget/                # Widget de la aplicación
│   │   │   │   ├── InventoryWidget.kt
│   │   │   │   └── WidgetProvider.kt
│   │   │   ├── biometric/             # Autenticación biométrica
│   │   │   │   └── BiometricManager.kt
│   │   │   └── utils/                 # Utilidades
│   │   │       └── Formatters.kt
│   │   ├── res/                       # Recursos
│   │   │   ├── layout/               # Layouts XML
│   │   │   ├── drawable/             # Imágenes y vectores
│   │   │   ├── values/               # Colores, strings, styles
│   │   │   └── xml/                  # Configuraciones
│   │   └── AndroidManifest.xml
├── build.gradle
└── gradle.properties
Instalación y Configuración
Clonar el proyecto en Android Studio:
```
bash
git clone <repository-url>
Sincronizar dependencias Gradle:
```
```
// Dependencias principales en build.gradle
implementation "androidx.room:room-runtime:2.5.0"
implementation "androidx.biometric:biometric:1.1.0"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0"
Configurar permisos en AndroidManifest.xml:
```
```xml
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
Historias de Usuario Implementadas
HU 1.0: Creación Widget app Inventory
Actor: Aplicación
Objetivo: Generar widget interactivo para acceso rápido al inventario

Criterios implementados:

 Widget con fondo negro transparente (#CC000000) y bordes redondeados

 Logo de la aplicación y texto "Inventory" en naranja (#e7522e)

 Texto "¿Cuánto tengo de inventario?" en blanco

 Zona interactiva para mostrar/ocultar saldo con ícono de ojo

 Cálculo automático del saldo total del inventario

 Formato de saldo con separadores de miles (3.326.000,00)

 Navegación a ventana de Login desde widget

HU 2.0: Ventana Login
Actor: App
Objetivo: Sistema de autenticación biométrica para acceso seguro

Criterios implementados:

 Interfaz sin toolbar con fondo gris oscuro

 Imagen alusiva al inventario y título "Inventory"

 Autenticación con huella digital mediante Lottie animations

 Ventana emergente de autenticación biométrica

 Manejo de sesiones con SharedPreferences

 Ícono personalizado para la aplicación

HU 3.0: Ventana Home Inventario
Actor: Usuario
Objetivo: Visualización completa de productos en inventario

Criterios implementados:

 Persistencia de sesión con SharedPreferences

 Toolbar gris (#424242) con título "Inventario"

 Ícono de cerrar sesión funcional

 Lista de productos con cards blancos y bordes redondeados

 Progress circular naranja durante carga

 FAB naranja para agregar productos

 Navegación a detalles del producto

HU 4.0: Ventana Agregar Producto
Actor: Usuario
Objetivo: Adición de nuevos productos al inventario

Criterios implementados:

 Formulario con campos validados (código, nombre, precio, cantidad)

 Text fields con hints animados y validación en tiempo real

 Botón guardar que se habilita solo con todos los campos completos

 Persistencia en Room Database

 Actualización automática de la lista principal

HU 5.0: Ventana Detalle del Producto
Actor: Usuario
Objetivo: Visualización detallada y gestión de productos individuales

Criterios implementados:

 Tarjeta informativa con todos los datos del producto

 Cálculo automático del total (precio × cantidad)

 Diálogo de confirmación para eliminación

 FAB para edición del producto

 Actualización en tiempo real del widget

HU 6.0: Ventana Editar Producto
Actor: Usuario
Objetivo: Modificación de productos existentes en el inventario

Criterios implementados:

 Formulario pre-cargado con datos actuales

 ID del producto no editable

 Validación de campos obligatorios

 Botón editar que se habilita solo con datos válidos

 Actualización inmediata en base de datos y vistas
```
Características Técnicas
Arquitectura
Patrón: MVVM (Model-View-ViewModel)

Persistencia: Room Database 

Biometría: Android Biometric API

Widget: AppWidgetProvider con actualizaciones automáticas

Seguridad
Autenticación biométrica nativa

Persistencia segura de sesiones

Validación de datos en capa de presentación y negocio

UX/UI
Diseño Material Design 3

Animaciones Lottie para interacciones

Colores corporativos (#e7522e naranja, #424242 gris)

Responsive para diferentes densidades de pantalla

<div align="center">
© 2025 Widget Inventory - Facultad de Ingeniería de Sistemas y Computación

</div> 
