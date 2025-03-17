package rhodium.nostr.relay.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RelayLimits(
    @SerialName("max_message_length")val maxMessageLength: Int,
    @SerialName("max_subscriptions")val maxSubscriptions: Int,
    @SerialName("max_filters")val maxFilters: Int,
    @SerialName("max_limit")val maxLimit: Int,
    @SerialName("max_subid_length")val maxSubscriptionIdLength: Int,
    @SerialName("max_event_tags")val maxEventTagNumber: Int,
    @SerialName("max_content_length")val maxContentLength: Int,
    @SerialName("min_pow_difficulty")val minPowDifficulty: Int,
    @SerialName("auth_required")val isAuthRequired: Boolean,
    @SerialName("payment_required")val isPaymentRequired: Boolean,
    @SerialName("restricted_writes")val writesAreRestricted: Boolean,
    @SerialName("created_at_lower_limit")val creationDateLowerLimit: Long,
    @SerialName("created_at_upper_limit")val creationDateUpperLimit: Long
)