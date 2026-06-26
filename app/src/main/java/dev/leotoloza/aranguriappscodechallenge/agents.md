# Contexto y Reglas generales del Proyecto "DisneyApp"

## Contexto del Proyecto
- **Proyecto**: Aplicación móvil android native "DisneyApp" (explorador de personajes de disney).
- **Tecnologías**: Kotlin 2.2.10, Jetpack Compose, Retrofit2, Dagger Hilt, Moshi, Coil, Navigation3.
- **Arquitectura**: Modular, MVVM + repository pattern, clean architecture.
- **API**: Los datos remotos se extraen de la REST api pública de disney "https://disneyapi.dev/docs/"
- **Testing**: Pirámide de tests con librerías Junit y (a definir por el agente de IA).

---

## 🛑 Reglas Estrictas para la IA (Sesiones de Desarrollo)

1. **Cero Hacks y Cambios Core**: No implementar "hacks" (soluciones rápidas o temporales de mala calidad) bajo ninguna circunstancia. Tampoco modificar la lógica core del negocio sin pedir autorización explícita cada vez.

2. **Arquitectura y Calidad**: Al desarrollar una feature o arreglar algo, respetar estrictamente las reglas de la arquitectura del proyecto, las buenas prácticas de Kotlin, Jetpack Compose y los principios SOLID.

3. **Honestidad Brutal y Certeza**: Al responder preguntas, ser brutalmente honesto. No intentar complacer ni dar la razón porque sí. **Obligatorio**: incluir siempre un porcentaje de certeza en la respuesta. Si el porcentaje es menor al 90%, es obligatorio repreguntar y aclarar las dudas antes de dar una respuesta definitiva.

4. **Testing Riguroso**: No implementar hacks bajo ninguna circunstancia solo para hacer que un test pase. Respetar las buenas prácticas de testing. No forzar el comportamiento de los tests importando funciones extrañas. No implementar nuevas librerías de testing ni recurrir a atajos sin consultar explícitamente cada vez.

5. **Clean Code**: Respetar estrictamente las siguientes reglas de calidad de código.
    - **Evitar literales/magic numbers**: Evitar el uso de literales ("magic strings") o números mágicos dentro de condiciones `if` o lógica de negocio. Extraerlos a constantes o utilizar `enums` según corresponda.
    - **Documentación Kdoc**: Asegurar que todas las clases públicas e interfaces, así como los métodos expuestos y propiedades de anotaciones (como `message()`, `groups()`, `payload()`), cuenten con su comentario Kdoc (el equivalente a Javadoc para kotlin).
    - **Múltiples Strings**: Evitar repetir el mismo literal String múltiples veces; extraer a una constante local o de clase.
    - **Imports al inicio**: No dejar nombres de clases completamente calificados (fully qualified names, ej. `java.util.Map`) inline dentro del código. Todos los imports deben declararse al inicio del archivo.

6. **Comunicación**:
    - **Lenguaje**: Todo el código generado, así como nombres de clases, funciones, variables etc, debe ir en inglés, sin embargo, la UX debe estar dirigida a usuarios que hablan español, por lo tanto la UI debe estar en español.
    - **Comentarios**: Los comentarios del código deben ir en español.

7. **Diseño de Servicios (Clean Code)**:
    - Respetar el Principio de "Single Level of Abstraction". Extraer mapeos manuales extensos (múltiples setters) a métodos privados auxiliares (como `toEntity(...)`) para mantener los métodos principales limpios y cortos.
    - Mantener consistencia de tipos: si la BD usa `UUID`, emplear `UUID` en todos los Response/Request DTOs para el ID, evitando desajustes con tipos `String`.

8. **Testing Aislado y Riguroso**:
    - **SRP en Tests**: Mantener la responsabilidad única en las pruebas. No agrupar múltiples escenarios independientes (ej. distintos campos inválidos que retornan 400) en un solo método `@Test`. Crear un test individual por escenario para que el fallo de uno no impida la ejecución de los demás.

9. **Librerías y plugins**:
   -**Nuevas librerías**: Para agregar una nueva dependencia tenés que pedir permiso, no agregues nuevas librerías sin previo aviso. Usar siempre "Gradle's Version Catalogs feature" definiendo las versiones en "libs.versions.toml".