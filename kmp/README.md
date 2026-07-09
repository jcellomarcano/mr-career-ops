# careerops-kmp — Job Search Engine (Kotlin Multiplatform)

Motor de búsqueda y scoring de ofertas de trabajo en **Kotlin Multiplatform**:
el mismo core corre en el CLI de escritorio (JVM) y, en el roadmap, en la app
Android y como binario nativo. Port del motor Python del sistema career-ops,
diseñado como **proyecto de portfolio**: API pública limpia, expect/actual,
coroutines-ready, contrato JSON estable.

> **Gate humano:** este software busca y puntúa; nunca aplica ni contacta.

## Arquitectura

```
core/  (KMP)                          cli/  (JVM)
├── Model.kt      datos @Serializable ├── Main.kt  run|demo
├── Scoring.kt    scorecard TASK-037  │
├── Sources.kt    APIs publicas       └── salida: pipeline.json + pipeline.md
│                 (Greenhouse, Lever,
│                  Ashby, RemoteOK)
└── expect httpGet() → actual por plataforma (JVM: java.net.http)
```

- **`pipeline.json`** es el contrato: lo consume la app Android (Carrer), el
  dashboard de career-ops o cualquier otro cliente. Mismo formato que emite el
  CLI Python hermano (`07_scripts/jobsearch`).
- **Scoring:** 6 ejes 0-5 (stack, tarifa vs suelo, remoto/CET, estabilidad,
  fricción fiscal, upside). Regla de negocio: ofertas B2B/contract se ordenan
  siempre por delante de empleo directo.

## Build & run

Requiere JDK 17+ y Gradle (o abrir `kmp/` en Android Studio / IntelliJ, que
trae Gradle embebido). Primera vez: `gradle wrapper` para generar `./gradlew`.

```bash
gradle :cli:run --args="demo"   # sin red, fixtures — verifica el scoring
gradle :cli:run --args="run"    # corrida real contra las APIs
gradle :core:jvmTest            # tests del scorecard
```

## Roadmap

- [ ] `androidTarget()` en `core` → consumirlo desde la app Carrer (Compose)
- [ ] Coroutines: fetch de fuentes en paralelo (`async` por board)
- [ ] Target nativo (`macosArm64`/`linuxX64`) → binario único sin JVM
- [ ] Fuente Himalayas + Working Nomads
- [ ] Config externa (JSON) en vez de boards hardcodeados en Main.kt
- [ ] GitHub Actions: build + tests + release del JAR
