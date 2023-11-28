package ktnostr.net

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(CIO) {
    config(this)
    engine {

    }
}