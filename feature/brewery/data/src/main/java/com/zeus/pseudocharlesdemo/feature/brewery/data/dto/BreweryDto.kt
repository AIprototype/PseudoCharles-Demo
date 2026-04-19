package com.zeus.pseudocharlesdemo.feature.brewery.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BreweryDto(
    val id: String,
    val name: String,
    @SerialName("brewery_type") val breweryType: String? = null,
    @SerialName("address_1") val address1: String? = null,
    @SerialName("address_2") val address2: String? = null,
    @SerialName("address_3") val address3: String? = null,
    val city: String? = null,
    @SerialName("state_province") val stateProvince: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    val country: String? = null,
    @Serializable(with = FlexibleDoubleSerializer::class) val longitude: Double? = null,
    @Serializable(with = FlexibleDoubleSerializer::class) val latitude: Double? = null,
    val phone: String? = null,
    @SerialName("website_url") val websiteUrl: String? = null
)

@Serializable
data class BrewerySuggestionDto(
    val id: String,
    val name: String
)
