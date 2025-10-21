package pe.edu.pucp.simulacion;

import pe.edu.pucp.models.Aeropuerto;
import pe.edu.pucp.models.Pedido;
import pe.edu.pucp.models.Vuelo;

import java.util.ArrayList;
import java.util.List;

public class Planificador {

    // === Atributos ===
    private List<Pedido> pedidos;
    private List<Vuelo> vuelos;
    private List<Aeropuerto> aeropuertos;
    private List<Aeropuerto> sedesPrincipales;

    // Parámetros GRASP
    private double alphaGRASP;
    private int tamanoRCL;

    // Parámetros GA (para futuro)
    private int tamañoPoblacion;
    private int numeroGeneraciones;
    private double tasaMutacion;
    private double tasaCrossover;

    private boolean usarGA;  // Flag para activar/desactivar GA

    // === Constructor ===
    public Planificador(List<Pedido> pedidos, List<Vuelo> vuelos,
                        List<Aeropuerto> aeropuertos, List<Aeropuerto> sedesPrincipales) {
        this.pedidos = pedidos;
        this.vuelos = vuelos;
        this.aeropuertos = aeropuertos;
        this.sedesPrincipales = sedesPrincipales;

        // Valores por defecto GRASP
        this.alphaGRASP = 0.3;
        this.tamanoRCL = 3;

        // Valores por defecto GA (para futuro)
        this.tamañoPoblacion = 10;
        this.numeroGeneraciones = 50;
        this.tasaMutacion = 0.1;
        this.tasaCrossover = 0.8;

        this.usarGA = false;  // Por defecto solo GRASP
    }

    // === Getters y Setters ===

    public void setParametrosGRASP(double alpha, int tamanoRCL) {
        this.alphaGRASP = alpha;
        this.tamanoRCL = tamanoRCL;
    }

    public void setParametrosGA(int tamañoPoblacion, int numeroGeneraciones,
                                double tasaMutacion, double tasaCrossover) {
        this.tamañoPoblacion = tamañoPoblacion;
        this.numeroGeneraciones = numeroGeneraciones;
        this.tasaMutacion = tasaMutacion;
        this.tasaCrossover = tasaCrossover;
    }

    public void activarGA(boolean activar) {
        this.usarGA = activar;
    }

    // === Método principal ===

    /**
     * Ejecuta la planificación completa
     * @return Mejor solución encontrada
     */
    public Solucion ejecutarPlanificacion() {
        System.out.println("=== INICIANDO PLANIFICACIÓN ===");
        System.out.println("Modo: " + (usarGA ? "GRASP + GA" : "GRASP solo"));
        System.out.println();

        if (usarGA) {
            return ejecutarGRASP_GA();
        } else {
            return ejecutarSoloGRASP();
        }
    }

    /**
     * Ejecuta solo GRASP (modo actual)
     * @return Mejor solución de GRASP
     */
    private Solucion ejecutarSoloGRASP() {
        System.out.println("--- Ejecutando GRASP ---");

        // Crear instancia de GRASP
        GRASP grasp = new GRASP(pedidos, vuelos, aeropuertos, sedesPrincipales,
                alphaGRASP, tamanoRCL);

        // Generar solución
        Solucion solucion = grasp.generarSolucion();

        // Mostrar resumen
        System.out.println("\n--- Solución GRASP generada ---");
        mostrarResumenSolucion(solucion);

        return solucion;
    }

    /**
     * Ejecuta GRASP para generar población inicial, luego GA para evolucionar
     * @return Mejor solución después de GA
     */
    private Solucion ejecutarGRASP_GA() {
        System.out.println("--- Fase 1: Generando población inicial con GRASP ---");

        // Generar población inicial con GRASP
        List<Solucion> poblacionInicial = generarPoblacionInicialGRASP();

        System.out.println("Población inicial generada: " + poblacionInicial.size() + " soluciones");
        System.out.println("Mejor fitness inicial: " + obtenerMejorSolucion(poblacionInicial).getFitness());

        System.out.println("\n--- Fase 2: Evolucionando con GA ---");

        // TODO: Implementar GA en el futuro
        // GA ga = new GA(poblacionInicial, parametros...);
        // Solucion mejorSolucion = ga.evolucionar();

        // Por ahora, retornar la mejor de la población inicial
        Solucion mejorSolucion = obtenerMejorSolucion(poblacionInicial);

        System.out.println("\n--- Mejor solución encontrada ---");
        mostrarResumenSolucion(mejorSolucion);

        return mejorSolucion;
    }

    /**
     * Genera múltiples soluciones con GRASP para crear población inicial
     * @return Lista de soluciones (población)
     */
    private List<Solucion> generarPoblacionInicialGRASP() {
        List<Solucion> poblacion = new ArrayList<>();

        for (int i = 0; i < tamañoPoblacion; i++) {
            System.out.println("  Generando solución " + (i+1) + "/" + tamañoPoblacion + "...");

            // Crear nueva instancia de GRASP (con aleatorización generará soluciones diferentes)
            GRASP grasp = new GRASP(pedidos, vuelos, aeropuertos, sedesPrincipales,
                    alphaGRASP, tamanoRCL);

            // Generar solución
            Solucion solucion = grasp.generarSolucion();
            poblacion.add(solucion);

            System.out.println("    Fitness: " + solucion.getFitness());
        }

        return poblacion;
    }

    /**
     * Encuentra la mejor solución de una población
     * @param poblacion Lista de soluciones
     * @return Solución con mayor fitness
     */
    private Solucion obtenerMejorSolucion(List<Solucion> poblacion) {
        if (poblacion.isEmpty()) {
            return null;
        }

        Solucion mejor = poblacion.get(0);
        for (Solucion solucion : poblacion) {
            if (solucion.getFitness() > mejor.getFitness()) {
                mejor = solucion;
            }
        }

        return mejor;
    }

    /**
     * Muestra un resumen de la solución
     * @param solucion Solución a mostrar
     */
    private void mostrarResumenSolucion(Solucion solucion) {
        System.out.println("  Rutas creadas: " + solucion.getNumeroDeRutas());
        System.out.println("  Pedidos a tiempo: " + solucion.getPedidosEntregadosATiempo());
        System.out.println("  Violaciones vuelos: " + solucion.getViolacionesCapacidadVuelos());
        System.out.println("  Violaciones almacenes: " + solucion.getViolacionesCapacidadAlmacenes());
        System.out.println("  Fitness: " + String.format("%.2f", solucion.getFitness()));
    }

    @Override
    public String toString() {
        return "Planificador{" +
                "pedidos=" + pedidos.size() +
                ", vuelos=" + vuelos.size() +
                ", aeropuertos=" + aeropuertos.size() +
                ", usarGA=" + usarGA +
                '}';
    }
}