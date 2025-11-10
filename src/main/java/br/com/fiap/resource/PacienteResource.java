package br.com.fiap.resource;

import br.com.fiap.dto.PacienteRequestDto;
import br.com.fiap.dto.PacienteResponseDto;
import br.com.fiap.service.PacienteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

/**
 * Recurso REST para gerenciar pacientes.
 * Fornece endpoints para CRUD (Create, Read, Update, Delete) de pacientes.
 * Todas as respostas são no formato JSON.
 */
@Path("/paciente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    @Inject
    private PacienteService pacienteService;

    /**
     * Lista todos os pacientes.
     *
     * @return Response com a lista de pacientes e status 200 OK,
     */
    @GET
    public Response listar() {
        try {
            List<PacienteResponseDto> pacientes = pacienteService.listar();
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            System.err.println("Erro ao listar pacientes: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao listar pacientes.")
                    .build();
        }
    }

    /**
     * Busca um paciente pelo ID.
     *
     * @param id ID do paciente
     * @return Response com o paciente encontrado e status 200 OK,
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID do paciente deve ser maior que zero.")
                        .build();
            }

            PacienteResponseDto paciente = pacienteService.buscarPorId(id);
            return Response.ok(paciente).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Paciente não encontrado com ID: " + id)
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar paciente.")
                    .build();
        }
    }

    /**
     * Busca um paciente pelo CPF.
     *
     * @param cpf CPF do paciente (11 dígitos)
     * @return Response com o paciente encontrado e status 200 OK,
     */
    @GET
    @Path("/cpf/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        try {
            if (cpf == null || !cpf.matches("\\d{11}")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("CPF deve conter exatamente 11 dígitos numéricos.")
                        .build();
            }

            PacienteResponseDto paciente = pacienteService.buscarPorCpf(cpf);
            if (paciente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Paciente não encontrado com CPF: " + cpf)
                        .build();
            }

            return Response.ok(paciente).build();

        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por CPF: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar paciente por CPF.")
                    .build();
        }
    }

    /**
     * Cadastra um novo paciente.
     *
     * @param pacienteDto Dados do paciente a serem cadastrados
     * @param uriInfo Informações do contexto da URI para retornar o Location do recurso criado
     * @return Response com o paciente cadastrado e status 201 Created,
     */
    @POST
    public Response cadastrar(PacienteRequestDto pacienteDto, @Context UriInfo uriInfo) {
        try {
            if (pacienteDto == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Os dados do paciente não podem ser nulos.")
                        .build();
            }

            pacienteDto.cleanData();

            if (!pacienteDto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Dados do paciente inválidos ou incompletos.")
                        .build();
            }

            PacienteResponseDto pacienteCadastrado = pacienteService.cadastrar(pacienteDto);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(pacienteCadastrado.getId()));
            URI location = builder.build();

            return Response.created(location).entity(pacienteCadastrado).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados inválidos: " + e.getMessage())
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("cpf") ||
                    e.getMessage().toLowerCase().contains("duplicate") ||
                    e.getMessage().toLowerCase().contains("unique")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("CPF já cadastrado.")
                        .build();
            }
            System.err.println("Erro ao cadastrar paciente: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao cadastrar.")
                    .build();
        }
    }

    /**
     * Atualiza um paciente existente pelo ID.
     *
     * @param id ID do paciente a ser atualizado
     * @param pacienteDto Dados atualizados do paciente
     * @return Response com status 200 OK se atualizado,
     */
    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, PacienteRequestDto pacienteDto) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID do paciente deve ser maior que zero.")
                        .build();
            }

            if (pacienteDto == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Os dados do paciente não podem ser nulos.")
                        .build();
            }

            pacienteDto.cleanData();
            if (!pacienteDto.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Dados do paciente inválidos ou incompletos.")
                        .build();
            }

            pacienteDto.setId(id);
            pacienteService.atualizar(id, pacienteDto);
            return Response.ok("Paciente atualizado com sucesso.").build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Paciente não encontrado com ID: " + id)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados inválidos: " + e.getMessage())
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("cpf") ||
                    e.getMessage().toLowerCase().contains("duplicate") ||
                    e.getMessage().toLowerCase().contains("unique")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Conflito de dados: CPF já cadastrado.")
                        .build();
            }
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar paciente.")
                    .build();
        }
    }

    /**
     * Exclui um paciente pelo ID.
     *
     * @param id ID do paciente a ser excluído
     * @return Response com status 204 No Content se excluído com sucesso,
     */
    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O ID do paciente deve ser maior que zero.")
                        .build();
            }

            pacienteService.excluir(id);
            return Response.noContent().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Id nao encontrado: " + id)
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("foreign key")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Não é possível excluir o paciente: há registros relacionados.")
                        .build();
            }
            System.err.println("Erro ao excluir paciente: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao excluir paciente.")
                    .build();
        }
    }
}
