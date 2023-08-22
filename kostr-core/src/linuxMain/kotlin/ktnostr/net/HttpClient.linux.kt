package ktnostr.net

import io.ktor.client.*
import io.ktor.client.engine.curl.*

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Curl) {
    config(this)
    engine {
        sslVerify = true
    }
}