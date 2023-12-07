package br.ufsm.csi.pilacoin.usuario;

import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import br.ufsm.csi.pilacoin.query.model.UsuarioResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<UsuarioResult, String> {
}
