package br.com.fiap.resource;

import br.com.fiap.dto.ProfissionalRequestDto;
import br.com.fiap.dto.ProfissionalResponseDto;
import br.com.fiap.service.ProfissionalService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.List;

/**
 * Recurso REST para gerenciar profissionais.
 * Fornece endpoints para CRUD (Create, Read, Update, Delete) de profissionais.
 * Todas as respostas são no formato JSON.
 */
@Path("/profissionais")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProfissionalResource {

    @Inject
    private ProfissionalService profissionalService;

    /**
     * Lista todos os profissionais cadastrados.
     *
     * @return Response com a lista de profissionais e status 200 OK,
     * ou 500 em caso de erro interno.
     */
    @GET
    public Response listar() {
        try {
            List<ProfissionalResponseDto> lista = profissionalService.listar();
            return Response.ok(lista).build();
        } catch (Exception e) {
            System.err.println("Erro ao listar profissionais: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao listar profissionais.")
                    .build();
        }
    }

    /**
     * Busca um profissional pelo ID.
     *
     * @param id ID do profissional
     * @return Response com o profissional encontrado e status 200 OK,
     * 400 se ID inválido, 404 se não encontrado, ou 500 em caso de erro interno.
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID do profissional deve ser maior que zero.")
                        .build();
            }

            ProfissionalResponseDto profissional = profissionalService.buscarPorId(id);
            return Response.ok(profissional).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Profissional não encontrado com ID: " + id)
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar profissional por ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar profissional.")
                    .build();
        }
    }

    /**
     * Busca um profissional pelo CRM.
     *
     * @param crm CRM do profissional
     * @return Response com o profissional encontrado e status 200 OK,
     * 400 se CRM inválido, 404 se não encontrado, ou 500 em caso de erro interno.
     */
    @GET
    @Path("/crm/{crm}")
    public Response buscarPorCrm(@PathParam("crm") int crm) {
        try {
            if (crm <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("CRM deve ser maior que zero.")
                        .build();
            }

            ProfissionalResponseDto profissional = profissionalService.buscarPorCrm(crm);
            if (profissional == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Profissional não encontrado com CRM: " + crm)
                        .build();
            }

            return Response.ok(profissional).build();

        } catch (Exception e) {
            System.err.println("Erro ao buscar profissional por CRM: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar profissional por CRM.")
                    .build();
        }
    }

    /**
     * Cadastra um novo profissional.
     *
     * @param profissionalDto Dados do profissional a serem cadastrados
     * @param uriInfo Informações do contexto da URI para retornar o Location do recurso criado
     * @return Response com o profissional cadastrado e status 201 Created,
     * 400 se dados inválidos, 409 se CRM já cadastrado, ou 500 em caso de erro interno.
     */
    @POST
    public Response cadastrar(ProfissionalRequestDto profissionalDto, @Context UriInfo uriInfo) {
        try {
            if (profissionalDto == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Os dados do profissional não podem ser nulos.")
                        .build();
            }

            if (!profissionalDto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Dados do profissional inválidos ou incompletos.")
                        .build();
            }

            ProfissionalResponseDto novo = profissionalService.cadastrar(profissionalDto);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(novo.getIdProfissional()));
            URI location = builder.build();

            return Response.created(location).entity(novo).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados inválidos: " + e.getMessage())
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("crm") ||
                    e.getMessage().toLowerCase().contains("unique")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("CRM já cadastrado.")
                        .build();
            }
            System.err.println("Erro ao cadastrar profissional: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao cadastrar profissional.")
                    .build();
        }
    }

    /**
     * Atualiza um profissional existente pelo ID.
     *
     * @param id ID do profissional a ser atualizado
     * @param profissionalDto Dados atualizados do profissional
     * @return Response com status 200 OK se atualizado,
     * 400 se dados ou ID inválidos, 404 se não encontrado, 409 se CRM em conflito, ou 500 em caso de erro interno.
     */
    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, ProfissionalRequestDto profissionalDto) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID do profissional deve ser maior que zero.")
                        .build();
            }

            if (profissionalDto == null || !profissionalDto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Dados do profissional inválidos ou incompletos.")
                        .build();
            }

            profissionalService.atualizar(id, profissionalDto);
            return Response.ok("Profissional atualizado com sucesso.").build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Profissional não encontrado com ID: " + id)
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("crm") ||
                    e.getMessage().toLowerCase().contains("unique")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Conflito de dados: CRM já cadastrado.")
                        .build();
            }
            System.err.println("Erro ao atualizar profissional: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar profissional.")
                    .build();
        }
    }

    /**
     * Exclui um profissional pelo ID.
     *
     * @param id ID do profissional a ser excluído
     * @return Response com status 204 No Content se excluído com sucesso,
     * 400 se ID inválido, 404 se não encontrado, 409 se houver registros relacionados, ou 500 em caso de erro interno.
     */
    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID do profissional deve ser maior que zero.")
                        .build();
            }

            profissionalService.excluir(id);
            return Response.noContent().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Profissional não encontrado com ID: " + id)
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("foreign key")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Não é possível excluir o profissional: há registros relacionados.")
                        .build();
            }
            System.err.println("Erro ao excluir profissional: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao excluir profissional.")
                    .build();
        }
    }
}
