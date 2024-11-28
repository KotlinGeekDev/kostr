package ktnostr.android

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("kt.nostr.kostr_android.test", appContext.packageName)
    }
}