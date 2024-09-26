@ Mojo ( name = "lint" , defaultPhase = LifecyclePhase . VERIFY ) public class JSLintMojo extends AbstractMojo { private static final String REPORT_HTML = "report.html" ; private static final String REPORT_TXT = "report.txt" ; private static final String CHECKSTYLE_XML = "checkstyle.xml" ; private static final String DEFAULT_INCLUDES = "**/*.js" ; private static final String JSLINT_XML = "jslint.xml" ; private static final String JUNIT_XML = "junit.xml" ; @ Parameter ( property = "excludes" ) private final List < String > excludes = Lists . newArrayList ( ) ; @ Parameter ( property = "includes" ) private final List < String > includes = Lists . newArrayList ( ) ; @ Parameter ( readonly = true , required = true , defaultValue = "${basedir}/src/main/webapp" ) private File defaultSourceFolder ; @ Parameter private File [ ] sourceFolders = new File [ ] { } ; @ Parameter private final Map < String , String > options = Maps . newHashMap ( ) ; @ Parameter ( property = "encoding" , defaultValue = "${project.build.sourceEncoding}" ) private String encoding = "UTF-8" ; @ Parameter ( property = "jslint.outputFolder" , defaultValue = "${project.build.directory}/jslint4java" ) private File outputFolder = new File ( "target" ) ; @ Parameter ( property = "jslint.failOnError" , defaultValue = "true" ) private boolean failOnError = true ; @ Parameter ( property = "jslint.source" ) private File jslintSource ; @ Parameter ( property = "jslint.timeout" ) private long timeout ; @ Parameter ( property = "jslint.skip" , defaultValue = "false" ) private boolean skip = false ; void addOption ( Option sloppy , String value ) { options . put ( sloppy . name ( ) . toLowerCase ( Locale . ENGLISH ) , value ) ; } private void applyDefaults ( ) { if ( includes . isEmpty ( ) ) { includes . add ( DEFAULT_INCLUDES ) ; } if ( sourceFolders . length == 0 ) { sourceFolders = new File [ ] { defaultSourceFolder } ; } } private void applyOptions ( JSLint jsLint ) throws MojoExecutionException { for ( Entry < String , String > entry : options . entrySet ( ) ) { if ( entry . getValue ( ) == null || entry . getValue ( ) . equals ( "" ) ) { continue ; } Option option ; try { option = Option . valueOf ( entry . getKey ( ) . toUpperCase ( Locale . ENGLISH ) ) ; } catch ( IllegalArgumentException e ) { throw new MojoExecutionException ( "unknown option: " + entry . getKey ( ) ) ; } jsLint . addOption ( option , entry . getValue ( ) ) ; } } public void execute ( ) throws MojoExecutionException , MojoFailureException { if ( skip ) { getLog ( ) . info ( "skipping JSLint" ) ; return ; } JSLint jsLint = applyJSlintSource ( ) ; applyDefaults ( ) ; applyOptions ( jsLint ) ; List < File > files = getFilesToProcess ( ) ; int failures = 0 ; ReportWriter reporter = makeReportWriter ( ) ; try { reporter . open ( ) ; for ( File file : files ) { JSLintResult result = lintFile ( jsLint , file ) ; failures += result . getIssues ( ) . size ( ) ; logIssues ( result , reporter ) ; } } finally { reporter . close ( ) ; } if ( failures > 0 ) { String message = "JSLint found " + failures + " problems in " + files . size ( ) + " files" ; if ( failOnError ) { throw new MojoFailureException ( message ) ; } else { getLog ( ) . info ( message ) ; } } } private JSLint applyJSlintSource ( ) throws MojoExecutionException { JSLintBuilder builder = new JSLintBuilder ( ) ; if ( timeout > 0 ) { builder . timeout ( timeout ) ; } if ( jslintSource != null ) { try { return builder . fromFile ( jslintSource , Charset . forName ( encoding ) ) ; } catch ( IOException e ) { throw new MojoExecutionException ( "Cant' load jslint.js" , e ) ; } } else { return builder . fromDefault ( ) ; } } @ VisibleForTesting String getEncoding ( ) { return encoding ; } private List < File > getFilesToProcess ( ) throws MojoExecutionException { getLog ( ) . debug ( "includes=" + includes ) ; getLog ( ) . debug ( "excludes=" + excludes ) ; List < File > files = Lists . newArrayList ( ) ; for ( File folder : sourceFolders ) { getLog ( ) . debug ( "searching " + folder ) ; try { files . addAll ( new FileLister ( folder , includes , excludes ) . files ( ) ) ; } catch ( IOException e ) { throw new MojoExecutionException ( "Error listing files" , e ) ; } } return files ; } @ VisibleForTesting Map < String , String > getOptions ( ) { return options ; } private JSLintResult lintFile ( JSLint jsLint , File file ) throws MojoExecutionException { getLog ( ) . debug ( "lint " + file ) ; BufferedReader reader = null ; try { UnicodeBomInputStream stream = new UnicodeBomInputStream ( new FileInputStream ( file ) ) ; stream . skipBOM ( ) ; reader = new BufferedReader ( new InputStreamReader ( stream , getEncoding ( ) ) ) ; return jsLint . lint ( file . toString ( ) , reader ) ; } catch ( FileNotFoundException e ) { throw new MojoExecutionException ( "file not found: " + file , e ) ; } catch ( UnsupportedEncodingException e ) { throw new MojoExecutionException ( "unsupported character encoding UTF-8" , e ) ; } catch ( IOException e ) { throw new MojoExecutionException ( "problem whilst linting " + file , e ) ; } finally { Closeables . closeQuietly ( reader ) ; } } private void logIssues ( JSLintResult result , ReportWriter reporter ) { reporter . report ( result ) ; if ( result . getIssues ( ) . isEmpty ( ) ) { return ; } logIssuesToConsole ( result ) ; } private void logIssuesToConsole ( JSLintResult result ) { JSLintResultFormatter formatter = new PlainFormatter ( ) ; String report = formatter . format ( result ) ; for ( String line : report . split ( "\n" ) ) { getLog ( ) . info ( line ) ; } } private ReportWriter makeReportWriter ( ) { ReportWriterImpl f1 = new ReportWriterImpl ( new File ( outputFolder , JSLINT_XML ) , new JSLintXmlFormatter ( ) ) ; ReportWriterImpl f2 = new ReportWriterImpl ( new File ( outputFolder , JUNIT_XML ) , new JUnitXmlFormatter ( ) ) ; ReportWriterImpl f3 = new ReportWriterImpl ( new File ( outputFolder , CHECKSTYLE_XML ) , new CheckstyleXmlFormatter ( ) ) ; ReportWriterImpl f4 = new ReportWriterImpl ( new File ( outputFolder , REPORT_TXT ) , new PlainFormatter ( ) ) ; ReportWriterImpl f5 = new ReportWriterImpl ( new File ( outputFolder , REPORT_HTML ) , new ReportFormatter ( ) ) ; return new MultiReportWriter ( f1 , f2 , f3 , f4 , f5 ) ; } public void setDefaultSourceFolder ( File defaultSourceFolder ) { this . defaultSourceFolder = defaultSourceFolder ; } public void setEncoding ( String encoding ) { this . encoding = encoding ; } public void setExcludes ( List < String > excludes ) { this . excludes . clear ( ) ; this . excludes . addAll ( excludes ) ; } public void setFailOnError ( boolean b ) { failOnError = b ; } public void setIncludes ( List < String > includes ) { this . includes . clear ( ) ; this . includes . addAll ( includes ) ; } public void setJslint ( File jslintSource ) { this . jslintSource = jslintSource ; } public void setOptions ( Map < String , String > options ) { this . options . clear ( ) ; this . options . putAll ( options ) ; } public void setOutputFolder ( File outputFolder ) { this . outputFolder = outputFolder ; } public void setSkip ( boolean skip ) { this . skip = skip ; } public void setSourceFolders ( List < File > sourceFolders ) { this . sourceFolders = sourceFolders . toArray ( new File [ sourceFolders . size ( ) ] ) ; } } 