package service;

import model.Cliente;

public interface PanoramicaPalestraServiceIf {

    /**
     * Calcola la panoramica della palestra per il cliente.
     * @param cliente cliente loggato
     * @return testo da mostrare nella view (mai null, eventualmente stringa vuota)
     * @throws Exception se si verificano errori di accesso al DB
     */
    String generaPanoramica(Cliente cliente) throws Exception;
}
