package com.zeus.pseudocharlesdemo.feature.brewery.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeus.pseudocharlesdemo.core.domain.onFailure
import com.zeus.pseudocharlesdemo.core.domain.onSuccess
import com.zeus.pseudocharlesdemo.core.presentation.toUiText
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryRepository
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryType
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.model.toBreweryUi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class BrewerySearchViewModel(
    private val repository: BreweryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BrewerySearchState())
    val state: StateFlow<BrewerySearchState> = _state

    private val _events = Channel<BrewerySearchEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadRandomBreweries()

        _state
            .map { it.query }
            .distinctUntilChanged()
            .debounce(400)
            .onEach { query ->
                if (query.length >= 2) {
                    performSearch()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadRandomBreweries() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getRandomBreweries()
                .onSuccess { breweries ->
                    _state.update {
                        it.copy(
                            results = breweries.map { b -> b.toBreweryUi() }.toImmutableList(),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                    _events.send(BrewerySearchEvent.ShowError(error.toUiText()))
                }
        }
    }

    fun onAction(action: BrewerySearchAction) {
        when (action) {
            is BrewerySearchAction.OnQueryChange -> {
                _state.update { it.copy(query = action.query, errorMessage = null) }
            }
            is BrewerySearchAction.OnBreweryClick -> {
                viewModelScope.launch {
                    _events.send(BrewerySearchEvent.NavigateToDetail(action.id))
                }
            }
            is BrewerySearchAction.OnTypeToggle -> toggleType(action.type)
            BrewerySearchAction.OnSearch -> performSearch()
            BrewerySearchAction.OnClearFilters -> {
                _state.update { it.copy(selectedTypes = emptySet<BreweryType>().toImmutableSet()) }
                performSearch()
            }
        }
    }

    private fun toggleType(type: BreweryType) {
        _state.update { current ->
            val newTypes = current.selectedTypes.toMutableSet()
            if (type in newTypes) newTypes.remove(type) else newTypes.add(type)
            current.copy(selectedTypes = newTypes.toImmutableSet())
        }
        performSearch()
    }

    private fun performSearch() {
        val currentState = _state.value
        if (currentState.query.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val type = currentState.selectedTypes.singleOrNull()

            repository.searchBreweries(
                query = currentState.query,
                type = type
            ).onSuccess { breweries ->
                val filtered = if (currentState.selectedTypes.size > 1) {
                    breweries.filter { it.breweryType in currentState.selectedTypes }
                } else {
                    breweries
                }
                _state.update {
                    it.copy(
                        results = filtered.map { b -> b.toBreweryUi() }.toImmutableList(),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                _events.send(BrewerySearchEvent.ShowError(error.toUiText()))
            }
        }
    }
}
