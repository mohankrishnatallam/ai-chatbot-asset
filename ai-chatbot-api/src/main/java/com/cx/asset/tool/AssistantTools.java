package com.cx.asset.tool;

import dev.langchain4j.agent.tool.Tool;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

import com.cx.asset.service.Assistant;

import java.time.LocalTime;

@Component
public class AssistantTools {

    /**
     * This tool is available to {@link Assistant}
     */
    @Tool
    @Observed
    public String currentTime() {
        return LocalTime.now().toString();
    }
}
