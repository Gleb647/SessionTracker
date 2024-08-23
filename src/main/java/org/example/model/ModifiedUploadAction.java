package org.example.model;

import org.example.api.ApiException;
import org.openstreetmap.josm.actions.UploadAction;
import org.openstreetmap.josm.actions.upload.UploadHook;
import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.data.oauth.IOAuthToken;
import org.openstreetmap.josm.data.oauth.OAuth20Token;
import org.openstreetmap.josm.data.oauth.OAuthAccessTokenHolder;
import org.openstreetmap.josm.data.oauth.OAuthVersion;
import org.openstreetmap.josm.io.OsmApi;
import org.openstreetmap.josm.tools.Logging;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static org.example.api.ApiQuery.disconnect;

public class ModifiedUploadAction{

    private TimeTrackingManager timeTrackingManager;

    public ModifiedUploadAction(TimeTrackingManager timeTrackingManager) {
        this.timeTrackingManager = timeTrackingManager;
    }

    public void AddHook(){
        UploadAction.registerUploadHook(new SendSessionTimeHook());
    }

    private class SendSessionTimeHook implements UploadHook{

        @Override
        public boolean checkUpload(APIDataSet apiDataSet) {
            Logging.info("Uploading session time");
            try {
                URLConnection connection = sendPostRequest(
                        Json.createObjectBuilder()
                                .add("session_time", timeTrackingManager.uploadActionHappened()));
            } catch (ApiException.ConnectionFailure e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

        public static URLConnection sendPostRequest(final JsonObjectBuilder requestContent) throws ApiException.ConnectionFailure, MalformedURLException {
            String req = "http://127.0.0.1:8000/hello";
            URL url = new URL( req );
            final URLConnection connection;
            try {
                connection = url.openConnection();
            } catch (final IOException e) {
                throw new ApiException.ConnectionFailure(url, e);
            }
            try {
                connection.setRequestProperty("Content-Type", "application/json;charset=" + StandardCharsets.UTF_8.name());
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(10_000);
                if (connection instanceof HttpURLConnection) {
                    ((HttpURLConnection) connection).setChunkedStreamingMode(0);
                    ((HttpURLConnection) connection).setRequestMethod("POST");
                }
                OAuthAccessTokenHolder holder = OAuthAccessTokenHolder.getInstance();
                IOAuthToken token = holder.getAccessToken(OsmApi.getOsmApi().getServerUrl(), OAuthVersion.OAuth20);
                String key = "None";
                if (token instanceof OAuth20Token) {
                    key = ((OAuth20Token) token).getBearerToken();
                    Logging.info("Token: " + key);
                }
                connection.setRequestProperty("Authorization", "Bearer " + key);
                try (Writer writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                    final String request = requestContent.build().toString();
                    Logging.debug("Session time API request:\n{0}", request);
                    writer.write(request);
                }
            } catch (IOException e) {
                disconnect(connection);
                throw new ApiException.ConnectionFailure(url, e);
            }
            return connection;
        }
    }

