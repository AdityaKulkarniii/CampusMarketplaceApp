const http = require("node:http");
const fs = require("node:fs");
const path = require("node:path");

const root = path.join(__dirname, "static");
let port = Number(process.env.PREVIEW_PORT || 5173);
const contentTypes = { ".html": "text/html; charset=utf-8", ".css": "text/css; charset=utf-8", ".js": "text/javascript; charset=utf-8", ".svg": "image/svg+xml", ".png": "image/png", ".jpg": "image/jpeg", ".webp": "image/webp" };

const server = http.createServer((request, response) => {
  if (request.url.startsWith("/api/")) {
    const apiPort = Number(process.env.API_PORT || 8081);
    const upstream = http.request({ hostname: "127.0.0.1", port: apiPort, path: request.url, method: request.method, headers: { ...request.headers, host: `127.0.0.1:${apiPort}` } }, result => {
      response.writeHead(result.statusCode || 502, result.headers);
      result.pipe(response);
    });
    upstream.on("error", () => {
      response.writeHead(503, { "Content-Type": "application/json; charset=utf-8" });
      response.end(JSON.stringify({ message: "Spring Boot API is not running. Run the Maven wrapper from the backend folder." }));
    });
    request.pipe(upstream);
    return;
  }

  let pathname;
  try { pathname = decodeURIComponent(new URL(request.url, "http://localhost").pathname); }
  catch { response.writeHead(400).end(); return; }
  const file = path.resolve(root, `.${pathname === "/" ? "/index.html" : pathname}`);
  if (!file.startsWith(root + path.sep) && file !== path.join(root, "index.html")) { response.writeHead(403).end(); return; }
  fs.readFile(file, (error, content) => {
    if (error) { response.writeHead(404).end("Not found"); return; }
    response.writeHead(200, { "Content-Type": contentTypes[path.extname(file)] || "application/octet-stream", "X-Content-Type-Options": "nosniff" });
    response.end(content);
  });
});

server.on("error", error => {
  if (error.code === "EADDRINUSE" && port < 65535) {
    console.log(`Port ${port} is already in use; trying ${port + 1}.`);
    port += 1;
    server.listen(port, "127.0.0.1");
    return;
  }
  console.error(error);
  process.exitCode = 1;
});

server.listen(port, "127.0.0.1", () => console.log(`Campus Marketplace preview: http://localhost:${port}`));
