package com.zeus.pseudocharlesdemo.feature.brewery.domain

enum class BreweryType(val displayName: String) {
    MICRO("Micro"),
    NANO("Nano"),
    REGIONAL("Regional"),
    BREWPUB("Brewpub"),
    LARGE("Large"),
    PLANNING("Planning"),
    BAR("Bar"),
    CONTRACT("Contract"),
    PROPRIETOR("Proprietor"),
    CLOSED("Closed"),
    UNKNOWN("Unknown");

    companion object {
        fun fromApiValue(value: String?): BreweryType {
            if (value == null) return UNKNOWN
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
