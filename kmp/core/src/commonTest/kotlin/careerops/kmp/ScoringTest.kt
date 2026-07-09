package careerops.kmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScoringTest {

    @Test
    fun `b2b contract in EU scores max fiscal and no FTE flag`() {
        val job = JobPosting(
            title = "Senior Android Developer (Freelance)", company = "intent", url = "u",
            raw = "freelance b2b contract kotlin compose remote europe",
        )
        val scored = Task037.score(job)
        assertTrue(scored.b2b)
        assertEquals(5, scored.axes.fiscal)
        assertTrue(scored.flags.none { "FTE" in it })
    }

    @Test
    fun `fte offer gets fiscal 1 and flag`() {
        val job = JobPosting(
            title = "Senior Android Engineer", company = "x", url = "u",
            raw = "android kotlin full-time remote europe",
        )
        val scored = Task037.score(job)
        assertEquals(1, scored.axes.fiscal)
        assertTrue(scored.flags.any { "FTE" in it })
    }

    @Test
    fun `salary above sweet spot scores tarifa 4 plus`() {
        val job = JobPosting(
            title = "Android SDK Engineer", company = "x", url = "u", salary = "$120k",
            raw = "android kotlin sdk remote worldwide 120k",
        )
        val scored = Task037.score(job)
        assertTrue(scored.axes.tarifa >= 4)
    }

    @Test
    fun `rank puts b2b first even with lower score`() {
        val b2b = JobPosting(title = "Android dev", company = "a", url = "u", raw = "android contract b2b remote")
        val fte = JobPosting(
            title = "Android SDK Engineer", company = "b", url = "u", salary = "$227k",
            raw = "android kotlin sdk remote europe full-time ai 227k",
        )
        val ranked = Task037.rank(listOf(fte, b2b))
        assertEquals("a", ranked.first().posting.company)
    }

    @Test
    fun `irrelevant roles are filtered out`() {
        val go = JobPosting(title = "Backend Engineer (Go)", company = "acme", url = "u", raw = "golang backend onsite")
        assertTrue(Task037.rank(listOf(go)).isEmpty())
    }
}
