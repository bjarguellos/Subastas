package com.registro.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * La clase SubastasApplication es la clase principal de la aplicación
 * y el punto de entrada para el marco de trabajo Spring Boot.
 * <p>
 * Esta clase inicia la aplicación utilizando la anotación {@link SpringBootApplication},
 * que configura automáticamente Spring y escanea los componentes en el paquete actual y sus subpaquetes.
 * <p>
 * Además, la anotación {@link EnableScheduling} habilita la programación de tareas en la aplicación,
 * permitiendo la ejecución de tareas programadas mediante el uso de anotaciones como {@link org.springframework.scheduling.annotation.Scheduled}.
 */
@SpringBootApplication
@EnableScheduling
public class SubastasApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * <p>
     * Este método invoca el método estático {@link SpringApplication#run(Class, String...)}
     * para lanzar el contexto de la aplicación de Spring.
     * 
     * @param args Los argumentos de línea de comandos pasados a la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(SubastasApplication.class, args);
    }
}
