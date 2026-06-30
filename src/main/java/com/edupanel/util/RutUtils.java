package com.edupanel.util;

import java.util.Locale;

public final class RutUtils {

    private static final int MAXIMO_DIGITOS_CUERPO = 8;

    private RutUtils() {
    }
    public static String limpiar(String rut) {
        if (rut == null) {
            return null;
        }

        return rut.trim()
                .replace(".", "")
                .replace("-", "")
                .replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
    }
    public static String normalizar(String rut) {
        String rutLimpio = limpiar(rut);

        if (rutLimpio == null || rutLimpio.isBlank()) {
            throw new IllegalArgumentException("El RUT es obligatorio.");
        }

        if (!rutLimpio.matches("\\d{1," + MAXIMO_DIGITOS_CUERPO + "}[0-9K]")) {
            throw new IllegalArgumentException(
                    "El RUT debe tener hasta 8 numeros y terminar en un digito o K.");
        }

        String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
        char digitoVerificador = rutLimpio.charAt(rutLimpio.length() - 1);

        if (calcularDigitoVerificador(cuerpo) != digitoVerificador) {
            throw new IllegalArgumentException("El digito verificador del RUT no es valido.");
        }

        return cuerpo + "-" + digitoVerificador;
    }


    public static String formatear(String rut) {
        String rutLimpio = limpiar(rut);

        if (rutLimpio == null || !rutLimpio.matches("\\d{1," + MAXIMO_DIGITOS_CUERPO + "}[0-9K]")) {
            return rut == null ? "" : rut.trim();
        }

        String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
        char digitoVerificador = rutLimpio.charAt(rutLimpio.length() - 1);
        StringBuilder cuerpoFormateado = new StringBuilder(cuerpo);

        for (int posicion = cuerpoFormateado.length() - 3; posicion > 0; posicion -= 3) {
            cuerpoFormateado.insert(posicion, '.');
        }

        return cuerpoFormateado + "-" + digitoVerificador;
    }

    private static char calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;

        for (int posicion = cuerpo.length() - 1; posicion >= 0; posicion--) {
            suma += Character.getNumericValue(cuerpo.charAt(posicion)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }

        int resultado = 11 - (suma % 11);

        if (resultado == 11) {
            return '0';
        }
        if (resultado == 10) {
            return 'K';
        }
        return Character.forDigit(resultado, 10);
    }
}
