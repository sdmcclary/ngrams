public class Hive extends Observable implements Synchronizeable , Observer , Lockable , Nameable { private static final int DEFAULT_JDBC_TIMEOUT = 500 ; public static final int NEW_OBJECT_ID = 0 ; private HiveSemaphore semaphore ; private String hiveUri ; private ConnectionManager connection ; private PartitionDimension dimension ; private DirectoryFacade directory ; private Collection < Node > nodes = new ArrayList < Node > ( ) ; private DirectoryFacadeProvider directoryFacadeProvider ; private DataSource hiveDataSource ; private HiveDataSourceProvider dataSourceProvider ; private Assigner assigner = new RandomAssigner ( ) ; public Hive ( String hiveUri , int i , Status writable , HiveDataSourceProvider provider , DirectoryFacadeProvider directoryFacadeProvider ) { this ( hiveUri , i , writable , provider ) ; this . directoryFacadeProvider = directoryFacadeProvider ; } public static Hive load ( String hiveDatabaseUri , HiveDataSourceProvider dataSourceProvider ) { return load ( hiveDatabaseUri , dataSourceProvider , new RandomAssigner ( ) ) ; } public static Hive load ( String hiveDatabaseUri , HiveDataSourceProvider dataSourceProvider , Assigner assigner , DirectoryFacadeProvider directoryFacadeProvider ) { Hive hive = prepareHive ( hiveDatabaseUri , dataSourceProvider , assigner , directoryFacadeProvider ) ; hive . sync ( ) ; return hive ; } public static Hive load ( String hiveUri , HiveDataSourceProvider provider , Assigner assigner ) { DirectoryFacadeProvider directoryFacadeProvider = getDirectoryFacadeProvider ( ) ; return load ( hiveUri , provider , assigner , directoryFacadeProvider ) ; } private static DirectoryFacadeProvider getDirectoryFacadeProvider ( ) { DirectoryProvider directoryProvider = new DbDirectoryFactory ( CachingDataSourceProvider . getInstance ( ) ) ; DirectoryFacadeProvider directoryFacadeProvider = new DirectoryWrapperFactory ( directoryProvider , CachingDataSourceProvider . getInstance ( ) ) ; return directoryFacadeProvider ; } public static Hive create ( String hiveUri , String dimensionName , int indexType , HiveDataSourceProvider provider ) { return create ( hiveUri , dimensionName , indexType , provider , null ) ; } public static Hive create ( String hiveUri , String dimensionName , int indexType , HiveDataSourceProvider provider , Assigner assigner ) { Hive hive = prepareHive ( hiveUri , provider , assigner , getDirectoryFacadeProvider ( ) ) ; PartitionDimension dimension = new PartitionDimension ( dimensionName , indexType ) ; dimension . setIndexUri ( hiveUri ) ; DataSource ds = provider . getDataSource ( hiveUri ) ; PartitionDimensionDao dao = new PartitionDimensionDao ( ds ) ; final List < PartitionDimension > partitionDimensions = dao . loadAll ( ) ; if ( partitionDimensions . size ( ) == 0 ) { dao . create ( dimension ) ; Schemas . install ( new IndexSchema ( dimension ) , dimension . getIndexUri ( ) ) ; hive . incrementAndPersistHive ( ds ) ; return hive ; } else throw new HiveRuntimeException ( String . format ( "There is already a Hive with a partition dimension named %s intalled at this uri: %s" , Atom . getFirstOrThrow ( partitionDimensions ) . getName ( ) , hiveUri ) ) ; } private static Hive prepareHive ( String hiveUri , HiveDataSourceProvider provider , Assigner assigner , DirectoryFacadeProvider directoryFacadeProvider ) { DriverLoader . initializeDriver ( hiveUri ) ; Hive hive = new Hive ( hiveUri , 0 , Status . writable , provider , directoryFacadeProvider ) ; if ( assigner != null ) hive . setAssigner ( assigner ) ; return hive ; } public boolean sync ( ) { boolean updated = false ; HiveSemaphore hs = new HiveSemaphoreDao ( hiveDataSource ) . get ( ) ; if ( this . getRevision ( ) != hs . getRevision ( ) ) { this . setSemaphore ( hs ) ; initialize ( hiveDataSource ) ; updated = true ; } return updated ; } public boolean forceSync ( ) { initialize ( hiveDataSource ) ; return true ; } private void initialize ( DataSource ds ) { Collection < Node > nodes = new NodeDao ( hiveDataSource ) . loadAll ( ) ; synchronized ( this ) { this . nodes = nodes ; } try { PartitionDimension dimension = new PartitionDimensionDao ( ds ) . get ( ) ; DirectoryFacade directory = directoryFacadeProvider . getDirectoryFacade ( hiveUri , getAssigner ( ) , getSemaphore ( ) , dimension ) ; synchronized ( this ) { ConnectionManager connection = new ConnectionManager ( directory , this , dataSourceProvider ) ; this . dimension = dimension ; this . directory = directory ; this . connection = connection ; } } catch ( HiveRuntimeException e ) { } } protected Hive ( ) { this . semaphore = new HiveSemaphoreImpl ( ) ; } protected Hive ( String hiveUri , int revision , Status status , HiveDataSourceProvider dataSourceProvider ) { this ( ) ; this . hiveUri = hiveUri ; this . semaphore . setRevision ( revision ) ; this . semaphore . setStatus ( status ) ; this . dataSourceProvider = dataSourceProvider ; this . hiveDataSource = dataSourceProvider . getDataSource ( hiveUri ) ; } public String getUri ( ) { return hiveUri ; } public DataSourceProvider getDataSourceProvider ( ) { return dataSourceProvider ; } public void setDataSourceProvider ( HiveDataSourceProvider dataSourceProvider ) { this . dataSourceProvider = dataSourceProvider ; } public int hashCode ( ) { return HiveUtils . makeHashCode ( new Object [ ] { hiveUri , getRevision ( ) , dimension } ) ; } public boolean equals ( Object obj ) { return hashCode ( ) == obj . hashCode ( ) ; } public Status getStatus ( ) { return semaphore . getStatus ( ) ; } public void updateHiveStatus ( Status status ) { this . semaphore . setStatus ( status ) ; new HiveSemaphoreDao ( hiveDataSource ) . update ( this . semaphore ) ; } public int getRevision ( ) { return semaphore . getRevision ( ) ; } public HiveSemaphore getSemaphore ( ) { return semaphore ; } public HiveSemaphore setSemaphore ( HiveSemaphore semaphore ) { this . semaphore = semaphore ; return semaphore ; } public PartitionDimension getPartitionDimension ( ) { return this . dimension ; } public PartitionDimension setPartitionDimension ( PartitionDimension dimension ) { this . dimension = dimension ; incrementAndPersistHive ( hiveDataSource ) ; sync ( ) ; return getPartitionDimension ( ) ; } private void incrementAndPersistHive ( DataSource datasource ) { new HiveSemaphoreDao ( datasource ) . incrementAndPersist ( ) ; this . sync ( ) ; } public ConnectionManager connection ( ) { return this . connection ; } public String toString ( ) { return HiveUtils . toDeepFormatedString ( this , "HiveUri" , getUri ( ) , "Revision" , getRevision ( ) , "PartitionDimensions" , getPartitionDimension ( ) ) ; } public HiveDbDialect getDialect ( ) { return DriverLoader . discernDialect ( hiveUri ) ; } public void update ( Observable o , Object arg ) { if ( sync ( ) ) notifyObservers ( ) ; } public void notifyObservers ( ) { super . setChanged ( ) ; super . notifyObservers ( ) ; } public Assigner getAssigner ( ) { return assigner ; } public void setAssigner ( Assigner assigner ) { this . assigner = assigner ; } public DirectoryFacade directory ( ) { return this . directory ; } public void updateNodeStatus ( Node node , Status status ) { node . setStatus ( status ) ; try { this . updateNode ( node ) ; } catch ( HiveLockableException e ) { } } public Node addNode ( Node node ) throws HiveLockableException { Preconditions . isWritable ( this ) ; Preconditions . nameIsUnique ( getNodes ( ) , node ) ; NodeDao nodeDao = new NodeDao ( hiveDataSource ) ; nodeDao . create ( node ) ; incrementAndPersistHive ( hiveDataSource ) ; return node ; } public Collection < Node > addNodes ( Collection < Node > nodes ) throws HiveLockableException { Preconditions . isWritable ( this ) ; for ( Node node : nodes ) { Preconditions . nameIsUnique ( getNodes ( ) , node ) ; NodeDao nodeDao = new NodeDao ( hiveDataSource ) ; nodeDao . create ( node ) ; } incrementAndPersistHive ( hiveDataSource ) ; return nodes ; } public boolean doesResourceExist ( String resourceName ) { return ! Preconditions . isNameUnique ( dimension . getResources ( ) , resourceName ) ; } public Resource addResource ( Resource resource ) throws HiveLockableException { resource . setPartitionDimension ( dimension ) ; Preconditions . isWritable ( this ) ; Preconditions . isNameUnique ( dimension . getResources ( ) , resource . getName ( ) ) ; ResourceDao resourceDao = new ResourceDao ( hiveDataSource ) ; resourceDao . create ( resource ) ; incrementAndPersistHive ( hiveDataSource ) ; Schemas . install ( dimension ) ; return dimension . getResource ( resource . getName ( ) ) ; } public SecondaryIndex addSecondaryIndex ( Resource resource , SecondaryIndex secondaryIndex ) throws HiveLockableException { secondaryIndex . setResource ( resource ) ; Preconditions . isWritable ( this ) ; Preconditions . nameIsUnique ( resource . getSecondaryIndexes ( ) , secondaryIndex ) ; SecondaryIndexDao secondaryIndexDao = new SecondaryIndexDao ( hiveDataSource ) ; secondaryIndexDao . create ( secondaryIndex ) ; incrementAndPersistHive ( hiveDataSource ) ; Schemas . install ( dimension ) ; return secondaryIndex ; } public PartitionDimension updatePartitionDimension ( PartitionDimension partitionDimension ) throws HiveLockableException { Preconditions . isWritable ( this ) ; PartitionDimensionDao partitionDimensionDao = new PartitionDimensionDao ( hiveDataSource ) ; partitionDimensionDao . update ( partitionDimension ) ; incrementAndPersistHive ( hiveDataSource ) ; return partitionDimension ; } public Node updateNode ( Node node ) throws HiveLockableException { Preconditions . isWritable ( this ) ; Preconditions . idIsPresentInList ( getNodes ( ) , node ) ; new NodeDao ( hiveDataSource ) . update ( node ) ; incrementAndPersistHive ( hiveDataSource ) ; return node ; } public Node deleteNode ( Node node ) throws HiveLockableException { Preconditions . isWritable ( this ) ; Preconditions . idIsPresentInList ( getNodes ( ) , node ) ; NodeDao nodeDao = new NodeDao ( hiveDataSource ) ; nodeDao . delete ( node ) ; incrementAndPersistHive ( hiveDataSource ) ; connection . removeNode ( node ) ; return node ; } public Resource deleteResource ( Resource resource ) throws HiveLockableException { Preconditions . isWritable ( this ) ; Preconditions . idIsPresentInList ( this . getPartitionDimension ( ) . getResources ( ) , resource ) ; ResourceDao resourceDao = new ResourceDao ( hiveDataSource ) ; resourceDao . delete ( resource ) ; incrementAndPersistHive ( hiveDataSource ) ; return resource ; } public SecondaryIndex deleteSecondaryIndex ( SecondaryIndex secondaryIndex ) throws HiveLockableException { Preconditions . isWritable ( this ) ; Preconditions . idIsPresentInList ( secondaryIndex . getResource ( ) . getSecondaryIndexes ( ) , secondaryIndex ) ; SecondaryIndexDao secondaryindexDao = new SecondaryIndexDao ( hiveDataSource ) ; secondaryindexDao . delete ( secondaryIndex ) ; incrementAndPersistHive ( hiveDataSource ) ; return secondaryIndex ; } public Collection < Node > getNodes ( ) { return nodes ; } public Node getNode ( final String name ) { return Filter . grepSingle ( new Predicate < Node > ( ) { public boolean f ( Node item ) { return item . getName ( ) . equalsIgnoreCase ( name ) ; } } , getNodes ( ) ) ; } public Node getNode ( final int id ) { return Filter . grepSingle ( new Predicate < Node > ( ) { public boolean f ( Node item ) { return item . getId ( ) == id ; } } , getNodes ( ) ) ; } public String getName ( ) { return "hive" ; } } 