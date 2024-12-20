## Sommario

# 1. Creazione Progetti - Guida passo passo

1. Aprire la Macchina Virtuale, utente: CORSO_PD, password: CORSO_PD
2. Aprire Netbeans
3. Tasto destro sulle parti vuote sui progetti <br>
   a. **Creare Progetto Client**: Java with Ant > Java Application

    ![image.png](/classNotes/external/11_tutorial/image%200.png)

   b. **Creare Progetto Server**: Java Enterprise > EJB Module

    ![image.png](/classNotes/external/11_tutorial/image%201.png)

    ![image.png](/classNotes/external/11_tutorial/image%202.png)


## 1. Risultato ‘finale’ di un progetto

![image.png](/classNotes/external/11_tutorial/image%203.png)

# 1. Importare LE GIUSTE librerie!
## 1. Server
1. Prima di iniziare a scrivere qualsiasi classe Java, EJB o altro, importa le librerie corrette, perché se fai gli auto-import ti metterà tutta MUNNEZZA che non funziona
- Tasto destro sul progetto (server) > Properties > Libraries > Add Library

![image.png](/classNotes/external/11_tutorial/image%204.png)

- Assicurarsi di avere javaee-api-7.0.jar

## 1. Client

- Tasto destro sul progetto (client) > Properties > Libraries

![image.png](/classNotes/external/11_tutorial/image%205.png)

### **Nota**:

Le seguenti informazioni sono ESCLUSIVAMENTE per il progetto ‘Server’. Verrà specificato quando si interverrà sul progetto ‘Client’.

# 1. Creare l’entità

1. Potresti fare tasto destro sul progetto server e cliccare su **New > Entity Class**
2. Oppure crei una qualsiasi classe Java e inserisci a mano piano piano tutti i decoratori

**Come è fatta una classe entità delle tracce:**

- La classe è decorata con @Entity ed implementa Serializable
- Possiede un attributo (solitamente un Long o numerico) con decoratore ‘@Id’
- possiede un costruttore vuoto (necessario per interfacciarsi col Database)
- possiede un costruttore con tutti i parametri (per questioni di velocità)
- possiede una lista di queries
- possiede svariati attributi (seguendo la traccia)
    - e tutti gli appositi metodi getter e setter
- possiede un metodo String toString() [necessario per gli output delle tracce d’esame]
- Esempio codice:

    ```java
    package server;

    import java.io.Serializable;
    import javax.persistence.*;

    @Entity
    // @Table(name = "pazienti")
    @NamedQueries({
        @NamedQuery(name = "findAll", query = "SELECT b FROM Paziente b"),
        @NamedQuery(name = "findPatientByType", query = "SELECT b FROM Paziente b WHERE b.tipoAnimale = :tipo")
    })
    public class Paziente implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "tipo_animale", nullable = false)
        private String tipoAnimale;

        @Column(name = "nome", nullable = false)
        private String nome;

        @Column(name = "taglia", nullable = false)
        private String taglia;

        @Column(name = "sesso", nullable = false)
        private String sesso;

        @Column(name = "eta", nullable = false)
        private int eta;

        @Column(name = "microchip")
        private Long microchip;

        @Column(name = "status_ricovero", nullable = false)
        private boolean statusRicovero;

        // Costruttore vuoto richiesto da JPA
        public Paziente() {
        }

        // Costruttore completo
        public Paziente(String tipo, String nome, String taglia, String sesso, int eta, Long microchip, Boolean ricoverato) {
            this.tipoAnimale = tipo;
            this.nome = nome;
            this.taglia = taglia;
            this.sesso = sesso;
            this.eta = eta;
            this.microchip = microchip;
            this.statusRicovero = ricoverato;
        }

        // Getter e Setter
        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTipoAnimale() {
            return this.tipoAnimale;
        }

        public void setTipoAnimale(String tipoAnimale) {
            this.tipoAnimale = tipoAnimale;
        }

        public String getNome() {
            return this.nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getTaglia() {
            return this.taglia;
        }

        public void setTaglia(String taglia) {
            this.taglia = taglia;
        }

        public String getSesso() {
            return this.sesso;
        }

        public void setSesso(String sesso) {
            this.sesso = sesso;
        }

        public int getEta() {
            return this.eta;
        }

        public void setEta(int eta) {
            this.eta = eta;
        }

        public Long getMicrochip() {
            return this.microchip;
        }

        public void setMicrochip(Long microchip) {
            this.microchip = microchip;
        }

        public boolean isStatusRicovero() {
            return this.statusRicovero;
        }

        public void setStatusRicovero(boolean statusRicovero) {
            this.statusRicovero = statusRicovero;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Paziente");
            sb.append("{id=").append(id);
            sb.append(", tipoAnimale='").append(tipoAnimale).append('\'');
            sb.append(", nome='").append(nome).append('\'');
            sb.append(", taglia='").append(taglia).append('\'');
            sb.append(", sesso='").append(sesso).append('\'');
            sb.append(", eta=").append(eta);
            sb.append(", microchip=").append(microchip);
            sb.append(", statusRicovero=").append(statusRicovero);
            sb.append('}');
            return sb.toString();
        }

    }

    ```


