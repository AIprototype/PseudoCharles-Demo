package com.zeus.pseudocharlesdemo.feature.brewery.data

import com.zeus.pseudocharlesdemo.feature.brewery.data.dto.BreweryDto
import com.zeus.pseudocharlesdemo.feature.brewery.data.dto.BrewerySuggestionDto
import com.zeus.pseudocharlesdemo.feature.brewery.domain.Brewery
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BrewerySuggestion
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryType

fun BreweryDto.toBrewery(): Brewery {
    val addressParts = listOfNotNull(address1, address2, address3)
    return Brewery(
        id = id,
        name = name,
        breweryType = BreweryType.fromApiValue(breweryType),
        address = addressParts.joinToString(", "),
        city = city.orEmpty(),
        stateProvince = stateProvince.orEmpty(),
        postalCode = postalCode.orEmpty(),
        country = country.orEmpty(),
        longitude = longitude,
        latitude = latitude,
        phone = phone,
        websiteUrl = websiteUrl
    )
}

fun BrewerySuggestionDto.toBrewerySuggestion(): BrewerySuggestion {
    return BrewerySuggestion(id = id, name = name)
}
