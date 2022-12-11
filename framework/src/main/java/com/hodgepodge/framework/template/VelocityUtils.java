package main.java.com.hodgepodge.framework.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
public class VelocityUtils {

    public void evaluate(Map<String, String> params) {

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("runtime.log.logsystem.class", NullLogChute.class.getName());
        velocityEngine.init();

        String msg = "";

        String templateContent = "";

        VelocityContext ctx = new VelocityContext();
        boolean isParamValid = true;

        if (!MapUtils.isEmpty(params)) {
            for (String element : params.keySet()) {
                if (params.containsKey(element)) {
                    String value = params.get(element);
                    ctx.put(element, value);
                } else {
                    isParamValid = false;
                    break;
                }
            }
        }
        if (!isParamValid) {
            return;
        }
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(ctx, writer, "", templateContent);
        msg = writer.toString();

        try {
            writer.close();
        } catch (IOException e) {
            log.error("writer close failed", e);
        }
    }

}
