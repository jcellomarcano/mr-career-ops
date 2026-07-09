package careerops.kmp

import kotlinx.serialization.Serializable

/** Oferta cruda tal como llega de una fuente. */
@Serializable
data class JobPosting(
    val title: String,
    val company: String,
    val url: String,
    val location: String = "",
    val salary: String = "",
    val remote: Boolean = false,
    val source: String = "",
    /** Texto libre adicional (descripcion, tags) usado solo para scoring. */
    val raw: String = "",
)

/** Los 6 ejes del scorecard TASK-037 (0-5 cada uno). */
@Serializable
data class Axes(
    val stack: Int,
    val tarifa: Int,
    val remoto: Int,
    val estabilidad: Int,
    val fiscal: Int,
    val upside: Int,
) {
    val total: Int get() = stack + tarifa + remoto + estabilidad + fiscal + upside
}

@Serializable
data class ScoredJob(
    val posting: JobPosting,
    val score: Int,
    val axes: Axes,
    val flags: List<String>,
    val b2b: Boolean,
)

/** Contrato de datos compartido con el CLI Python y la app Android (Carrer). */
@Serializable
data class Pipeline(
    val generated: String,
    val gate: String = "HUMAN_ONLY",
    val jobs: List<ScoredJob>,
)

/** Perfil economico y de stack. Suelos del CFO (2026-07-08). */
@Serializable
data class Profile(
    val filterKeywords: List<String> = listOf("android", "kotlin", "mobile", "sdk", "payments"),
    val mustKeywords: List<String> = listOf("android", "kotlin"),
    val stackKeywords: List<String> = listOf(
        "kotlin", "android", "sdk", "compose", "jetpack", "coroutines",
        "kmp", "multiplatform", "nfc", "ble", "payments", "graphql", "swift",
    ),
    val floorMonthEur: Int = 2400,
    val targetMonthEur: Int = 3000,
    val sweetSpotMonthEur: Int = 3600,
    val b2bOnly: Boolean = true,
)
