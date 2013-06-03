package com.kalixia.ha.cloud;

import dagger.ObjectGraph;

import javax.inject.Inject;

public class Main {
    @Inject Gateway gateway;

    public void start() throws InterruptedException {
        gateway.start();
    }

    public static void main(String[] args) throws InterruptedException {
        ObjectGraph objectGraph = ObjectGraph.create(new GatewayModule());
        Main main = objectGraph.get(Main.class);
        main.start();
    }
}
