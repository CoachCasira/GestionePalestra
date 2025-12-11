package test.model;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

import model.Abbonamento;
import model.AbbonamentoBasico;
import model.AbbonamentoCompleto;
import model.AbbonamentoCorsi;

class AbbonamentoFactoryTest {

    @Test
public  void creaDaTipo_baseRitornaAbbonamentoBasico() {
        Abbonamento abb = Abbonamento.creaDaTipo("BASE", 1);

        assertNotNull(abb, "La factory non deve ritornare null per BASE");
        assertTrue(abb instanceof AbbonamentoBasico,
                "Per tipo BASE mi aspetto un AbbonamentoBasico");
    }

    @Test
 public   void creaDaTipo_completoRitornaAbbonamentoCompleto() {
        Abbonamento abb = Abbonamento.creaDaTipo("COMPLETO", 1);

        assertNotNull(abb, "La factory non deve ritornare null per COMPLETO");
        assertTrue(abb instanceof AbbonamentoCompleto,
                "Per tipo COMPLETO mi aspetto un AbbonamentoCompleto");
    }

    @Test
 public   void creaDaTipo_corsiRitornaAbbonamentoCorsi() {
        Abbonamento abb = Abbonamento.creaDaTipo("CORSI", 1);
 
        assertNotNull(abb, "La factory non deve ritornare null per CORSI");
        assertTrue(abb instanceof AbbonamentoCorsi,
                "Per tipo CORSI mi aspetto un AbbonamentoCorsi");
    }

    @Test
   public void creaDaTipo_tipoSconosciutoRitornaNull() {
        Abbonamento abb = Abbonamento.creaDaTipo("BOH", 1);
        assertNull(abb, "Per tipo sconosciuto la factory dovrebbe ritornare null");
    }
}
