package br.ufsm.csi.pilacoin.pila.repository;

import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PilaCoinRepository extends MongoRepository<PilaCoin, String> {
}
