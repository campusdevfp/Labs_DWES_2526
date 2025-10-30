# Práctica de bienvenida a DWES

[](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#pr%C3%A1ctica-de-bienvenida-a-dwes-y-daw-2024-2025)

-   [Práctica de bienvenida ](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#pr%C3%A1ctica-de-bienvenida-a-dwes-y-daw-2024-2025)
    -   [Acerca de](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#acerca-de)
    -   [Enunciado](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#enunciado)
    -   [Restricciones](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#restricciones)
    -   [Entrega](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#entrega)
    -   [Recursos](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#recursos)

## Acerca de

[](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#acerca-de)

Esta práctica tiene como objetivo ser una evaluación inicial sobre contenidos de 1º DAW.

Como bien sabes, para programar al lado de servidor debemos tener unos conceptos mínimos siempre en mente.

Te puedes apoyar en los apuntes de 1º DAW de Programación, Entornos de Desarrollo, Bases de Datos y Lenguajes de Marcas y Sistemas de Información. Pero yo no he sido el profesor de todos esos módulos, por lo que deberás revisarlos y responsabilizarte de lo aprendido en 1º DAW ya sea en este instituto o en otro.

## Enunciado

[](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#enunciado)

Como futuros desarrolladores de backend, te invito a que hagas una pequeña práctica de bienvenida a DWES y DAW. Para ello debemos hacer un aplicación con la siguiente funcionalidad y tenga las siguientes restricciones:

-   Es una aplicación en consola, que se llamara  `torneo_tenis.jar`

-   Esta aplicación acepta como máximo dos parámetros: fichero_entrada.csv y fichero_salida.xxx

    -   `fichero_entrada.csv`  contendrá los datos de los jugadores de tenis que participaran en la competición y siempre es obligatorio. Su extensión válida es .csv. El path debe ser válido en sistemas de archivos del sistema operativo actual.
    ```
    nombre,pais,altura,peso,puntos,mano,fecha_nacimiento
    Rafael Nadal,España,185,85,9500,DIESTRO,1986-06-03
    Novak Djokovic,Serbia,188,80,12000,DIESTRO,1987-05-22
    Carlos Alcaraz,España,183,74,8700,DIESTRO,2003-05-05
    Roger Federer,Suiza,185,81,11000,DIESTRO,1981-08-08
    Dominic Thiem,Austria,185,79,7200,ZURDO,1993-09-03
    Andy Murray,Reino Unido,190,84,8000,DIESTRO,1987-05-15
    ```

    -   `fichero_salida.xxx`  solo puede tener de extensión .csv, .json o .xml. El path debe ser válido en sistemas de archivos del sistema operativo actual. Si no escribes un path, el fichero se guardará en el directorio actual, con json como formato por defecto y con el nombre torneo_tenis.json.

    ```bash
        id,nombre,pais,altura,peso,puntos,mano,fecha_nacimiento,created_at,updated_at
        1,Rafael Nadal,España,185,85,9500,DIESTRO,1986-06-03,2025-09-23T10:30:01.123456,2025-09-23T10:30:01.123456
        2,Novak Djokovic,Serbia,188,80,12000,DIESTRO,1987-05-22,2025-09-23T10:30:01.123456,2025-09-23T10:30:01.123456
        3,Carlos Alcaraz,España,183,74,8700,DIESTRO,2003-05-05,2025-09-23T10:30:01.123456,2025-09-23T10:30:01.123456
    ```

```
java -jar torneo_tenis.jar fichero_entrada.csv fichero_salida.json
```


-   El programa debe contener una base de datos en ficheros del tipo SQLite o H2 para almacenar la información, para ello tendremos una tabla llamada tenistas con la siguiente información (te recomiendo analizar bien los tipos de datos):

    -   id (autonumérico)
    -   nombre: nombre del tenista completo
    -   pais: nombre del país
    -   altura: altura del tenista en cm
    -   peso: peso del tenista en kg
    -   puntos: puntos totales del tenista
    -   mano: mano del tenista, DIESTRO o ZURDO
    -   fecha_nacimiento: fecha de nacimiento del tenista, en formato AAAA-MM-DD (ISO 8601)
    -   created_at: fecha de creación del tenista, en formato AAAA-MM-DDTHH:MM:SS.SSSSSSS (ISO 8601)
    -   updated_at: fecha de actualización del tenista, en formato AAAA-MM-DDTHH:MM:SS.SSSSSSS (ISO 8601)
-   El programa debe leer el fichero de entrada e insertar los datos en la base de datos. El fichero de entrada debe ser abierto en modo lectura y debes analizar que todos los datos son válidos y están en formato correcto antes de ser insertados.

-   Además, tendremos una caché FIFO de la base de datos con tamaño de 5 elementos.

-   El programa debe generar un fichero de salida con la información de la base de datos.

-   Antes de ofrecer la salida debe mostrar en la consola las siguientes consultas usando api de colecciones:

    -   tenistas ordenados con ranking, es decir, por puntos de mayor a menor
    -   media de altura de los tenistas
    -   media de peso de los tenistas
    -   tenista más alto
    -   tenistas de España
    -   tenistas agrupados por país
    -   número de tenistas agrupados por país y ordenados por puntos descendente
    -   numero de tenistas agrupados por mano dominante y puntuación media de ellos
    -   puntuación total de los tenistas agrupados por país
    -   país con más puntuación total
    -   tenista con mejor ranking de España.

## Restricciones

[](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#restricciones)

-   **No hacer**: Se debe programar siguiendo el patrón ROP (Railway Oriented Programming) con un sistema de errores tipados al dominio, o usar excepciones dentro del dominio.
-   En todo momento debe haber un validador de datos que verifique que los datos antes de ser introducidos en la base de datos sean correctos.
-   Se debe implementa el repositorio de almacenamiento con las operaciones de selección completa, selección por id, selección por país, selección por ranking, inserción, actualización y borrado. Las salidas de listado deberán estar ordenadas por ranking ascendente. El ranking se calcula en base a puntos, tendrá mayor ranking cuando tengan más puntos.
-  **No hacer**: Se debe testear todos los elementos realizados y asegurar un 90% de cobertura de los elementos funcionales ya sea usando test unitarios y test de integración. Apóyate de una librería de test con dobles o mocks cuando sea necesario.
-  **No hacer**: Se debe usar un método manual o automatizado de inyección de dependencias.
-   La base de datos debe vaciarse a iniciarse la aplicación.
-   Los parámetros de configuración y uso de la base de datos deben estar en un fichero de  `.properties`.
-   El despliegue de la aplicación debe ser en un ejecutable .jar con el nombre torneo_tenis.jar.
-   Se debe comentar tu código y obtener esta documentación en html (puedes usar herramientas como Dokka o JavaDoc).
-   Se debe trabajar con Git y Github.
-   El lenguaje de programación debe ser Java y usar JDK 17 o superior (recomendable 21LTS).
-   Se debe usar log, pero los log por pamntalla para DEBUG, los niveles INFO o superiores deben a ir a un fichero en el directorio /log de la aplicación.
-   Se debe implementar las operaciones de colecciones usando la API de colecciones funcional de Java.
-   **Opcional:** La consola en su ejecución debe tener colores.
-   Haznos un favor a todos y usa  `.gitignore`  de manera correcta.

## Entrega

[](https://github.com/joseluisgs/practica-bienvenida-dwes-daw-2024-2025?tab=readme-ov-file#entrega)

-   Proyecto mediante  github classroom. El subdirectorio que debes hacer es apellido_apellido_nombre.
-   En ficho directorio debe estar el proyecto y un fichero README.md.
-   En el fichero README.md debe tener:
    -   Descripción del proyecto.
    -   Arquitectura usada.
    -   Enumerar los 5 principios SOLID y poner 2 ejemplos con capturas de tu código donde los usases.
    -   Justificación de las librerías usadas.


