package com.example.picksy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

data class AppUiState(
    val cartItems: List<CartItem> = emptyList(),
    val address: String = "",
    val paymentMethod: String = "Cash on Delivery"
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun addToCart(product: Product) {
        _uiState.update { currentState ->
            val existingItem = currentState.cartItems.find { it.product.id == product.id }
            val updatedItems = if (existingItem != null) {
                currentState.cartItems.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentState.cartItems + CartItem(product)
            }
            currentState.copy(cartItems = updatedItems)
        }
    }

    fun updateQuantity(product: Product, increase: Boolean) {
        _uiState.update { currentState ->
            val updatedItems = currentState.cartItems.mapNotNull { item ->
                if (item.product.id == product.id) {
                    val newQty = if (increase) item.quantity + 1 else item.quantity - 1
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
            currentState.copy(cartItems = updatedItems)
        }
    }

    fun removeFromCart(product: Product) {
        _uiState.update { currentState ->
            currentState.copy(cartItems = currentState.cartItems.filter { it.product.id != product.id })
        }
    }

    fun updateAddress(newAddress: String) {
        _uiState.update { it.copy(address = newAddress) }
    }

    fun updatePaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList()) }
    }

    fun getTotalPrice(): Int {
        return _uiState.value.cartItems.sumOf {
            val priceStr = it.product.price.replace("₹", "").replace(",", "").trim()
            (priceStr.toIntOrNull() ?: 0) * it.quantity
        }
    }
}
