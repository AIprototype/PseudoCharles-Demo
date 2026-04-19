package com.zeus.pseudocharlesdemo.feature.brewery.data

import com.zeus.pseudocharlesdemo.core.domain.DataError
import com.zeus.pseudocharlesdemo.core.domain.Result
import com.zeus.pseudocharlesdemo.core.domain.map
import com.zeus.pseudocharlesdemo.feature.brewery.domain.Brewery
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryRepository
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BrewerySuggestion
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryType

class KtorBreweryRepository(
    private val api: OpenBreweryApi
) : BreweryRepository {

    override suspend fun searchBreweries(
        query: String,
        type: BreweryType?,
        page: Int,
        perPage: Int
    ): Result<List<Brewery>, DataError.Network> {
        return api.searchBreweries(
            query = query,
            type = type?.name?.lowercase(),
            page = page,
            perPage = perPage
        ).map { dtos -> dtos.map { it.toBrewery() } }
    }

    override suspend fun getBrewery(id: String): Result<Brewery, DataError.Network> {
        return api.getBrewery(id).map { it.toBrewery() }
    }

    override suspend fun autocomplete(query: String): Result<List<BrewerySuggestion>, DataError.Network> {
        return api.autocomplete(query).map { dtos -> dtos.map { it.toBrewerySuggestion() } }
    }

    override suspend fun getRandomBreweries(size: Int): Result<List<Brewery>, DataError.Network> {
        return api.randomBreweries(size).map { dtos -> dtos.map { it.toBrewery() } }
    }
}
