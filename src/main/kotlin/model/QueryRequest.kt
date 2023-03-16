package model

import kotlinx.serialization.Serializable


@Serializable
data class QueryRequest(
    val queryRegex: String,
    val fromTime: String? = "",
    val toTime: String? = ""
)