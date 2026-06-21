package com.nasanbus.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info =
        Info(
            title = "NasanBus API",
            version = "v1",
            description = "HTTP API for NasanBus services",
        ),
    servers = [Server(url = "http://localhost:8080", description = "Local")],
)
class OpenApiConfig
