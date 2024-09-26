<<<<<<< HEAD
public class Cache2 { private static final Logger LOG = Logger . getLogger ( Cache2 . class . getName ( ) ) ; private File cacheFolder ; private long sizeLimit ; private Executor executor ; public Cache2 ( File aCacheFolder , long aSizeLimit ) { cacheFolder = aCacheFolder ; sizeLimit = aSizeLimit ; executor = ExecutorFactory . createPooledExecutor ( "Cached Images Writer" , 1 , 10000 ) ; if ( ! cacheFolder . exists ( ) ) cacheFolder . mkdir ( ) ; } public void put ( URL url , Image image ) { if ( url == null || image == null || isLocal ( url ) ) return ; recordMemoryImage ( image , url ) ; if ( LOG . isLoggable ( Level . FINE ) ) LOG . fine ( "Put " + url ) ; image . getSource ( ) . addConsumer ( new Cache2 . ImageWaiter ( url , image ) ) ; } private void onImageReady ( URL url , Image image ) { Cache2 . WriteTask writeTask = new Cache2 . WriteTask ( url , image ) ; try { executor . execute ( writeTask ) ; } catch ( InterruptedException e ) { LOG . warning ( Strings . error ( "img.image.caching.executed.directly" ) ) ; writeTask . run ( ) ; } } public Image get ( URL url ) { if ( url == null ) return null ; Image image = lookupMemoryImage ( url ) ; if ( image != null || isLocal ( url ) ) return image ; File file = new File ( cacheFolder , urlToFilename ( url ) ) ; if ( file . exists ( ) ) { try { url = file . toURI ( ) . toURL ( ) ; image = ImageFetcher . load ( url ) ; file . setLastModified ( System . currentTimeMillis ( ) ) ; recordMemoryImage ( image , url ) ; } catch ( IOException e ) { LOG . log ( Level . SEVERE , Strings . error ( "img.failed.to.load.image" ) , e ) ; } } return image ; } private static boolean isLocal ( URL url ) { return url . getProtocol ( ) . equals ( "file" ) ; } private static String urlToFilename ( URL url ) { int hashCode = url . toString ( ) . toLowerCase ( ) . hashCode ( ) ; return Integer . toHexString ( hashCode ) . toUpperCase ( ) ; } private class WriteTask implements Runnable { private URL url ; private Image image ; public WriteTask ( URL aUrl , Image aImage ) { url = aUrl ; image = aImage ; } public void run ( ) { File file = new File ( cacheFolder , urlToFilename ( url ) ) ; if ( file . exists ( ) ) return ; if ( LOG . isLoggable ( Level . FINE ) ) LOG . fine ( "Writing " + url ) ; boolean created = false ; try { created = file . createNewFile ( ) ; BufferedImage buf = null ; if ( image instanceof BufferedImage ) { buf = ( BufferedImage ) image ; } else { int width = image . getWidth ( null ) ; int height = image . getHeight ( null ) ; if ( width > 0 && height > 0 ) { buf = new BufferedImage ( width , height , BufferedImage . TYPE_INT_ARGB ) ; Graphics g = buf . createGraphics ( ) ; g . drawImage ( image , 0 , 0 , null ) ; } } if ( buf != null ) { ImageIO . write ( buf , "PNG" , file ) ; verifyLimits ( ) ; } } catch ( Throwable e ) { LOG . log ( Level . WARNING , Strings . error ( "img.cache.writing.failed" ) , e ) ; if ( created ) file . delete ( ) ; } } private void verifyLimits ( ) { long size = calcSize ( ) ; if ( size > sizeLimit ) { removeOldEntries ( size - sizeLimit ) ; } } private void removeOldEntries ( long size ) { File [ ] files = cacheFolder . listFiles ( ) ; Arrays . sort ( files , new Cache2 . FileAccessComparator ( ) ) ; long leftToFree = size ; for ( int i = 0 ; leftToFree > 0 && i < files . length - 1 ; i ++ ) { File file = files [ i ] ; if ( file . isFile ( ) && file . exists ( ) ) { if ( file . delete ( ) ) leftToFree -= file . length ( ) ; } } } private long calcSize ( ) { long size = 0 ; File [ ] files = cacheFolder . listFiles ( ) ; for ( int i = 0 ; i < files . length ; i ++ ) { File file = files [ i ] ; size += file . isFile ( ) ? file . length ( ) : 0 ; } return size ; } } private static class FileAccessComparator implements Comparator { public int compare ( Object o1 , Object o2 ) { File f1 = ( File ) o1 ; File f2 = ( File ) o2 ; long l1 = f1 . lastModified ( ) ; long l2 = f2 . lastModified ( ) ; return l1 == l2 ? 0 : l1 < l2 ? - 1 : 1 ; } } private class ImageWaiter implements ImageConsumer { private final URL imageURL ; private final Image image ; public ImageWaiter ( URL aImageURL , Image anImage ) { imageURL = aImageURL ; image = anImage ; } public void imageComplete ( int status ) { if ( status == STATICIMAGEDONE || status == SINGLEFRAMEDONE ) { image . getSource ( ) . removeConsumer ( this ) ; if ( status == STATICIMAGEDONE ) onImageReady ( imageURL , image ) ; } } public void setColorModel ( ColorModel model ) { } public void setDimensions ( int width , int height ) { } public void setHints ( int hintflags ) { } public void setPixels ( int x , int y , int w , int h , ColorModel model , byte pixels [ ] , int off , int scansize ) { } public void setPixels ( int x , int y , int w , int h , ColorModel model , int pixels [ ] , int off , int scansize ) { } public void setProperties ( Hashtable props ) { } } private final Map memoryMap = new HashMap ( ) ; private void recordMemoryImage ( Image image , URL url ) { if ( image == null || url == null ) return ; String key = url . toString ( ) ; synchronized ( memoryMap ) { memoryMap . put ( key , new SoftReference ( image ) ) ; } cleanMemoryMap ( ) ; } private Image lookupMemoryImage ( URL url ) { Image image = null ; if ( url != null ) { String key = url . toString ( ) ; synchronized ( memoryMap ) { SoftReference r = ( SoftReference ) memoryMap . get ( key ) ; image = r == null ? null : ( Image ) r . get ( ) ; } } return image ; } private void cleanMemoryMap ( ) { synchronized ( memoryMap ) { Iterator it = memoryMap . entrySet ( ) . iterator ( ) ; while ( it . hasNext ( ) ) { Map . Entry entry = ( Map . Entry ) it . next ( ) ; SoftReference r = ( SoftReference ) entry . getValue ( ) ; if ( r . get ( ) == null ) it . remove ( ) ; } } } } 
=======
class SingleDataDerivType extends DataDerivType { private PatternMemo memo ; SingleDataDerivType ( ) { } PatternMemo dataDeriv ( ValidatorPatternBuilder builder , Pattern p , String str , ValidationContext vc , List < DataDerivFailure > fail ) { if ( memo == null ) memo = super . dataDeriv ( builder , p , str , vc , null ) ; return memo ; } DataDerivType copy ( ) { return new SingleDataDerivType ( ) ; } DataDerivType combine ( DataDerivType ddt ) { return ddt ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