# 1. Java Class - DatabaseProducer

- Serve ESCLUSIVAMENTE per restituire l’Entity Manager
- New > Java Class
- Modificare opportunamente l’unitName con quella **specificata nella traccia!**

```java
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Angiopasqui
 */

public class DatabaseProducer {
  @Produces
  @PersistenceContext(unitName="EsamePU")
  private EntityManager em;
}

```

- Ricordare che bisognerà modificare opportunamente anche il file **persistence.xml**

# 1. EJB Singleton - DatabasePopulator

- Ha il compito di popolare ed eliminare alcuni record dal database che utilizzeremo durante l’esecuzione
- Tasto destro sul progetto > New > Session Bean > Stateless
- Contiene la definizione del DataSource e dei parametri necessari per la connessione al database
- Modificare opportunamente l’unitName con quella **specificata nella traccia!**
- Esempio

    ```java
    package server;

    import javax.annotation.PostConstruct;
    import javax.annotation.sql.DataSourceDefinition;
    import javax.ejb.Singleton;
    import javax.ejb.Startup;
    import javax.persistence.EntityManager;
    import javax.persistence.PersistenceContext;

    @Startup
    // sotto dove c'è ( # ) significa che è specificato nella traccia 
    @DataSourceDefinition(
        className ="org.apache.derby.jdbc.EmbeddedDataSource",  //JDBC Connection Pool
        name ="java:global/jdbc/EsameDS", // # JDBC Resources>JNDI Name 'DataSource Name' 
        user ="APP", // necessario su VM è definito solo questo
        password ="APP",  // necessario su VM è definito solo questo
        databaseName ="EsameDB", // # Qualsiasi, viene creato on the go 
        properties = {"connectionAttributes=;create=true"} // IMPORTANTISSIMO
    )
    @Singleton
    public class DatabasePopulator {

        @PersistenceContext(unitName = "EsamePU")
        private EntityManager em;

        @PostConstruct
        public void init() {
            // Inizializzazione dell'archivio con alcuni dati di esempio
            em.persist(new Paziente("cane", "Fido", "media", "maschio", 5, 123456789L, false));
            em.persist(new Paziente("gatto", "Micia", "piccola", "femmina", 3, 0L, true));
            em.persist(new Paziente("coccodrillo", "Coco", "grande", "maschio", 10, 0L, false));
        }
    }

    ```

- Ricordare che bisognerà modificare opportunamente anche il file **persistence.xml**

# 1. Configurazione - persistence.xml

Tasto destro > New > Persistence Unit

![image.png](/classNotes/external/11_tutorial/image%206.png)

![image.png](/classNotes/external/11_tutorial/image%207.png)

- Scrivere la corretta Persistence Unit name
- Scrivere la corretta Data Source (specificata anche in DatabasePopulator)

**NOTA**: è possibile cliccare ‘**Source**’ invece di ‘**Design**’ per modificare direttamente il file xml (se ti piace)

