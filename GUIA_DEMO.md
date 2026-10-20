# Guía de la demo — Azure DevOps Básico

**Proyecto:** Order Status API · **Fecha:** 21 de octubre de 2026 · **Duración sugerida de la demo:** 12–15 min

---

## 0. ⚠️ Lo primero (hoy mismo)

**Organizaciones nuevas de Azure DevOps NO traen agentes de Microsoft gratis activados.** Si tu pipeline se queda en *"No hosted parallelism has been purchased or granted"*, tienes dos opciones:

| Opción | Cuándo | Qué hacer |
|---|---|---|
| A. Org existente (Accenture / una que ya usaste) | Si ya corre pipelines | Usa `azure-pipelines.yml` tal cual |
| B. Solicitar grant gratuito | Tarda 2–3 días hábiles → **no llega para mañana** | Formulario: aka.ms/azpipelines-parallelism-request |
| C. **Agente propio en tu laptop** (recomendado si es org nueva) | Funciona en 15 min | Ver sección 2.4 y usa `azure-pipelines-selfhosted.yml` |

Haz una corrida completa de prueba **hoy**. Mañana solo repites.

---

## 1. Archivos del repositorio

```
order-status-api/
├── pom.xml                         # Java 17 + JUnit 5
├── azure-pipelines.yml             # CI con agente de Microsoft (Maven@4)
├── azure-pipelines-selfhosted.yml  # CI con agente propio (pool Default)
├── .gitignore
├── README.md
├── src/main/java/com/demo/orders/
│   ├── OrderStatusNormalizer.java  # Versión INICIAL: sin trim() (el "bug")
│   └── App.java
├── src/test/java/com/demo/orders/
│   └── OrderStatusNormalizerTest.java  # 2 pruebas iniciales
└── demo-cambios/                   # ⚠️ NO subir a main. Es tu "acordeón" para la demo
    ├── OrderStatusNormalizer.java      # Versión FINAL con trim()
    └── OrderStatusNormalizerTest.java  # + prueba eliminaEspaciosExternos
```

**El cambio de la demo es una sola línea:**

```java
// Antes
return rawStatus.toUpperCase();
// Después
return rawStatus.trim().toUpperCase();
```

Más una prueba nueva que representa el criterio de aceptación:

```java
@Test
void eliminaEspaciosExternos() {
    assertEquals("SHIPPED", normalizer.normalize("  shipped  "));
}
```

---

## 2. Preparación en Azure DevOps (hoy)

### 2.1 Organización y proyecto
1. Entra a **dev.azure.com** → *New organization* (o usa una existente).
2. **+ New project**
   - Name: `Order Status API`
   - Visibility: Private
   - Advanced → Version control: **Git** · Work item process: **Agile** (así aparece "User Story" y el campo *Acceptance Criteria*, igual que tu slide 11).

### 2.2 Subir el repo
1. **Repos → Files** → copia la URL de clonación → **Generate Git Credentials** (o usa tu cuenta Microsoft).
2. En tu laptop (el `.gitignore` ya excluye `demo-cambios/` y esta guía):

```bash
cd order-status-api
git init -b main
git add .
git commit -m "Initial commit: Order Status API"
git remote add origin https://dev.azure.com/<org>/Order%20Status%20API/_git/Order%20Status%20API
git push -u origin main
```


### 2.3 Crear la pipeline
1. **Pipelines → New pipeline** → *Azure Repos Git* → selecciona el repo.
2. *Existing Azure Pipelines YAML file* → rama `main` → `/azure-pipelines.yml` (o `-selfhosted.yml`).
3. **Run**. Debe terminar en verde con **2 pruebas pasadas** (pestaña *Tests*).
4. Renómbrala: ⋯ → Rename → `order-status-api-CI`.

### 2.4 (Solo opción C) Agente propio en tu laptop
1. Requisitos: **JDK 17** y **Maven** en el PATH (`java -version`, `mvn -v`).
2. Avatar → **Personal access tokens** → New token → scope **Agent Pools (Read & manage)**. Guárdalo, no lo pongas en ningún archivo.
3. **Project settings → Agent pools → Default → New agent** → descarga el zip de tu SO.
4. Descomprime en `C:\agent` y ejecuta:
   ```
   .\config.cmd     (URL: https://dev.azure.com/<org>, auth: PAT, pool: Default)
   .\run.cmd
   ```
5. Deja la consola abierta durante la demo. El agente debe verse **Online**.

### 2.5 Políticas de rama en `main` (slide 13)
**Repos → Branches → main → ⋯ → Branch policies**

| Política | Valor |
|---|---|
| Require a minimum number of reviewers | 1 · ✅ *Allow requestors to approve their own changes* (estás solo en la demo) |
| Check for linked work items | **Required** |
| Check for comment resolution | **Required** |
| Limit merge types | Solo **Squash merge** |
| Build validation → + | Pipeline `order-status-api-CI` · Trigger: Automatic · **Required** |

