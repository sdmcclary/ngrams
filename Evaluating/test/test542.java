<<<<<<< HEAD
public final class WordCompletionProposalComputer implements ICompletionProposalComputer , ICompletionListener { static WordRanker basicWordRanker = new WordRanker ( ) ; static UserWordRanker userWordRanker = new UserWordRanker ( basicWordRanker ) ; static String stateLocation ; static { Runtime . getRuntime ( ) . addShutdownHook ( new Thread ( ) { public void run ( ) { dispose ( ) ; } } ) ; } private static final int PREFIX_RANK_SHIFT = 500 ; public WordCompletionProposalComputer ( ) { stateLocation = com . aptana . semantic . ui . text . spelling . Activator . getDefault ( ) . getStateLocation ( ) . toString ( ) ; try { basicWordRanker . loadFromStream ( new FileInputStream ( stateLocation + "\\rates.txt" ) ) ; userWordRanker . loadFromStream ( new FileInputStream ( stateLocation + "\\userRates.txt" ) ) ; } catch ( FileNotFoundException e ) { } } public List computeCompletionProposals ( ContentAssistInvocationContext context , IProgressMonitor monitor ) { if ( this . contributes ( ) ) { try { final IDocument document = context . getDocument ( ) ; final int offset = context . getInvocationOffset ( ) ; final IRegion region = document . getLineInformationOfOffset ( offset ) ; final String content = document . get ( region . getOffset ( ) , region . getLength ( ) ) ; int index = offset - region . getOffset ( ) - 1 ; while ( ( index >= 0 ) && Character . isLetter ( content . charAt ( index ) ) ) { index -- ; } final int start = region . getOffset ( ) + index + 1 ; boolean isSentenceBeginning = checkSentenceBeginning ( start , document ) ; final String candidate = content . substring ( index + 1 , offset - region . getOffset ( ) ) ; if ( candidate . length ( ) > 0 ) { final ISpellCheckEngine engine = SpellCheckEngine . getInstance ( ) ; final ISpellChecker checker = engine . getSpellChecker ( ) ; if ( checker != null ) { Set proposals2 = null ; if ( checker instanceof DefaultSpellChecker ) { final DefaultSpellChecker sp = ( DefaultSpellChecker ) checker ; proposals2 = sp . getCompletionProposals ( candidate , Character . isUpperCase ( candidate . charAt ( 0 ) ) ) ; } else { proposals2 = checker . getProposals ( candidate , Character . isUpperCase ( candidate . charAt ( 0 ) ) ) ; } final List proposals = new ArrayList ( proposals2 ) ; final List result = new ArrayList ( proposals . size ( ) ) ; for ( final Iterator it = proposals . iterator ( ) ; it . hasNext ( ) ; ) { final RankedWordProposal word = ( RankedWordProposal ) it . next ( ) ; String text = word . getText ( ) ; if ( text . startsWith ( candidate ) ) { word . setRank ( word . getRank ( ) + PREFIX_RANK_SHIFT ) ; } if ( isSentenceBeginning && text . length ( ) > 0 ) text = Character . toUpperCase ( text . charAt ( 0 ) ) + text . substring ( 1 ) ; result . add ( new SpellingCompletionProposal ( text , start , candidate . length ( ) , text . length ( ) , JavaPluginImages . get ( JavaPluginImages . IMG_CORRECTION_RENAME ) , text , null , null , userWordRanker ) ) ; } ; Collections . sort ( result , new Comparator < SpellingCompletionProposal > ( ) { public int compare ( SpellingCompletionProposal o1 , SpellingCompletionProposal o2 ) { String displayString = o1 . getDisplayString ( ) ; int a1 = userWordRanker . getRateForWord ( displayString ) ; if ( a1 < 0 ) a1 = userWordRanker . putWithDefaultRank ( displayString ) ; if ( displayString . startsWith ( candidate ) ) { a1 = a1 / 2 - 1 ; } String displayString2 = o2 . getDisplayString ( ) ; int a2 = userWordRanker . getRateForWord ( displayString2 ) ; if ( a2 < 0 ) a2 = userWordRanker . putWithDefaultRank ( displayString2 ) ; if ( displayString2 . startsWith ( candidate ) ) { a2 = a2 / 2 - 1 ; } if ( a1 < a2 ) return - 1 ; if ( a1 > a2 ) return 1 ; if ( displayString . length ( ) < displayString2 . length ( ) ) return - 1 ; if ( displayString . length ( ) > displayString2 . length ( ) ) return 1 ; return 0 ; } } ) ; return result ; } } } catch ( final BadLocationException exception ) { Activator . log ( exception ) ; } } return Collections . EMPTY_LIST ; } public static boolean checkSentenceBeginning ( int offset , final IDocument document ) throws BadLocationException { if ( offset > 0 ) offset -- ; while ( offset > 0 && ( document . get ( offset , 1 ) . equals ( " " ) || document . get ( offset , 1 ) . equals ( "\t" ) ) ) offset -- ; if ( offset == 0 || document . get ( offset , 1 ) . equals ( "." ) || document . get ( offset , 1 ) . equals ( "\r" ) || document . get ( offset , 1 ) . equals ( "\n" ) ) return true ; return false ; } private boolean contributes ( ) { return true || PreferenceConstants . getPreferenceStore ( ) . getBoolean ( PreferenceConstants . SPELLING_ENABLE_CONTENTASSIST ) ; } public List computeContextInformation ( ContentAssistInvocationContext context , IProgressMonitor monitor ) { return Collections . EMPTY_LIST ; } public String getErrorMessage ( ) { return null ; } public void sessionStarted ( ) { } public void sessionEnded ( ) { } public static void dispose ( ) { try { FileOutputStream fos = new FileOutputStream ( stateLocation + "\\rates.txt" ) ; basicWordRanker . saveToStream ( fos ) ; fos . close ( ) ; fos = new FileOutputStream ( stateLocation + "\\userRates.txt" ) ; userWordRanker . saveToStream ( fos ) ; fos . close ( ) ; } catch ( FileNotFoundException e ) { e . printStackTrace ( ) ; } catch ( IOException e ) { e . printStackTrace ( ) ; } } public void assistSessionEnded ( ContentAssistEvent event ) { } public void assistSessionStarted ( ContentAssistEvent event ) { } public void selectionChanged ( ICompletionProposal proposal , boolean smartToggle ) { } } 
=======
public class Iri extends IriRef { public static final Iri THE_INSTANCE = new Iri ( ) ; protected Iri ( ) { super ( ) ; } protected boolean isAbsolute ( ) { return true ; } @ Override public String getName ( ) { return "absolute IRI" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
