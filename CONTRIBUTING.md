# Guía de Contribución – Granotec Inventory System

Este documento explica el flujo de trabajo en Git para colaborar en el proyecto **Granotec Inventory System** usando GitHub.  
Nuestro stack: **Spring Boot + Angular + MySQL**, con ramas organizadas para un desarrollo ordenado.


## 🌳 Estructura de ramas
- **`main`** → Rama **estable (producción)**.  
  Solo recibe código probado desde `develop`.

- **`develop`** → Rama **de integración**.  
  Aquí se unen todas las funcionalidades antes de pasar a `main`.

- **`feature/...`** → Ramas **individuales de desarrollo**.  
  Cada desarrollador crea una rama por tarea/feature (ejemplo: `feature/login`, `feature/productos`).

-----------------------------------------------------------


# --- PRIMERA VEZ (solo la primera vez) ---
# Clonar el proyecto
git clone https://github.com/Erick-98/Granotec.git
cd Granotec

# Cambiarse a la rama de desarrollo
git checkout develop

# --- PARA CREAR UNA NUEVA TAREA ---
# Asegurarse de estar en develop y actualizar 
git checkout develop

# Traer lo último desde Github  
git pull origin develop

# Crear una nueva rama de tarea (ejemplo: login de usuarios)
git checkout -b feature/login

Ejemplos:
feature/login
feature/productos
feature/api-clientes

# ---Realiza tus cambios en esa rama. ---
# --- CUANDO HAGAN CAMBIOS ---
# Agregar archivos al commit
git add .

# Crear el commit con un mensaje claro
git commit -m "feat(login): implementar pantalla de login"

# Subir la rama al remoto (GitHub)
git push -u origin feature/login

# CUANDO TEMRMINEN UNA FUNCIONALIDAD
1. Ir a github y abrir un pull request (PR)
    - De su rama a develop
    - Agregar descripción del cambio
2. Avisar por el grupo de whastapp para aceptar
3. Una vez aceptado
4. Eliminar rama local 
    - git checkout develop
    - git pull origin develop
    - git branch -d nombre-de-rama
5. Eliminar rama en github (si les deja hacerlo)
    - git push origin --delete nombre-de-rama

# --- ANTES DE EMPEZAR UN NUEVO DÍA DE TRABAJO ---
git checkout develop
git pull origin develop


# ---Reglas generales ---

Nunca trabajes directamente en main ni en develop.
Una tarea = una rama feature/....
Commits pequeños y claros → más fácil de revisar.
Antes de empezar una tarea, siempre actualiza tu develop local:
git checkout develop
git pull origin develop