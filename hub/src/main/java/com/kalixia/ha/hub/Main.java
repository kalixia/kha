package com.kalixia.ha.hub;

import dagger.ObjectGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class Main {
    @Inject
    Hub hub;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public void start() throws InterruptedException {
        hub.start();
    }

    public static void main(String[] args) throws InterruptedException {
        logger.info("Starting Kha Hub...");

        ObjectGraph objectGraph = ObjectGraph.create(new HubModule());
        Main main = objectGraph.get(Main.class);
        main.start();
    }
}
