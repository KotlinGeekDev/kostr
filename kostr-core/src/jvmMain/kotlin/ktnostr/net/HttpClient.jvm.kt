package ktnostr.net

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    config(this)

}