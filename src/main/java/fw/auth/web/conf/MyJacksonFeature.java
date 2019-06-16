package fw.auth.web.conf;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public final class MyJacksonFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        return true;
    }
}
