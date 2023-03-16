@file:OptIn(ExperimentalTime::class)

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.cli.*
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import model.QueryRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import model.SearchResults
import java.io.File
import java.time.format.DateTimeParseException

val DATETIME_PATTERN = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z")

fun main(args: Array<String>) = runBlocking {
    val parser = ArgParser("Log Client")
    val servers by parser.option(ArgType.String, shortName = "s", description = "Log server urls separated by commas.").required()
    val query by parser.option(ArgType.String, shortName = "q", description = "Query regular expression").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output file name")
    val from by parser.option(
        ArgType.String,
        shortName = "f",
        description = "Datetime to filter from (dd/MMM/yyyy:HH:mm:ss Z)"
    )
    val to by parser.option(
        ArgType.String,
        shortName = "t",
        description = "Datetime to filter to (dd/MMM/yyyy:HH:mm:ss Z)"
    )
    val numResults by parser.option(ArgType.Int, shortName = "n", description = "Number of results to display")
        .default(10)
    parser.parse(args)

    if (!from.isNullOrBlank()) {
        try {
            LocalDateTime.parse(from, DATETIME_PATTERN)
        } catch (e: DateTimeParseException) {
            println("Invalid date format for `from` date.")
            kotlin.system.exitProcess(-1)
        }
    }

    if (!to.isNullOrBlank()) {
        try {
            LocalDateTime.parse(to, DATETIME_PATTERN)
        } catch (e: DateTimeParseException) {
            println("Invalid date format for `to` date.")
            kotlin.system.exitProcess(-1)
        }
    }

    val (results, searchTime) = measureTimedValue {
        val logServerAddrs = servers.split(",")
        val queryReq = QueryRequest(query, from, to)
        val queryJobs = logServerAddrs.map {
            async {
                val response = queryLogServer(it, queryReq)
                Json.decodeFromString<SearchResults>(response)
            }
        }

        queryJobs.joinAll()
        queryJobs.flatMap { it.getCompleted().matches }
    }

    println("Found ${results.count()} matches for \"$query\" ${if (from != null) "from: $from" else ""} ${if (to != null) " to: $to" else ""} in ${searchTime.inWholeMilliseconds}ms")
    if (results.count() > numResults) {
        println("Displaying $numResults of ${results.count()}")
    }

    results.take(numResults).forEach {
        println(it)
    }

    if (!output.isNullOrBlank()) {
        println("Writing results to file  $output")
        File(output!!).writeText(results.joinToString("\n"))
    }
}

suspend fun queryLogServer(addr: String, queryRequest: QueryRequest): String {
    HttpClient().use { client ->
        println("Querying: ${"$addr/logs/search"}")
        val response = client.post("$addr/logs/search") {
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(Json.encodeToString(queryRequest))
        }
        return response.body()
    }
}