public class TweetStore { private static final Logger LOGGER = TwitLogic . getLogger ( TweetStore . class ) ; private static final int DEFAULT_PORT = 8182 ; private final Sail sail ; private boolean doNotRefreshCoreMetadata = false ; private Repository repository ; private SesameManagerFactory elmoManagerFactory ; private boolean initialized = false ; private Factory < SailConnectionListener > sailConnectionListenerFactory ; final Set < TweetStoreConnection > openConnections ; private TwitterClient twitterClient ; private static TweetStore INSTANCE ; public static TweetStore getInstance ( ) { return INSTANCE ; } public TweetStore ( ) throws TweetStoreException { this ( createSail ( ) ) ; INSTANCE = this ; } public TweetStore ( final Sail sail ) { this . sail = sail ; openConnections = Collections . synchronizedSet ( new HashSet < TweetStoreConnection > ( ) ) ; } public TwitterClient getTwitterClient ( ) { return twitterClient ; } public void initialize ( ) throws TweetStoreException { if ( initialized ) { throw new IllegalStateException ( "store has already been initialized" ) ; } LOGGER . info ( "initializing TwitLogic local store" ) ; repository = new SailRepository ( sail ) ; if ( ! doNotRefreshCoreMetadata ) { refreshCoreMetadata ( repository ) ; } ElmoModule adminElmoModule = new ElmoModule ( ) ; adminElmoModule . setGraph ( null ) ; adminElmoModule . addConcept ( Thing . class ) ; adminElmoModule . addConcept ( ObjectProperty . class ) ; adminElmoModule . addConcept ( AdministrativeDivision . class ) ; adminElmoModule . addConcept ( Agent . class ) ; adminElmoModule . addConcept ( City . class ) ; adminElmoModule . addConcept ( Country . class ) ; adminElmoModule . addConcept ( Document . class ) ; adminElmoModule . addConcept ( Feature . class ) ; adminElmoModule . addConcept ( Graph . class ) ; adminElmoModule . addConcept ( Image . class ) ; adminElmoModule . addConcept ( MicroblogPost . class ) ; adminElmoModule . addConcept ( Neighborhood . class ) ; adminElmoModule . addConcept ( Point . class ) ; adminElmoModule . addConcept ( PointOfInterest . class ) ; adminElmoModule . addConcept ( SpatialThing . class ) ; adminElmoModule . addConcept ( UserAccount . class ) ; adminElmoModule . addConcept ( org . openrdf . concepts . rdfs . Class . class ) ; elmoManagerFactory = new SesameManagerFactory ( adminElmoModule , repository ) ; elmoManagerFactory . setQueryLanguage ( QueryLanguage . SPARQL ) ; elmoManagerFactory . setInferencingEnabled ( false ) ; addPeriodicDump ( ) ; initialized = true ; } private void addPeriodicDump ( ) throws TweetStoreException { TypedProperties conf = TwitLogic . getConfiguration ( ) ; try { File file = conf . getFile ( TwitLogic . DUMP_FILE , null ) ; if ( null == file ) { LOGGER . info ( "no dump file specified. Periodic data dumps will not be generated." ) ; } else { long interval = conf . getLong ( TwitLogic . DUMP_INTERVAL , - 1 ) ; if ( - 1 == interval ) { LOGGER . warning ( "no dump interval specified. Periodic data dumps will not be generated." ) ; } else { boolean compressed = false ; String s = file . getName ( ) ; if ( s . endsWith ( ".gz" ) ) { compressed = true ; s = s . substring ( 0 , s . length ( ) - ".gz" . length ( ) ) ; } int i = s . lastIndexOf ( '.' ) ; if ( i <= 0 ) { LOGGER . warning ( "dump file name could not be parsed. Periodic data dumps will not be generated." ) ; } else { String ext = s . substring ( i + 1 ) ; RDFFormat format = SesameTools . rdfFormatByExtension ( ext ) ; if ( null == format ) { LOGGER . warning ( "dump file format not recognized. Periodic data dumps will not be generated." ) ; } else { new Timer ( ) . schedule ( new DumpFileGeneratorTask ( this , file , format , compressed ) , interval , interval ) ; } } } } } catch ( PropertyException e ) { throw new TweetStoreException ( e ) ; } } public TweetStoreConnection createConnection ( ) throws TweetStoreException { return new TweetStoreConnection ( this , sailConnectionListenerFactory ) ; } void notifyClosed ( final TweetStoreConnection c ) { openConnections . remove ( c ) ; } public Sail getSail ( ) { if ( ! initialized ) { throw new IllegalStateException ( "not yet initialized" ) ; } return sail ; } public Repository getRepository ( ) { if ( ! initialized ) { throw new IllegalStateException ( "not yet initialized" ) ; } return repository ; } public ElmoManagerFactory getElmoManagerFactory ( ) { return elmoManagerFactory ; } public void shutDown ( ) throws TweetStoreException { if ( ! initialized ) { throw new IllegalStateException ( "not yet initialized" ) ; } LOGGER . info ( "shutting down TwitLogic local store" ) ; Collection < TweetStoreConnection > cons = new LinkedList < TweetStoreConnection > ( ) ; cons . addAll ( openConnections ) ; for ( TweetStoreConnection c : cons ) { c . close ( ) ; } LOGGER . info ( "shutting down triple store" ) ; try { sail . shutDown ( ) ; } catch ( SailException e ) { throw new TweetStoreException ( e ) ; } LOGGER . info ( "done with shutdown" ) ; } public void dump ( final OutputStream out ) throws RepositoryException , RDFHandlerException { RDFFormat format = RDFFormat . TRIG ; LOGGER . info ( "dumping triple store in format " + format . getName ( ) + " to output stream" ) ; RDFHandler h = Rio . createWriter ( format , out ) ; RepositoryConnection rc = getRepository ( ) . getConnection ( ) ; try { rc . begin ( ) ; rc . export ( h ) ; } finally { rc . rollback ( ) ; rc . close ( ) ; } } public void dumpToFile ( final File file , final RDFFormat format ) throws IOException , RepositoryException , RDFHandlerException { LOGGER . info ( "dumping triple store in format " + format . getName ( ) + " to file: " + file ) ; OutputStream out = new FileOutputStream ( file ) ; try { RDFHandler h = Rio . createWriter ( format , out ) ; RepositoryConnection rc = getRepository ( ) . getConnection ( ) ; try { rc . export ( h ) ; } finally { rc . close ( ) ; } } finally { out . close ( ) ; } } public void dumpToCompressedFile ( final File file , final RDFFormat format ) throws IOException , RepositoryException , RDFHandlerException { LOGGER . info ( "dumping compressed triple store in format " + format . getName ( ) + " to file: " + file ) ; OutputStream out = new FileOutputStream ( file ) ; try { OutputStream gzipOut = new GZIPOutputStream ( out ) ; try { RDFHandler h = Rio . createWriter ( format , gzipOut ) ; RepositoryConnection rc = getRepository ( ) . getConnection ( ) ; try { rc . export ( h ) ; } finally { rc . close ( ) ; } } finally { gzipOut . close ( ) ; } } finally { out . close ( ) ; } } public void clear ( ) throws TweetStoreException { try { RepositoryConnection rc = repository . getConnection ( ) ; try { rc . begin ( ) ; rc . clear ( ) ; rc . commit ( ) ; } finally { rc . rollback ( ) ; rc . close ( ) ; } } catch ( RepositoryException e ) { throw new TweetStoreException ( e ) ; } } public void load ( final File file , final RDFFormat format ) throws TweetStoreException { try { RepositoryConnection rc = repository . getConnection ( ) ; try { rc . begin ( ) ; try { rc . add ( file , "http://example.org/baseURI" , format ) ; } catch ( IOException e ) { throw new TweetStoreException ( e ) ; } catch ( RDFParseException e ) { throw new TweetStoreException ( e ) ; } rc . commit ( ) ; } finally { rc . rollback ( ) ; rc . close ( ) ; } } catch ( RepositoryException e ) { throw new TweetStoreException ( e ) ; } } public static Sail createSail ( ) throws TweetStoreException { TypedProperties props = TwitLogic . getConfiguration ( ) ; String sailType ; try { sailType = props . getString ( TwitLogic . SAIL_CLASS ) ; } catch ( PropertyException e ) { throw new TweetStoreException ( e ) ; } System . out . println ( "creating Sail of type: " + sailType ) ; SailFactory factory ; if ( sailType . equals ( MemoryStore . class . getName ( ) ) ) { factory = new MemoryStoreFactory ( props ) ; } else if ( sailType . equals ( NativeStore . class . getName ( ) ) ) { factory = new NativeStoreFactory ( props ) ; } else if ( sailType . equals ( "com.knowledgereefsystems.agsail.AllegroSail" ) ) { factory = new AGRepositorySailFactory ( props , false ) ; } else if ( sailType . equals ( "com.tinkerpop.blueprints.pgm.oupls.sail.GraphSail" ) ) { factory = new Neo4jSailFactory ( props ) ; } else { throw new TweetStoreException ( "unhandled Sail type: " + sailType ) ; } try { return factory . makeSail ( ) ; } catch ( SailException e ) { throw new TweetStoreException ( e ) ; } catch ( PropertyException e ) { throw new TweetStoreException ( e ) ; } } private void refreshCoreMetadata ( final Repository repository ) throws TweetStoreException { LOGGER . info ( "adding/refreshing core metadata" ) ; try { RepositoryConnection rc = repository . getConnection ( ) ; try { rc . begin ( ) ; rc . remove ( ( Resource ) null , null , null , TwitLogic . CORE_GRAPH ) ; rc . clearNamespaces ( ) ; String baseURI = "http://example.org/baseURI/" ; rc . add ( TwitLogic . class . getResourceAsStream ( "namespaces.ttl" ) , baseURI , RDFFormat . TURTLE , TwitLogic . CORE_GRAPH ) ; rc . add ( TwitLogic . class . getResourceAsStream ( "twitlogic-void.ttl" ) , baseURI , RDFFormat . TURTLE , TwitLogic . CORE_GRAPH ) ; rc . add ( TwitLogic . class . getResourceAsStream ( "twitterplaces.ttl" ) , baseURI , RDFFormat . TURTLE , TwitLogic . CORE_GRAPH ) ; rc . commit ( ) ; } finally { rc . rollback ( ) ; rc . close ( ) ; } } catch ( IOException e ) { throw new TweetStoreException ( e ) ; } catch ( RDFParseException e ) { throw new TweetStoreException ( e ) ; } catch ( RepositoryException e ) { throw new TweetStoreException ( e ) ; } } public void doNotRefreshCoreMetadata ( ) { this . doNotRefreshCoreMetadata = true ; } public void setSailConnectionListenerFactory ( Factory < SailConnectionListener > sailConnectionListenerFactory ) { this . sailConnectionListenerFactory = sailConnectionListenerFactory ; } public void startServer ( final TwitterClient client ) throws ServerException { twitterClient = client ; try { String internalBaseURI = TwitLogic . getConfiguration ( ) . getURI ( TwitLogic . SERVER_BASEURI ) . toString ( ) ; String externalBaseURI = TwitLogic . BASE_URI ; final String datasetURI = TwitLogic . TWITLOGIC_DATASET ; int port = TwitLogic . getConfiguration ( ) . getInt ( TwitLogic . SERVER_PORT , DEFAULT_PORT ) ; File staticContentDir = TwitLogic . getConfiguration ( ) . getFile ( TwitLogic . SERVER_STATICCONTENTDIRECTORY ) ; LinkedDataServer server = new LinkedDataServer ( this . getSail ( ) , internalBaseURI , externalBaseURI , datasetURI ) ; Component component = new Component ( ) ; server . setInboundRoot ( component ) ; component . getServers ( ) . add ( Protocol . HTTP , port ) ; component . getServers ( ) . add ( Protocol . FILE , port ) ; component . getDefaultHost ( ) . attach ( "/" , new Directory ( server . getContext ( ) , "file://" + staticContentDir + "/" ) ) ; for ( TwitLogic . ResourceType t : TwitLogic . ResourceType . values ( ) ) { String p = t . getUriPath ( ) ; if ( ! p . equals ( "graph" ) && ! p . equals ( "person" ) ) { component . getDefaultHost ( ) . attach ( "/" + p + "/" , WebResource . class ) ; } } component . getDefaultHost ( ) . attach ( "/person/twitter/" , PersonResource . class ) ; component . getDefaultHost ( ) . attach ( "/graph/" , GraphResource . class ) ; component . getDefaultHost ( ) . attach ( "/sparql" , new SparqlResource ( ) ) ; component . getDefaultHost ( ) . attach ( "/stream/relatedTweets" , new RelatedTweetsResource ( ) ) ; component . getDefaultHost ( ) . attach ( "/stream/relatedTags" , new RelatedHashtagsResource ( ) ) ; server . start ( ) ; } catch ( Throwable e ) { throw new ServerException ( e ) ; } } } 