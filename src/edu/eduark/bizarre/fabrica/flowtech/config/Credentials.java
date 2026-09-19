package edu.eduark.bizarre.fabrica.flowtech.config;
 
public class Credentials {
    public static final String DB_URL = construirUrl();
    public static final String DB_USER = obtenerVariable("USER_MY_SQL");
    public static final String DB_PASSWORD = obtenerVariable("PASS_MY_SQL");

    private Credentials() {
        // Evita crear instancias de una clase de configuración.
    }

    private static String construirUrl() {
        String url = obtenerVariable("URL_MYSQL_DB");
        String baseDatos = obtenerVariable("DATA_BASE_3");

        if (url.contains("{DATABASE}")) {
            return url.replace("{DATABASE}", baseDatos);
        }

        // Si la URL ya trae una base de datos, se respeta tal como fue configurada.
        String urlSinParametros = url.split("\\?", 2)[0];
        String resto = url.length() > urlSinParametros.length() ? url.substring(urlSinParametros.length()) : "";
        if (urlSinParametros.matches("jdbc:mysql://[^/]+/?")) {
            return urlSinParametros.replaceAll("/$", "") + "/" + baseDatos + resto;
        }
        return url;
    }

    private static String obtenerVariable(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno: " + nombre);
        }
        return valor.trim();
    }
}
 