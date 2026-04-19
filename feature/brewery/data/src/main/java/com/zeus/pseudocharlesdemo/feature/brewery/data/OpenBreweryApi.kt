package com.zeus.pseudocharlesdemo.feature.brewery.data

import com.zeus.pseudocharlesdemo.core.data.safeCall
import com.zeus.pseudocharlesdemo.core.domain.DataError
import com.zeus.pseudocharlesdemo.core.domain.Result
import com.zeus.pseudocharlesdemo.feature.brewery.data.dto.BreweryDto
import com.zeus.pseudocharlesdemo.feature.brewery.data.dto.BrewerySuggestionDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class OpenBreweryApi(private val httpClient: HttpClient) {

    suspend fun searchBreweries(
        query: String,
        type: String?,
        page: Int,
        perPage: Int
    ): Result<List<BreweryDto>, DataError.Network> {
        return safeCall {
            httpClient.get("breweries") {
                parameter("by_name", query)
                type?.let { parameter("by_type", it) }
                parameter("page", page)
                parameter("per_page", perPage)
            }
        }
    }

    suspend fun getBrewery(id: String): Result<BreweryDto, DataError.Network> {
        return safeCall {
            httpClient.get("breweries/$id")
        }
    }

    suspend fun autocomplete(query: String): Result<List<BrewerySuggestionDto>, DataError.Network> {
        return safeCall {
            httpClient.get("breweries/autocomplete") {
                parameter("query", query)
            }
        }
    }

    suspend fun randomBreweries(size: Int): Result<List<BreweryDto>, DataError.Network> {
        return safeCall {
            httpClient.get("breweries/random") {
                parameter("size", size)
            }
        }
    }
}
