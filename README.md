# Catálogo de Campeones y Habilidades (ACB)

## Descripción del Proyecto

El **Catálogo de Campeones y Habilidades (ACB)** es una aplicación Java diseñada para gestionar información relacionada con campeones y habilidades en un juego ficticio. El proyecto se organiza en tres entidades principales: Regiones, Campeones y Habilidades. Proporciona funcionalidades como agregar, eliminar, editar, listar y cargar datos desde archivos CSV.

## Estructura del Proyecto

El proyecto está dividido en varias clases que cumplen funciones específicas:

- **ACBMain:** Contiene el método principal `main` que inicia la aplicación y gestiona las interacciones con el usuario.
- **ACBMenu:** Implementa el menú interactivo para que el usuario pueda seleccionar las acciones deseadas.
- **RegionController:** Gestiona las operaciones relacionadas con las regiones del juego.
- **CampeonController:** Maneja las operaciones relacionadas con los campeones del juego.
- **HabilidadController:** Se encarga de las operaciones relacionadas con las habilidades de los campeones.

## Funcionalidades Principales

1. **Regiones:**
   - Agregar y eliminar regiones.
   - Listar regiones ordenadas por nombre, id o historia relacionada.
   - Editar regiones mediante la interacción con el usuario.

2. **Campeones:**
   - Agregar y eliminar campeones.
   - Listar campeones ordenados por nombre, apodo, id, región, rol, raza o dificultad.
   - Editar campeones mediante la interacción con el usuario.

3. **Habilidades:**
   - Agregar y eliminar habilidades.
   - Listar habilidades ordenadas por nombre, id, campeón, pasiva o asignación de tecla.
   - Editar habilidades mediante la interacción con el usuario.

4. **Carga desde Archivos CSV:**
   - Permite cargar datos de regiones, campeones y habilidades desde archivos CSV.

5. **Vaciar Tablas:**
   - Permite vaciar completamente las tablas de habilidades, campeones y regiones, reiniciando las secuencias.

## Instrucciones de Uso

1. Ejecutar el programa desde la clase `ACBMain`.
2. Seleccionar las opciones del menú para realizar las operaciones deseadas.
3. Seguir las instrucciones en la consola para interactuar con la aplicación.

## Requisitos

- Java 8 o superior.
- Base de datos PostgreSQL (se debe configurar la conexión en `ConnectionFactory`).

## Autor

Este proyecto fue desarrollado por Arnau Baena Perez.

## Agradecimientos

Agradecemos a OpenCSV y PostgreSQL por proporcionar herramientas útiles para el desarrollo de este proyecto.
