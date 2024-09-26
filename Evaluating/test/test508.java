public class StatisticalSummary implements java . io . Serializable { public static class Watch { double x ; public int count ; public Watch ( double x ) { this . x = x ; count = 0 ; } public void note ( double val ) { if ( val == x ) { count ++ ; } } public String toString ( ) { return x + " occured " + count + " times " ; } public void reset ( ) { count = 0 ; } } public String name ; private double logsum ; private double sum ; private double sumsq ; private double min ; private double max ; private double mean ; private double gm ; private double sd ; int n ; boolean valid ; public Watch watch ; public StatisticalSummary ( ) { this ( "" ) ; } public StatisticalSummary ( String name ) { this . name = name ; n = 0 ; sum = 0 ; sumsq = 0 ; min = Double . POSITIVE_INFINITY ; max = Double . NEGATIVE_INFINITY ; watch = null ; valid = false ; } public final void reset ( ) { n = 0 ; sum = 0 ; sumsq = 0 ; logsum = 0 ; min = Double . POSITIVE_INFINITY ; max = Double . NEGATIVE_INFINITY ; if ( watch != null ) { watch . reset ( ) ; } } public double max ( ) { return max ; } public double min ( ) { return min ; } public double mean ( ) { if ( ! valid ) computeStats ( ) ; return mean ; } public double gm ( ) { if ( ! valid ) computeStats ( ) ; return gm ; } public static double sigDiff ( StatisticalSummary s1 , StatisticalSummary s2 ) { return StatisticalTests . tNotPaired ( s1 . mean ( ) , s2 . mean ( ) , s1 . sumSquareDiff ( ) , s2 . sumSquareDiff ( ) , s1 . n , s2 . n , true ) ; } public double sumSquareDiff ( ) { return sumsq - n * mean ( ) * mean ( ) ; } private void computeStats ( ) { if ( ! valid ) { mean = sum / n ; gm = Math . exp ( logsum / n ) ; double num = sumsq - ( n * mean * mean ) ; if ( num < 0 ) { num = 0 ; } sd = Math . sqrt ( num / ( n - 1 ) ) ; valid = true ; } } public double sd ( ) { if ( ! valid ) computeStats ( ) ; return sd ; } public int n ( ) { return n ; } public double stdErr ( ) { return sd ( ) / Math . sqrt ( n ) ; } public void add ( StatisticalSummary ss ) { n += ss . n ; sum += ss . sum ; sumsq += ss . sumsq ; logsum += ss . logsum ; max = Math . max ( max , ss . max ) ; min = Math . min ( min , ss . min ) ; valid = false ; } public void add ( double d ) { n ++ ; sum += d ; sumsq += d * d ; if ( d > 0 ) { logsum += Math . log ( d ) ; } min = Math . min ( min , d ) ; max = Math . max ( max , d ) ; if ( watch != null ) { watch . note ( d ) ; } valid = false ; } public void add ( Number n ) { add ( n . doubleValue ( ) ) ; } public void add ( double [ ] d ) { for ( int i = 0 ; i < d . length ; i ++ ) { add ( d [ i ] ) ; } } public void add ( Vector v ) { for ( int i = 0 ; i < v . size ( ) ; i ++ ) { try { add ( ( ( Number ) v . elementAt ( i ) ) . doubleValue ( ) ) ; } catch ( Exception e ) { } } } public String toString ( ) { String s = ( name == null ) ? "" : name + "\n" ; s += " min = " + min ( ) + "\n" + " max = " + max ( ) + "\n" + " ave = " + mean ( ) + "\n" + " sd  = " + sd ( ) + "\n" + " n   = " + n ; return s ; } public void save ( String path ) { try { ObjectOutputStream oos = new ObjectOutputStream ( new FileOutputStream ( path ) ) ; oos . writeObject ( this ) ; oos . close ( ) ; } catch ( Exception e ) { System . out . println ( e ) ; } } public static StatisticalSummary load ( String path ) { try { ObjectInputStream ois = new ObjectInputStream ( new FileInputStream ( path ) ) ; StatisticalSummary ss = ( StatisticalSummary ) ois . readObject ( ) ; ois . close ( ) ; return ss ; } catch ( Exception e ) { System . out . println ( e ) ; return null ; } } public static void main ( String [ ] args ) throws Exception { StatisticalSummary ts1 = new StatisticalSummary ( ) ; StatisticalSummary ts2 = new StatisticalSummary ( ) ; for ( int i = 0 ; i < 100 ; i ++ ) { ts1 . add ( i / 10 ) ; ts2 . add ( i / 10 + new Double ( args [ 0 ] ) . doubleValue ( ) ) ; } System . out . println ( ts1 ) ; System . out . println ( ts2 ) ; System . out . println ( StatisticalSummary . sigDiff ( ts1 , ts2 ) ) ; System . out . println ( ( ts2 . mean ( ) - ts1 . mean ( ) ) / ts1 . stdErr ( ) ) ; System . exit ( 0 ) ; System . out . println ( "Creating summaries" ) ; StatisticalSummary trainSummary = new StatisticalSummary ( ) ; System . out . println ( "1" ) ; System . out . println ( "2" ) ; System . out . println ( "3" ) ; System . out . println ( "4" ) ; System . out . println ( "5" ) ; System . out . println ( "Created summaries" ) ; StatisticalSummary s10 = new StatisticalSummary ( ) ; StatisticalSummary s20 = new StatisticalSummary ( ) ; StatisticalSummary s3 = new StatisticalSummary ( ) ; StatisticalSummary s4 = new StatisticalSummary ( ) ; StatisticalSummary s5 = new StatisticalSummary ( ) ; StatisticalSummary ss = new StatisticalSummary ( "Hello" ) ; for ( int i = 0 ; i < 20 ; i ++ ) { ss . add ( 0.71 ) ; } System . out . println ( ss ) ; System . exit ( 0 ) ; StatisticalSummary s1 = new StatisticalSummary ( ) ; StatisticalSummary s2 = new StatisticalSummary ( ) ; System . out . println ( sigDiff ( s1 , s2 ) ) ; for ( int i = 0 ; i < 20 ; i ++ ) { s1 . add ( Math . random ( ) ) ; s2 . add ( Math . random ( ) + 0.5 ) ; System . out . println ( sigDiff ( s1 , s2 ) ) ; } } } 