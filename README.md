# Amma Biryani – Vellore | Online Ordering Website

Full-stack food ordering site: **Java Spring Boot** REST API + plain **HTML/CSS/JS** frontend.

## Project Structure
```
amma-biryani/
├── backend/          Spring Boot API (Java 17, Maven)
│   └── src/main/java/com/ammabiryani/
│       ├── model/        MenuItem, Order, OrderItem, OrderStatus
│       ├── repository/   Spring Data JPA repositories
│       ├── service/      Business logic
│       ├── controller/   REST endpoints
│       ├── dto/          Request objects
│       └── config/       CORS, error handling, sample menu data
└── frontend/          index.html, style.css, script.js
```

## Run it locally

**Requirements:** Java 17+, Maven (JDK already includes what you need — just install Maven: `sudo apt install maven`)

```bash
cd backend
mvn spring-boot:run
```
This starts the API at `http://localhost:8080`, using an in-memory H2 database pre-loaded with Amma Biryani's menu (biryanis, starters, add-ons, desserts). No setup needed — data resets each restart, which is fine while you're developing.

Then just open `frontend/index.html` in your browser (or serve it with `python3 -m http.server` from the `frontend` folder). It calls the API at `http://localhost:8080/api`.

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/menu` | List available menu items |
| GET | `/api/menu/{id}` | Get one item |
| POST | `/api/menu` | Add item (owner/admin) |
| PUT | `/api/menu/{id}` | Edit item |
| DELETE | `/api/menu/{id}` | Remove item |
| POST | `/api/orders` | Place an order |
| GET | `/api/orders` | List all orders (owner/admin) |
| GET | `/api/orders/{id}` | Get one order |
| PUT | `/api/orders/{id}/status?status=CONFIRMED` | Update order status |

## Deploying to AWS (your DevOps practice)

1. **RDS**: Create a MySQL RDS instance. Uncomment the MySQL block in `application.properties`, comment out the H2 block.
2. **EC2**: Launch an instance (t3.micro is fine to start), install Java 17, copy the built jar (`mvn clean package` → `target/amma-biryani-backend-1.0.0.jar`), run it with environment variables for `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`. Consider running it as a `systemd` service so it restarts on reboot.
3. **S3 + CloudFront**: Upload the `frontend/` folder to an S3 bucket, enable static website hosting, put CloudFront in front for HTTPS. Update `API_BASE` in `script.js` to your backend's public URL.
4. **Security group**: Only open port 8080 (or better, put it behind an ALB/Nginx on 443) and restrict RDS access to the EC2 instance's security group only.
5. **Route 53 + ACM**: Point a domain at CloudFront (frontend) and the ALB (backend), issue a free TLS cert via ACM.
6. **CI/CD**: Add a GitHub Actions workflow that builds the jar and deploys on push to `main` — strong resume material.
7. **Cost control**: Set up an AWS Budget alert immediately (e.g. alert at ₹1,500/month) so you don't get a surprise bill while learning.

## Notes / Next steps before going live with a real hotel
- The admin endpoints (`POST/PUT/DELETE /api/menu`, `GET /api/orders`) are currently open — add Spring Security with a login for the owner/staff before this handles real orders.
- Add a payment gateway (Razorpay is popular in India) if you want online payment rather than cash/UPI on delivery.
- Add SNS/SES so the owner gets an SMS/email the moment an order comes in.
