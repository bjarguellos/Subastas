package com.registro.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * La clase RegistroUsuariosSpringSecurityApplication es la clase principal de la aplicación 
 * y el punto de entrada para el marco de trabajo Spring Boot.
 * <p>
 * Esta clase inicia la aplicación utilizando la anotación {@link SpringBootApplication}, 
 * que es una combinación de las siguientes tres anotaciones:
 * <ul>
 *   <li>{@link org.springframework.boot.autoconfigure.SpringBootApplication}: Activa la configuración automática de Spring Boot, escanea los componentes en el paquete actual y en sus subpaquetes, y permite la configuración de beans de Spring.</li>
 *   <li>{@link org.springframework.context.annotation.ComponentScan}: Escanea y registra los componentes en el contexto de Spring.</li>
 *   <li>{@link org.springframework.context.annotation.Configuration}: Marca la clase como fuente de definiciones de beans.</li>
 * </ul>
 * <p>
 * El método {@code main} es el punto de entrada de la aplicación. Invoca el método estático 
 * {@code run} de la clase {@link SpringApplication} para lanzar la aplicación.
 */
@SpringBootApplication
public class RegistroUsuariosSpringSecurityApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * 
     * @param args Los argumentos de línea de comandos pasados a la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(RegistroUsuariosSpringSecurityApplication.class, args);
    }
}
