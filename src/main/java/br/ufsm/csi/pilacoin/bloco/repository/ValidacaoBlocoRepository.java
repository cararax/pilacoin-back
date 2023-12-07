package br.ufsm.csi.pilacoin.bloco.repository;

import br.ufsm.csi.pilacoin.bloco.model.ValidacaoBloco;
import br.ufsm.csi.pilacoin.pila.model.ValidacaoPilaCoin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ValidacaoBlocoRepository extends MongoRepository<ValidacaoBloco, String> {
}
