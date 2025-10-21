package pe.edu.pucp.utils;

import pe.edu.pucp.models.Aeropuerto;
import pe.edu.pucp.models.Pedido;
import pe.edu.pucp.models.Vuelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorCSV {

    /**
     * Lee el archivo de aeropuertos
     * Formato: codigo,nombre,pais,capacidad,capacidadAct,husoHorario,continente
     * @param rutaArchivo Ruta del archivo CSV
     * @return Lista de aeropuertos
     */
    public static List<Aeropuerto> leerAeropuertos(String rutaArchivo) {
        List<Aeropuerto> aeropuertos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Saltar encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(",");

                if (datos.length >= 7) {
                    String codigo = datos[0].trim();
                    String nombre = datos[1].trim();
                    String pais = datos[2].trim();
                    int capacidad = Integer.parseInt(datos[3].trim());
                    int husoHorario = Integer.parseInt(datos[5].trim());
                    String continente = datos[6].trim();

                    Aeropuerto aeropuerto = new Aeropuerto(codigo, nombre, pais,
                            capacidad, husoHorario, continente);
                    aeropuertos.add(aeropuerto);
                }
            }

            System.out.println("✅ Aeropuertos cargados: " + aeropuertos.size());

        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo de aeropuertos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("❌ Error al parsear datos de aeropuertos: " + e.getMessage());
        }

        return aeropuertos;
    }

    /**
     * Lee el archivo de pedidos
     * Formato: dd-hh-mm-DEST-###-IdCliente
     * @param rutaArchivo Ruta del archivo CSV
     * @return Lista de pedidos
     */
    public static List<Pedido> leerPedidos(String rutaArchivo) {
        List<Pedido> pedidos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Parsear formato: dd-hh-mm-DEST-###-IdCliente
                String[] partes = linea.trim().split("-");

                if (partes.length >= 6) {
                    int dia = Integer.parseInt(partes[0]);
                    int hora = Integer.parseInt(partes[1]);
                    int minuto = Integer.parseInt(partes[2]);
                    String destino = partes[3];
                    int cantidad = Integer.parseInt(partes[4]);
                    String idCliente = partes[5];

                    Pedido pedido = new Pedido(dia, hora, minuto, destino, cantidad, idCliente);
                    pedidos.add(pedido);
                }
            }

            System.out.println("✅ Pedidos cargados: " + pedidos.size());

        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo de pedidos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("❌ Error al parsear datos de pedidos: " + e.getMessage());
        }

        return pedidos;
    }

    /**
     * Lee el archivo de vuelos y genera instancias para cada día de la semana
     * Formato: ORIGEN-DESTINO-HH:MM-HH:MM-CAPACIDAD
     * @param rutaArchivo Ruta del archivo CSV
     * @param aeropuertos Lista de aeropuertos (para buscar referencias)
     * @return Lista de vuelos (7 instancias por cada plan de vuelo)
     */
    public static List<Vuelo> leerVuelos(String rutaArchivo, List<Aeropuerto> aeropuertos) {
        List<Vuelo> vuelos = new ArrayList<>();

        // Crear mapa para búsqueda rápida de aeropuertos
        Map<String, Aeropuerto> mapaAeropuertos = new HashMap<>();
        for (Aeropuerto a : aeropuertos) {
            mapaAeropuertos.put(a.getCodigo(), a);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int planesLeidos = 0;

            while ((linea = br.readLine()) != null) {
                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Parsear formato: ORIGEN-DESTINO-HH:MM-HH:MM-CAPACIDAD
                String[] partes = linea.trim().split("-");

                if (partes.length >= 5) {
                    String codigoOrigen = partes[0];
                    String codigoDestino = partes[1];
                    String[] horaSalidaParts = partes[2].split(":");
                    String[] horaLlegadaParts = partes[3].split(":");
                    int capacidad = Integer.parseInt(partes[4]);

                    // Buscar aeropuertos
                    Aeropuerto origen = mapaAeropuertos.get(codigoOrigen);
                    Aeropuerto destino = mapaAeropuertos.get(codigoDestino);

                    if (origen == null || destino == null) {
                        System.err.println("⚠️ Aeropuerto no encontrado en línea: " + linea);
                        continue;
                    }

                    int horaSalida = Integer.parseInt(horaSalidaParts[0]);
                    int minutoSalida = Integer.parseInt(horaSalidaParts[1]);
                    int horaLlegada = Integer.parseInt(horaLlegadaParts[0]);
                    int minutoLlegada = Integer.parseInt(horaLlegadaParts[1]);

                    // Crear 7 instancias del vuelo (uno por cada día de la semana)
                    for (int dia = 1; dia <= 7; dia++) {
                        LocalDateTime fechaSalida = LocalDateTime.of(2025, 1, dia, horaSalida, minutoSalida);
                        LocalDateTime fechaLlegada = LocalDateTime.of(2025, 1, dia, horaLlegada, minutoLlegada);

                        // Si la hora de llegada es menor que la de salida, es del día siguiente
                        if (fechaLlegada.isBefore(fechaSalida)) {
                            fechaLlegada = fechaLlegada.plusDays(1);
                        }

                        Vuelo vuelo = new Vuelo(origen, destino, fechaSalida, fechaLlegada, capacidad);
                        vuelos.add(vuelo);
                    }

                    planesLeidos++;
                }
            }

            System.out.println("✅ Planes de vuelo leídos: " + planesLeidos);
            System.out.println("✅ Instancias de vuelos generadas: " + vuelos.size() + " (7 días)");

        } catch (IOException e) {
            System.err.println("❌ Error al leer archivo de vuelos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("❌ Error al parsear datos de vuelos: " + e.getMessage());
        }

        return vuelos;
    }

    /**
     * Identifica las sedes principales a partir de códigos dados
     * @param aeropuertos Lista de todos los aeropuertos
     * @param codigosSedes Lista de códigos de sedes (ej: ["SPJC", "EBBR", "UBBB"])
     * @return Lista de aeropuertos que son sedes principales
     */
    public static List<Aeropuerto> identificarSedesPrincipales(List<Aeropuerto> aeropuertos,
                                                               List<String> codigosSedes) {
        List<Aeropuerto> sedes = new ArrayList<>();

        for (String codigo : codigosSedes) {
            for (Aeropuerto a : aeropuertos) {
                if (a.getCodigo().equals(codigo)) {
                    sedes.add(a);
                    break;
                }
            }
        }

        System.out.println("✅ Sedes principales identificadas: " + sedes.size());
        for (Aeropuerto sede : sedes) {
            System.out.println("   - " + sede.getNombre() + " (" + sede.getCodigo() + ")");
        }

        return sedes;
    }
}