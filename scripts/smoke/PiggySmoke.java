import com.amazonaws.services.lambda.runtime.*;
import com.fasterxml.jackson.databind.*;
import com.mongodb.client.MongoClients;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.*;
import com.sun.net.httpserver.HttpServer;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.bson.Document;

/** Runs the shipped handlers with real JWT verification and an isolated in-memory MongoDB. */
public class PiggySmoke {
    static final ObjectMapper JSON = new ObjectMapper();
    static RSAKey key;
    static String issuer;
    static int checks;
    static final Context CONTEXT = new Context() {
        public String getAwsRequestId() { return "smoke"; }
        public String getLogGroupName() { return "smoke"; }
        public String getLogStreamName() { return "smoke"; }
        public String getFunctionName() { return "smoke"; }
        public String getFunctionVersion() { return "$LATEST"; }
        public String getInvokedFunctionArn() { return "smoke"; }
        public CognitoIdentity getIdentity() { return null; }
        public ClientContext getClientContext() { return null; }
        public int getRemainingTimeInMillis() { return 30000; }
        public int getMemoryLimitInMB() { return 1536; }
        public LambdaLogger getLogger() {
            return new LambdaLogger() {
                public void log(String message) { System.out.print(message); }
                public void log(byte[] message) { System.out.writeBytes(message); }
            };
        }
    };

