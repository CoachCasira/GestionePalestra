package service;

import java.util.List;


import db.dao.PalestraDAO;
import db.dao.PalestraDAO.MacchinarioInfo;
import db.dao.PalestraDAO.SalaCorsoInfo;
import db.dao.corso.CorsoDAO;
import model.corsi.*;
import model.Abbonamento;
import model.Cliente;

public class PanoramicaPalestraService implements PanoramicaPalestraServiceIf {

    @Override
    public String generaPanoramica(Cliente cliente) throws Exception {
        Abbonamento abb = cliente.getAbbonamento();
        if (abb == null || abb.getTipo() == null) {
            // nessuna area disponibile
            return "";
        }

        String tipo = abb.getTipo().trim().toUpperCase();

        boolean canSalaPesi = false;
        boolean canSpa      = false;
        boolean canCorsi    = false;

        switch (tipo) {
            case "BASE":
                canSalaPesi = true;
                break;
            case "COMPLETO":
                canSalaPesi = true;
                canSpa = true;
                break;
            case "CORSI":
                canCorsi = true;
                break;
            default:
                // tipo non supportato → nessuna area
                return "";
        }

        StringBuilder sb = new StringBuilder();

        // ===== SALA PESI =====
        if (canSalaPesi) {
            appendSalaPesi(sb);
        }

        // ===== SPA =====
        if (canSpa) {
            appendSpa(sb);
        }

        // ===== SALE CORSI =====
        if (canCorsi) {
            appendSaleCorsi(sb);
        }

        return sb.toString();
    }

    private void appendSalaPesi(StringBuilder sb) throws Exception {
        PalestraDAO.SalaPesiInfo info = PalestraDAO.getSalaPesiInfo();

        sb.append("=== SALA PESI ===\n\n");

        if (info == null) {
            sb.append("La sala pesi non è al momento configurata nel sistema.\n\n");
        } else {
            sb.append("Orari apertura: ").append(info.orariApertura).append("\n");
            sb.append("Capienza massima: ").append(info.capienza).append(" persone\n");
            sb.append("Metratura: ").append(info.metratura).append(" m²\n");
            sb.append("Numero macchinari: ").append(info.numMacchinari).append("\n");
            sb.append("Numero panche: ").append(info.numPanche).append("\n");
            sb.append("Pesi liberi disponibili: ").append(info.numPesiLiberi).append("\n");
            sb.append("Disponibilità attuale: ")
              .append(info.disponibilita ? "aperta" : "chiusa")
              .append("\n\n");
        }

        sb.append("Macchinari disponibili:\n");
        List<MacchinarioInfo> macchinari = PalestraDAO.getMacchinariSalaPesi();
        if (macchinari.isEmpty()) {
            sb.append("- Nessun macchinario presente a catalogo.\n\n");
        } else {
            for (MacchinarioInfo m : macchinari) {
                sb.append("- ").append(m.descrizione()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("\n");
    }

    private void appendSpa(StringBuilder sb) throws Exception {
        PalestraDAO.SpaInfo spa = PalestraDAO.getSpaInfo();

        sb.append("=== SPA ===\n\n");

        if (spa == null) {
            sb.append("La SPA non è al momento configurata nel sistema.\n\n");
        } else {
            sb.append("Orari apertura: ").append(spa.orariApertura).append("\n");
            sb.append("Capienza massima: ").append(spa.capienza).append(" persone\n");
            sb.append("Numero saune: ").append(spa.numSaune).append("\n");
            sb.append("Numero piscine: ").append(spa.numPiscine).append("\n");
            sb.append("Disponibilità attuale: ")
              .append(spa.disponibilita ? "aperta" : "chiusa")
              .append("\n\n");

            sb.append("La SPA offre aree relax dedicate con saune e piscine,\n")
              .append("pensate per il recupero post-allenamento e il benessere generale.\n\n");
        }

        sb.append("\n");
    }

    private void appendSaleCorsi(StringBuilder sb) throws Exception {
        List<SalaCorsoInfo> saleCorsi = PalestraDAO.getSaleCorsiInfo();

        sb.append("=== SALE CORSI ===\n\n");

        if (saleCorsi.isEmpty()) {
            sb.append("Al momento non sono configurate sale corsi.\n\n");
        } else {
            int idx = 1;
            for (SalaCorsoInfo s : saleCorsi) {
                sb.append("Sala corsi #").append(idx++).append("\n");
                sb.append("Orari apertura sala: ").append(s.orariApertura).append("\n");
                sb.append("Capienza: ").append(s.capienza).append(" persone\n");

                if (s.orarioCorso != null && !s.orarioCorso.isEmpty()) {
                    sb.append("Programmazione corso: ")
                      .append(s.orarioCorso)
                      .append("\n");
                } else {
                    sb.append("Programmazione corso: non specificata a sistema\n");
                }

                sb.append("Disponibilità: ")
                  .append(s.disponibilita ? "attiva" : "non attiva")
                  .append("\n\n");
            }
        }

        // corsi a catalogo (optional)
        try {
            List<CorsoInfo> corsiCatalogo = CorsoDAO.getTuttiICorsi();
            if (!corsiCatalogo.isEmpty()) {
                sb.append("Corsi a catalogo:\n\n");
                for (CorsoInfo c : corsiCatalogo) {
                    sb.append("- ").append(c.nome).append("\n");
                    sb.append("  Durata: ").append(c.durataMinuti).append(" minuti\n");
                    sb.append("  Descrizione: ").append(c.descrizione).append("\n\n");
                }
            }
        } catch (Exception ignore) {
            // se fallisce, non blocchiamo tutta la panoramica
        }

        sb.append("\n");
    }
}
