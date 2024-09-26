<<<<<<< HEAD
public class FakeFileSystem extends FileSystem { private FakeFile root ; private FakeFilePath workingDirectory ; public static FakeFileSystem installed ( ) { FakeFileSystem fs = new FakeFileSystem ( ) ; Context . instance ( ) . fs = fs ; return fs ; } public FakeFileSystem ( ) { separator = "/" ; windows = false ; root = FakeFile . directory ( "" ) ; workingDirectory = ( FakeFilePath ) resolve ( "/" ) ; } @ Override public String workingDir ( ) { return workingDirectory . getAbsolutePath ( ) ; } public void setWorkingDirectory ( String path ) { final FakeFilePath filePath = ( FakeFilePath ) resolve ( path ) ; filePath . mkdirs ( ) ; workingDirectory = filePath ; } public String inspect ( ) { StringBuffer buffer = new StringBuffer ( ) ; inspect ( root , buffer ) ; return buffer . toString ( ) ; } private void inspect ( FakeFile file , StringBuffer buffer ) { for ( int i = 0 ; i < file . depth ( ) ; i ++ ) buffer . append ( "| " ) ; if ( file . isDirectory ) buffer . append ( "+ " ) ; else buffer . append ( "- " ) ; buffer . append ( file . name ) ; if ( ! file . isDirectory ) buffer . append ( " : " ) . append ( file . content . length ) . append ( " bytes" ) ; buffer . append ( Util . ENDL ) ; if ( file . isDirectory ) { List < String > childNames = new ArrayList < String > ( file . children . keySet ( ) ) ; Collections . sort ( childNames ) ; for ( String childName : childNames ) inspect ( file . children . get ( childName ) , buffer ) ; } } @ Override protected Path resolve ( String path ) { return new FakeFilePath ( this , path ) ; } private static class FakeFile { private FakeFile parent ; private String name ; private byte [ ] content ; private boolean isDirectory ; private Map < String , FakeFile > children ; private long modificationTime ; public FakeFile ( String name ) { this . name = name ; modified ( ) ; } public static FakeFile directory ( String name ) { FakeFile dir = new FakeFile ( name ) ; dir . isDirectory = true ; dir . children = new HashMap < String , FakeFile > ( ) ; return dir ; } public static FakeFile file ( String name ) { FakeFile file = new FakeFile ( name ) ; file . isDirectory = false ; return file ; } public void add ( FakeFile file ) { children . put ( file . name , file ) ; file . parent = this ; } public FakeFile get ( String name ) { if ( "." . equals ( name ) ) return this ; else if ( ".." . equals ( name ) ) return parent == null ? this : parent ; else return children . get ( name ) ; } public int depth ( ) { return parent == null ? 0 : parent . depth ( ) + 1 ; } public void modified ( ) { modificationTime = System . currentTimeMillis ( ) ; } } private static class FakeFileOutputStream extends ByteArrayOutputStream { private FakeFile file ; public FakeFileOutputStream ( FakeFile file ) { super ( ) ; this . file = file ; } @ Override public void close ( ) throws IOException { super . close ( ) ; file . content = this . toByteArray ( ) ; file . modified ( ) ; } } private static class FakeFilePath implements Path { private FakeFileSystem fs ; private String path ; private FakeFile file ; public FakeFilePath ( FakeFileSystem fs , String path ) { this . fs = fs ; this . path = path ; } private FakeFile resolvePath ( String path ) { if ( "." . equals ( path ) || path == null ) return fs . workingDirectory . fake ( ) ; else if ( isRoot ( path ) ) return fs . root ; String parentPath = fs . parentPath ( path ) ; FakeFile parent = resolveParent ( parentPath ) ; if ( parent != null ) return parent . get ( fs . filename ( path ) ) ; return null ; } private boolean isRoot ( String path ) { return "" . equals ( path ) || "/" . equals ( path ) ; } private FakeFile resolveParent ( String parentPath ) { return parentPath == null ? fs . workingDirectory . fake ( ) : resolvePath ( parentPath ) ; } private FakeFile fake ( ) { if ( file == null ) file = resolvePath ( path ) ; return file ; } private void ensureExistence ( ) { if ( ! exists ( ) ) throw new LimelightException ( "[FakeFileSystem] File not found: " + path ) ; } public boolean exists ( ) { return fake ( ) != null ; } public void mkdirs ( ) { if ( exists ( ) ) return ; final FakeFilePath parentPath = new FakeFilePath ( fs , fs . parentPath ( path ) ) ; parentPath . mkdirs ( ) ; final FakeFile newDir = FakeFile . directory ( fs . filename ( path ) ) ; parentPath . fake ( ) . add ( newDir ) ; } public boolean isDirectory ( ) { return exists ( ) && fake ( ) . isDirectory ; } public OutputStream outputStream ( ) { final FakeFilePath parentPath = new FakeFilePath ( fs , fs . parentPath ( path ) ) ; parentPath . mkdirs ( ) ; final FakeFile file = FakeFile . file ( fs . filename ( path ) ) ; parentPath . fake ( ) . add ( file ) ; return new FakeFileOutputStream ( fake ( ) ) ; } public InputStream inputStream ( ) { ensureExistence ( ) ; return new ByteArrayInputStream ( fake ( ) . content ) ; } public String getAbsolutePath ( ) { if ( path == null || "." . equals ( path ) ) return fs . workingDirectory . getAbsolutePath ( ) ; else if ( isRoot ( path ) ) return "/" ; else if ( path . startsWith ( fs . separator ( ) ) ) return path ; else return fs . join ( fs . workingDirectory . getAbsolutePath ( ) , path ) ; } public void delete ( ) { if ( ! exists ( ) ) return ; FakeFile parent = new FakeFilePath ( fs , fs . parentPath ( path ) ) . fake ( ) ; parent . children . remove ( fake ( ) . name ) ; } public String [ ] listing ( ) { ensureExistence ( ) ; if ( ! fake ( ) . isDirectory ) throw new LimelightException ( "Not a directory: " + path ) ; final Set < String > childNames = fake ( ) . children . keySet ( ) ; String [ ] files = new String [ childNames . size ( ) ] ; int i = 0 ; for ( String childName : childNames ) files [ i ++ ] = childName ; return files ; } public long lastModified ( ) { ensureExistence ( ) ; return fake ( ) . modificationTime ; } public File file ( ) { throw new LimelightException ( "FakeFilePath.file() not supported" ) ; } public boolean isRoot ( ) { return fake ( ) == fs . root ; } public String parentPath ( ) { if ( isRoot ( path ) ) return "/" ; int lastSeparatorIndex = path . lastIndexOf ( fs . separator ) ; if ( lastSeparatorIndex != - 1 ) return path . substring ( 0 , lastSeparatorIndex ) ; else return fs . workingDir ( ) ; } public boolean isAbsolute ( ) { return path . startsWith ( "/" ) ; } public Path append ( String part ) { return new FakeFilePath ( fs , fs . removeDuplicateSeparators ( path + "/" + part , "/" ) ) ; } public String toPath ( ) { return path ; } } } 
=======
public class MatchablePatternLoader { public static final int COMPACT_SYNTAX_FLAG = 0x1 ; public static final int FEASIBLE_FLAG = 0x2 ; public MatchablePattern load ( Input input , SAXResolver saxResolver , ErrorHandler eh , DatatypeLibraryFactory dlf , int flags ) throws IOException , SAXException , IncorrectSchemaException { SchemaPatternBuilder spb = new SchemaPatternBuilder ( ) ; Parseable < Pattern , com . thaiopensource . relaxng . pattern . NameClass , Locator , VoidValue , CommentListImpl , AnnotationsImpl > parseable ; if ( ( flags & COMPACT_SYNTAX_FLAG ) != 0 ) parseable = new CompactParseable < Pattern , NameClass , Locator , VoidValue , CommentListImpl , AnnotationsImpl > ( input , saxResolver . getResolver ( ) , eh ) ; else parseable = new SAXParseable < Pattern , NameClass , Locator , VoidValue , CommentListImpl , AnnotationsImpl > ( saxResolver . createSAXSource ( input ) , saxResolver , eh ) ; if ( dlf == null ) dlf = new DatatypeLibraryLoader ( ) ; try { Pattern start = SchemaBuilderImpl . parse ( parseable , eh , dlf , spb , false ) ; if ( ( flags & FEASIBLE_FLAG ) != 0 ) start = FeasibleTransform . transform ( spb , start ) ; return new MatchablePatternImpl ( spb , start ) ; } catch ( IllegalSchemaException e ) { throw new IncorrectSchemaException ( ) ; } } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
