/**
 *  Questo bean gestisce le operazioni CRUD e le query necessarie.
 */
package clinica_veterinaria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PazienteBean {

    @PersistenceContext(unitName = "EsameDB")
    private EntityManager em;

    public Paziente trovaPerId(int id) {
        return em.find(Paziente.class, id);
    }

    public List<Paziente> trovaPerTipo(String tipo) {
        return em.createQuery("SELECT p FROM Paziente p WHERE p.tipoAnimale = :tipo", Paziente.class)
                .setParameter("tipo", tipo)
                .getResultList();
    }

    public List<Paziente> trovaPerStatusRicovero(boolean status) {
        return em.createQuery("SELECT p FROM Paziente p WHERE p.statusRicovero = :status", Paziente.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Paziente> trovaTutti() {
        return em.createQuery("SELECT p FROM Paziente p", Paziente.class).getResultList();
    }

    public List<Paziente> trovaSenzaMicrochip() {
        return em.createQuery("SELECT p FROM Paziente p WHERE p.microchip = 0", Paziente.class).getResultList();
    }
}


