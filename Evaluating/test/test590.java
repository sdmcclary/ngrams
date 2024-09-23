public class Circle extends AbstractInt { public static final Circle THE_INSTANCE = new Circle ( ) ; private Circle ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { List < CharSequenceWithOffset > list = split ( literal , ',' ) ; if ( list . size ( ) != 3 ) { throw newDatatypeException ( "A circle must have three comma-separated integers." ) ; } CharSequenceWithOffset withOffset = list . get ( 0 ) ; checkInt ( withOffset . getSequence ( ) , withOffset . getOffset ( ) ) ; withOffset = list . get ( 1 ) ; checkInt ( withOffset . getSequence ( ) , withOffset . getOffset ( ) ) ; withOffset = list . get ( 2 ) ; checkIntNonNegative ( withOffset . getSequence ( ) , withOffset . getOffset ( ) ) ; } @ Override public String getName ( ) { return "circle" ; } } 