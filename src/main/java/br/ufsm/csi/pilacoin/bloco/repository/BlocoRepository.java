package br.ufsm.csi.pilacoin.bloco.repository;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlocoRepository extends MongoRepository<Bloco, String> {
}
