package careerops.kmp

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** HTTP GET multiplataforma: JVM usa java.net.http; Android usara OkHttp; Native, curl/ktor. */
expect fun httpGet(url: String): String?

/**
 * Fuentes con API JSON publica (sin scraping, sin login).
 * Validadas en RUN-049-01 (2026-07-08).
 */
object Sources {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun parse(text: String?): JsonElement? =
        text?.let { runCatching { json.parseToJsonElement(it) }.getOrNull() }

    private fun JsonElement.str(key: String): String =
        runCatching { jsonObject[key]?.jsonPrimitive?.content }.getOrNull() ?: ""

    fun greenhouse(board: String): List<JobPosting> {
        val root = parse(httpGet("https://boards-api.greenhouse.io/v1/boards/$board/jobs")) ?: return emptyList()
        val jobs = runCatching { root.jsonObject["jobs"]!!.jsonArray }.getOrNull() ?: return emptyList()
        return jobs.map { j ->
            val loc = runCatching { j.jsonObject["location"]!!.str("name") }.getOrNull() ?: ""
            JobPosting(
                title = j.str("title"), company = board, url = j.str("absolute_url"),
                location = loc, source = "greenhouse:$board", raw = j.str("title") + " " + loc,
            )
        }
    }

    fun lever(company: String): List<JobPosting> {
        val root = parse(httpGet("https://api.lever.co/v0/postings/$company?mode=json")) ?: return emptyList()
        val jobs = runCatching { root.jsonArray }.getOrNull() ?: return emptyList()
        return jobs.map { j ->
            val cats = runCatching { j.jsonObject["categories"]!! }.getOrNull()
            val loc = cats?.str("location") ?: ""
            val commitment = cats?.str("commitment") ?: ""
            JobPosting(
                title = j.str("text"), company = company, url = j.str("hostedUrl"),
                location = loc, source = "lever:$company",
                raw = listOf(j.str("text"), loc, commitment, j.str("descriptionPlain").take(2000)).joinToString(" "),
            )
        }
    }

    fun ashby(board: String): List<JobPosting> {
        val root = parse(httpGet("https://api.ashbyhq.com/posting-api/job-board/$board?includeCompensation=true"))
            ?: return emptyList()
        val jobs = runCatching { root.jsonObject["jobs"]!!.jsonArray }.getOrNull() ?: return emptyList()
        return jobs.map { j ->
            val comp = runCatching { j.jsonObject["compensation"]!!.str("compensationTierSummary") }.getOrNull() ?: ""
            val isRemote = runCatching {
                j.jsonObject["isRemote"]?.jsonPrimitive?.content == "true"
            }.getOrNull() ?: false
            JobPosting(
                title = j.str("title"), company = board,
                url = j.str("jobUrl").ifEmpty { j.str("applyUrl") },
                location = j.str("location"), salary = comp, remote = isRemote,
                source = "ashby:$board", raw = j.str("title") + " " + j.str("location") + " " + comp,
            )
        }
    }

    fun remoteok(tag: String): List<JobPosting> {
        val root = parse(httpGet("https://remoteok.com/remote-$tag-jobs.json")) ?: return emptyList()
        val items = runCatching { root.jsonArray }.getOrNull() ?: return emptyList()
        return items.drop(1).mapNotNull { j ->
            val obj = runCatching { j.jsonObject }.getOrNull() ?: return@mapNotNull null
            val tags = runCatching { obj["tags"]!!.jsonArray.joinToString(" ") { it.jsonPrimitive.content } }
                .getOrNull() ?: ""
            JobPosting(
                title = j.str("position"), company = j.str("company"), url = j.str("url"),
                location = j.str("location"),
                salary = j.str("salary_min").let { if (it.isEmpty()) "" else "$$it-${j.str("salary_max")}" },
                source = "remoteok",
                raw = listOf(j.str("position"), j.str("location"), tags, j.str("description").take(2000)).joinToString(" "),
            )
        }
    }
}
