package com.kalixia.ha.gateway;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Gateway gateway = new GatewayImpl();
        gateway.start();
    }
}
