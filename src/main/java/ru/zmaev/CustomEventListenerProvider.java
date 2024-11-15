package ru.zmaev;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.*;
import org.keycloak.services.resource.RealmResourceProvider;
import ru.zmaev.dto.UserKeycloakDataDto;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class CustomEventListenerProvider implements EventListenerProvider, RealmResourceProvider {

    private static final Logger log = Logger.getLogger("CustomEventListenerProvider");

    private final KeycloakSession session;
    private final RealmModel realmModel;

    public CustomEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.realmModel = session.getContext().getRealm();
    }

    @Override
    public void onEvent(Event event) {
        UserModel user = session.users().getUserById(realmModel, event.getUserId());
        log.info(() -> "## NEW " + event.getType() + " EVENT");
        log.info("-----------------------------------------------------------");

        if (event.getType() == EventType.REGISTER) {
            UserKeycloakDataDto userData = new UserKeycloakDataDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
            user.setEmailVerified(false);
            sendRequestToEndpoint(userData);
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        if (ResourceType.USER.equals(adminEvent.getResourceType())
                && OperationType.CREATE.equals(adminEvent.getOperationType())) {
            log.info("A new user has been created");
            log.info("-----------------------------------------------------------");
        }
    }

    private void sendRequestToEndpoint(UserKeycloakDataDto userData) {
        log.info(() -> "Sending POST request to server");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(userData);

            HttpURLConnection connection = getHttpURLConnection(jsonInputString);

            int responseCode = connection.getResponseCode();
            log.info(() -> "POST Response Code: " + responseCode);
        } catch (Exception e) {
            log.warning(() -> "Exception while POST request sending to server: " + e.getMessage());
        }
    }

    private static HttpURLConnection getHttpURLConnection(String jsonInputString) throws IOException {
        URL url = new URL("http://host.docker.internal:8080/api/v1/auth/register");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public Object getResource() {
        return null;
    }
}
