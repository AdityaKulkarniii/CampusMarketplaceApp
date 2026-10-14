# Campus Marketplace

A student-to-student marketplace based on the supplied project abstract. The browser app is served by Spring Boot and uses REST endpoints, JWT authentication, and MySQL persistence.

## Project layout

- `frontend/` contains the browser app, product images, and static preview server.
- `backend/` contains the Spring Boot API, Maven build, and Maven wrapper.
- `.vscode/` contains tasks that launch the frontend and backend from their folders.

## Deploy the frontend to Vercel

The repository can be deployed with either of these Vercel settings:

- **Root Directory: `.` (repository root):** Vercel reads the root `vercel.json` and serves `frontend/static`.
- **Root Directory: `frontend`:** Vercel reads `frontend/vercel.json` and serves `static`.

Both configs rewrite frontend page requests to `index.html` while excluding `/api/` requests. Choose the matching root/output configuration in the Vercel project's **Settings → Build and Deployment**. Keep the framework preset as **Other** and leave the build and install commands blank for this plain HTML/CSS/JavaScript app.

The Spring Boot backend is not deployed by this static Vercel configuration. Deploy `backend/` to a Java-capable host separately and configure a Vercel rewrite for `/api/:path*` to that backend's public URL, or update the frontend API base URL and configure the backend to allow the Vercel origin. Until the API is deployed and connected, the frontend can display sample listings but sign-in and data-changing operations will not work.

After pushing a change to `main`, Vercel redeploys automatically when the GitHub repository is connected to the Vercel project.

## Requirements

- Java 17 or later
- Maven 3.8 or later (or use the included `mvnw.cmd`)
- Node.js 18 or later, for the optional frontend preview server
- Optional: MySQL 8 or later, if you want to run against the MySQL profile instead of the default local H2 database

The project defaults to the local H2 profile so it can boot without any external database setup. To use MySQL instead, set `SPRING_PROFILES_ACTIVE=mysql` and configure `DB_URL`, `DB_USER`, and `DB_PASSWORD`. Hibernate creates and updates the development schema on application startup. Listing prices are shown in Indian rupees (INR). Sellers can list items for sale or rent; rental listings include a per-day, per-week, or per-month price and a minimum rental duration. To enable a 360° product spin, sellers add up to eight photo URLs showing different angles; customers can drag the listing photo, use the arrow buttons, or choose a thumbnail to inspect the item.

## Run in VS Code

Open `D:\CampusMarketplaceApp` as the VS Code workspace and start the tasks from the VS Code Tasks menu. The bundled tasks detect Java and Maven automatically and launch the backend and frontend without hard-coded machine paths.

To run the backend manually from the repository root:

```powershell
Set-Location backend
.\mvnw.cmd spring-boot:run
```

From the repository root, start the frontend preview:

```powershell
npm.cmd run dev
```

The equivalent command from inside the frontend folder is `npm.cmd run dev`. Do not run `npm install` to start the app; this frontend has no external npm dependencies.

Visit `http://localhost:8081` for the Spring Boot app and `http://localhost:5173` for the static preview. The preview proxy uses port 8081 by default.

To build and run the checks:

```powershell
Set-Location backend
.\mvnw.cmd test
```

## Local accounts

The local development profile seeds an example student and administrator for local testing only:

- Student: `maya@campus.edu` / `Student123!`
- Administrator: `admin@campus.edu` / `ChangeMe123!`

New student accounts can also be registered from the sign-in dialog. The local profile uses demo credentials; do not reuse them on a shared server. Outside the local profile, set `ADMIN_PASSWORD` and a private, sufficiently long `JWT_SECRET` before startup. Sample student and listing seed data is disabled outside the local profile. Store database credentials in environment variables, not in source control.

## API overview

- `POST /api/auth/register`, `POST /api/auth/login`
- Authenticated, campus-scoped `GET /api/products?query=&category=&sort=` and `GET /api/products/{id}`
- `POST /api/products`, `PATCH /api/products/{id}/sold`
- `POST /api/products/{id}/contact`, `/report`, and `/rating`
- `GET /api/wishlist`, `POST /api/wishlist/{productId}`, `DELETE /api/wishlist/{productId}`
- `GET /api/notifications`
- Administrator-only `GET /api/admin/{reports,products,users,categories}` plus report resolution, product removal, user suspension, and category management endpoints.

Authenticated routes expect `Authorization: Bearer <token>`. Real listings require sign-in and students only see listings for their registered campus; administrators can review across campuses. Student signup accepts the email domain of the student's institution because a single campus email domain was not specified in the abstract.

## Included workflows

Browse and filter campus listings, view pickup details and seller ratings, save items, register or sign in, publish listings, message sellers through notifications, report listings, mark owned listings sold, rate a completed handoff, and manage users, products, reports, and categories as an administrator. If the API is offline, the frontend remains browseable with sample listings; changes and sign-in require the Spring Boot API and MySQL.