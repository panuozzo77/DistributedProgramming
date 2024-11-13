[Lezion10.pdf](/slides/10_1_EJB_1_NEW.pdf)
# Enterprise Java Beans

## Il ruolo degli EJB
- Il layer di persistenza rende facilmente gestibile la memorizzazione, ma non è adatto per il business Processing
- User Interfaces, allo stesso modo, non sono adatte per eseguire logica di business.
- La logica di business ha bisogno di un layer dedicato per le caratteristiche proprie.
<br><br>
- JPA (Data Layer) ha modellato i 'sostantivi' della nostra architettura, mentre EJB (Business Layer) modella 'i verbi'
- Il Business Layer ha il compito di:
  - gestire transazioni in sicurezza;
  - gestire comunicazione con componenti esterne ed interne all'architettura
    - interagire coi servizi esterni (SOAP e RESTful web services)
    - inviare messaggi asincroni (con JMS)
    - orchestrare componenti del DB verso sistemi esterni
    - servire il layer di presentazione
  - Tipi di EJB:
    - Stateless
    - Stateful
    - Singleton

## Servizi Forniti dai Container
- Comunicazione remota: client EJB possono invocare metodi remoti per mezzo di protocolli standard
- Iniezione di dipendenze JMS destination e factories, datasources, altri EJB, come pure altri POJO
- Gestione dello stato (per gli stateful)
- Pooling (efficienza, per gli stateless): creazione di un pool di istanze che possono essere condivise tra client multipli
- Ciclo di vita
- Gestione dei messaggi JMS
- Transazioni
- Sicurezza
- Concorrenza
- Interceptor e metodi
- Invocazione asincrona (senza messaggi)

### Interazione con il Container
- Fatto il deployment, il container offre i servizi, il programmatore si concentra sulla logica di business
- Gli EJB sono oggetti managed
- Quando un client invoca un metodo di un EJB, in effetti, invoca un proxy su di esso, frapposto dal container (che lo usa per fornire i servizi)
  - chiamata intercettata dal container, in maniera totalmente trasparente al client
- Specifiche EJB Lite permettono di implementare solo una parte delle specifiche
  - per permetter implementazioni non impegnative per i container provider.

# Tipi di EJBs
- Stateless
  - il session bean non contiene conversational state tra i metodi ed ogni istanza può essere usata da ogni client
- Stateful
  - il session bean contiene un conversational state che deve essere mantenuto attraverso i metodi per ogni singolo utente
- Singleton
  - un session bean è condiviso tra vari client e supporta accessi concorrenti
  - un container deve assicurare che esista una sola istanza per l'intera applicazione

```Java
// un EJB Stateless
@Stateless
public class BoojEJB {
    @PersistenceContext(unitName = "chapter07PU")
    private EntityManager em;
    
    public Book findBookById(Long id){
        return em.find(Book.class, id)
    }
    
    public Book createBook(Book book){
        em.persist(book);
        return book;
    }
}
```

### Anatomia di un EJB
- Un EJB si compone dei seguenti elementi:
  - **una classe**: annotata con @Stateless, @Stateful o @Singleton
  - **interfaccia di business**: locale, remota o nessuna (accesso locale - invocando la classe bean stessa, client ed EJB nello stesso package)

## Caratteristiche della classe Bean
- Una classe session Bean è una classe Java standard che implementa la logica di business:
  - I requirements per implementare un session bean sono i seguenti:
    - annotata con @Stateless, @Stateful o @Singleton o equivalente descriptor nell'XML
  - Implementare i metodi delle interfacce (se esistono)
  - Essere Public e non Final o Abstract
  - Costruttore pubblico senza parametri
  - Nessun metodo finalize()
  - I metodi non possono iniziare per ejb e non possono essere final o static
  - Argomenti e valori di ritorno devono essere tipi legali per RMI

## Remote, Local e No-Interface Views
- A seconda di __dove un client invoca un session bean__ il bean deve implementare un'interfaccia remota locale o una no-interface view
- Se l'architettura ha client che risiedono all'**esterno** dell'EJB container's JVM Instance, deve usare l'interfaccia remota.
- Si verifica per client in esecuzione:
  - su una JVM separata (o rich client)
  - in un application client container (ACC)
  - in un external web o EJB container
- In questo caso i client invocheranno i metodi del bean attraverso Remote Method Invocation (RMI)
<br><br>
- Si possono usare invocazioni locali quando i bean sono in esecuzione nella stessa JVM
  - Un EJB che invoca un altro EJB o una web component (Servlet, JSF) in esecuzione in un web container nella stessa JVM
- È possibile usare sia chiamate locali che remote sullo stesso session bean
<br><br>
- Un session bean può implementare diverse interfacce o nessuna
- Le annotazioni:
  - @Remote: denota una remote business interface
    - **Method parameters passati per valori e serializzati** essendo parte del protocollo RMI
  - @Local: denota una classe local business interface
  - **Method parameters passati per riferimento** dal client al bean
<br><br>
- No-Interface view
  - La vista senza interfaccia è una variante della vista locale (Local Interface) che espone tutti i metodi pubblici di business della classe bean localmente, senza l'utilizzo di un'interfaccia separata