> Esto explica tu slide 22: en Azure Repos, la ejecución en PRs **no** se controla desde el YAML (`pr:`), sino con *Build validation*.

### 2.6 Boards (opcional pero se ve bien)
- Crea un **Epic** "Gestión de pedidos" y una **Feature** "API de estados de pedido".
- **Project settings → Team configuration → Iterations**: asegúrate de que exista *Sprint 1* con fechas que incluyan el 21/10.
- **NO** crees la User Story todavía: la creas en vivo.

### 2.7 Ensayo general
Haz el flujo completo una vez (sección 3), luego **borra la rama y abandona el PR** o crea una segunda historia para mañana. Así llegas con main limpio pero sabiendo que todo funciona.

---

## 3. Demo en vivo — paso a paso

### Antes de empezar (checklist)
- [ ] Navegador con 3 pestañas: **Boards**, **Repos**, **Pipelines**
- [ ] IntelliJ abierto con el repo en `main` actualizado (`git pull`)
- [ ] Agente Online (si es self-hosted)
- [ ] Zoom del navegador al 125–150 %
- [ ] Notificaciones silenciadas
- [ ] Capturas de una corrida exitosa en una carpeta (plan B)

### D1 — Azure Boards (≈3 min)
1. **Boards → Work items → New Work Item → User Story**
2. Llena:
   - **Title:** `Normalizar estado de pedido`
   - **Description:** Como consumidor de la API, necesito recibir estados de pedido normalizados para interpretar la información de manera consistente.
   - **Acceptance Criteria:**
     - Given: un estado de pedido con espacios adicionales (`"  shipped  "`).
     - When: el servicio procesa el valor.
     - Then: devuelve `"SHIPPED"`, sin espacios externos y en mayúsculas.
   - **Assigned To:** tú · **Priority:** 1 · **Story Points:** 2 · **Iteration:** Sprint 1
3. **Save** → señala el **ID** (ej. `#5`).
4. Muéstrala en **Boards → Boards** (columna *New*) y arrástrala a **Active**.

### D2 — Azure Repos (≈5 min)
1. Desde la User Story → sección **Development → Create branch**
   - Name: `feature/5-normalize-order-status` · Based on: `main` (ya queda vinculada).
2. En IntelliJ:
   ```bash
   git fetch
   git checkout feature/5-normalize-order-status
   ```
3. Edita `OrderStatusNormalizer.java` → agrega `.trim()`.
4. Agrega la prueba `eliminaEspaciosExternos` en `OrderStatusNormalizerTest.java`.
5. (Opcional) `mvn clean verify` local → 3 pruebas OK.
6. Commit y push:
   ```bash
   git add .
   git commit -m "Fix order status normalization #5"
   git push
   ```
7. En Repos aparece el banner → **Create a pull request** hacia `main`.
   - Title: `Fix order status normalization`
   - Description: `Aplica trim() antes de normalizar. Cubre el criterio de aceptación de la historia #5.`
   - Work items: verifica que aparece **#5**.
   - **Create**.
8. Muestra en el PR: pestaña **Files** (diff de 1 línea + prueba), panel de **políticas** (revisor, work item, build).
9. Agrega un comentario como revisor (ej. "¿Cubrimos tabs y saltos de línea?") → responde ("Sí, trim() los elimina") → **Resolve**.

### D3 — Azure Pipelines (≈4 min)
1. En el PR, clic en la validación de build en curso → abre la ejecución.
2. Muestra: **agente** usado, **pasos de Maven**, log de `Tests run: 3, Failures: 0`.
3. Pestaña **Tests** → 3 pruebas, 100 % pass.
4. Vuelve al PR → todas las políticas en verde → **Approve**.
5. **Complete** → Squash merge → ✅ *Complete associated work items* → ✅ *Delete source branch* → **Complete merge**.
6. Se dispara la pipeline de `main` (CI por `trigger`).
7. Cierra el círculo: abre la User Story #5 → muestra en *Development* la rama, el commit, el PR y el build, y el estado **Closed/Resolved**.

### 🔥 Opcional: demostrar un fallo (slide 22, +3 min)
Antes del commit "bueno", haz push **solo de la prueba nueva sin el trim()**:
- La build validation falla → `expected: <SHIPPED> but was: <  SHIPPED  >`.
- El PR queda **bloqueado** por la política.
- Muestras el primer paso que falla y el log → agregas `.trim()` → push → verde.

Es el momento que más enseña, pero hazlo solo si tu ensayo salió bien y tienes tiempo.

---

## 4. Guion (diálogo)

### Apertura (slide 1)
> "Buenos días a todos. Soy Ricardo Vallejo y hoy vamos a ver cómo un requerimiento se convierte en software validado usando Azure DevOps. La idea no es solo ver la herramienta, sino entender por qué un flujo controlado reduce errores y retrabajo."

