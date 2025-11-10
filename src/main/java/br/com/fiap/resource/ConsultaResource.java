package br.com.fiap.resource;

import br.com.fiap.dto.ConsultaRequestDto;
import br.com.fiap.dto.ConsultaResponseDto;
import br.com.fiap.models.Consulta;
import br.com.fiap.service.ConsultaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Recurso REST para gerenciar consultas.
 * Fornece endpoints para CRUD (Create, Read, Update, Delete) de consultas.
 * Todas as respostas são no formato JSON.
 */
@Path("/consultas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ConsultaResource {

    @Inject
    private ConsultaService consultaService;

    /**
     * Lista todas as consultas cadastradas.
     *
     * @return Response com a lista de consultas no corpo e status 200 OK,
     * ou 500 em caso de erro interno.
     */
    @GET
    public Response listar() {
        try {
            List<ConsultaResponseDto> consultas = consultaService.listar();
            return Response.ok(consultas).build();
        } catch (Exception e) {
            System.err.println("Erro ao listar consultas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao listar consultas.")
                    .build();
        }
    }

    /**
     * Busca uma consulta por seu ID.
     *
     * @param id ID da consulta
     * @return Response com a consulta encontrada e status 200 OK,
     * 400 se o ID for inválido, 404 se não encontrado, ou 500 em caso de erro interno.
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID da consulta deve ser maior que zero.")
                        .build();
            }

            ConsultaResponseDto consulta = consultaService.buscarPorId(id);
            if (consulta == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Consulta não encontrada com ID: " + id)
                        .build();
            }

            return Response.ok(consulta).build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar consulta por ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar consulta.")
                    .build();
        }
    }

    /**
     * Cadastra uma nova consulta.
     *
     * @param dto      Dados da consulta a serem cadastrados
     * @param uriInfo  Informações do contexto da URI para retornar o Location do recurso criado
     * @return Response com a consulta cadastrada e status 201 Created,
     * 400 se os dados forem inválidos, ou 500 em caso de erro interno.
     */
    @POST
    public Response cadastrar(ConsultaRequestDto dto, @Context UriInfo uriInfo) {
        try {
            if (dto == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Os dados da consulta não podem ser nulos.")
                        .build();
            }

            dto.cleanData();

            if (!dto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Dados da consulta inválidos ou incompletos.")
                        .build();
            }

            if (dto.getDataConsulta() != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate data = LocalDate.parse(dto.getDataConsulta(), formatter);
                    dto.setDataConsultaLocal(data);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Formato de data inválido. Use yyyy-MM-dd.")
                            .build();
                }
            }

            ConsultaResponseDto novaConsulta = consultaService.cadastrar(dto);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(novaConsulta.getId()));
            URI location = builder.build();

            return Response.created(location).entity(novaConsulta).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados inválidos: " + e.getMessage())
                    .build();
        } catch (RuntimeException e) {
            System.err.println("Erro ao cadastrar consulta: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao cadastrar consulta.")
                    .build();
        }
    }

    /**
     * Atualiza uma consulta existente pelo ID.
     *
     * @param id  ID da consulta a ser atualizada
     * @param dto Dados atualizados da consulta
     * @return Response com a consulta atualizada e status 200 OK,
     * 400 se os dados ou ID forem inválidos, 404 se não encontrado, ou 500 em caso de erro interno.
     */
    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, ConsultaRequestDto dto) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID da consulta deve ser maior que zero.")
                        .build();
            }

            if (dto == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Os dados da consulta não podem ser nulos.")
                        .build();
            }

            dto.cleanData();

            if (!dto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Dados da consulta inválidos ou incompletos.")
                        .build();
            }

            Consulta atualizada = consultaService.atualizar(id, dto);
            return Response.ok(atualizada).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Consulta não encontrada com ID: " + id)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados inválidos: " + e.getMessage())
                    .build();
        } catch (RuntimeException e) {
            System.err.println("Erro ao atualizar consulta: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar consulta.")
                    .build();
        }
    }

    /**
     * Exclui uma consulta pelo ID.
     *
     * @param id ID da consulta a ser excluída
     * @return Response com status 204 No Content se excluída com sucesso,
     * 400 se ID inválido, 404 se não encontrado, 409 se houver dependências, ou 500 em caso de erro interno.
     */
    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID da consulta deve ser maior que zero.")
                        .build();
            }

            consultaService.excluir(id);
            return Response.noContent().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Consulta não encontrada com ID: " + id)
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("foreign key")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Não é possível excluir a consulta: há registros relacionados.")
                        .build();
            }
            System.err.println("Erro ao excluir consulta: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao excluir consulta.")
                    .build();
        }
    }
}
