package com.zeus.pseudocharlesdemo.feature.brewery.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeus.pseudocharlesdemo.core.domain.onFailure
import com.zeus.pseudocharlesdemo.core.domain.onSuccess
import com.zeus.pseudocharlesdemo.core.presentation.toUiText
import com.zeus.pseudocharlesdemo.feature.brewery.domain.BreweryRepository
import com.zeus.pseudocharlesdemo.feature.brewery.presentation.model.toBreweryUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreweryDetailViewModel(
    private val repository: BreweryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breweryId: String = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(BreweryDetailState())
    val state: StateFlow<BreweryDetailState> = _state

    private val _events = Channel<BreweryDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadBrewery()
    }

    fun onAction(action: BreweryDetailAction) {
        when (action) {
            BreweryDetailAction.OnBackClick -> {
                viewModelScope.launch { _events.send(BreweryDetailEvent.NavigateBack) }
            }
            is BreweryDetailAction.OnOpenWebsite -> {
                viewModelScope.launch { _events.send(BreweryDetailEvent.OpenUrl(action.url)) }
            }
            is BreweryDetailAction.OnCallPhone -> {
                viewModelScope.launch { _events.send(BreweryDetailEvent.DialPhone(action.phone)) }
            }
        }
    }

    private fun loadBrewery() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getBrewery(breweryId)
                .onSuccess { brewery ->
                    _state.update {
                        it.copy(brewery = brewery.toBreweryUi(), isLoading = false)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.toUiText())
                    }
                }
        }
    }
}
