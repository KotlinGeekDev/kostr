package ktnostr.net

import io.ktor.client.*

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient