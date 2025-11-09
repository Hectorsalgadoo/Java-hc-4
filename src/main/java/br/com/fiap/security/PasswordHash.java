package br.com.fiap.security;

import io.quarkus.elytron.security.common.BcryptUtil;

/**
 * Classe utilitária para hash e verificação de senhas utilizando Bcrypt.
 * Fornece métodos estáticos para gerar hash seguro e validar senhas.
 */
public class PasswordHash {

    /**
     * Gera o hash Bcrypt de uma senha fornecida.
     *
     * @param senha A senha em texto claro que será convertida para hash.
     * @return Uma String contendo o hash Bcrypt da senha.
     * @throws IllegalArgumentException Se a senha for nula ou vazia.
     */
    public static String hashPassword(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
        }
        return BcryptUtil.bcryptHash(senha);
    }

    /**
     * Verifica se a senha digitada corresponde ao hash armazenado.
     *
     * @param senhaDigitada A senha em texto claro fornecida pelo usuário.
     * @param hashSenha O hash Bcrypt armazenado para comparação.
     * @return {@code true} se a senha digitada corresponder ao hash; {@code false} caso contrário.
     */
    public static boolean verificarSenha(String senhaDigitada, String hashSenha) {
        if (senhaDigitada == null || hashSenha == null) {
            return false;
        }
        return BcryptUtil.matches(senhaDigitada, hashSenha);
    }
}
