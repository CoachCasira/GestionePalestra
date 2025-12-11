package test.model;

import model.Cliente;
import model.Abbonamento;
import model.AbbonamentoBasico;
import model.Pagamento;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteSottoscriviAbbonamentoTest {

    /**
     * Crea un cliente fittizio per i test.
     * Adatta solo se i parametri del tuo costruttore sono diversi.
     */
    private Cliente creaClienteDiTest() {
        return new Cliente(
                "userTest",             // username
                "passwordTest",         // password
                "Mario",                // nome
                "Rossi",                // cognome
                "RSSMRA00A01H501U",     // codice fiscale
                "Bergamo",             // luogo nascita
                java.sql.Date.valueOf("2000-01-01"), // data nascita
                "IT99A010000324569" ,    // IBAN
                "cmatteo453@gmail.com"
        );
    }

   @Test
  public void sottoscriviAbbonamento_associaAbbonamentoEPagamento() {
        // ARRANGE
        Cliente cliente = creaClienteDiTest();
        Abbonamento abb = new AbbonamentoBasico();
        abb.setPrezzo(30);
        abb.setFasciaOrariaConsentita("Accesso sala pesi");

        Pagamento pagamento = new Pagamento("Contanti", abb.getPrezzo(), new Date());

        // ACT
        cliente.sottoscriviAbbonamento(abb, pagamento);

        // ASSERT
        assertSame(abb, cliente.getAbbonamento(),
                "L'abbonamento assegnato al cliente deve corrispondere a quello passato.");
        assertSame(pagamento, cliente.getPagamento(),
                "Il pagamento assegnato al cliente deve essere quello utilizzato.");
        assertEquals("Contanti", pagamento.getMetodo());
        assertEquals(30, pagamento.getImporto());
    }

    @Test
  public  void sottoscriviAbbonamento_secondaVoltaSovrascriveQuelloVecchio() {
        Cliente cliente = creaClienteDiTest();

        // Primo abbonamento e pagamento
        Abbonamento abb1 = new AbbonamentoBasico();
        abb1.setPrezzo(30);
        Pagamento pag1 = new Pagamento("Contanti", abb1.getPrezzo(), new Date());
        cliente.sottoscriviAbbonamento(abb1, pag1);

        // Secondo abbonamento e pagamento
        Abbonamento abb2 = new AbbonamentoBasico();
        abb2.setPrezzo(40);
        Pagamento pag2 = new Pagamento("Carta", abb2.getPrezzo(), new Date());
        cliente.sottoscriviAbbonamento(abb2, pag2);

        // Controllo che abbia sovrascritto
        assertSame(abb2, cliente.getAbbonamento(),
                "Il secondo abbonamento deve sostituire il primo.");
        assertSame(pag2, cliente.getPagamento(),
                "Il secondo pagamento deve sostituire il primo.");
        assertEquals(40, pag2.getImporto());
        assertEquals("Carta", pag2.getMetodo());
    }
}