![image.png](/classNotes/external/11_tutorial/image%208.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
  <persistence-unit name="EsamePU" transaction-type="JTA">
    <jta-data-source>java:global/jdbc/EsameDS</jta-data-source>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>
    <properties>
      <property name="javax.persistence.schema-generation.database.action" value="create"/>
      <property name="eclipselink.logging.level" value="FINE"/> <!-- Optional: for logging -->
    </properties>
  </persistence-unit>
</persistence>
```

## Nota:

Il Progetto lato Server potrebbe dirsi concluso per svolgere il primo punto del compito.

Non ci serve che creare l’ultimo Enterprise Java Bean che verrà chiamato dal client per eseguire alcune operazioni (coincidono solitamente con le query sull’entità e la manipolazione di questi dati).

**Segui i prossimi passi per capire come fare.**

# 1. EJB Stateless + Interfaccia Remota

Tasto destro sul progetto > New > Session Bean

![image.png](/classNotes/external/11_tutorial/image%209.png)

- Gli diamo un nome, ‘NewSessionBean’ ad esempio, lo inseriamo in un package. Questo package verrà clonato anche dal lato Client specificato nel ‘Remote in project’ dove avremo cura di indicare il progetto ‘Client’ creato all’inizio. Nel package del lato client si genererà un’interfaccia di questa classe Bean. Sono i metodi che ‘esporremo’ all’esterno dell’applicazione server.
- Esempio codice Server:

    ```java
    /*
     * Questo EJB si trova nel package server del progetto Server e creerà un'interfaccia
     * nel package server del progetto Client, di default col nome
     * <nomeDiQuestaClasse>Remote
     */
    package server;

    import java.util.List;
    import javax.ejb.Stateless;
    import javax.inject.Inject;
    import javax.persistence.EntityManager;

    @Stateless
    public class PazienteBean implements PazienteBeanRemote {

        @Inject
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
    ```
## 1. Riguardo alle query...
Alternativamente a definire le Query all'interno del Bean, la professoressa ci ha spiegato che preferisce avere la definizione delle query all'interno della classe entità a cui appartengono, e poi di creare le query aggiungendone gli eventuali parametri nel Bean.
<table>
<tr>
<th> Classe Entità </th>
<th> Classe Bean </th>
</tr>
<tr>
<td>

```java
// ... import librerie

@Entity
@NamedQueries({
        @NamedQuery(name = "findAll", query = "SELECT a FROM Animal a"),
        @NamedQuery(name = "findById", query = "SELECT a FROM Animal a WHERE a.id = :id"),
        @NamedQuery(name = "findByType", query = "SELECT a FROM Animal a WHERE a.type = :tipo"),
        @NamedQuery(name = "findByHospitalizationStatus", query = "SELECT a FROM Animal a WHERE a.hospitalized = :status"),
        @NamedQuery(name = "noMicrochip", query = "SELECT a FROM Animal a WHERE a.microchip = 0 OR a.microchip IS NULL")
})
public class Animal implements Serializable {
    // ... resto della classe
}

```

</td>
<td>

```java
// ... import librerie

@Stateless(name = "AnimalBean")
public class AnimalBean implements AnimalBeanRemote {

    @Inject
    private EntityManager em;

    public Animal searchById(int id) {
        return em.createNamedQuery("findById", Animal.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Animal> searchByType(String tipo) {
        return em.createNamedQuery("findByType", Animal.class)
                .setParameter("tipo", tipo)
                .getResultList();
    }

    public List<Animal> searchHospitalized(boolean status) {
        return em.createNamedQuery("findByHospitalizationStatus", Animal.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Animal> findAll() {
        return em.createNamedQuery("findAll", Animal.class)
                .getResultList();
    }

    public List<Animal> searchNoMicrochip() {
        return em.createNamedQuery("noMicrochip", Animal.class).getResultList();
    }
} 
```

</td>
</tr>
</table>

- Esempio codice Client:

    ```java
    /*
     * Auto-Generata, vanno solo specificati i metodi richiamabili dal client!
     * si trova nel package server del Client
     */
    package server;

    import java.util.List;
    import javax.ejb.Remote;

    @Remote
    public interface PazienteBeanRemote {
                Paziente trovaPerId(int id);

        List<Paziente> trovaPerTipo(String tipo);

        List<Paziente> trovaPerStatusRicovero(boolean status);

        List<Paziente> trovaTutti();

        List<Paziente> trovaSenzaMicrochip();
    }

    ```


## 1. beans.xml

- Se non modificassimo questo file di configurazione il Bean non potrebbe essere visto dall’esterno del progetto Server!
- Tasto destro sul progetto > New > beans.xml
- **Modificare  bean-discovery-mode su "all"**

![image.png](/classNotes/external/11_tutorial/image%2010.png)

# 1. Non ci resta che terminare il Client

- Abbiamo terminato il codice lato Server.
- **Ora dobbiamo importare l'entità dal Server ed inserirla nel Client**
- Non ci resta che invocare il nostro Bean con il Context Lookup ed eseguire le varie operazioni specificate nella traccia.
- Il formato del Context Lookup è così definito:
ctx.lookup("java:global/clinica_veterinaria_new/PazienteBean!server.PazienteBeanRemote");
    - java:global/     è necessario
    - clinica_veterinaria_new/     è il nome del progetto Server
    - PazienteBean!     è il nome del Bean presente sul progetto Server
    - server.PazienteBeanRemote"     è il percorso del Client che specifica dove si trova l’interfaccia di PazienteBean
- Utilizzare il bean per eseguire le operazioni
- Esempio

    ```java
    package clinica_veterinaria_client_new;

    import java.util.List;
    import javax.naming.Context;
    import javax.naming.InitialContext;
    import javax.naming.NamingException;
    import server.PazienteBeanRemote;
    import server.Paziente;

    public class Clinica_veterinaria_client_new {

        /**
         * @param args the command line arguments
         */
        public static void main(String[] args) {
            try {
                Context ctx;
                ctx= new InitialContext();
                PazienteBeanRemote pazienteBean = (PazienteBeanRemote)ctx.lookup("java:global/clinica_veterinaria_new/PazienteBean!server.PazienteBeanRemote");
                // Recupera la lista dei pazienti
                List<Paziente> pazienti = pazienteBean.trovaTutti();

                // Stampa la lista dei pazienti
                System.out.println("Lista dei pazienti:");
                for (Paziente paziente : pazienti) {
                    System.out.println(paziente);
                }

            } catch (NamingException e) {
                e.printStackTrace();
            }
        }

    }

    ```

# 2. Definire un Interceptor
Andiamo a definire una classe Java dal lato Server e utilizziamo gli appositi decoratori

```Java
package server;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Interceptor
public class MethodCallInterceptor {

    private static final Logger logger = Logger.getLogger(MethodCallInterceptor.class.getName());
    
    // Mappa per tenere traccia delle chiamate
    private static final Map<String, Integer> methodCallCount = new HashMap<>();

    @AroundInvoke
    public Object countMethodCalls(InvocationContext context) throws Exception {
        // Ottieni il nome del metodo chiamato
        String methodName = context.getMethod().getName();
        
        // Incrementa il conteggio per il metodo
        methodCallCount.put(methodName, methodCallCount.getOrDefault(methodName, 0) + 1);
        
        // Stampa il numero di invocazioni
        logger.info("Metodo " + methodName + " chiamato " + methodCallCount.get(methodName) + " volte.");
        
        // Continua con l'esecuzione del metodo originale
        return context.proceed();
    }
}
```
Successivamente, andiamo a decorare i metodi che ci interessano con ```Java @Interceptors(MethodCallInterceptor.class)```. È possibile applicarlo anche all'intera classe Bean, in modo tale che venga applicato di default a tutti i metodi come nell'esempio:
```Java
/// librerie...
@Interceptors(MethodCallInterceptor.class)
public class CircoloBean implements CircoloBeanRemote {
    /// resto del codice della classe
```
![alt text](external/10_4_tutorial/img.png)
![alt text](external/10_4_tutorial/img-7.png)


# 3. Sistema di messaggistica

- MessageWrapper è la 'classe fittizia' che contiene solo i dati 'minimi' che mandiamo per messaggio della classe del nostro programma, senza utilizzare l'intera classe che usiamo nel progetto.
    - deve implementare Serializable altrimenti non è inviabile tramite messaggi
    - essendo un oggetto 'prodotto' e 'consumato' tra un client e server, così come l'Entità, va inserito da entrambe le parti (client e server)

    ![alt text](external/10_4_tutorial/img-1.png)

- Questa classe si occupa di INVIARE il messaggio, utilizza un JMS (lato client)
    - Al posto di fare ```new MessageWrapper(...)``` andrebbe reso 'interattivo' e si dovrebbe chiedere all'utente quali siano i parametri da inserire con lo scanner... 
![alt text](external/10_4_tutorial/img-2.png)

- Nella RICEZIONE, abbiamo un MDB (lato server)
    - Implementa MessageListener
        - quindi dobbiamo definire onMessage(Message msg)
        - specifichiamo il topic
        - injectiamo l'EJB (per leggere/scrivere sul db)
        - injectiamo l'evento, se necessario (scritto nella traccia)
        - grazie all'EJB otteniamo l'oggetto per ID
        - eseguiamo le operazioni (in questo caso aggiornare facendo +pezziVenduti al numero delle vendite)
        - leggi 3.1 per il codice ↓
## 3.1 Eventi
- **SE DOVESSIMO MANDARE UN EVENTO** serve il metodo fire()
![alt text](external/10_4_tutorial/img-3.png)

L'evento invece è questo
![alt text](external/10_4_tutorial/img-4.png)

Dovremmo avere qualcosa di simile nel nostro progetto
![alt text](external/10_4_tutorial/img-5.png)

# 4. Web Services
- Un'entità deve essere serializzabile per essere trasferibile/utilizzabile come webService.
    - non è l'interfaccia Serializable!
    - **va trasmesso in formattazione xml!**
        - aggiungendo @XmlRootElement
        - possiamo definire sui singoli attributi della classe se sono obbligatori e possiamo rimapparli con un altro nome, come nell'esempio:

        ![alt text](external/10_4_tutorial/img-8.png)


    ![alt text](external/10_4_tutorial/img-6.png)
- l'EJB se è esposto va aggiunto il decoratore @WebService
    - Poi va dichiarato l'endpoint
    - ed implementato il codice deli metodi offerti dall'interfaccia che useremo

    ![alt text](external/10_4_tutorial/img-9.png)

## 4. Creazione progetto per Web Services
![alt text](external/10_4_tutorial/img-10.png)
![alt text](external/10_4_tutorial/img-11.png)
![alt text](external/10_4_tutorial/img-12.png)
![alt text](external/10_4_tutorial/img-13.png)
![alt text](external/10_4_tutorial/img-14.png)
![alt text](external/10_4_tutorial/img-15.png)
![alt text](external/10_4_tutorial/img-16.png)
![alt text](external/10_4_tutorial/img-17.png)
![alt text](external/10_4_tutorial/img-18.png)
![alt text](external/10_4_tutorial/img-19.png)
![alt text](external/10_4_tutorial/img-20.png)
## 4.1 Testing
![alt text](external/10_4_tutorial/img-21.png)
![alt text](external/10_4_tutorial/img-22.png)
![alt text](external/10_4_tutorial/img-23.png)
![alt text](external/10_4_tutorial/img-24.png)

## 4.2 Client Web
![alt text](external/10_4_tutorial/img-25.png)
![alt text](external/10_4_tutorial/img-26.png)
![alt text](external/10_4_tutorial/img-27.png)
![alt text](external/10_4_tutorial/img-28.png)
![alt text](external/10_4_tutorial/img-29.png)
![alt text](external/10_4_tutorial/img-30.png)
![alt text](external/10_4_tutorial/img-31.png)
![alt text](external/10_4_tutorial/img-32.png)
![alt text](external/10_4_tutorial/img-33.png)
![alt text](external/10_4_tutorial/img-34.png)
![alt text](external/10_4_tutorial/img-35.png)
![alt text](external/10_4_tutorial/img-36.png)
![alt text](external/10_4_tutorial/img-37.png)
![alt text](external/10_4_tutorial/img-38.png)

## 4.3 Altro esempio, riprendiamo CreditCardValidator
- avevamo creato nella lezione 12 un Web Service che esponeva dei metodi per controllare se le carte fossero valide
![alt text](external/10_4_tutorial/img-39.png)
![alt text](external/10_4_tutorial/img-40.png)
![alt text](external/10_4_tutorial/img-41.png)
![alt text](external/10_4_tutorial/img-42.png)
![alt text](external/10_4_tutorial/img-43.png)
![alt text](external/10_4_tutorial/img-44.png)
![alt text](external/10_4_tutorial/img-45.png)
![alt text](external/10_4_tutorial/img-46.png)
![alt text](external/10_4_tutorial/img-47.png)
![alt text](external/10_4_tutorial/img-48.png)
![alt text](external/10_4_tutorial/img-49.png)
![alt text](external/10_4_tutorial/img-50.png)
![alt text](external/10_4_tutorial/img-51.png)
![alt text](external/10_4_tutorial/img-52.png)
