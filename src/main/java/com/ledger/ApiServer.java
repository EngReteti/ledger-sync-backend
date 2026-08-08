package com.ledger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ApiServer {
    private static final TransactionManager manager = new TransactionManager();

    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Routes
        server.createContext("/api/transfer", new TransferHandler());
        server.createContext("/api/accounts", new AccountsHandler());
        server.setExecutor(null);

        System.out.println("==================================================");
        System.out.println(" Ledger REST API server running on port " + port);
        System.out.println(" GET  http://localhost:" + port + "/api/accounts");
        System.out.println(" POST http://localhost:" + port + "/api/transfer");
        System.out.println("==================================================");

        server.start();
    }

    // Handles GET /api/accounts
    static class AccountsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed. Use GET.\"}");
                    return;
                }

                StringBuilder json = new StringBuilder("[");
                try (Connection conn = DatabaseConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM accounts");
                     ResultSet rs = pstmt.executeQuery()) {

                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append(String.format("{\"id\": %d, \"holder\": \"%s\", \"balance\": %.2f}",
                                rs.getInt("account_id"),
                                rs.getString("account_holder"),
                                rs.getBigDecimal("balance")));
                        first = false;
                    }
                    conn.commit();
                }
                json.append("]");

                sendResponse(exchange, 200, json.toString());
            } catch (Exception e) {
                try {
                    sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
                } catch (Exception ignored) {}
            }
        }
    }

    // Handles POST /api/transfer
    static class TransferHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed. Use POST.\"}");
                    return;
                }

                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                int srcId = Integer.parseInt(extractJsonValue(body, "srcId"));
                int targetId = Integer.parseInt(extractJsonValue(body, "targetId"));
                BigDecimal amount = new BigDecimal(extractJsonValue(body, "amount"));

                boolean success = manager.transfer(srcId, targetId, amount);

                if (success) {
                    sendResponse(exchange, 200, "{\"status\": \"SUCCESS\", \"message\": \"Transfer executed successfully.\"}");
                } else {
                    sendResponse(exchange, 400, "{\"status\": \"FAILED\", \"message\": \"Transaction failed (insufficient funds or invalid account).\"}");
                }

            } catch (Exception e) {
                try {
                    sendResponse(exchange, 500, "{\"error\": \"Bad Request or Internal Error: " + e.getMessage() + "\"}");
                } catch (Exception ignored) {}
            }
        }

        private String extractJsonValue(String json, String key) {
            String target = "\"" + key + "\"";
            int index = json.indexOf(target);
            if (index == -1) return "0";
            int start = json.indexOf(":", index) + 1;
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).replace("\"", "").trim();
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws Exception {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}

