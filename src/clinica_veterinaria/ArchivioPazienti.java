/**
 *  Il bean singleton inizializza l'archivio di pazienti al momento dell'avvio dell'applicazione.
 */
package clinica_veterinaria;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Startup
@Singleton
public class ArchivioPazienti {

    @PersistenceContext(unitName = "EsameDB")
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Inizializzazione dell'archivio con alcuni dati di esempio
        em.persist(new Paziente("cane", "Fido", "media", "maschio", 5, 123456789L, false));
        em.persist(new Paziente("gatto", "Micia", "piccola", "femmina", 3, 0L, true));
        em.persist(new Paziente("coccodrillo", "Coco", "grande", "maschio", 10, 0L, false));
    }
}

