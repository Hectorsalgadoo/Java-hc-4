package br.com.fiap.security;

import br.com.fiap.dto.PacienteRequestDto;
import br.com.fiap.models.Paciente;
import br.com.fiap.service.PacienteService;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Recurso REST para autenticação de usuários.
 * Fornece endpoint para login de pacientes, gerando token JWT.
 */
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    PacienteService pacienteService;

    /**
     * Realiza o login de um paciente utilizando CPF e senha.
     * Caso os dados estejam corretos, retorna um token JWT válido por 2 horas.
     * @param dto Dados do paciente para autenticação (CPF e senha)
     */
    @POST
    @Path("/login")
    public Response login(PacienteRequestDto dto) {
        try {
            if (dto.getCpf() == null || dto.getSenha() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("CPF e senha são obrigatórios")
                        .build();
            }
            Paciente paciente = pacienteService.buscarPorCpfRaw(dto.getCpf());

            if (paciente == null || !PasswordHash.verificarSenha(dto.getSenha(), paciente.getSenha())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("CPF ou senha inválidos")
                        .build();
            }
            Set<String> roles = new HashSet<>();
            roles.add("PACIENTE");

            String token = Jwt.issuer("https://fiap.com.br")
                    .upn(paciente.getCpf())
                    .groups(roles)
                    .expiresIn(Duration.ofHours(2))
                    .sign();

            return Response.ok().entity("{\"token\":\"" + token + "\"}").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao gerar token: " + e.getMessage())
                    .build();
        }
    }
}
