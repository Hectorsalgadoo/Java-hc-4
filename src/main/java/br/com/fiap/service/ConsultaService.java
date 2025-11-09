package br.com.fiap.service;

import br.com.fiap.dao.ConsultaDao;
import br.com.fiap.dto.ConsultaRequestDto;
import br.com.fiap.dto.ConsultaResponseDto;
import br.com.fiap.models.Consulta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar operações relacionadas a consultas médicas.
 * Fornece métodos para listar, buscar, cadastrar, atualizar e excluir consultas.
 */
@ApplicationScoped
public class ConsultaService {

    @Inject
    private ConsultaDao consultaDao;

    /**
     * Lista todas as consultas cadastradas.
     *
     * @return Lista de {@link ConsultaResponseDto} contendo as consultas.
     * @throws RuntimeException Caso ocorra algum erro interno ao listar consultas.
     */
    public List<ConsultaResponseDto> listar() {
        try {
            List<Consulta> consultas = consultaDao.listarConsultas();
            return consultas.stream()
                    .map(ConsultaResponseDto::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao listar consultas: " + e.getMessage());
            throw new RuntimeException("Erro interno ao listar consultas.", e);
        }
    }

    /**
     * Busca uma consulta pelo seu ID.
     *
     * @param id ID da consulta.
     * @return {@link ConsultaResponseDto} correspondente ao ID informado.
     * @throws IllegalArgumentException Caso o ID seja menor ou igual a zero.
     * @throws NotFoundException Caso a consulta não seja encontrada.
     * @throws RuntimeException Em caso de erro interno.
     */
    public ConsultaResponseDto buscarPorId(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("O ID da consulta deve ser maior que zero.");
            }

            Consulta consulta = consultaDao.buscarPorId(id);
            if (consulta == null) {
                throw new NotFoundException("Consulta não encontrada com ID: " + id);
            }

            return ConsultaResponseDto.convertToDto(consulta);

        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao buscar consulta por ID: " + e.getMessage());
            throw new RuntimeException("Erro interno ao buscar consulta.", e);
        }
    }

    /**
     * Cadastra uma nova consulta a partir de um DTO.
     *
     * @param dto {@link ConsultaRequestDto} com os dados da nova consulta.
     * @return {@link ConsultaResponseDto} com os dados da consulta cadastrada.
     * @throws IllegalArgumentException Caso o DTO seja nulo ou contenha dados inválidos.
     * @throws RuntimeException Em caso de erro interno ao cadastrar consulta.
     */
    public ConsultaResponseDto cadastrar(ConsultaRequestDto dto) {
        try {
            if (dto == null) {
                throw new IllegalArgumentException("Os dados da consulta não podem ser nulos.");
            }

            dto.cleanData();

            StringBuilder erros = new StringBuilder();
            if (dto.getTipoConsulta() == null || dto.getTipoConsulta().isBlank()) {
                erros.append("Tipo da consulta é obrigatório.\n");
            }
            if (dto.getDataConsulta() == null || dto.getDataConsulta().isBlank()) {
                erros.append("Data da consulta é obrigatória.\n");
            }
            if (dto.getMotivoConsulta() == null || dto.getMotivoConsulta().isBlank()) {
                erros.append("Motivo da consulta é obrigatório.\n");
            }

            if (!erros.isEmpty()) {
                throw new IllegalArgumentException(erros.toString().trim());
            }

            Consulta nova = new Consulta();
            nova.setTipoConsulta(dto.getTipoConsulta());
            nova.setMotivoConsulta(dto.getMotivoConsulta());

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate data = LocalDate.parse(dto.getDataConsulta(), formatter);
                nova.setDataConsulta(data);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Formato de data inválido. Use yyyy-MM-dd.");
            }

            consultaDao.cadastrarConsulta(nova);

            return ConsultaResponseDto.convertToDto(nova);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar consulta: " + e.getMessage());
            throw new RuntimeException("Erro interno ao cadastrar consulta.", e);
        }
    }

    /**
     * Atualiza uma consulta existente com base no ID e dados fornecidos.
     *
     * @param id  ID da consulta a ser atualizada.
     * @param dto {@link ConsultaRequestDto} com os dados a serem atualizados.
     * @return {@link Consulta} atualizado.
     * @throws IllegalArgumentException Caso o ID ou DTO sejam inválidos.
     * @throws NotFoundException Caso a consulta não seja encontrada.
     * @throws RuntimeException Em caso de erro interno.
     */
    public Consulta atualizar(Integer id, ConsultaRequestDto dto) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("O ID da consulta deve ser maior que zero.");
            }

            if (dto == null) {
                throw new IllegalArgumentException("Os dados da consulta não podem ser nulos.");
            }

            dto.cleanData();

            Consulta existente = consultaDao.buscarPorId(id);
            if (existente == null) {
                throw new NotFoundException("Consulta não encontrada com ID: " + id);
            }

            if (dto.getTipoConsulta() != null) {
                existente.setTipoConsulta(dto.getTipoConsulta());
            }

            if (dto.getDataConsulta() != null && !dto.getDataConsulta().isBlank()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate data = LocalDate.parse(dto.getDataConsulta(), formatter);
                    existente.setDataConsulta(data);
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("Formato de data inválido. Use yyyy-MM-dd.");
                }
            }

            if (dto.getMotivoConsulta() != null) {
                existente.setMotivoConsulta(dto.getMotivoConsulta());
            }

            Consulta atualizada = consultaDao.atualizarConsulta(existente);
            System.out.println("Consulta atualizada com sucesso: " + atualizada);
            return atualizada;

        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar consulta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro interno ao atualizar consulta.", e);
        }
    }

    /**
     * Exclui uma consulta com base no ID.
     *
     * @param id ID da consulta a ser excluída.
     * @throws IllegalArgumentException Caso o ID seja inválido.
     * @throws NotFoundException Caso a consulta não seja encontrada.
     * @throws RuntimeException Em caso de erro interno ou se houver dependências que impeçam a exclusão.
     */
    public void excluir(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("O ID da consulta deve ser maior que zero.");
            }

            Consulta consulta = consultaDao.buscarPorId(id);
            if (consulta == null) {
                throw new NotFoundException("Consulta não encontrada com ID: " + id);
            }

            consultaDao.excluirConsulta(id);

        } catch (IllegalArgumentException | NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key")) {
                throw new RuntimeException("Não é possível excluir a consulta: há registros relacionados.", e);
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao excluir consulta: " + e.getMessage());
            throw new RuntimeException("Erro interno ao excluir consulta.", e);
        }
    }
}
