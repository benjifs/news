package feeds

import common.ConfRepository
import db.database
import entries.EntriesRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import links.LinksRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

class FeedsModelTests {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun beforeEachTest() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun afterEachTest() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `init`(): Unit = runBlocking {
        val db = database()
        val feedsRepo = FeedsRepository(db = db, api = mockk(),)
        val entriesRepo = EntriesRepository(db = db, api = mockk(),)
        val linksRepo = LinksRepository(db = database())
        val confRepo = ConfRepository(db = db)

        val model = FeedsModel(
            feedsRepo = feedsRepo,
            entriesRepo = entriesRepo,
            linksRepo = linksRepo,
            confRepo = confRepo,
        )

        var attempts = 0

        while (model.state.value !is FeedsModel.State.Loaded) {
            if (attempts++ > 100) {
                assertTrue { false }
            } else {
                delay(10)
            }
        }
    }
}