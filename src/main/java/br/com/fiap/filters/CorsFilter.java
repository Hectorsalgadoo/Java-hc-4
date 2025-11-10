package br.com.fiap.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        // Pega a origem da requisição (ex: https://nexushealth.vercel.app)
        String origin = requestContext.getHeaderString("Origin");

        // URLs permitidas
        // (Troque "https://nexushealth.vercel.app" pela sua URL real, se for diferente)
        if ("http://localhost:5173".equals(origin) || "https://nexushealth.vercel.app".equals(origin)) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        } else {
            // Se a origem não for permitida, ainda assim lidamos com o preflight
            // Mas não adicionamos o header 'Allow-Origin' para a origem desconhecida
        }


        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, Origin, X-Requested-With, Accept");

        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");

        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.setStatus(Response.Status.OK.getStatusCode());
        }
    }
}