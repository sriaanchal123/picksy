package com.example.picksy

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

// --- Data Models ---
data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val weight: String,
    val imageRes: Int,
    val category: String,
    val description: String = "Premium quality fresh product, sourced directly from the best farms for your daily needs.",
)

data class CategoryData(val name: String, val iconRes: Int)

// --- Main HomeScreen Navigation Handler ---
@Composable
fun HomeScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val themeGreen = Color(0xFF1B5E20)
    val backgroundMix = Brush.verticalGradient(
        colors = listOf(Color(0xFF81C784), Color(0xFFE8F5E9), Color.White)
    )

    val uiState by viewModel.uiState.collectAsState()
    
    // Internal States
    var currentSubScreen by remember { mutableStateOf("home") } 
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryData?>(null) }
    var lastOrderId by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val allProducts = listOf(
        Product(1, "Fresh Milk", "₹30", "500ml", R.drawable.milk, "Dairy"),
        Product(2, "Broccoli", "₹80", "500g", R.drawable.brocolli, "Vegetables"),
        Product(3, "Amul Butter", "₹56", "100g", R.drawable.amul, "Dairy"),
        Product(4, "Brown Bread", "₹45", "400g", R.drawable.brownbread, "Dairy"),
        Product(5, "Cold Drinks", "₹95", "1L", R.drawable.beverages__2_, "Beverages"),
        Product(6, "Snacks Box", "₹145", "200g", R.drawable.snacks2, "Snacks"),
        Product(7, "Bananas", "₹40", "1kg", R.drawable.banana, "Fruits"),
        Product(8, "Mixed Veg", "₹120", "1kg", R.drawable.vegetables, "Vegetables"),
        Product(9, "Kitkat", "₹45", "120g", R.drawable.kitkat, "Snacks"),
    )

    val categories = listOf(
        CategoryData("Vegetables", R.drawable.vegetables),
        CategoryData("Fruits", R.drawable.fruits),
        CategoryData("Dairy", R.drawable.dairy),
        CategoryData("Beverages", R.drawable.beverages),
        CategoryData("Snacks", R.drawable.snacks)
    )

    val filteredProducts = if (searchQuery.isEmpty()) allProducts 
                          else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }

    // Navigation Logic BackPress
    BackHandler(enabled = currentSubScreen != "home" || selectedProduct != null || selectedCategory != null) {
        if (selectedProduct != null) selectedProduct = null
        else if (selectedCategory != null) selectedCategory = null
        else if (currentSubScreen == "cart") currentSubScreen = "home"
        else if (currentSubScreen == "checkout") currentSubScreen = "cart"
        else if (currentSubScreen == "success") currentSubScreen = "home"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentSubScreen) {
            "cart" -> CartScreen(viewModel, themeGreen, { currentSubScreen = "home" }, { currentSubScreen = "checkout" })
            "checkout" -> CheckoutScreen(viewModel, themeGreen, { currentSubScreen = "cart" }, { id -> lastOrderId = id; currentSubScreen = "success" })
            "success" -> OrderSuccessScreen(lastOrderId, themeGreen, { viewModel.clearCart(); currentSubScreen = "home" })
            else -> {
                if (selectedProduct != null) {
                    ProductDetailScreen(selectedProduct!!, themeGreen, { selectedProduct = null }, { viewModel.addToCart(it) })
                } else if (selectedCategory != null) {
                    CategoryDetailScreen(selectedCategory!!, allProducts.filter { it.category == selectedCategory!!.name }, themeGreen, { selectedCategory = null }, { selectedProduct = it }, { viewModel.addToCart(it) })
                } else {
                    HomeMainContent(
                        viewModel, categories, filteredProducts, searchQuery, { searchQuery = it },
                        themeGreen, backgroundMix, onBack, { selectedProduct = it }, { selectedCategory = it }, { currentSubScreen = "cart" }
                    )
                }
            }
        }
    }
}

