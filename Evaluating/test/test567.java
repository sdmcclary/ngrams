public class TimeDatetime extends AbstractDatetime { public static final TimeDatetime THE_INSTANCE = new TimeDatetime ( ) ; public int i ; private static final Pattern THE_PATTERN = Pattern . compile ( "^[ \\t\\r\\n\\f]*(?:(?:([0-9]{4,})-([0-9]{2}))|(?:([0-9]{4,})-([0-9]{2})-([0-9]{2}))|(?:([0-9]{2})-([0-9]{2}))|(?:([0-9]{2}):([0-9]{2})(?::([0-9]{2})(?:\\.([0-9]+))?)?)|(?:([0-9]{4,})-([0-9]{2})-([0-9]{2})(?:T| )([0-9]{2}):([0-9]{2})(?::([0-9]{2})(?:\\.([0-9]+))?)?)|(?:Z|(?:[+-]([0-9]{2}):?([0-9]{2})))|(?:([0-9]{4,})-([0-9]{2})-([0-9]{2})(?:T| )([0-9]{2}):([0-9]{2})(?::([0-9]{2})(?:\\.([0-9]+))?)?(?:Z|(?:[+-]([0-9]{2}):?([0-9]{2}))))|(?:([0-9]{4,})-W([0-9]{2}))|(?:([0-9]{4,}))|(?:P(?:(?:[0-9]+D)|(?:(?:[0-9]+D)?T[0-9]+H)|(?:(?:[0-9]+D)?T(?:[0-9]+H)?[0-9]+M)|(?:(?:[0-9]+D)?T(?:(?:[0-9]+)H)?(?:(?:[0-9]+)M)?(?:[0-9]+(?:\\.([0-9]+))?S))))|(?:[ \\t\\r\\n\\f]*[0-9]+(?:(?:[ \\t\\r\\n\\f]*(?:[Ww]|[Dd]|[Hh]|[Mm]))|(?:(?:\\.([0-9]+))?[ \\t\\r\\n\\f]*[Ss])))+)[ \\t\\r\\n\\f]*$" ) ; private TimeDatetime ( ) { super ( ) ; } @ Override protected Pattern getPattern ( ) { return THE_PATTERN ; } @ Override public String getName ( ) { return "time-datetime" ; } } 