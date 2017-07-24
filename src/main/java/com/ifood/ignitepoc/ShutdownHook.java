package com.ifood.ignitepoc;

import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHook implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    private Ignite ignite;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        if (!event.getApplicationContext().equals(appContext)) {
            return;
        }

        ignite.close();
    }
}
