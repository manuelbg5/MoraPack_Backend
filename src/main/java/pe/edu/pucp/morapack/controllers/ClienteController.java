package pe.edu.pucp.morapack.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.morapack.models.Cliente;
import pe.edu.pucp.morapack.repository.ClienteReposoitory;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteReposoitory clienteReposoitory;

    @CrossOrigin
    @GetMapping
    public List<Cliente> listar(){
        return clienteReposoitory.findAll();
    }

    @CrossOrigin
    @GetMapping("/{nombres}")
    public List<Cliente> listarPorNombres(@PathVariable String nombres){
        return clienteReposoitory.findByNombres(nombres);
    }
}
