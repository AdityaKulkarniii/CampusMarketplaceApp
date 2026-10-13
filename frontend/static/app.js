const API = "/api";
const demoProducts = [
  { id: 1, title: "Botany: An Introduction", category: "Textbooks", price: 28, description: "Clean copy with light highlighting in two chapters. Great for the intro course.", pickupLocation: "Science Library steps", imageUrl: "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=900&q=80", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 2, title: "Desk lamp, warm light", category: "Furniture", price: 16, description: "Three brightness levels, works perfectly. Bulb included.", pickupLocation: "Maple Hall lobby", imageUrl: "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80", sellerName: "Jordan K.", sellerRating: 4.8, campus: "North Campus", status: "AVAILABLE" },
  { id: 3, title: "Film camera + case", category: "Electronics", price: 42, description: "A little vintage point-and-shoot. Tested, ready for a new roll.", pickupLocation: "Student Union", imageUrl: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80", sellerName: "Sam D.", sellerRating: 5.0, campus: "North Campus", status: "AVAILABLE" },
  { id: 4, title: "Canvas everyday tote", category: "Clothing", price: 9, description: "Roomy, sturdy, and freshly washed. Small inside pocket.", pickupLocation: "Arts building courtyard", imageUrl: "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=900&q=80", sellerName: "Riley T.", sellerRating: 4.7, campus: "North Campus", status: "AVAILABLE" },
  { id: 5, title: "Scientific calculator", category: "Supplies", price: 20, description: "TI-36X Pro in great shape. Ideal for engineering and calculus.", pickupLocation: "Engineering quad", imageUrl: "https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80", sellerName: "Alex W.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 6, title: "Oak bedside table", category: "Furniture", price: 34, description: "Solid, compact table with a single drawer. Fits perfectly by a dorm bed.", pickupLocation: "Willow Hall front desk", imageUrl: "https://images.unsplash.com/photo-1499933374294-4584851497cc?auto=format&fit=crop&w=900&q=80", sellerName: "Casey L.", sellerRating: 4.6, campus: "North Campus", status: "AVAILABLE" },
  { id: 7, title: "Calculus II study guide", category: "Textbooks", price: 12, description: "Worked examples and a formula sheet. A few pencilled notes inside.", pickupLocation: "Main library cafe", imageUrl: "https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=900&q=80", sellerName: "Noah P.", sellerRating: 4.8, campus: "North Campus", status: "AVAILABLE" },
  { id: 8, title: "Bluetooth headphones", category: "Electronics", price: 30, description: "Comfortable over-ear headphones. Holds charge and pairs easily.", pickupLocation: "Student Union entrance", imageUrl: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80", sellerName: "Taylor B.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 9, title: "Casio fx-991CW ClassWiz", category: "Supplies", price: 24, description: "Casio fx-991CW scientific calculator. Fully working with protective case.", pickupLocation: "Engineering quad", imageUrl: "/images/casio-fx-991cw.svg", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 10, title: "Data Structures and Algorithms", category: "Textbooks", price: 32, description: "CSE textbook covering arrays, trees, graphs, sorting, and algorithm analysis.", pickupLocation: "Computer Science building", imageUrl: "/images/cse-data-structures.svg", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 11, title: "Operating Systems: Concepts and Design", category: "Textbooks", price: 30, description: "CSE reference for processes, memory management, file systems, and concurrency.", pickupLocation: "Computer Science building", imageUrl: "/images/cse-operating-systems.svg", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 12, title: "Database Systems", category: "Textbooks", price: 27, description: "CSE textbook on relational databases, SQL, normalization, and transactions.", pickupLocation: "Main library cafe", imageUrl: "/images/cse-database-systems.svg", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 13, title: "Computer Networks", category: "Textbooks", price: 29, description: "CSE course book covering network layers, routing, protocols, and security basics.", pickupLocation: "Computer Science building", imageUrl: "/images/cse-computer-networks.svg", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" },
  { id: 14, title: "A4 printer paper, 500 sheets", category: "Supplies", price: 6, description: "Unopened ream of 80 gsm A4 white printer paper.", pickupLocation: "Campus print center", imageUrl: "/images/a4-paper-ream.svg", sellerName: "Maya R.", sellerRating: 4.9, campus: "North Campus", status: "AVAILABLE" }
];
const state = { products: [], category: "All", query: "", sort: "newest", savedOnly: false, saved: new Set(JSON.parse(localStorage.getItem("campusSaved") || "[]")), user: JSON.parse(localStorage.getItem("campusUser") || "null"), token: localStorage.getItem("campusToken"), authMode: "login", detail: null, adminTab: "reports" };
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
const escapeHtml = value => String(value ?? "").replace(/[&<>"']/g, char => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char]);
const money = value => new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 2 }).format(value);
const listingPrice = item => `${money(item.price)}${item.listingType === "RENT" ? ` / ${String(item.rentalPeriod || "DAY").toLowerCase()}` : ""}`;
const productImages = item => [...new Set([...(Array.isArray(item.imageUrls) ? item.imageUrls : []), item.imageUrl].filter(url => typeof url === "string" && url.trim()))].slice(0, 8);
const rentalDuration = item => {
  const period = String(item.rentalPeriod || "DAY").toLowerCase();
  const duration = Number(item.minimumRentalDuration || 1);
  return `${duration} ${period}${duration === 1 ? "" : "s"}`;
};
const initials = name => String(name || "").trim().split(/\s+/).slice(0, 2).map(part => part[0]).join("").toUpperCase() || "↗";

async function api(path, options = {}) {
  const headers = { ...(options.body ? { "Content-Type": "application/json" } : {}), ...(state.token ? { Authorization: `Bearer ${state.token}` } : {}), ...options.headers };
  const response = await fetch(`${API}${path}`, { ...options, headers });
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;
  if (!response.ok) throw new Error(data?.message || data?.error || `Request failed (${response.status})`);
  return data;
}
function setNotice(message, error = false) { const notice = $("#notice"); notice.textContent = message; notice.classList.toggle("error", error); notice.classList.remove("hidden"); window.clearTimeout(setNotice.timer); setNotice.timer = window.setTimeout(() => notice.classList.add("hidden"), 4500); }
function updateAccount() { const button = $("#account-button"); button.textContent = state.user ? initials(state.user.name) : "↗"; button.classList.toggle("signed-in", Boolean(state.user)); button.setAttribute("aria-label", state.user ? `Signed in as ${state.user.name}. Open notifications` : "Sign in"); button.title = state.user ? "Notifications" : "Sign in"; $("#saved-count").textContent = state.saved.size; }
let rotatingImage = null;
let rotationStart = null;
let rotationMoved = false;
let suppressRotatedClick = false;

document.addEventListener("pointerdown", event => {
  const image = event.target.closest(".product-image, .detail-image");
  if (!image || (event.pointerType === "mouse" && event.button !== 0)) return;
  suppressRotatedClick = false;
  rotatingImage = image;
  rotationMoved = false;
  rotationStart = {
    pointerId: event.pointerId,
    x: event.clientX,
    y: event.clientY,
    rotateX: Number(image.dataset.rotateX || 2),
    rotateY: Number(image.dataset.rotateY || -3),
    imageIndex: Number(image.dataset.imageIndex || 0)
  };
  image.classList.add("is-rotating");
  try { image.setPointerCapture(event.pointerId); } catch { }
});

document.addEventListener("pointermove", event => {
  if (!rotatingImage || event.pointerId !== rotationStart.pointerId) return;
  const deltaX = event.clientX - rotationStart.x;
  const deltaY = event.clientY - rotationStart.y;
  if (!rotationMoved && Math.hypot(deltaX, deltaY) < 4) return;
  rotationMoved = true;
  event.preventDefault();
  const item = state.products.find(product => product.id === Number(rotatingImage.dataset.imageProduct));
  const images = item ? productImages(item) : [];
  if (images.length > 1) {
    const indexOffset = Math.trunc(deltaX / 35);
    const index = ((rotationStart.imageIndex - indexOffset) % images.length + images.length) % images.length;
    rotatingImage.dataset.imageIndex = String(index);
    rotatingImage.src = images[index];
    const viewer = rotatingImage.closest("[data-image-viewer]");
    if (viewer) setViewerImage(viewer, index);
    else {
      const counter = rotatingImage.parentElement.querySelector(".card-image-count");
      if (counter) counter.textContent = `${index + 1} / ${images.length}`;
    }
    return;
  }
  const rotateX = Math.max(-35, Math.min(35, rotationStart.rotateX - deltaY * 0.45));
  const rotateY = Math.max(-45, Math.min(45, rotationStart.rotateY + deltaX * 0.45));
  rotatingImage.dataset.rotateX = String(rotateX);
  rotatingImage.dataset.rotateY = String(rotateY);
  rotatingImage.style.setProperty("--rotate-x", `${rotateX}deg`);
  rotatingImage.style.setProperty("--rotate-y", `${rotateY}deg`);
});

function finishImageRotation(event, canceled = false) {
  if (!rotatingImage || event.pointerId !== rotationStart.pointerId) return;
  const image = rotatingImage;
  if (image.hasPointerCapture?.(event.pointerId)) image.releasePointerCapture(event.pointerId);
  image.classList.remove("is-rotating");
  suppressRotatedClick = rotationMoved && !canceled && Boolean(image.closest("[data-detail]"));
  rotatingImage = null;
  rotationStart = null;
  rotationMoved = false;
}

document.addEventListener("pointerup", event => finishImageRotation(event));
document.addEventListener("pointercancel", event => finishImageRotation(event, true));
document.addEventListener("dragstart", event => {
  if (event.target.closest(".product-image, .detail-image")) event.preventDefault();
});
document.addEventListener("click", event => {
  if (!suppressRotatedClick) return;
  suppressRotatedClick = false;
  if (!event.target.closest("[data-detail]")) return;
  event.preventDefault();
  event.stopImmediatePropagation();
}, true);
function setViewerImage(viewer, requestedIndex) {
  const item = state.products.find(product => product.id === Number(viewer.dataset.imageViewer));
  if (!item) return;
  const images = productImages(item);
  if (!images.length) return;
  const index = ((requestedIndex % images.length) + images.length) % images.length;
  viewer.dataset.imageIndex = String(index);
  const image = $("[data-viewer-image]", viewer);
  image.src = images[index];
  image.dataset.imageIndex = String(index);
  const counter = $("[data-image-count]", viewer);
  if (counter) counter.textContent = `${index + 1} / ${images.length}`;
  $$("[data-viewer-index]", viewer).forEach(button => {
    const selected = Number(button.dataset.viewerIndex) === index;
    button.classList.toggle("active", selected);
    button.setAttribute("aria-pressed", String(selected));
  });
}
document.addEventListener("click", event => {
  const stepButton = event.target.closest("[data-viewer-step]");
  if (stepButton) {
    const viewer = stepButton.closest("[data-image-viewer]");
    setViewerImage(viewer, Number(viewer.dataset.imageIndex || 0) + Number(stepButton.dataset.viewerStep));
    return;
  }
  const imageButton = event.target.closest("[data-viewer-index]");
  if (imageButton) setViewerImage(imageButton.closest("[data-image-viewer]"), Number(imageButton.dataset.viewerIndex));
});
async function loadProducts() {
  const params = new URLSearchParams();
  if (state.query) params.set("query", state.query);
  if (state.category !== "All") params.set("category", state.category);
  if (state.sort !== "newest") params.set("sort", state.sort);
  if (!state.token) {
    state.products = demoProducts.filter(item => (state.category === "All" || item.category === state.category) && (!state.query || `${item.title} ${item.description} ${item.category}`.toLowerCase().includes(state.query.toLowerCase())));
    sortProducts(state.products);
    renderProducts();
    return;
  }
  try { state.products = await api(`/products?${params}`); }
  catch { state.products = demoProducts.filter(item => (state.category === "All" || item.category === state.category) && (!state.query || `${item.title} ${item.description} ${item.category}`.toLowerCase().includes(state.query.toLowerCase()))); sortProducts(state.products); }
  renderProducts();
}
function sortProducts(items) { if (state.sort === "price-asc") items.sort((a, b) => a.price - b.price); else if (state.sort === "price-desc") items.sort((a, b) => b.price - a.price); else items.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0)); }
function renderProducts() {
  const found = state.savedOnly ? state.products.filter(item => state.saved.has(item.id)) : state.products;
  const grid = $("#product-grid");
  $("#total-count").textContent = String(state.products.length);
  $("#results-title").textContent = state.savedOnly ? "Your saved finds" : state.category === "All" ? "Fresh on campus" : state.category;
  $("#results-meta").textContent = `${found.length} ${found.length === 1 ? "find" : "finds"}`;
  $("#empty-state").classList.toggle("hidden", found.length > 0);
  grid.classList.toggle("hidden", found.length === 0);
  grid.innerHTML = found.map((item, index) => {
    const images = productImages(item);
    const image = images[0] || "https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=900&q=80";
    return `<article class="product-card" style="animation-delay:${Math.min(index * 35, 210)}ms"><div class="product-image-wrap"><button class="product-image-trigger" type="button" data-detail="${item.id}" aria-label="${images.length > 1 ? "Drag to spin through product angles or" : ""} view ${escapeHtml(item.title)} details" title="${images.length > 1 ? "Drag to spin through product angles; click for details" : "Click to view details"}"><img class="product-image ${images.length > 1 ? "is-multi-angle" : ""}" data-image-product="${item.id}" data-image-index="0" src="${escapeHtml(image)}" alt="${escapeHtml(item.title)}" loading="lazy"><span class="card-category">${escapeHtml(item.category)}</span>${item.listingType === "RENT" ? "<span class=\"listing-type-tag\">RENT</span>" : ""}${images.length > 1 ? `<span class="card-image-count" aria-hidden="true">1 / ${images.length}</span>` : ""}</button><button class="save-button ${state.saved.has(item.id) ? "saved" : ""}" data-save="${item.id}" aria-label="${state.saved.has(item.id) ? "Remove from" : "Add to"} saved items"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg></button>${item.status === "SOLD" ? "<span class=\"sold-tag\">PASSED ALONG</span>" : ""}</div><button class="product-info text-button" data-detail="${item.id}" style="display:block;width:100%;text-align:left"><span class="product-topline"><span class="product-title">${escapeHtml(item.title)}</span><span class="product-price">${listingPrice(item)}</span></span><span class="product-subline"><span>${escapeHtml(item.pickupLocation || item.campus || "Campus pickup")}</span><span class="seller-rating">★ ${Number(item.sellerRating || 0).toFixed(1)}</span></span></button></article>`;
  }).join("");
  updateAccount();
}
function showAuth() { state.authMode = "login"; setAuthMode(); $("#auth-error").textContent = ""; $("#auth-modal").showModal(); }
function setAuthMode() { const registering = state.authMode === "register"; $$(".auth-tab").forEach(tab => tab.classList.toggle("active", tab.dataset.authMode === state.authMode)); $("#auth-title").textContent = registering ? "Join your campus." : "Welcome back."; $("#name-field").classList.toggle("hidden", !registering); $("#campus-field").classList.toggle("hidden", !registering); $("#auth-submit").textContent = registering ? "Create account ↗" : "Sign in ↗"; $("#auth-form").elements.password.autocomplete = registering ? "new-password" : "current-password"; }
function openListing(item) {
  state.detail = item;
  const images = productImages(item);
  const fallback = "https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=900&q=80";
  const thumbnails = images.length > 1
    ? `<div class="viewer-thumbnails" aria-label="Product photo angles">${images.map((image, index) => `<button type="button" class="viewer-thumbnail ${index === 0 ? "active" : ""}" data-viewer-index="${index}" aria-label="Show product angle ${index + 1}" aria-pressed="${index === 0}"><img src="${escapeHtml(image)}" alt=""></button>`).join("")}</div>`
    : "";
  const viewer = `<div class="product-viewer ${images.length > 1 ? "is-multi-angle" : ""}" data-image-index="0"><img class="detail-image ${images.length > 1 ? "is-multi-angle" : ""}" data-viewer-image data-image-product="${item.id}" data-image-index="0" src="${escapeHtml(images[0] || fallback)}" alt="${escapeHtml(item.title)}">${images.length > 1 ? `<button type="button" class="viewer-arrow viewer-previous" data-viewer-step="-1" aria-label="Previous product angle">‹</button><button type="button" class="viewer-arrow viewer-next" data-viewer-step="1" aria-label="Next product angle">›</button><span class="viewer-count" data-image-count aria-live="polite">1 / ${images.length}</span>` : ""}</div>`;
  $("#detail-content").innerHTML = `<div class="detail-layout"><div class="detail-gallery" data-image-viewer="${item.id}" data-image-index="0">${viewer}${thumbnails}</div><div class="detail-copy"><span class="detail-category">${escapeHtml(item.category)}</span><h2>${escapeHtml(item.title)}</h2><div class="detail-price">${listingPrice(item)}</div>${item.listingType === "RENT" ? `<p class="detail-rental-minimum">Minimum rental: ${escapeHtml(rentalDuration(item))}</p>` : ""}<p class="detail-description">${escapeHtml(item.description)}</p><div class="detail-meta"><div><strong>Pickup</strong> · ${escapeHtml(item.pickupLocation || "Campus pickup")}</div><div><strong>Seller</strong> · ${escapeHtml(item.sellerName || "Campus student")} <span class="seller-rating">★ ${Number(item.sellerRating || 0).toFixed(1)}</span></div><div><strong>Campus</strong> · ${escapeHtml(item.campus || "Your campus")}</div></div><div class="detail-actions">${item.status === "AVAILABLE" ? `<button class="button button-dark" data-contact="${item.id}">Contact seller ↗</button>${state.user?.id === item.sellerId ? `<button class="button button-light" data-sold="${item.id}">Mark as passed along</button>` : ""}` : `<button class="button button-light" disabled>Already passed along</button>${state.user && state.user.id !== item.sellerId ? `<button class="button button-dark" data-rate="${item.id}">Rate this handoff ★</button>` : ""}`}</div><button class="detail-report" data-report="${item.id}">Report this listing</button></div></div>`;
  $("#detail-modal").showModal();
}
function toggleSave(id) { if (state.saved.has(id)) state.saved.delete(id); else state.saved.add(id); localStorage.setItem("campusSaved", JSON.stringify([...state.saved])); if (state.token) { const method = state.saved.has(id) ? "POST" : "DELETE"; api(`/wishlist/${id}`, { method }).catch(() => {}); } renderProducts(); }
function openMessage(id) { state.detail = state.products.find(item => item.id === id) || state.detail; $("#message-item").textContent = state.detail ? `Message the seller about “${state.detail.title}”.` : "Send a message to the seller."; $("#message-error").textContent = ""; $("#detail-modal").close(); $("#message-modal").showModal(); }
function openReport(id) { state.detail = state.products.find(item => item.id === id) || state.detail; $("#report-item").textContent = state.detail ? `Listing: “${state.detail.title}”.` : "Tell us what seems wrong."; $("#report-error").textContent = ""; $("#detail-modal").close(); $("#report-modal").showModal(); }
async function loadNotifications() { try { const items = await api("/notifications"); $("#notification-list").innerHTML = items.length ? items.map(item => `<div class="notification-item ${item.read ? "" : "unread"}">${escapeHtml(item.message)}<small>${new Date(item.createdAt).toLocaleString()}</small></div>`).join("") : "<div class=\"admin-empty\">You're all caught up.</div>"; } catch { $("#notification-list").innerHTML = "<div class=\"admin-empty\">Sign in to view your notifications.</div>"; } $("#notifications-modal").showModal(); }
async function loadAdmin(tab = state.adminTab) { state.adminTab = tab; $$(".admin-tab").forEach(button => button.classList.toggle("active", button.dataset.adminTab === tab)); const content = $("#admin-content"); content.innerHTML = "<div class=\"admin-empty\">Loading...</div>"; const endpoints = { reports: "/admin/reports", listings: "/admin/products", users: "/admin/users", categories: "/admin/categories" }; try { const data = await api(endpoints[tab]); if (tab === "categories") { content.innerHTML = `<form id="category-form" class="field-pair"><input name="name" placeholder="New category" required><button class="button button-dark">Add category</button></form>${data.map(item => `<div class="admin-row"><span>${escapeHtml(item.name)}</span><button class="danger" data-delete-category="${item.id}">Remove</button></div>`).join("") || "<div class=\"admin-empty\">No categories yet.</div>"}`; } else { content.innerHTML = data.length ? data.map(item => { const title = tab === "reports" ? `${item.reason} · ${item.productTitle}` : tab === "listings" ? `${item.title} · ${listingPrice(item)}` : `${item.name} · ${item.email}`; return `<div class="admin-row"><span>${escapeHtml(title)}<small>${escapeHtml(item.status || item.campus || item.createdAt || "")}</small></span>${tab === "reports" ? `<button class="danger" data-resolve-report="${item.id}">Resolve</button>` : tab === "listings" ? `<button class="danger" data-admin-delete-product="${item.id}">Remove</button>` : `<button class="danger" data-toggle-user="${item.id}">${item.active ? "Suspend" : "Restore"}</button>`}</div>`; }).join("") : "<div class=\"admin-empty\">Nothing to review.</div>"; } } catch (error) { content.innerHTML = `<div class="admin-empty">${escapeHtml(error.message)}. Sign in with an administrator account to manage the marketplace.</div>`; } if (!$("#admin-modal").open) $("#admin-modal").showModal(); }

$("#search-form").addEventListener("submit", event => { event.preventDefault(); state.query = $("#search-input").value.trim(); state.savedOnly = false; loadProducts(); });
$("#search-input").addEventListener("input", event => { if (!event.target.value.trim() || event.target.value.trim().length > 1) { state.query = event.target.value.trim(); state.savedOnly = false; loadProducts(); } });
$("#sort-select").addEventListener("change", event => { state.sort = event.target.value; loadProducts(); });
$(".category-strip").addEventListener("click", event => { const button = event.target.closest("[data-category]"); if (!button) return; state.category = button.dataset.category; state.savedOnly = false; $$(".category-chip").forEach(chip => chip.classList.toggle("active", chip === button)); loadProducts(); });
$("#saved-toggle").addEventListener("click", () => { state.savedOnly = !state.savedOnly; $("#saved-toggle").classList.toggle("saved-active", state.savedOnly); if (state.savedOnly) loadProducts(); else renderProducts(); });
$("#reset-filters").addEventListener("click", () => { state.query = ""; state.category = "All"; state.savedOnly = false; $("#search-input").value = ""; $$(".category-chip").forEach(chip => chip.classList.toggle("active", chip.dataset.category === "All")); loadProducts(); });
$("#account-button").addEventListener("click", () => state.user ? loadNotifications() : showAuth());
$("#sell-button").addEventListener("click", () => state.user ? $("#listing-modal").showModal() : showAuth());
$(".auth-tabs").addEventListener("click", event => { const tab = event.target.closest("[data-auth-mode]"); if (!tab) return; state.authMode = tab.dataset.authMode; setAuthMode(); $("#auth-error").textContent = ""; });
$("#auth-form").addEventListener("submit", async event => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const form = new FormData(formElement);
  const payload = Object.fromEntries(form.entries());
  const endpoint = state.authMode === "register" ? "/auth/register" : "/auth/login";
  $("#auth-error").textContent = "";
  try {
    const result = await api(endpoint, { method: "POST", body: JSON.stringify(payload) });
    state.token = result.token;
    state.user = result.user;
    localStorage.setItem("campusToken", result.token);
    localStorage.setItem("campusUser", JSON.stringify(result.user));
    formElement.reset();
    $("#auth-modal").close();
    updateAccount();
    if (result.user.role === "ADMIN") loadAdmin();
    else {
      try {
        const savedItems = await api("/wishlist");
        state.saved = new Set(savedItems.map(item => item.id));
        localStorage.setItem("campusSaved", JSON.stringify([...state.saved]));
      } catch { }
      await loadProducts();
      setNotice(`You're in, ${result.user.name.split(" ")[0]}. Welcome to the exchange.`);
    }
  } catch (error) {
    $("#auth-error").textContent = error.message.includes("Failed to fetch") ? "Marketplace API unavailable. Start the Spring Boot server to sign in." : error.message;
  }
});
function updateRentalFields() {
  const renting = $("#listing-type").value === "RENT";
  $("#rental-fields").classList.toggle("hidden", !renting);
  $("#listing-form").elements.minimumRentalDuration.disabled = !renting;
  $("#price-label").textContent = renting ? `Rent price (per ${$("#listing-form").elements.rentalPeriod.value.toLowerCase()})` : "Sale price";
}
$("#listing-type").addEventListener("change", updateRentalFields);
$("#listing-form").elements.rentalPeriod.addEventListener("change", updateRentalFields);
$("#listing-form").addEventListener("reset", () => window.setTimeout(updateRentalFields));
updateRentalFields();
$("#listing-form").addEventListener("submit", async event => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const form = Object.fromEntries(new FormData(formElement).entries());
  form.price = Number(form.price);
  if (form.listingType === "RENT") form.minimumRentalDuration = Number(form.minimumRentalDuration);
  form.imageUrls = String(form.additionalImageUrls || "").split(/[\n,]+/).map(url => url.trim()).filter(Boolean);
  delete form.additionalImageUrls;
  if (form.imageUrls.length > 7) {
    $("#listing-error").textContent = "Add up to 7 extra photo URLs.";
    return;
  }
  if (form.imageUrls.some(url => {
    if (url.startsWith("/images/")) return false;
    try {
      const parsed = new URL(url);
      return !["http:", "https:"].includes(parsed.protocol);
    } catch {
      return true;
    }
  })) {
    $("#listing-error").textContent = "Extra photos must use an http:// or https:// URL (or a local /images/ path).";
    return;
  }
  $("#listing-error").textContent = "";
  try {
    const listing = await api("/products", { method: "POST", body: JSON.stringify(form) });
    formElement.reset();
    $("#listing-modal").close();
    setNotice("Your item is up. Here's to a good handoff.");
    await loadProducts();
    openListing(listing);
  } catch (error) {
    $("#listing-error").textContent = error.message.includes("Failed to fetch") ? "Marketplace API unavailable. Start the Spring Boot server to publish." : error.message;
  }
});
$("#message-form").addEventListener("submit", async event => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const message = new FormData(formElement).get("message");
  try {
    await api(`/products/${state.detail.id}/contact`, { method: "POST", body: JSON.stringify({ message }) });
    formElement.reset();
    $("#message-modal").close();
    setNotice("Your note is on its way to the seller.");
  } catch (error) { $("#message-error").textContent = error.message; }
});
$("#report-form").addEventListener("submit", async event => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const reason = new FormData(formElement).get("reason");
  try {
    await api(`/products/${state.detail.id}/report`, { method: "POST", body: JSON.stringify({ reason }) });
    formElement.reset();
    $("#report-modal").close();
    setNotice("Thanks for looking out for the campus community.");
  } catch (error) { $("#report-error").textContent = error.message; }
});
document.addEventListener("click", async event => { const save = event.target.closest("[data-save]"); if (save) { toggleSave(Number(save.dataset.save)); return; } const detail = event.target.closest("[data-detail]"); if (detail) { const item = state.products.find(product => product.id === Number(detail.dataset.detail)); if (item) openListing(item); return; } const contact = event.target.closest("[data-contact]"); if (contact) { if (!state.user) { $("#detail-modal").close(); showAuth(); return; } openMessage(Number(contact.dataset.contact)); return; } const report = event.target.closest("[data-report]"); if (report) { openReport(Number(report.dataset.report)); return; } const sold = event.target.closest("[data-sold]"); if (sold) { try { await api(`/products/${sold.dataset.sold}/sold`, { method: "PATCH" }); $("#detail-modal").close(); setNotice("Marked as passed along. Thanks for keeping it circular."); await loadProducts(); } catch (error) { setNotice(error.message, true); } return; } if (event.target.closest("[data-close]")) { event.target.closest("dialog").close(); return; } const tab = event.target.closest("[data-admin-tab]"); if (tab) { loadAdmin(tab.dataset.adminTab); return; } const resolve = event.target.closest("[data-resolve-report]"); if (resolve) { try { await api(`/admin/reports/${resolve.dataset.resolveReport}/resolve`, { method: "PATCH" }); loadAdmin(); } catch (error) { setNotice(error.message, true); } return; } const removeProduct = event.target.closest("[data-admin-delete-product]"); if (removeProduct) { try { await api(`/admin/products/${removeProduct.dataset.adminDeleteProduct}`, { method: "DELETE" }); loadAdmin(); loadProducts(); } catch (error) { setNotice(error.message, true); } return; } const toggleUser = event.target.closest("[data-toggle-user]"); if (toggleUser) { try { await api(`/admin/users/${toggleUser.dataset.toggleUser}/toggle`, { method: "PATCH" }); loadAdmin(); } catch (error) { setNotice(error.message, true); } return; } const deleteCategory = event.target.closest("[data-delete-category]"); if (deleteCategory) { try { await api(`/admin/categories/${deleteCategory.dataset.deleteCategory}`, { method: "DELETE" }); loadAdmin(); } catch (error) { setNotice(error.message, true); } } });
document.addEventListener("click", event => { const rate = event.target.closest("[data-rate]"); if (!rate) return; $("#rating-item").textContent = `Rate your experience with “${state.detail?.title || "this item"}”.`; $("#rating-error").textContent = ""; $("#detail-modal").close(); $("#rating-modal").showModal(); });
$("#rating-form").addEventListener("submit", async event => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const rating = Number(new FormData(formElement).get("rating"));
  try {
    await api(`/products/${state.detail.id}/rating`, { method: "POST", body: JSON.stringify({ rating }) });
    formElement.reset();
    $("#rating-modal").close();
    setNotice("Thanks for rating the handoff.");
    await loadProducts();
  } catch (error) { $("#rating-error").textContent = error.message; }
});
$("#admin-content").addEventListener("submit", async event => { if (event.target.id !== "category-form") return; event.preventDefault(); const name = new FormData(event.target).get("name"); try { await api("/admin/categories", { method: "POST", body: JSON.stringify({ name }) }); loadAdmin("categories"); } catch (error) { setNotice(error.message, true); } });
document.addEventListener("keydown", event => { if (event.key === "/" && !["INPUT", "TEXTAREA", "SELECT"].includes(document.activeElement.tagName)) { event.preventDefault(); $("#search-input").focus(); } if (event.key === "Escape") $$('dialog[open]').forEach(dialog => dialog.close()); });
document.addEventListener("click", event => { const dialog = event.target.closest("dialog"); if (dialog && event.target === dialog) dialog.close(); });
updateAccount();
loadProducts();