// --- 1. Home Main View ---
@Composable
fun HomeMainContent(
    viewModel: MainViewModel,
    categories: List<CategoryData>,
    products: List<Product>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    themeGreen: Color,
    backgroundMix: Brush,
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (CategoryData) -> Unit,
    onCartClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(modifier = Modifier.fillMaxWidth(), color = themeGreen, shadowElevation = 8.dp) {
                Row(modifier = Modifier.statusBarsPadding().padding(horizontal = 8.dp, vertical = 12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                    TextField(
                        value = searchQuery, onValueChange = onSearchChange,
                        placeholder = { Text("Search products...", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        modifier = Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(painter = painterResource(id = R.drawable.app_logo), null, modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(Color.White))
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = uiState.cartItems.isNotEmpty(), enter = slideInVertically(initialOffsetY = { it }) + fadeIn()) {
                MiniCartBar(uiState.cartItems.sumOf { it.quantity }, viewModel.getTotalPrice(), themeGreen, onCartClick)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).background(brush = backgroundMix)) {
            item {
                val pagerState = rememberPagerState(pageCount = { 4 })
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(240.dp), contentPadding = PaddingValues(horizontal = 16.dp), pageSpacing = 12.dp) { page ->
                    Card(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(8.dp)) {
                        val img = when (page) { 0 -> R.drawable.__card; 1 -> R.drawable._card; 2 -> R.drawable.vegetables; else -> R.drawable.snacks }
                        Image(painter = painterResource(id = img), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }
            item {
                Text("Shop by Category", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 24.dp), color = themeGreen)
                Spacer(Modifier.height(16.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    items(categories) { category ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onCategoryClick(category) }) {
                            Surface(modifier = Modifier.size(85.dp), shape = CircleShape, color = Color.White, shadowElevation = 4.dp) {
                                Image(painterResource(category.iconRes), null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                            }
                            Text(category.name, Modifier.padding(top = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(28.dp))
                Text(if(searchQuery.isEmpty()) "Best Sellers" else "Results for '$searchQuery'", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 24.dp), color = themeGreen)
                Spacer(Modifier.height(14.dp))
            }
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    products.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { p -> ProductItem(p, Modifier.weight(1f).padding(8.dp), themeGreen, { onProductClick(p) }, { viewModel.addToCart(it) }) }
                            if (row.size == 1) Spacer(Modifier.weight(1f).padding(8.dp))
                        }
                    }
                }
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

// --- 2. Cart Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: MainViewModel, themeGreen: Color, onBack: () -> Unit, onCheckout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("My Cart", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) },
        bottomBar = {
            if (uiState.cartItems.isNotEmpty()) {
                Surface(shadowElevation = 20.dp, color = Color.White) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Payable", fontSize = 14.sp, color = Color.Gray)
                            Text("₹${viewModel.getTotalPrice() + 20}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = themeGreen)
                        }
                        Button(onClick = onCheckout, modifier = Modifier.height(55.dp).weight(1.5f), colors = ButtonDefaults.buttonColors(themeGreen), shape = RoundedCornerShape(14.dp)) {
                            Text("Checkout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF7F7F7)), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.cartItems) { item ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painterResource(item.product.imageRes), null, Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.product.name, fontWeight = FontWeight.Bold)
                                Text(item.product.price, color = themeGreen, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.updateQuantity(item.product, false) }) {
                                    if (item.quantity > 1) Box(Modifier.size(16.dp, 2.dp).background(themeGreen))
                                    else Icon(Icons.Default.Delete, null, tint = Color.Red)
                                }
                                Text(item.quantity.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                IconButton(onClick = { viewModel.updateQuantity(item.product, true) }) { Icon(Icons.Default.Add, null, tint = themeGreen) }
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("Bill Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Card(Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(Color.White)) {
                        Column(Modifier.padding(16.dp)) {
                            val sub = viewModel.getTotalPrice()
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Item Total"); Text("₹$sub") }
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Delivery Fee"); Text("₹20") }
                            Divider(Modifier.padding(vertical = 8.dp))
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("To Pay", fontWeight = FontWeight.Bold); Text("₹${sub + 20}", fontWeight = FontWeight.Bold, color = themeGreen) }
                        }
                    }
                }
            }
        }
    }
}

// --- 3. Checkout Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(viewModel: MainViewModel, themeGreen: Color, onBack: () -> Unit, onOrderPlaced: (String) -> Unit) {
    var address by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Checkout") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) },
        bottomBar = { Button(onClick = { if (address.isNotBlank()) onOrderPlaced(UUID.randomUUID().toString().substring(0, 8).uppercase()) }, modifier = Modifier.fillMaxWidth().padding(16.dp).height(55.dp).navigationBarsPadding(), colors = ButtonDefaults.buttonColors(themeGreen), enabled = address.isNotBlank(), shape = RoundedCornerShape(14.dp)) { Text("Place Order", fontWeight = FontWeight.Bold) } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(value = address, onValueChange = { address = it }, Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("Flat No, Building, Area...") }, shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(24.dp))
            Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = true, onClick = {}); Text("Cash on Delivery (COD)", Modifier.padding(start = 8.dp)) }
        }
    }
}

