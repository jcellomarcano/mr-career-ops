package careerops.kmp

/**
 * Scorecard TASK-037 — port del motor Python (07_scripts/jobsearch/jobsearch.py).
 * 6 ejes 0-5. Regla dura: B2B primero (restriccion: no empleo directo).
 * GATE HUMANO: este modulo puntua y ordena; nunca aplica ni contacta.
 */
object Task037 {

    private val salaryRegex = Regex("""(\d{2,3})[.,]?000|\b(\d{2,3})k\b""")

    fun score(p: JobPosting, profile: Profile = Profile()): ScoredJob {
        val txt = (p.raw + " " + p.title + " " + p.location + " " + p.salary).lowercase()
        val flags = mutableListOf<String>()

        // 1. encaje de stack
        val hits = profile.stackKeywords.count { it in txt }
        val hasMust = profile.mustKeywords.any { it in txt }
        val stack = if (hasMust) minOf(5, hits * 2) else minOf(2, hits)

        // 2. tarifa vs suelo (solo si hay cifra)
        var tarifa = 2
        val nums = salaryRegex.findAll(txt.replace(",", ""))
            .mapNotNull { m -> (m.groupValues[1].ifEmpty { m.groupValues[2] }).toIntOrNull() }
            .toList()
        if (nums.isNotEmpty()) {
            val topAnnual = nums.max() * 1000
            val sweetAnnual = profile.sweetSpotMonthEur * 12
            val floorAnnual = profile.floorMonthEur * 12
            tarifa = when {
                topAnnual >= sweetAnnual * 3 / 2 -> 5
                topAnnual >= sweetAnnual -> 4
                topAnnual >= floorAnnual -> 3
                else -> 1
            }
        }

        // 3. remoto / CET
        var remoto = 0
        val isRemote = p.remote || "remote" in txt || "remoto" in txt
        if (isRemote) {
            remoto = 3
            if (listOf("europe", "emea", "cet", "spain", "barcelona", "worldwide", "anywhere").any { it in txt }) remoto = 5
            if (listOf("us only", "united states only", "north america only", "americas").any { it in txt }) {
                remoto = 1
                flags += "geo: posiblemente solo America"
            }
        } else if ("barcelona" in txt || "hybrid" in txt) {
            remoto = 3
        }

        // 4. estabilidad
        val estabilidad = if (listOf("full-time", "full time", "permanent", "indefinido").any { it in txt }) 4 else 3

        // 5. friccion fiscal — restriccion: solo contractor/B2B viable
        val b2b = listOf("b2b", "contract", "contractor", "freelance", "freelancer").any { it in txt }
        val fiscal = when {
            b2b && listOf("europe", "eu", "spain").any { it in txt } -> 5
            b2b -> 4
            else -> {
                flags += "FTE: preguntar si aceptan B2B (restriccion: no empleo directo)"
                1
            }
        }

        // 6. upside
        val upsideKw = listOf("sdk", "ai", "ml", "llm", "xr", "vision", "game", "gaming", "payments")
        var upside = if (upsideKw.any { it in txt }) 4 else 2
        if ("sdk" in txt && listOf("ai", "game", "payments").any { it in txt }) upside = 5

        val axes = Axes(stack, tarifa, remoto, estabilidad, fiscal, upside)
        return ScoredJob(p.copy(raw = ""), axes.total, axes, flags, b2b)
    }

    /** Filtra por keywords de perfil, puntua y ordena B2B primero, luego score. */
    fun rank(postings: List<JobPosting>, profile: Profile = Profile()): List<ScoredJob> =
        postings
            .filter { p -> profile.filterKeywords.any { it in (p.title + " " + p.raw).lowercase() } }
            .map { score(it, profile) }
            .sortedWith(compareByDescending<ScoredJob> { it.b2b }.thenByDescending { it.score })
}
