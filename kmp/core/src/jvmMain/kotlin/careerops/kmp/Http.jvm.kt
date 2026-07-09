package careerops.kmp

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

private val client: HttpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(15))
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build()

actual fun httpGet(url: String): String? = runCatching {
    val req = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(25))
        .header("User-Agent", "Mozilla/5.0 (careerops-kmp; personal job-search tool)")
        .GET()
        .build()
    val res = client.send(req, HttpResponse.BodyHandlers.ofString())
    if (res.statusCode() in 200..299) res.body() else null
}.getOrNull()