## Esempio di utilizzo

```Java
@Local
public interface ItemLocal {
    List<Book> findBooks();
    List<CD> findCDs();
    //...
}
```

```Java
@Remote
public interface ItemRemote {
  List<Book> findBooks();
  List<CD> findCDs();
  Book createBook(Book book);
  CD createCD(CD cd);
  //...
}
```

```Java
// Utilizzo delle interfacce precedentemente annotate
@Stateless
public class ItemEJB implements ItemLocal, ItemRemote {
    //...
}
```

## Metodo alternativo per interfacce Legacy

```java
public interface ItemLocal {
    List<Book> findBooks();
    List<CD> findCDs();
    //...
}
```
```java
public interface ItemRemote {
  List<Book> findBooks();
  List<CD> findCDs();
  Book createBook(Book book);
  CD createCD(CD cd);
  //...
}
```

```java
@Stateless
@Remote(ItemRemote.class)
@Local(ItemLocal.class)
@LocalBean // ha una no-view interface
public static ItemEJB implements ItemLocal, ItemRemote {
    //...
}
```

## EJB e JNDI
- Alla creazione di un EJB viene creato un nome Java Naming and Directory Interface (JNDI)
- **java:<scope>[/<app-name>]/<module-name>/<bean-name>[!<FQ-interface-name>]**
  - **scope**:
    - global: permette l'esecuzione fuori l'applicazione Java EE per un accesso al namespace globale
      - accesso a bean remoti attraverso JNDI lookups
    - app: permette l'esecuzione della componente l'applicazione Java EE per un accesso al namespace dell'applicazione
      - all'interno della stessa applicazione
    - module: permette ad una componente l'esecuzione in l'accesso a namespace con moduli specifici
      - all'interno dello stesso modulo
  - **app-name** : richiesto solo se il bean viene packaged in un file .ear o .war
  - **module-name** : nome del modulo in cui il session bean è impacchettato
  - **bean-name** : nome del session bean
  - **fully-qualified-interface-name** : fully qualified name di ogni interfaccia definita

# Stateless Bean
- Il tipo più semplice e popolare di EJB
  - quello dove la gestione del container è più efficiente (pooling)
- Stateless: un task viene completato in una singola invocazione di un metodo (nessuna memoria di precedenti interazioni)
- Il container mantiene un pool di EJB stateless dello stesso tipo, che vengono assegnati a chi li richiede per la durata dell'esecuzione, per poi tornare disponibili
- Un piccolo numero di EJB può servire molti client (e il container può gestire tutto il loro ciclo di vita in maniera autonoma)

```java
@Stateless //Annotazione 
public class ItemEJB {  //nome del bean
@PersistenceContext(unitName ="chapter07PU") //iniezione dell'EM
  private EntityManager em;

  public List<Book> findBooks() { //esegue Named Query sui libri
    TypedQuery<Book> query = em.createNamedQuery(Book.FIND_ALL, Book.class);
    return query.getResultList();
  }

  public List<CD> findCDs() { //esegue Named Query sui CD
    TypedQuery<CD> query = em.createNamedQuery(CD.FIND_ALL, CD.class);
    return query.getResultList();
  }
  public Book createBook(Book book) { //crea libro
    em.persist(book);
    return book;
  }

  public CD createCD(CD cd) {  //crea CD
    em.persist(cd);
    return cd;
  }
}
```

# Stateful Beans
- EJB stateless non mantengono stato con i client: 
  - ogni client è come se fosse “nuovo” per loro EJB stateful mantengono lo stato della conversazione (esempio: il carrello degli
  acquisti in un negozio di e-commerce)
- Relazione 1-1 con il numero di client: tanti client, tanti EJB (e tanto carico!)
- Per ridurre il carico, tecniche di attivazione e passivazione permettono di serializzare l’EJB su memoria di massa . . . 
  - e riportarlo attivo quando serve 
  - Fatto automaticamente dal container, che così
  permette la scalabilità automaticamente

```java
@Stateful //Annotazione
@StatefulTimeout(value = 20, unit = TimeUnit.SECONDS) //tempo consentino per rimanere in idle
public class ShoppingCartEJB {
    
  private List<Item> cartItems = new ArrayList<>(); //struttura dati che mantiene lo stato
  
  public void addItem(Item item) {
      if (!cartItems.contains(item))
          cartItems.add(item);
  }
  public void removeItem(Item item) {
    if(cartItems.contains(item))
        cartItems.remove(item);
  }
  
  public Integer getNumberOfItems() {
    if(cartItems == null|| cartItems.isEmpty())
        return 0;
    return cartItems.size();
  }
  
  public Float getTotal(){
    if(cartItems == null|| cartItems.isEmpty())
      return 0f;
    Float total = 0f;
    for(Item cartItem : cartItems) { 
        total += (cartItem.getPrice());
    }
    return total;
  }
  
  public void empty() {
      cartItems.clear();
  }
  
  @Remove
  public void checkout() { //metodo usato alla distruzione oppure post-ordinazione
    // Do some business logic shit [salvare lo stato del carrello o cose simili]
    cartItems.clear();
  }
}
//...
```

