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

        // Endpoints
        server.createContext("/", new StaticUiHandler());
        server.createContext("/api/accounts", new AccountsHandler());
        server.createContext("/api/transfer", new TransferHandler());
        server.createContext("/api/logs", new LogsHandler());
        server.setExecutor(null);

        System.out.println("==================================================");
        System.out.println(" Ledger Enterprise Platform running on port " + port);
        System.out.println(" UI Dashboard: http://localhost:" + port + "/");
        System.out.println(" API Accounts: GET  http://localhost:" + port + "/api/accounts");
        System.out.println(" API Transfer: POST http://localhost:" + port + "/api/transfer");
        System.out.println(" API Audit Log: GET  http://localhost:" + port + "/api/logs");
        System.out.println("==================================================");

        server.start();
    }

    // Handles GET /api/logs (Enterprise Audit Trail)
    static class LogsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed. Use GET.\"}");
                    return;
                }

                StringBuilder json = new StringBuilder("[");
                String sql = "SELECT transaction_id, source_account_id, target_account_id, amount, status, created_at FROM transaction_logs ORDER BY transaction_id DESC LIMIT 50";
                
                try (Connection conn = DatabaseConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {

                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append(String.format("{\"id\": %d, \"src\": %d, \"target\": %d, \"amount\": %.2f, \"status\": \"%s\", \"time\": \"%s\"}",
                                rs.getInt("transaction_id"),
                                rs.getInt("source_account_id"),
                                rs.getInt("target_account_id"),
                                rs.getBigDecimal("amount"),
                                rs.getString("status"),
                                rs.getTimestamp("created_at").toString()));
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

    // Serves Interactive UI with Audit Section
    static class StaticUiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Enterprise Ledger Sync</title>
                        <style>
                            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 15px; background: #0f172a; color: #f8fafc; }
                            .card { background: #1e293b; padding: 15px; border-radius: 8px; margin-bottom: 15px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.3); }
                            h2, h3 { margin-top: 0; color: #38bdf8; }
                            table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 14px; }
                            th, td { text-align: left; padding: 8px; border-bottom: 1px solid #334155; }
                            th { color: #94a3b8; }
                            .badge-success { color: #4ade80; font-weight: bold; }
                            .badge-failed { color: #f87171; font-weight: bold; }
                            input, button { width: 100%; padding: 10px; margin: 5px 0 10px 0; border-radius: 6px; border: 1px solid #475569; background: #0f172a; color: white; box-sizing: border-box; }
                            button { background: #0284c7; font-weight: bold; border: none; cursor: pointer; }
                            button:active { background: #0369a1; }
                            #status { font-weight: bold; margin-top: 5px; }
                        </style>
                    </head>
                    <body>
                        <h2>Enterprise Ledger Platform</h2>
                        
                        <div class="card">
                            <h3>Live Account Balances</h3>
                            <table>
                                <thead><tr><th>ID</th><th>Holder</th><th>Balance</th></tr></thead>
                                <tbody id="accountsBody"></tbody>
                            </table>
                        </div>

                        <div class="card">
                            <h3>Execute Financial Transfer</h3>
                            <label>Sender Account ID:</label>
                            <input type="number" id="srcId" value="1">
                            
                            <label>Receiver Account ID:</label>
                            <input type="number" id="targetId" value="2">
                            
                            <label>Amount (Ksh):</label>
                            <input type="number" id="amount" step="0.01" value="100.00">
                            
                            <button onclick="executeTransfer()">Submit Transfer</button>
                            <div id="status"></div>
                        </div>

                        <div class="card">
                            <h3>System Audit Log</h3>
                            <button onclick="fetchLogs()" style="background: #334155;">Refresh Audit Trail</button>
                            <table>
                                <thead><tr><th>Tx ID</th><th>From</th><th>To</th><th>Amount</th><th>Status</th></tr></thead>
                                <tbody id="logsBody"></tbody>
                            </table>
                        </div>

                        <script>
                            async function fetchAccounts() {
                                const res = await fetch('/api/accounts');
                                const data = await res.json();
                                const tbody = document.getElementById('accountsBody');
                                tbody.innerHTML = '';
                                data.forEach(acc => {
                                    tbody.innerHTML += `<tr><td>${acc.id}</td><td>${acc.holder}</td><td>Ksh ${acc.balance.toFixed(2)}</td></tr>`;
                                });
                            }

                            async function fetchLogs() {
                                const res = await fetch('/api/logs');
                                const data = await res.json();
                                const tbody = document.getElementById('logsBody');
                                tbody.innerHTML = '';
                                data.forEach(log => {
                                    const badgeClass = log.status === 'SUCCESS' ? 'badge-success' : 'badge-failed';
                                    tbody.innerHTML += `<tr><td>#${log.id}</td><td>Acc ${log.src}</td><td>Acc ${log.target}</td><td>Ksh ${log.amount.toFixed(2)}</td><td class="${badgeClass}">${log.status}</td></tr>`;
                                });
                            }

                            async function executeTransfer() {
                                const srcId = document.getElementById('srcId').value;
                                const targetId = document.getElementById('targetId').value;
                                const amount = document.getElementById('amount').value;
                                const statusDiv = document.getElementById('status');

                                statusDiv.innerText = "Processing...";
                                statusDiv.style.color = "#facc15";

                                const res = await fetch('/api/transfer', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify({ srcId: parseInt(srcId), targetId: parseInt(targetId), amount: parseFloat(amount) })
                                });

                                const data = await res.json();
                                if (res.ok) {
                                    statusDiv.innerText = data.message;
                                    statusDiv.style.color = "#4ade80";
                                } else {
                                    statusDiv.innerText = data.message || "Transfer Failed";
                                    statusDiv.style.color = "#f87171";
                                }
                                fetchAccounts();
                                fetchLogs();
                            }

                            fetchAccounts();
                            fetchLogs();
                        </script>
                    </body>
                    </html>
                """;

                byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } catch (Exception e) {
                try {
                    exchange.sendResponseHeaders(500, -1);
                } catch (Exception ignored) {}
            }
        }
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

