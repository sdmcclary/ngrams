<<<<<<< HEAD
public class HBaseManager { private HBaseAdmin _hbaseAdmin ; private String schemaFileName = null ; private String configName = null ; private boolean verbose = false ; private int configNum = - 1 ; private XMLConfiguration config = null ; private String configBaseKey = null ; private ArrayList < TableSchema > schemas = null ; private HTableDescriptor [ ] remoteTables = null ; private CommandLine cmd = null ; private String quorum = null ; private String zkPort = null ; public HBaseManager ( final String [ ] args ) throws ParseException , ConfigurationException { parseArgs ( args ) ; verbose = cmd . hasOption ( "v" ) ; boolean createConfig = cmd . hasOption ( "c" ) ; if ( ! createConfig ) { readConfiguration ( ) ; } else { final String [ ] rem = cmd . getArgs ( ) ; schemaFileName = rem [ 0 ] ; if ( verbose ) { System . out . println ( "schema filename: " + schemaFileName ) ; } configName = rem . length > 1 ? rem [ 1 ] : "new" ; if ( verbose ) { System . out . println ( "configuration used: " + configName ) ; } quorum = cmd . getOptionValue ( "q" ) ; if ( cmd . hasOption ( "p" ) ) { zkPort = cmd . getOptionValue ( "p" ) ; } if ( quorum == null ) { System . err . println ( "ERROR: zookeeper quorum not specified, use -q option." ) ; System . exit ( - 9 ) ; } } } private void readConfiguration ( ) throws ConfigurationException { final String [ ] rem = cmd . getArgs ( ) ; schemaFileName = rem [ 0 ] ; if ( verbose ) { System . out . println ( "schema filename: " + schemaFileName ) ; } configName = rem . length > 1 ? rem [ 1 ] : null ; if ( verbose ) { System . out . println ( "configuration used: " + ( configName != null ? configName : "default" ) ) ; } config = new XMLConfiguration ( schemaFileName ) ; configNum = getConfigurationNumber ( config , configName ) ; if ( verbose ) { System . out . println ( "using config number: " + ( configNum == - 1 ? "default" : configNum ) ) ; } configBaseKey = configNum == - 1 ? "configuration." : "configuration(" + configNum + ")." ; schemas = new ArrayList < TableSchema > ( ) ; readTableSchemas ( ) ; if ( verbose ) { System . out . println ( "table schemas read from config: \n  " + schemas ) ; } } private void parseArgs ( final String [ ] args ) throws ParseException { final Options options = new Options ( ) ; options . addOption ( "l" , "list" , false , "lists all tables but performs no further action." ) ; options . addOption ( "n" , "dryrun" , false , "do not create or change tables, just print out actions." ) ; options . addOption ( "c" , "create-config" , false , "creates a config from the tables." ) ; options . addOption ( "q" , "quorum" , true , "the list of quorum servers, e.g. \"foo.com,bar.com\"" ) ; options . addOption ( "p" , "client-port" , true , "the zookeeper client port to use, default: 2181" ) ; options . addOption ( "v" , "verbose" , false , "print verbose output." ) ; if ( args . length == 0 ) { final HelpFormatter formatter = new HelpFormatter ( ) ; formatter . printHelp ( "HBaseManager [<options>] [<schema-xml-filename>] [<config-name>]" , options ) ; System . exit ( - 1 ) ; } final CommandLineParser parser = new PosixParser ( ) ; cmd = parser . parse ( options , args ) ; } private void readTableSchemas ( ) { final int maxTables = config . getMaxIndex ( configBaseKey + "schema.table" ) ; for ( int t = 0 ; t <= maxTables ; t ++ ) { final String base = configBaseKey + "schema.table(" + t + ")." ; final TableSchema ts = new TableSchema ( ) ; ts . setName ( config . getString ( base + "name" ) ) ; if ( config . containsKey ( base + "description" ) ) { ts . setDescription ( config . getString ( base + "description" ) ) ; } if ( config . containsKey ( base + "deferred_log_flush" ) ) { ts . setDeferredLogFlush ( config . getBoolean ( base + "deferred_log_flush" ) ) ; } if ( config . containsKey ( base + "max_file_size" ) ) { ts . setMaxFileSize ( config . getLong ( base + "max_file_size" ) ) ; } if ( config . containsKey ( base + "memstore_flush_size" ) ) { ts . setMemStoreFlushSize ( config . getLong ( base + "memstore_flush_size" ) ) ; } if ( config . containsKey ( base + "read_only" ) ) { ts . setReadOnly ( config . getBoolean ( base + "read_only" ) ) ; } final int maxCols = config . getMaxIndex ( base + "column_family" ) ; for ( int c = 0 ; c <= maxCols ; c ++ ) { final String base2 = base + "column_family(" + c + ")." ; final ColumnDefinition cd = new ColumnDefinition ( ) ; cd . setName ( config . getString ( base2 + "name" ) ) ; cd . setDescription ( config . getString ( base2 + "description" ) ) ; String val = config . getString ( base2 + "max_versions" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setMaxVersions ( Integer . parseInt ( val ) ) ; } val = config . getString ( base2 + "compression" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setCompression ( val ) ; } val = config . getString ( base2 + "in_memory" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setInMemory ( Boolean . parseBoolean ( val ) ) ; } val = config . getString ( base2 + "block_cache_enabled" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setBlockCacheEnabled ( Boolean . parseBoolean ( val ) ) ; } val = config . getString ( base2 + "block_size" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setBlockSize ( Integer . parseInt ( val ) ) ; } val = config . getString ( base2 + "time_to_live" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setTimeToLive ( Integer . parseInt ( val ) ) ; } val = config . getString ( base2 + "bloom_filter" ) ; if ( val != null && val . length ( ) > 0 ) { cd . setBloomFilter ( val ) ; } val = config . getString ( base2 + "replication_scope" ) ; if ( val != null && val . length ( ) > 0 ) { System . err . println ( "WARN: cannot set replication scope!" ) ; } ts . addColumn ( cd ) ; } schemas . add ( ts ) ; } } private void process ( ) throws IOException { _hbaseAdmin = new HBaseAdmin ( getConfiguration ( ) ) ; if ( cmd . hasOption ( "l" ) ) { listTables ( ) ; } else if ( cmd . hasOption ( "c" ) ) { createConfiguration ( ) ; } else { for ( final TableSchema schema : schemas ) { createOrChangeTable ( schema , ! cmd . hasOption ( "n" ) ) ; } } } private void listTables ( ) throws IOException { getTables ( true ) ; System . out . println ( "tables found: " + remoteTables . length ) ; for ( final HTableDescriptor d : remoteTables ) { System . out . println ( "  " + d . getNameAsString ( ) ) ; } } private void createConfiguration ( ) throws IOException { if ( verbose ) { System . out . println ( "creating configuration..." ) ; } OutputStream out = "-" . equals ( schemaFileName ) ? System . out : new FileOutputStream ( schemaFileName ) ; PrintStream w = new PrintStream ( new BufferedOutputStream ( out ) , false , "UTF-8" ) ; w . println ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ) ; w . println ( "<configurations>" ) ; w . println ( "  <configuration>" ) ; w . println ( "    <name>" + configName + "</name>" ) ; w . println ( "    <zookeeper_quorum>" + quorum + "</zookeeper_quorum>" ) ; if ( zkPort != null ) { w . println ( "    <zookeeper_client_port>" + zkPort + "</zookeeper_client_port>" ) ; } w . println ( "    <schema>" ) ; getTables ( true ) ; System . out . println ( "tables found: " + remoteTables . length ) ; for ( final HTableDescriptor d : remoteTables ) { w . println ( "      <table>" ) ; w . println ( "        <name>" + d . getNameAsString ( ) + "</name>" ) ; w . println ( "        <!-- Default: " + HTableDescriptor . DEFAULT_DEFERRED_LOG_FLUSH + " -->" ) ; w . println ( "        <deferred_log_flush>" + d . isDeferredLogFlush ( ) + "</deferred_log_flush>" ) ; w . println ( "        <!-- Default: " + HTableDescriptor . DEFAULT_MAX_FILESIZE + " -->" ) ; w . println ( "        <max_file_size>" + d . getMaxFileSize ( ) + "</max_file_size>" ) ; w . println ( "        <!-- Default: " + HTableDescriptor . DEFAULT_MEMSTORE_FLUSH_SIZE + " -->" ) ; w . println ( "        <memstore_flush_size>" + d . getMemStoreFlushSize ( ) + "</memstore_flush_size>" ) ; w . println ( "        <!-- Default: false -->" ) ; w . println ( "        <read_only>" + d . isReadOnly ( ) + "</read_only>" ) ; for ( HColumnDescriptor col : d . getColumnFamilies ( ) ) { w . println ( "        <column_family>" ) ; w . println ( "          <name>" + col . getNameAsString ( ) + "</name>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_VERSIONS + " -->" ) ; w . println ( "          <max_versions>" + col . getMaxVersions ( ) + "</max_versions>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_COMPRESSION + " -->" ) ; w . println ( "          <compression>" + col . getCompressionType ( ) + "</compression>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_IN_MEMORY + " -->" ) ; w . println ( "          <in_memory>" + col . isInMemory ( ) + "</in_memory>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_BLOCKCACHE + " -->" ) ; w . println ( "          <block_cache_enabled>" + col . isBlockCacheEnabled ( ) + "</block_cache_enabled>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_BLOCKSIZE + " -->" ) ; w . println ( "          <block_size>" + col . getBlocksize ( ) + "</block_size>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_TTL + ( HColumnDescriptor . DEFAULT_TTL == Integer . MAX_VALUE ? " (forever)" : "" ) + " -->" ) ; w . println ( "          <time_to_live>" + col . getTimeToLive ( ) + "</time_to_live>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_BLOOMFILTER + " -->" ) ; w . println ( "          <bloom_filter>" + col . getBloomFilterType ( ) + "</bloom_filter>" ) ; w . println ( "          <!-- Default: " + HColumnDescriptor . DEFAULT_REPLICATION_SCOPE + " -->" ) ; w . println ( "          <replication_scope>" + col . getScope ( ) + "</replication_scope>" ) ; w . println ( "        </column_family>" ) ; } w . println ( "      </table>" ) ; } w . println ( "    </schema>" ) ; w . println ( "  </configuration>" ) ; w . println ( "</configurations>" ) ; w . flush ( ) ; w . close ( ) ; } private Configuration getConfiguration ( ) { final Configuration hbaseConfig = HBaseConfiguration . create ( ) ; String master = getStringProperty ( "hbase_master" , null ) ; if ( master != null ) { hbaseConfig . set ( "hbase.master" , master ) ; } String q = getStringProperty ( "zookeeper_quorum" , quorum ) ; if ( q != null ) { hbaseConfig . set ( "hbase.zookeeper.quorum" , q ) ; } else { System . err . println ( "ERROR: ZooKeeper quorum not set!" ) ; System . exit ( - 10 ) ; } String p = getStringProperty ( "zookeeper_client_port" , zkPort ) ; if ( p != null ) { hbaseConfig . set ( "hbase.zookeeper.property.clientPort" , p ) ; } if ( verbose ) { System . out . println ( "hbase.master -> " + hbaseConfig . get ( "hbase.master" ) ) ; System . out . println ( "zookeeper.quorum -> " + hbaseConfig . get ( "hbase.zookeeper.quorum" ) ) ; System . out . println ( "zookeeper.clientPort -> " + hbaseConfig . get ( "hbase.zookeeper.property.clientPort" ) ) ; } return hbaseConfig ; } private String getStringProperty ( final String key , final String defValue ) { return config != null ? config . getString ( configBaseKey + key , defValue ) : defValue ; } @ SuppressWarnings ( "rawtypes" ) private int getConfigurationNumber ( final XMLConfiguration config , final String name ) { if ( name == null ) { return - 1 ; } final Object p = config . getProperty ( "configuration.name" ) ; if ( p instanceof Collection ) { int n = 0 ; for ( final Object o : ( Collection ) p ) { if ( o . toString ( ) . equalsIgnoreCase ( name ) ) { return n ; } n ++ ; } } else if ( p . toString ( ) . equalsIgnoreCase ( name ) ) { return 0 ; } return - 1 ; } private void createOrChangeTable ( final TableSchema schema , final boolean createOrChange ) throws IOException { HTableDescriptor desc = null ; if ( verbose ) { System . out . println ( "authoritative -> " + createOrChange ) ; } if ( verbose ) { System . out . println ( "name -> " + schema . getName ( ) ) ; } if ( verbose ) { System . out . println ( "tableExists -> " + tableExists ( schema . getName ( ) , false ) ) ; } if ( tableExists ( schema . getName ( ) , false ) ) { desc = getTable ( schema . getName ( ) , false ) ; if ( createOrChange ) { System . out . println ( "checking table " + desc . getNameAsString ( ) + "..." ) ; final HTableDescriptor d = convertSchemaToDescriptor ( schema ) ; final List < HColumnDescriptor > modCols = new ArrayList < HColumnDescriptor > ( ) ; for ( final HColumnDescriptor cd : desc . getFamilies ( ) ) { final HColumnDescriptor cd2 = d . getFamily ( cd . getName ( ) ) ; if ( cd2 != null && ! cd . equals ( cd2 ) ) { modCols . add ( cd2 ) ; } } final List < HColumnDescriptor > delCols = new ArrayList < HColumnDescriptor > ( desc . getFamilies ( ) ) ; delCols . removeAll ( d . getFamilies ( ) ) ; final List < HColumnDescriptor > addCols = new ArrayList < HColumnDescriptor > ( d . getFamilies ( ) ) ; addCols . removeAll ( desc . getFamilies ( ) ) ; if ( modCols . size ( ) > 0 || addCols . size ( ) > 0 || delCols . size ( ) > 0 || ! hasSameProperties ( desc , d ) ) { System . out . println ( "disabling table..." ) ; _hbaseAdmin . disableTable ( schema . getName ( ) ) ; if ( verbose ) { System . out . println ( "table disabled" ) ; } if ( modCols . size ( ) > 0 || addCols . size ( ) > 0 || delCols . size ( ) > 0 ) { for ( final HColumnDescriptor col : modCols ) { if ( verbose ) { System . out . println ( "found different column -> " + col ) ; } _hbaseAdmin . modifyColumn ( schema . getName ( ) , col . getNameAsString ( ) , col ) ; } for ( final HColumnDescriptor col : addCols ) { if ( verbose ) { System . out . println ( "found new column -> " + col ) ; } _hbaseAdmin . addColumn ( schema . getName ( ) , col ) ; } for ( final HColumnDescriptor col : delCols ) { if ( verbose ) { System . out . println ( "found removed column -> " + col ) ; } _hbaseAdmin . deleteColumn ( schema . getName ( ) , col . getNameAsString ( ) + ":" ) ; } } else if ( ! hasSameProperties ( desc , d ) ) { System . out . println ( "found different table properties..." ) ; _hbaseAdmin . modifyTable ( Bytes . toBytes ( schema . getName ( ) ) , d ) ; } System . out . println ( "enabling table..." ) ; _hbaseAdmin . enableTable ( schema . getName ( ) ) ; System . out . println ( "table enabled" ) ; desc = getTable ( schema . getName ( ) , false ) ; System . out . println ( "table changed" ) ; } else { System . out . println ( "no changes detected!" ) ; } } } else if ( createOrChange ) { desc = convertSchemaToDescriptor ( schema ) ; System . out . println ( "creating table " + desc . getNameAsString ( ) + "..." ) ; _hbaseAdmin . createTable ( desc ) ; System . out . println ( "table created" ) ; } } private boolean hasSameProperties ( HTableDescriptor desc1 , HTableDescriptor desc2 ) { return desc1 . isDeferredLogFlush ( ) == desc2 . isDeferredLogFlush ( ) && desc1 . getMaxFileSize ( ) == desc2 . getMaxFileSize ( ) && desc1 . getMemStoreFlushSize ( ) == desc2 . getMemStoreFlushSize ( ) && desc1 . isReadOnly ( ) == desc2 . isReadOnly ( ) ; } private HTableDescriptor convertSchemaToDescriptor ( final TableSchema schema ) { HTableDescriptor desc ; desc = new HTableDescriptor ( schema . getName ( ) ) ; desc . setDeferredLogFlush ( schema . isDeferredLogFlush ( ) ) ; desc . setMaxFileSize ( schema . getMaxFileSize ( ) ) ; desc . setMemStoreFlushSize ( schema . getMemStoreFlushSize ( ) ) ; desc . setReadOnly ( schema . isReadOnly ( ) ) ; final Collection < ColumnDefinition > cols = schema . getColumns ( ) ; for ( final ColumnDefinition col : cols ) { final HColumnDescriptor cd = new HColumnDescriptor ( Bytes . toBytes ( col . getColumnName ( ) ) , col . getMaxVersions ( ) , col . getCompression ( ) , col . isInMemory ( ) , col . isBlockCacheEnabled ( ) , col . getBlockSize ( ) , col . getTimeToLive ( ) , col . getBloomFilter ( ) , col . getReplicationScope ( ) ) ; desc . addFamily ( cd ) ; } return desc ; } private synchronized HTableDescriptor getTable ( final String name , final boolean force ) throws IOException { if ( remoteTables == null || force ) { remoteTables = _hbaseAdmin . listTables ( ) ; } for ( final HTableDescriptor d : remoteTables ) { if ( d . getNameAsString ( ) . equals ( name ) ) { return d ; } } return null ; } private boolean tableExists ( final String name , final boolean force ) throws IOException { getTables ( force ) ; for ( final HTableDescriptor d : remoteTables ) { if ( d . getNameAsString ( ) . equals ( name ) ) { return true ; } } return false ; } private void getTables ( final boolean force ) throws IOException { if ( remoteTables == null || force ) { remoteTables = _hbaseAdmin . listTables ( ) ; } } public static void main ( final String [ ] args ) { try { final HBaseManager hm = new HBaseManager ( args ) ; hm . process ( ) ; System . out . println ( "done." ) ; } catch ( final Exception e ) { e . printStackTrace ( ) ; } } } 
=======
public class MicrodataChecker extends Checker { class Element { public final Locator locator ; public final String [ ] itemProp ; public final String [ ] itemRef ; public final boolean itemScope ; public final List < Element > children ; private final int order ; public Element ( Locator locator , String [ ] itemProp , String [ ] itemRef , boolean itemScope ) { this . locator = locator ; this . itemProp = itemProp ; this . itemRef = itemRef ; this . itemScope = itemScope ; this . children = new LinkedList < Element > ( ) ; this . order = counter ++ ; } @ Override public boolean equals ( Object that ) { return this == that ; } @ Override public int hashCode ( ) { return order ; } class Builder { public final Builder parent ; public final int depth ; public Builder ( Builder parent , int depth ) { this . parent = parent ; this . depth = depth ; } public void appendChild ( Element elm ) { Element . this . children . add ( elm ) ; } } } private int depth ; private Element . Builder builder ; private static int counter ; private List < Element > items ; private Set < Element > properties ; private Map < String , Element > idmap ; private Locator locator ; @ Override public void reset ( ) { depth = 0 ; builder = null ; counter = 0 ; items = new LinkedList < Element > ( ) ; properties = new LinkedHashSet < Element > ( ) ; idmap = new HashMap < String , Element > ( ) ; } @ Override public void startElement ( String uri , String localName , String qName , Attributes atts ) throws SAXException { depth ++ ; if ( "http://www.w3.org/1999/xhtml" != uri ) { return ; } String id = null ; String [ ] itemProp = null ; String [ ] itemRef = null ; boolean itemScope = false ; int len = atts . getLength ( ) ; for ( int i = 0 ; i < len ; i ++ ) { if ( atts . getURI ( i ) . isEmpty ( ) ) { String attLocal = atts . getLocalName ( i ) ; String attValue = atts . getValue ( i ) ; if ( "id" == attLocal ) { id = attValue ; } else if ( "itemprop" == attLocal ) { itemProp = AttributeUtil . split ( attValue ) ; } else if ( "itemref" == attLocal ) { itemRef = AttributeUtil . split ( attValue ) ; } else if ( "itemscope" == attLocal ) { itemScope = true ; } } } if ( id != null || itemProp != null || itemScope == true ) { Element elm = new Element ( new LocatorImpl ( locator ) , itemProp , itemRef , itemScope ) ; if ( itemProp != null ) { properties . add ( elm ) ; } else if ( itemScope ) { items . add ( elm ) ; } if ( ! idmap . containsKey ( id ) ) { idmap . put ( id , elm ) ; } if ( builder != null ) { builder . appendChild ( elm ) ; } builder = elm . new Builder ( builder , depth ) ; } } @ Override public void endElement ( String uri , String localName , String qName ) throws SAXException { if ( builder != null && builder . depth == depth ) { builder = builder . parent ; } depth -- ; } @ Override public void endDocument ( ) throws SAXException { for ( Element item : items ) { checkItem ( item , new ArrayDeque < Element > ( ) ) ; } for ( Element prop : properties ) { err ( "The “itemprop” attribute was specified, but the element is not a property of any item." , prop . locator ) ; } } private void checkItem ( Element root , Deque < Element > parents ) throws SAXException { Deque < Element > pending = new ArrayDeque < Element > ( ) ; Set < Element > memory = new HashSet < Element > ( ) ; memory . add ( root ) ; for ( Element child : root . children ) { pending . push ( child ) ; } if ( root . itemRef != null ) { for ( String id : root . itemRef ) { Element refElm = idmap . get ( id ) ; if ( refElm != null ) { pending . push ( refElm ) ; } else { err ( "The “itemref” attribute referenced “" + id + "”, but there is no element with an “id” attribute with that value." , root . locator ) ; } } } boolean memoryError = false ; while ( pending . size ( ) > 0 ) { Element current = pending . pop ( ) ; if ( memory . contains ( current ) ) { memoryError = true ; continue ; } memory . add ( current ) ; if ( ! current . itemScope ) { for ( Element child : current . children ) { pending . push ( child ) ; } } if ( current . itemProp != null ) { properties . remove ( current ) ; if ( current . itemScope ) { if ( ! parents . contains ( current ) ) { parents . push ( root ) ; checkItem ( current , parents ) ; parents . pop ( ) ; } else { err ( "The “itemref” attribute created a circular reference with another item." , current . locator ) ; } } } } if ( memoryError ) { err ( "The “itemref” attribute contained redundant references." , root . locator ) ; } } @ Override public void setDocumentLocator ( Locator locator ) { this . locator = locator ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
