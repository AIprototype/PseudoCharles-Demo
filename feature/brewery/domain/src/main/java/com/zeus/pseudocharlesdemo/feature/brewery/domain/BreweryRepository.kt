package com.zeus.pseudocharlesdemo.feature.brewery.domain

import com.zeus.pseudocharlesdemo.core.domain.DataError
import com.zeus.pseudocharlesdemo.core.domain.Result

interface BreweryRepository {
    suspend fun searchBreweries(
        query: String,
        type: BreweryType? = null,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Brewery>, DataError.Network>

    suspend fun getBrewery(id: String): Result<Brewery, DataError.Network>

    suspend fun autocomplete(query: String): Result<List<BrewerySuggestion>, DataError.Network>

    suspend fun getRandomBreweries(size: Int = 15): Result<List<Brewery>, DataError.Network>
}
