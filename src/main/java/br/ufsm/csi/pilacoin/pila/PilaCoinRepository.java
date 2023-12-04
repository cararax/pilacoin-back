package br.ufsm.csi.pilacoin.pila;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PilaCoinRepository extends MongoRepository<PilaCoin, String> {
}