    public static void main(String[] args) {
        int exit = 1;
        MongoServer mongo = new MongoServer(new MemoryBackend());
        HttpServer http = null;
        try {
            var address = mongo.bind();
            String uri = "mongodb://" + address.getHostString() + ":" + address.getPort() + "/smoke";
            System.setProperty("MONGODB_URI", uri);
            key = new RSAKeyGenerator(2048).keyID("smoke").generate();
            http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            issuer = "http://127.0.0.1:" + http.getAddress().getPort();
            http.createContext("/.well-known/jwks.json", exchange -> {
                byte[] body = new JWKSet(key.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            http.createContext("/rates/", exchange -> {
                byte[] body = "{\"rates\":{\"USD\":1,\"EUR\":0.9,\"RUB\":80}}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            http.start();
            System.setProperty("COGNITO_ISSUER", issuer);
            System.setProperty("COGNITO_TOKEN_URI", issuer + "/token");
            System.setProperty("SERVICES_BASE_URL", issuer);
            System.setProperty("rates.url", issuer + "/rates/{base}");
            String module = args[0];
            String root = module.equals("account") ? "accounts"
                    : module.equals("statistics") ? "statistics" : "notifications";
            String className = module.equals("account") ? "com.piggymetrics.account.StreamLambdaHandler"
                    : module.equals("statistics") ? "com.piggymetrics.statistics.StreamLambdaHandler"
                    : module.equals("jobs") ? "com.piggymetrics.notification.ScheduledNotificationHandler"
                    : "com.piggymetrics.notification.StreamLambdaHandler";
            if (module.equals("account")) {
                try (var client = MongoClients.create(uri)) {
                    client.getDatabase("smoke").getCollection("accounts")
                            .insertOne(Document.parse("{\"_id\":\"demo\",\"incomes\":[],\"expenses\":[],"
                                    + "\"saving\":{\"amount\":0,\"currency\":\"USD\",\"interest\":0}}"));
                }
            }
            Object handler = Class.forName(className).getDeclaredConstructor().newInstance();
            if (module.equals("jobs")) {
                @SuppressWarnings("unchecked")
                RequestHandler<Map<String, String>, String> jobs = (RequestHandler<Map<String, String>, String>) handler;
                for (String job : List.of("REMIND", "BACKUP")) {
                    check(jobs.handleRequest(Map.of("job", job), CONTEXT).equals(job + " notifications sent"), job);
                }
                try {
                    jobs.handleRequest(Map.of("job", "NOPE"), CONTEXT);
                    throw new AssertionError("Unknown job was accepted");
                } catch (IllegalArgumentException expected) {
                    checks++;
                }
            } else {
                RequestStreamHandler stream = (RequestStreamHandler) handler;
                String user = token("local-browser", "access", "aws.cognito.signin.user.admin");
                String machine = token("local-service", "access", "piggymetrics/server");
                String path = root.equals("notifications") ? "/recipients/current" : "/current";
                call(stream, root, "GET", path, null, null, 401);
                call(stream, root, "GET", path, machine, null, 403);
                call(stream, root, "GET", path, token("other-client", "access", "aws.cognito.signin.user.admin"), null, 401);
                call(stream, root, "GET", path, token("local-browser", "id", "aws.cognito.signin.user.admin"), null, 401);
                if (module.equals("account")) {
                    check(call(stream, root, "GET", "/demo", null, null, 200).get("name").asText().equals("demo"), "demo");
                    JsonNode created = call(stream, root, "POST", path, user, null, 200);
                    check(created.get("name").asText().equals("alice"), "JWT username");
                    call(stream, root, "POST", path, user, null, 200);
                    ((com.fasterxml.jackson.databind.node.ObjectNode) created).put("name", "mallory").put("note", "saved");
                    call(stream, root, "PUT", path, user, created, 200);
                    JsonNode saved = call(stream, root, "GET", path, user, null, 200);
                    check(saved.get("name").asText().equals("alice") && saved.get("note").asText().equals("saved"), "saved identity");
                    call(stream, root, "GET", "/bob", user, null, 403);
                    call(stream, root, "GET", "/alice", machine, null, 200);
                } else if (module.equals("statistics")) {
                    call(stream, root, "GET", "/demo", null, null, 200);
                    JsonNode account = JSON.readTree("{\"incomes\":[],\"expenses\":[],"
                            + "\"saving\":{\"amount\":0,\"currency\":\"USD\",\"interest\":0,\"deposit\":false,\"capitalization\":false}}");
                    call(stream, root, "PUT", "/alice", user, account, 403);
                    call(stream, root, "PUT", "/alice", machine, account, 200);
                    call(stream, root, "PUT", "/alice", machine, account, 200);
                    check(call(stream, root, "GET", path, user, null, 200).size() == 1, "datapoint upsert");
                    call(stream, root, "GET", "/bob", user, null, 403);
                    check(call(stream, root, "GET", "/rates", null, null, 200).get("USD").asInt() == 1, "rates");
                } else {
                    JsonNode settings = JSON.readTree("{\"accountName\":\"mallory\",\"email\":\"smoke@example.invalid\","
                            + "\"scheduledNotifications\":{\"REMIND\":{\"active\":false,\"frequency\":\"MONTHLY\"}}}");
                    call(stream, root, "PUT", path, user, settings, 200);
                    JsonNode saved = call(stream, root, "GET", path, user, null, 200);
                    check(saved.get("accountName").asText().equals("alice"), "settings identity");
                    ((com.fasterxml.jackson.databind.node.ObjectNode) settings).put("email", "invalid");
                    call(stream, root, "PUT", path, user, settings, 400);
                }
            }
            System.out.println("PASS " + module + ": " + checks + " checks");
            exit = 0;
        } catch (Throwable failure) {
            failure.printStackTrace();
        } finally {
            if (http != null) http.stop(0);
            mongo.shutdownNow();
        }
        // Spring and Mongo clients intentionally remain cached by the Lambda handler.
        System.exit(exit);
    }

    static String token(String client, String use, String scope) throws Exception {
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(),
                new JWTClaimsSet.Builder().issuer(issuer).subject("uuid-smoke")
                        .claim("username", "alice").claim("client_id", client).claim("token_use", use)
                        .claim("scope", scope).issueTime(new Date())
                        .expirationTime(Date.from(Instant.now().plusSeconds(300))).build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    static JsonNode call(RequestStreamHandler handler, String root, String method, String path,
                         String token, JsonNode body, int expected) throws Exception {
        Map<String, String> headers = new HashMap<>(Map.of("Accept", "application/json", "Content-Type", "application/json"));
        if (token != null) headers.put("Authorization", "Bearer " + token);
        Map<String, Object> event = new HashMap<>();
        event.put("resource", "/" + root + "/{proxy+}");
        event.put("path", "/" + root + path);
        event.put("httpMethod", method);
        event.put("headers", headers);
        Map<String, List<String>> multiValueHeaders = new HashMap<>();
        headers.forEach((name, value) -> multiValueHeaders.put(name, List.of(value)));
        event.put("multiValueHeaders", multiValueHeaders);
        event.put("requestContext", Map.of("resourcePath", "/" + root + "/{proxy+}",
                "httpMethod", method, "stage", "$default", "identity", Map.of("sourceIp", "127.0.0.1")));
        event.put("body", body == null ? null : JSON.writeValueAsString(body));
        event.put("isBase64Encoded", false);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        handler.handleRequest(new ByteArrayInputStream(JSON.writeValueAsBytes(event)), output, CONTEXT);
        JsonNode response = JSON.readTree(output.toByteArray());
        check(response.get("statusCode").asInt() == expected, method + " /" + root + path + " expected " + expected + ", got " + response);
        String result = response.path("body").asText();
        return result.isBlank() ? JSON.nullNode() : JSON.readTree(result);
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
