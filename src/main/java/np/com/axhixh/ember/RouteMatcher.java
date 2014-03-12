package np.com.axhixh.ember;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author ashish
 */
public class RouteMatcher {

    private final EnumMap<HttpMethod, List<Route>> routes = new EnumMap<>(HttpMethod.class);

    Route findTarget(HttpMethod method, String path) {
        List<Route> routeList = routes.get(method);
        if (routeList == null) {
            return null;
        }

        for (Route route : routeList) {
            // first match; not best match
            if (match(route, path)) {
                return route;
            }
        }
        return null;
    }

    void addRoute(HttpMethod method, Route route) {
        List<Route> routeList = routes.get(method);
        if (routeList == null) {
            routeList = new ArrayList<>();
        }
        //if (!present)
        routeList.add(route);
        routes.put(method, routeList);
    }

    private boolean match(Route route, String path) {
        if (route.getPath().equals(path)) {
            return true;
        }
        
        String[] routeParts = route.getPath().split("/");
        String[] pathParts = path.split("/");
        
        if (routeParts.length != pathParts.length) {
            return false;
        }
        
        for (int i = 0; i < routeParts.length; i++) {
            if (!routeParts[i].startsWith(":") && !routeParts[i].equals(pathParts[i])) {
                return false;
            }
        }
        return true;
    }
}
