package com.zeus.pseudocharlesdemo.feature.brewery.presentation.model

import androidx.compose.runtime.Immutable
import com.zeus.pseudocharlesdemo.feature.brewery.domain.Brewery
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryType

@Immutable
data class BreweryUi(
    val id: String,
    val name: String,
    val type: BreweryType,
    val typeLabel: String,
    val location: String,
    val address: String,
    val phone: String?,
    val websiteUrl: String?,
    val hasCoordinates: Boolean
)

fun Brewery.toBreweryUi(): BreweryUi {
    val locationParts = listOfNotNull(
        city.ifBlank { null },
        stateProvince.ifBlank { null },
        country.ifBlank { null }
    )
    return BreweryUi(
        id = id,
        name = name,
        type = breweryType,
        typeLabel = breweryType.displayName,
        location = locationParts.joinToString(", "),
        address = address,
        phone = phone,
        websiteUrl = websiteUrl,
        hasCoordinates = latitude != null && longitude != null
    )
}
