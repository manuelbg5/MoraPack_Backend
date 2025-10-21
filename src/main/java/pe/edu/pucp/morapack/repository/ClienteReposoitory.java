package pe.edu.pucp.morapack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.morapack.models.Cliente;

import java.util.List;

public interface ClienteReposoitory extends JpaRepository<Cliente,Integer> {

        List<Cliente> findByNombres(String nombre);
}
