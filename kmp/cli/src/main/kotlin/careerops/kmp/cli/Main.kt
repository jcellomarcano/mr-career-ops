package careerops.kmp.cli

import careerops.kmp.JobPosting
import careerops.kmp.Pipeline
import careerops.kmp.Profile
import careerops.kmp.Sources
import careerops.kmp.Task037
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

/**
 * careerops-kmp CLI — motor de busqueda de empleo (scorecard TASK-037).
 * GATE HUMANO: solo busca y presenta; aplicar/contactar lo decide el humano.
 *
 * Uso:
 *   ./gradlew :cli:run --args="demo"   # sin red, fixtures
 *   ./gradlew :cli:run --args="run"    # corrida real (Greenhouse/Lever/Ashby/RemoteOK)
 */
fun main(args: Array<String>) {
    val mode = args.firstOrNull() ?: "demo"
    val profile = Profile()

    val postings: List<JobPosting> = when (mode) {
        "run" -> buildList {
            // Boards sembrados de RUN-049-01; editar aqui o cargar de config
            addAll(Sources.ashby("revenuecat"))
            addAll(Sources.greenhouse("speechify"))
            addAll(Sources.greenhouse("spcareers"))
            addAll(Sources.lever("airalo"))
            addAll(Sources.remoteok("android"))
        }
        else -> demoJobs
    }

    val ranked = Task037.rank(postings, profile)
    val json = Json { prettyPrint = true }
    val pipeline = Pipeline(generated = LocalDate.now().toString(), jobs = ranked.take(25))

    File("pipeline.json").writeText(json.encodeToString(pipeline))

    val md = buildString {
        appendLine("# Job Pipeline (careerops-kmp) — ${pipeline.generated}")
        appendLine()
        appendLine("GATE HUMANO: solo presenta. Orden: B2B primero, luego score.")
        appendLine()
        appendLine("| # | Titulo | Empresa | B2B | Score | Flags | Link |")
        appendLine("|---|---|---|---|---|---|---|")
        pipeline.jobs.forEachIndexed { i, sj ->
            val f = if (sj.flags.isEmpty()) "-" else sj.flags.joinToString("; ")
            appendLine(
                "| ${i + 1} | ${sj.posting.title.take(60)} | ${sj.posting.company} " +
                    "| ${if (sj.b2b) "SI" else "no"} | ${sj.score}/30 | $f | [ver](${sj.posting.url}) |"
            )
        }
    }
    File("pipeline.md").writeText(md)

    println("${ranked.size} ofertas puntuadas -> pipeline.json + pipeline.md")
    ranked.take(5).forEach { println("  ${it.score}/30 ${if (it.b2b) "[B2B]" else "     "} ${it.posting.title} @ ${it.posting.company}") }
}

private val demoJobs = listOf(
    JobPosting(
        title = "Senior Android SDK Engineer", company = "revenuecat",
        url = "https://jobs.ashbyhq.com/revenuecat/x", salary = "$227,000", remote = true,
        raw = "senior android sdk engineer kotlin remote americas 227,000 full-time ai payments",
    ),
    JobPosting(
        title = "Senior Android Developer (Freelance)", company = "intent",
        url = "https://withintent.com/x", location = "Remote EU",
        raw = "senior android developer freelance b2b contract kotlin compose remote europe",
    ),
    JobPosting(
        title = "Backend Engineer (Go)", company = "acme",
        url = "https://acme.com/x", location = "Onsite Denver",
        raw = "backend engineer go onsite denver full-time",
    ),
)
