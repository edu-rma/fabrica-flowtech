package edu.eduark.bizarre.fabrica.flowtech.config;

public class Credentials {

    public static String getUrlDb() {
        String baseUrl = getEnvOrThrow("URL_MYSQL_DB");
        String dbName = getEnvOrThrow("DATA_BASE_3");
        return baseUrl + dbName;
    }

    public static String getUserDb() {
        return getEnvOrThrow("USER_MY_SQL");
    }

    public static String getPassDb() {
        // En algunas configuraciones la contraseña de MySQL puede ser vacía (""),
        // por lo que solo se valida que la variable exista.
        String pass = System.getenv("PASS_MY_SQL");
        if (pass == null) {
            throw new IllegalStateException("La variable de entorno 'PASS_MY_SQL' no está definida en el sistema.");
        }
        return pass;
    }

    private static String getEnvOrThrow(String key) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("La variable de entorno '" + key + "' no está definida en el sistema.");
        }
        return value.trim();
    }
}