### Transición a teoría → reto (slide 7)
> "Antes de hablar de herramientas, pensemos en el problema. ¿A quién le ha pasado que no sabe en qué estado está una entrega, o que un error se detecta hasta producción? Eso es justo lo que DevOps busca evitar."

### Transición a la demo (slide 17)
> "Ya vimos la teoría: Boards para planificar, Repos para controlar el código y Pipelines para validar. Ahora vamos a ponerlo en práctica con un caso muy pequeño pero realista: una API de pedidos que recibe estados con espacios de más. Por ejemplo, llega `'  shipped  '` y el sistema que la consume espera `'SHIPPED'`."

### D1 — Boards
> "Todo empieza aquí: nada se programa si no está registrado. Creo una historia de usuario con su descripción y, muy importante, sus criterios de aceptación en formato Given-When-Then. Esto nos permite saber objetivamente cuándo el trabajo está terminado."

> "Al guardar, Azure DevOps le asigna un ID, en este caso el número 5. Este número es el hilo que va a conectar todo lo que hagamos después."

> "Lo muevo a *Active*: así cualquier persona del equipo sabe que ya alguien lo está trabajando."

### D2 — Repos
> "Desde la misma historia creo la rama. Fíjense en el nombre: `feature/5-normalize-order-status`. Con solo leerlo sé qué tipo de cambio es y a qué requerimiento pertenece. Y nunca trabajo directamente sobre main."

> "El cambio es mínimo: agrego `trim()` para quitar los espacios. Pero no basta con cambiar código: agrego una prueba que verifica exactamente el criterio de aceptación. La prueba es la evidencia."

> "En el mensaje del commit incluyo `#5`; así Azure DevOps vincula automáticamente el commit con la historia."

> "Ahora abro el Pull Request. Aquí está la diferencia entre 'yo creo que funciona' y 'el equipo verificó que funciona'. Vean las políticas: necesita un revisor, un work item vinculado, comentarios resueltos y una build exitosa. Si algo de esto falta, el botón de completar simplemente no se habilita."

> "Como revisor dejo una pregunta… la respondo y la marco como resuelta. Esa conversación queda guardada: dentro de seis meses alguien puede saber por qué se tomó esta decisión."

### D3 — Pipelines
> "Mientras revisábamos, la pipeline ya se ejecutó sola gracias a la política de *Build validation*. Aquí vemos el agente que la corrió y cada paso de Maven: compilar y ejecutar pruebas."

> "En la pestaña *Tests* vemos 3 pruebas ejecutadas, todas exitosas. Esta es la señal automatizada de calidad: no depende de que alguien se acuerde de correr las pruebas."

> "Con todo en verde, apruebo y completo el PR con squash merge. Marco 'completar work items asociados' y 'borrar la rama'."

### Cierre de la demo — trazabilidad (slide 16)
> "Y aquí cierro el círculo. Regreso a la historia #5 y en una sola pantalla veo: qué necesidad resolvimos, en qué rama, con qué commit, quién lo revisó y que la pipeline lo validó. Esto es trazabilidad de extremo a extremo, y es lo que facilita auditorías o investigar una incidencia, algo especialmente valioso en sectores regulados como banca."

### Si algo falla en vivo
> "Perfecto, esto también es parte de la demo: el primer paso es identificar qué falló antes de cambiar la configuración. Veamos el log…"

Si no se resuelve en 1 minuto: "Les muestro la corrida que hice en el ensayo" → capturas.

### Cierre (slide 24)
> "Resumiendo: Boards nos dice *qué* y *por qué*, Repos nos dice *cómo* y *quién lo revisó*, y Pipelines nos da la *evidencia* de que funciona. Muchas gracias por su tiempo, ¿preguntas?"

---

## 5. Preguntas probables

| Pregunta | Respuesta corta |
|---|---|
| ¿Diferencia entre CI y CD? | CI integra y valida cada cambio; CD prepara/despliega artefactos a ambientes, con aprobaciones. |
| ¿Por qué no usar `pr:` en el YAML? | En Azure Repos se ignora; la validación de PRs se configura con *Build validation* en políticas de rama. |
| ¿YAML o editor clásico? | YAML: versionado junto al código, revisable en PRs y reutilizable. |
| ¿Dónde guardo secretos? | Variables secretas, *variable groups* o Azure Key Vault. Nunca en el repo ni en logs. |
| ¿Funciona con GitHub? | Sí, Pipelines y Boards se integran con GitHub (en commits se usa `AB#ID`). |
| ¿Qué es squash merge? | Junta todos los commits de la rama en uno solo sobre main → historial limpio. |

---

## 6. Detalles a corregir en la presentación

- **Slide 23:** dice *"Hello word"* → debería ser **"Hello World"**.
- **Copyright © 2020** en el pie de página → considera actualizar el año.
- **Slides 9 y 15** no tienen texto (imágenes/diagramas): verifica que se vean bien en el equipo donde presentarás.
- **Slide 3:** el "05" y "Trazar" están en líneas separadas; revisa la alineación.