# Design Pattern Singleton
- Session bean istanziato una sola volta per applicazione
- Utile in alcuni contesti
  - se si vuole gestire una cache di oggetti (hashmap) per tutta l'applicazione
<br><br>
- In un'__application managed environment__ bisogna fare diverse modifiche per trasformare un POJO in un bean singleton:
  - Prevenire che qualcuno crei altre cache
    - usando un costruttore privato
  - Per ottenere un'istanza, si deve avere un metodo sincronizzato che permette di ottenere la cache
    - getInstance() restituisce una singola istanza della classe CacheSingleton class
  - Se un client vuole aggiungere un oggetto alla cache deve invocare
    - CacheSingleton.getInstance().addToCache(the_object)
```java
//Esempio di POJO che applica il design pattern Singleton
public class Cache {
  private static Cache instance = new Cache(); //istanza privata e static, inizializzata thread-safe a caricamento della classe nella JVM
  private Map<Long, Object> cache = new HashMap<>();
  
  private Cache() {} //nessun altro può istanziare un'altra cache, il costruttore è privato

  public static synchronized Cache getInstance() { //restituisce l'istanza in maniera sincronizzata
      return instance;
  }
  
  public void addToCache(Long id, Object object) {
      if(!cache.containsKey(id))
          cache.put(id, object);
  }
  
  public void removeFromCache(Long id) {
      if(cache.containsKey(id))
          cache.remove(id);
  }
  
  public Object getFromCache(Long id) {
      if (cache.containsKey(id))
          return cache.get(id);
      else
          return null;
  }
}
```

### Rendere un POJO un EJB Singleton
```java
@Singleton
public class CacheEJB {
  private Map<Long, Object> cache = new HashMap<>();

  public void addToCache(Long id, Object object) {
    if(!cache.containsKey(id))
      cache.put(id, object);
  }

  public void removeFromCache(Long id) {
    if(cache.containsKey(id))
      cache.remove(id);
  }

  public Object getFromCache(Long id) {
    if (cache.containsKey(id))
      return cache.get(id);
    else
      return null;
  }
}
```

## **EJB sul Server**
- Necessario il packaging per porre gli EJB in un container
- Necessario mettere insieme: classi EJB, interfacce EJB, superclassi/interfacce, eccezioni, classi ausiliarie ed un deployment-descriptor
- Il file si chiama: Enterprise Archive (EAR)
- Un file EAR raggruppa in maniera coerente EJB che hanno necessità di essere deployati insieme
<br><br>
- Il client di un EJB può essere di diversi tipi: un POJO, un client grafico, un CDI managed Bean, una Servlet, un Bean JSF, un Web Service (SOAP o REST) o altro EJB.
- Il client NON può fare new() su un EJB. Ha bisogno di un riferimento, che può essere:
  - iniettato via @EJB o @Inject
  - acceduto via un lookup JNDI
### Invocazione **con Iniezione del Riferimento**
- Se il Bean è del tipo 'no-interface' allora il client deve solo ottenere un riferimento alla classe bean stessa
- attraverso l'annotazione @EJB
```java
// server:
@Stateless
public class ItemEJB {
    // implementazione
}

// client:
@EJB ItemEJB itemEJB;
```

```java
// server: 
@Stateless 
@Remote(ItemRemote.class)
@Local(ItemLocal.class)
@LocalBean
public class ItemEJB implements ItemLocal, ItemRemote {
    // implementazione
}

// client: 
@EJB ItemEJB itemEJB;
@EJB ItemLocal itemEJBLocal;
@EJB ItemRemote itemEJBRemote;
```

- se l'EJB è remoto, si può specificare il nome JNDI
- l'@EJB API ha diversi attributi, tra cui il nome JNDI dell'EJB che vogliamo iniettare
- Utile per remote EJBs in esecuzione su un server differente
```java
//...
@EJB(lookup='java:global/classes/ItemEJB') ItemRemote itemEJBRemote;
```

- sarebbe possibile usare @Inject generica di CDI al posto di @EJB ma non si può passare la stringa di lookup. Bisogna produrre il remote EJB da iniettare:
```java
// codice che produce una remote EJB
@Produces @EJB(lookup='java:global/classes/ItemEJB') ItemRemote itemEJBRemote;

// codice client che inietta l'EJB remoto prodotto
@Inject ItemRemote itemEJBRemote;
```

## Invocazione **diretta di JNDI**
- JNDI è usato di solito per accesso remoto
- Ma anche per accesso locale: in questa maniera si evita il costoso resource injection
  - si chiedono dati quando servono, invece di farcene fare il push anche se poi non dovessero servire
- Si usa il contesto iniziale di JNDI (settabile da parametri su linea di comando) per effettuare la query con il nome globale standard JNDI

```java
import javax.naming.InitialContext;

Context ctx = new InitialContext();
ItemRemote = itemEJB = (ItemRemote) ctx.lookup('java:global/cdbookstore/ItemEJB!org.agoncal.book.javaee7.ItemRemote');
```