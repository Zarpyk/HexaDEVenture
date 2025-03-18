package com.hexadeventure.bootstrap;

import org.springframework.boot.SpringApplication;
import com.hexadeventure.HexaDEVentureApplication;

/**
 * Launcher for the application: starts the Spring application.
 *
 * @author Sven Woltmann
 * @see
 * <a href="https://github.com/SvenWoltmann/hexagonal-architecture-java/blob/1b9c4d48198ca48204d71de77d7d0dda85443f5a/bootstrap/src/main/java/eu/happycoders/shop/bootstrap/Launcher.java">Launcher.java</a>
 */

public class Launcher {
    
    public static void main(String[] args) {
        SpringApplication.run(HexaDEVentureApplication.class, args);
    }
}
