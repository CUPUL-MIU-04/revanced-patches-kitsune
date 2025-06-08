package app.kitsune.extension.shared.returnyoutubeusername.requests;

import static app.kitsune.extension.shared.requests.Route.Method.GET;

import java.io.IOException;
import java.net.HttpURLConnection;

import app.kitsune.extension.shared.requests.Requester;
import app.kitsune.extension.shared.requests.Route;

public class ChannelRoutes {
    public static final String YOUTUBEI_V3_GAPIS_URL = "https://www.googleapis.com/youtube/v3/";

    public static final Route GET_CHANNEL_DETAILS = new Route(GET, "channels?part=brandingSettings&maxResults=1&prettyPrint=false&forHandle={handle}&key={api_key}");

    public ChannelRoutes() {
    }

    public static HttpURLConnection getChannelConnectionFromRoute(Route route, String... params) throws IOException {
        return Requester.getConnectionFromRoute(YOUTUBEI_V3_GAPIS_URL, route, params);
    }
}