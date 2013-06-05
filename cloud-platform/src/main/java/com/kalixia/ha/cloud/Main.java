package com.kalixia.ha.cloud;

import dagger.ObjectGraph;

import javax.inject.Inject;

public class Main {
    @Inject
    CloudPlatform cloudPlatform;

    public void start() throws InterruptedException {
        cloudPlatform.start();
    }

    public static void main(String[] args) throws InterruptedException {
        ObjectGraph objectGraph = ObjectGraph.create(new CloudPlatformModule());
        Main main = objectGraph.get(Main.class);
        main.start();
    }
}
