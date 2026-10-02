package com.example.Trabo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita execução assíncrona com @Async.
 * Necessário para que o AuditoriaService não bloqueie as requisições.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
