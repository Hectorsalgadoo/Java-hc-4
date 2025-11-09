package br.com.fiap.enums;

public enum TipoAtendiEnum {
    Presencial,
    Teleconsulta;


    public static TipoAtendiEnum fromString(String value) {
        if (value == null) return null;
        value = value.trim().toLowerCase();
        return switch (value) {
            case "presencial" -> Presencial;
            case "teleconsulta" -> Teleconsulta;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return this.name();
    }
}