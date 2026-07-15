// ---------------------------------------------------------------
// Change this to your deployed backend URL, e.g.
// "https://api.ammabiryani.com/api" once it's live on AWS.
// ---------------------------------------------------------------
const API_BASE = "http://localhost:8080/api";

let menuItems = [];
const cart = {}; // { menuItemId: { item, quantity } }

const menuGrid = document.getElementById("menu-grid");
const cartBar = document.getElementById("cart-bar");
const cartSummary = document.getElementById("cart-summary");
const cartModal = document.getElementById("cart-modal");
const cartItemsEl = document.getElementById("cart-items");
const cartTotalEl = document.getElementById("cart-total-amount");
const checkoutForm = document.getElementById("checkout-form");
const orderConfirmation = document.getElementById("order-confirmation");

async function loadMenu() {
  try {
    const res = await fetch(`${API_BASE}/menu`);
    if (!res.ok) throw new Error("Failed to load menu");
    menuItems = await res.json();
    renderMenu();
  } catch (err) {
    menuGrid.innerHTML = `<p class="loading">Could not load menu. Please make sure the backend server is running.</p>`;
    console.error(err);
  }
}

function renderMenu() {
  if (menuItems.length === 0) {
    menuGrid.innerHTML = `<p class="loading">No items available right now.</p>`;
    return;
  }

  menuGrid.innerHTML = menuItems.map(item => `
    <div class="menu-card">
      <h3>${escapeHtml(item.name)}</h3>
      <p>${escapeHtml(item.description || "")}</p>
      <div class="price">₹${item.price}</div>
      <div class="qty-controls">
        <button onclick="changeQty(${item.id}, -1)">-</button>
        <span id="qty-${item.id}">${cart[item.id]?.quantity || 0}</span>
        <button onclick="changeQty(${item.id}, 1)">+</button>
      </div>
    </div>
  `).join("");
}

function changeQty(itemId, delta) {
  const item = menuItems.find(m => m.id === itemId);
  if (!item) return;

  const current = cart[itemId]?.quantity || 0;
  const next = Math.max(0, current + delta);

  if (next === 0) {
    delete cart[itemId];
  } else {
    cart[itemId] = { item, quantity: next };
  }

  document.getElementById(`qty-${itemId}`).textContent = next;
  updateCartBar();
}

function updateCartBar() {
  const entries = Object.values(cart);
  const totalItems = entries.reduce((sum, e) => sum + e.quantity, 0);
  const totalPrice = entries.reduce((sum, e) => sum + e.quantity * e.item.price, 0);

  cartSummary.textContent = `${totalItems} item${totalItems !== 1 ? "s" : ""} · ₹${totalPrice.toFixed(2)}`;
  cartBar.classList.toggle("hidden", totalItems === 0);
}

function renderCartModal() {
  const entries = Object.values(cart);

  if (entries.length === 0) {
    cartItemsEl.innerHTML = `<p>Your cart is empty.</p>`;
    cartTotalEl.textContent = "0";
    return;
  }

  cartItemsEl.innerHTML = entries.map(e => `
    <div class="cart-item">
      <span>${escapeHtml(e.item.name)} × ${e.quantity}</span>
      <span>₹${(e.item.price * e.quantity).toFixed(2)}</span>
    </div>
  `).join("");

  const total = entries.reduce((sum, e) => sum + e.quantity * e.item.price, 0);
  cartTotalEl.textContent = total.toFixed(2);
}

document.getElementById("open-cart-btn").addEventListener("click", () => {
  renderCartModal();
  checkoutForm.classList.remove("hidden");
  orderConfirmation.classList.add("hidden");
  cartModal.classList.remove("hidden");
});

document.getElementById("close-cart-btn").addEventListener("click", () => {
  cartModal.classList.add("hidden");
});

checkoutForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const entries = Object.values(cart);
  if (entries.length === 0) {
    alert("Your cart is empty.");
    return;
  }

  const orderRequest = {
    customerName: document.getElementById("customerName").value.trim(),
    phone: document.getElementById("phone").value.trim(),
    address: document.getElementById("address").value.trim(),
    items: entries.map(e => ({ menuItemId: e.item.id, quantity: e.quantity }))
  };

  const placeBtn = document.getElementById("place-order-btn");
  placeBtn.disabled = true;
  placeBtn.textContent = "Placing order...";

  try {
    const res = await fetch(`${API_BASE}/orders`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(orderRequest)
    });

    if (!res.ok) {
      const errBody = await res.json().catch(() => ({}));
      throw new Error(errBody.message || "Failed to place order");
    }

    const order = await res.json();

    document.getElementById("confirm-id").textContent = order.id;
    document.getElementById("confirm-name").textContent = ` ${order.customerName}`;
    document.getElementById("confirm-phone").textContent = order.phone;

    checkoutForm.classList.add("hidden");
    orderConfirmation.classList.remove("hidden");

    // Reset cart
    Object.keys(cart).forEach(id => delete cart[id]);
    updateCartBar();
    renderMenu();
    checkoutForm.reset();
  } catch (err) {
    alert("Could not place order: " + err.message);
    console.error(err);
  } finally {
    placeBtn.disabled = false;
    placeBtn.textContent = "Place Order";
  }
});

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

loadMenu();
