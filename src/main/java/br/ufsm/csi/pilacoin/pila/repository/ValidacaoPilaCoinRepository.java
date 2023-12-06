package br.ufsm.csi.pilacoin.pila.repository;

import br.ufsm.csi.pilacoin.pila.model.ValidacaoPilaCoin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ValidacaoPilaCoinRepository extends MongoRepository<ValidacaoPilaCoin, String> {
}
