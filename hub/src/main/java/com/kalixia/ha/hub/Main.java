package com.kalixia.ha.hub;

import dagger.ObjectGraph;

import javax.inject.Inject;

public class Main {
    @Inject
    Hub hub;

    public void start() throws InterruptedException {
        hub.start();
    }

    public static void main(String[] args) throws InterruptedException {
        ObjectGraph objectGraph = ObjectGraph.create(new HubModule());
        Main main = objectGraph.get(Main.class);
        main.start();
    }
}
