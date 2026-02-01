# GestionePalestra 🏋️‍♂️📱

**GestionePalestra** è un’applicazione sviluppata per il corso di **Ingegneria del Software** con l’obiettivo di digitalizzare le principali operazioni di una palestra tramite un’interfaccia in stile mobile (**tema scuro + accento arancione**).

L’app permette al **cliente** di sottoscrivere e gestire un abbonamento, prenotare consulenze con **personal trainer** o **nutrizionista** verificandone la disponibilità, e accedere alle funzionalità offerte dalla palestra (corsi, aree, panoramica servizi).  
Il sistema persiste i dati su database e adotta una separazione chiara tra responsabilità (**view / action / controller / DAO**) per garantire **manutenibilità** e facilità di evoluzione.

---

## ✅ Funzionalità principali

- **Registrazione e accesso cliente**
- **Sottoscrizione e pagamento abbonamento** *(BASE / COMPLETO / CORSI)*
- **Prenotazione consulenze** *(con controllo disponibilità e fasce orarie)*
- **Visualizzazione aree/servizi** della palestra e **gestione corsi** *(in base al tipo di abbonamento)*

---

## 🧩 Struttura e scelte progettuali

- **Architettura**: MVC con **interfacce di comunicazione** (package `action`) per ridurre l’accoppiamento tra View e Controller
- **Persistenza**: **DAO + JDBC** su database relazionale
- **UI**: Swing con layout ottimizzati per finestra **“mobile-like”**

---

## 📁 Struttura repository

- `codice/` → sorgenti applicazione
- `documenti/` → documentazione di progetto *(requisiti, project plan, maintenance, ecc.)*
- `Palestra/` → modello UML *(Papyrus)*

---

## 👥 Autori

- **Matteo Casiraghi** — 1092288  
- **Alberto Barcella** — 1092001  
- **Oscar Begnini** — 1075319
