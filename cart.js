function addToCart(event, button) {
    event.preventDefault();
    const productCard = button.closest('.product-card');
    
    const imgElement = productCard.querySelector('.product-thumb img');
    // Using the image alt attribute as the product name as requested
    const name = imgElement.getAttribute('alt') || 'Unknown Product';
    const imageSrc = imgElement.getAttribute('src');
    
    const priceText = productCard.querySelector('.price-current').innerText;
    const price = parseFloat(priceText.replace(/[^0-9.]/g, ''));
    
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    let existingItem = cart.find(item => item.name === name);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            name: name,
            image: imageSrc,
            price: price,
            quantity: 1
        });
    }
    
    localStorage.setItem('cart', JSON.stringify(cart));
    window.location.href = 'Cart.html';
}

function loadCart() {
    const tbody = document.getElementById('cart-tbody');
    const summaryTotal = document.getElementById('summary-total');
    const summarySubtotal = document.getElementById('summary-subtotal');
    const cartHeader = document.getElementById('cart-header-title');
    
    if (!tbody) return; 
    
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    tbody.innerHTML = '';
    
    let totalItems = 0;
    let subtotal = 0;
    
    cart.forEach((item, index) => {
        totalItems += item.quantity;
        let itemTotal = item.price * item.quantity;
        subtotal += itemTotal;
        
        let tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="cart-item-cell">
                    <img src="${item.image}" alt="${item.name}" class="cart-item-img">
                    <div class="cart-item-info">
                        <h4>${item.name}</h4>
                    </div>
                </div>
            </td>
            <td>
                <span>$${item.price.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}</span>
            </td>
            <td>
                <div class="quantity-control">
                    <button class="qty-btn" onclick="updateQty(${index}, -1)">-</button>
                    <input type="text" class="qty-input" value="${item.quantity}" readonly>
                    <button class="qty-btn" onclick="updateQty(${index}, 1)">+</button>
                </div>
            </td>
            <td>
                <span style="font-weight: 700; color: var(--primary);">$${itemTotal.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}</span>
            </td>
            <td>
                <button class="cart-remove-btn" title="Remove item" onclick="removeItem(${index})">
                    <i class="fa-regular fa-trash-can"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
    
    if (cart.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem;">Your cart is empty.</td></tr>';
    }
    
    let discount = 0;
    const isCouponApplied = localStorage.getItem('couponApplied') === 'true';
    const discountRow = document.getElementById('discount-row');
    const summaryDiscount = document.getElementById('summary-discount');
    
    if (isCouponApplied && subtotal > 0) {
        discount = subtotal * 0.08;
        if (discountRow) discountRow.style.display = 'flex';
        if (summaryDiscount) summaryDiscount.innerText = `-$${discount.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
    } else {
        if (discountRow) discountRow.style.display = 'none';
    }
    
    let total = subtotal - discount;

    if (cartHeader) cartHeader.innerText = `Shopping Cart (${totalItems} Items)`;
    if (summarySubtotal) summarySubtotal.innerText = `$${subtotal.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
    if (summaryTotal) summaryTotal.innerText = `$${total.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
}

function updateQty(index, change) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    if (cart[index]) {
        cart[index].quantity += change;
        if (cart[index].quantity <= 0) {
            cart.splice(index, 1);
        }
        localStorage.setItem('cart', JSON.stringify(cart));
        loadCart();
    }
}

function removeItem(index) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    cart.splice(index, 1);
    localStorage.setItem('cart', JSON.stringify(cart));
    loadCart();
}

document.addEventListener('DOMContentLoaded', loadCart);

function applyCoupon() {
    const input = document.getElementById('coupon-input');
    if (!input || !input.value.trim()) {
        alert("Please enter a valid coupon code.");
        return;
    }
    localStorage.setItem('couponApplied', 'true');
    alert("Coupon applied! 8% discount added.");
    loadCart();
}

function checkout() {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    if (cart.length === 0) {
        alert("Your cart is empty!");
        return;
    }
    localStorage.removeItem('cart');
    localStorage.removeItem('couponApplied');
    alert("Checkout successful! Thank you for your order.");
    window.location.href = "Home-Page.html";
}
