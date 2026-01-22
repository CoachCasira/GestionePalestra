package test.model;

import model.Cliente;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ClientePrenotaConsulenzaTest {

    private Cliente creaClienteDiTest() {
        return new Cliente(
                "userTest",
                "passwordTest",
                "Mario",
                "Rossi",
                "RSSMRA00A01H501U",
                "Bergamo",
                new Date(),                 // data nascita (dummy)
                "IT99A0100000324569",       // iban (dummy)
                "test@mail.com"
        );
    }

    @Test
    public void prenotaConsulenza_nonLanciaEccezioni_conParametriNull() {
        Cliente cliente = creaClienteDiTest();

        assertDoesNotThrow(() -> cliente.prenotaConsulenza(null, null),
                "prenotaConsulenza() non dovrebbe lanciare eccezioni anche se non implementato");
    }

    @Test
    public void prenotaConsulenza_nonLanciaEccezioni_conDataValida() {
        Cliente cliente = creaClienteDiTest();
        Date data = new Date();

        assertDoesNotThrow(() -> cliente.prenotaConsulenza(null, data),
                "prenotaConsulenza() deve essere invocabile nel flusso e non deve lanciare eccezioni");
    }

    @Test
    public void prenotaConsulenza_nonModificaAbbonamentoEPagamento_seMetodoNonImplementato() {
        Cliente cliente = creaClienteDiTest();

        // stato iniziale
        assertNull(cliente.getAbbonamento(), "All'inizio il cliente non dovrebbe avere un abbonamento assegnato");
        assertNull(cliente.getPagamento(), "All'inizio il cliente non dovrebbe avere un pagamento assegnato");

        // chiamata metodo (attualmente vuoto)
        cliente.prenotaConsulenza(null, new Date());

        // nessun side-effect inatteso (coerente con implementazione attuale)
        assertNull(cliente.getAbbonamento(), "prenotaConsulenza() non dovrebbe toccare l'abbonamento (metodo non implementato)");
        assertNull(cliente.getPagamento(), "prenotaConsulenza() non dovrebbe toccare il pagamento (metodo non implementato)");
    }
}