// --- 4. Order Success Screen ---
@Composable
fun OrderSuccessScreen(id: String, themeGreen: Color, onBackToHome: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.CheckCircle, null, Modifier.size(100.dp), themeGreen)
        Text("Order Confirmed!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Order ID: #$id", color = Color.Gray)
        Spacer(Modifier.height(12.dp))
        Text("Estimated Delivery: 15-20 Mins", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(48.dp))
        Button(onClick = onBackToHome, Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(themeGreen), shape = RoundedCornerShape(14.dp)) { Text("Back to Home") }
    }
}

// --- Common Components ---
@Composable
fun MiniCartBar(itemCount: Int, totalPrice: Int, themeGreen: Color, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding().clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = themeGreen, shadowElevation = 10.dp) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("$itemCount ITEMS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("₹$totalPrice", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("View Cart", color = Color.White, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun CategoryItem(category: CategoryData, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(modifier = Modifier.size(85.dp), shape = CircleShape, color = Color.White, shadowElevation = 4.dp) {
            Image(painterResource(category.iconRes), null, Modifier.fillMaxSize().clip(CircleShape).padding(10.dp), contentScale = ContentScale.Crop)
        }
        Text(category.name, Modifier.padding(top = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProductItem(product: Product, modifier: Modifier, themeGreen: Color, onProductClick: () -> Unit, onAddToCart: (Product) -> Unit) {
    Card(modifier = modifier.clickable { onProductClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Image(painterResource(product.imageRes), null, Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)
            Text(product.name, Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(product.weight, fontSize = 12.sp, color = Color.Gray)
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(product.price, fontWeight = FontWeight.Bold, color = themeGreen)
                Surface(Modifier.size(32.dp).clickable { onAddToCart(product) }, RoundedCornerShape(10.dp), themeGreen) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.padding(6.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(category: CategoryData, products: List<Product>, themeGreen: Color, onBack: () -> Unit, onProductClick: (Product) -> Unit, onAddToCart: (Product) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(category.name) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
        LazyVerticalGrid(columns = GridCells.Fixed(2), Modifier.fillMaxSize().padding(padding).background(Color(0xFFF7F7F7)), contentPadding = PaddingValues(8.dp)) {
            items(products) { p -> ProductItem(p, Modifier.padding(8.dp), themeGreen, { onProductClick(p) }, onAddToCart) }
        }
    }
}

@Composable
fun ProductDetailScreen(product: Product, themeGreen: Color, onBack: () -> Unit, onAddToCart: (Product) -> Unit) {
    Scaffold(bottomBar = { Surface(shadowElevation = 10.dp, color = Color.White) { Button(onClick = { onAddToCart(product) }, modifier = Modifier.fillMaxWidth().padding(16.dp).height(55.dp).navigationBarsPadding(), colors = ButtonDefaults.buttonColors(themeGreen), shape = RoundedCornerShape(14.dp)) { Text("Add to Cart - ${product.price}", fontWeight = FontWeight.Bold) } } } ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Box(Modifier.fillMaxWidth().height(320.dp).background(Color(0xFFF5F5F5))) {
                Image(painterResource(product.imageRes), null, Modifier.fillMaxSize().padding(32.dp), contentScale = ContentScale.Fit)
                IconButton(onClick = onBack, modifier = Modifier.statusBarsPadding().padding(16.dp).background(Color.White, CircleShape)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = themeGreen) }
            }
            Column(Modifier.padding(24.dp)) {
                Text(product.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(product.weight, fontSize = 18.sp, color = Color.Gray)
                Spacer(Modifier.height(24.dp))
                Text("Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(product.description, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp), lineHeight = 22.sp)
            }
        }
    }
}
