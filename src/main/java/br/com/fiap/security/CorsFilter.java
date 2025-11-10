package br.com.fiap.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        // Permite que qualquer origem acesse a API
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        // Quais cabeçalhos são permitidos
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");

        // Quais métodos HTTP são permitidos
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // Permite que o navegador mantenha a autorização
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
    }
}
