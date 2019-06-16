package fw.auth.web.v1;

import org.apache.logging.log4j.Logger;

public class AbstractWebService {

    static void logError(Logger logger, long tx, Exception ex) {
        logger.error(String.format("[%s] failed, ex:{%s}", tx, ex.getMessage()), ex);
    }

}
