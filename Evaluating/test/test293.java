<<<<<<< HEAD
@ SuppressWarnings ( "boxing" ) public class RadioTapHeader extends AbstractPacket { int version ; int pad ; int length ; int presentFields ; long tsft ; int channelFrequency ; int channelFlags ; int fhss ; int rate ; int dBmSignal ; int dBmNoise ; int dBSignal ; int dBNoise ; int lockQuality ; int txAttenuation ; int dBTxAttenuation ; int dBmTxPower ; int flags ; int antenna ; static int IEEE80211_RADIOTAP_TSFT = 1 << 0 ; static int IEEE80211_RADIOTAP_FLAGS = 1 << 1 ; static int IEEE80211_RADIOTAP_RATE = 1 << 2 ; static int IEEE80211_RADIOTAP_CHANNEL = 1 << 3 ; static int IEEE80211_RADIOTAP_FHSS = 1 << 4 ; static int IEEE80211_RADIOTAP_DBM_ANTSIGNAL = 1 << 5 ; static int IEEE80211_RADIOTAP_DBM_ANTNOISE = 1 << 6 ; static int IEEE80211_RADIOTAP_LOCK_QUALITY = 1 << 7 ; static int IEEE80211_RADIOTAP_TX_ATTENUATION = 1 << 8 ; static int IEEE80211_RADIOTAP_DB_TX_ATTENUATION = 1 << 9 ; static int IEEE80211_RADIOTAP_DBM_TX_POWER = 1 << 10 ; static int IEEE80211_RADIOTAP_ANTENNA = 1 << 11 ; static int IEEE80211_RADIOTAP_DB_ANTSIGNAL = 1 << 12 ; static int IEEE80211_RADIOTAP_DB_ANTNOISE = 1 << 13 ; static int IEEE80211_RADIOTAP_EXT = 1 << 31 ; static int IEEE80211_CHAN_TURBO = 0x0010 ; static int IEEE80211_CHAN_CCK = 0x0020 ; static int IEEE80211_CHAN_OFDM = 0x0040 ; static int IEEE80211_CHAN_2GHZ = 0x0080 ; static int IEEE80211_CHAN_5GHZ = 0x0100 ; static int IEEE80211_CHAN_PASSIVE = 0x0200 ; static int IEEE80211_CHAN_DYN = 0x0400 ; static int IEEE80211_CHAN_GFSK = 0x0800 ; static int IEEE80211_RADIOTAP_F_CFP = 0x01 ; static int IEEE80211_RADIOTAP_F_SHORTPRE = 0x02 ; static int IEEE80211_RADIOTAP_F_WEP = 0x04 ; static int IEEE80211_RADIOTAP_F_FRAG = 0x08 ; static int IEEE80211_RADIOTAP_F_FCS = 0x10 ; private final boolean nativeBigEndian = ( ByteOrder . nativeOrder ( ) == ByteOrder . BIG_ENDIAN ) ; protected int hostOrderUnpack16 ( ) { if ( nativeBigEndian ) { return unpack16 ( ) ; } else { return swap16 ( unpack16 ( ) ) ; } } protected int hostOrderUnpack32 ( ) { if ( nativeBigEndian ) { return unpack32 ( ) ; } else { return swap32 ( unpack32 ( ) ) ; } } @ Override protected int minimumHeaderLength ( ) { return 8 ; } @ Override protected void packHeader ( ) { } @ Override protected void unpackHeader ( ) { version = unpack8 ( ) ; pad = unpack8 ( ) ; length = hostOrderUnpack16 ( ) ; presentFields = hostOrderUnpack32 ( ) ; if ( ( presentFields & IEEE80211_RADIOTAP_EXT ) != 0 ) { System . out . println ( String . format ( "extra bits 0x%08x" , hostOrderUnpack32 ( ) ) ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_TSFT ) != 0 ) { tsft = unpack64 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_FLAGS ) != 0 ) { flags = unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_RATE ) != 0 ) { rate = unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_CHANNEL ) != 0 ) { channelFrequency = hostOrderUnpack16 ( ) ; channelFlags = hostOrderUnpack16 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_FHSS ) != 0 ) { fhss = hostOrderUnpack16 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_DBM_ANTSIGNAL ) != 0 ) { dBmSignal = ( byte ) unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_DBM_ANTNOISE ) != 0 ) { dBmNoise = ( byte ) unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_LOCK_QUALITY ) != 0 ) { lockQuality = hostOrderUnpack16 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_TX_ATTENUATION ) != 0 ) { txAttenuation = hostOrderUnpack16 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_DB_TX_ATTENUATION ) != 0 ) { dBTxAttenuation = - unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_DBM_TX_POWER ) != 0 ) { dBmTxPower = - unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_ANTENNA ) != 0 ) { antenna = unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_DB_ANTSIGNAL ) != 0 ) { dBSignal = - unpack8 ( ) ; } if ( ( presentFields & IEEE80211_RADIOTAP_DB_ANTNOISE ) != 0 ) { dBNoise = - unpack8 ( ) ; } } @ Override public int headerLength ( ) { if ( length == 0 ) { return 8 ; } else { return length + pad ; } } public Long tsft ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_TSFT ) != 0 ) { return tsft ; } return null ; } public Integer flags ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_FLAGS ) != 0 ) { return flags ; } return null ; } public Integer rate ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_RATE ) != 0 ) { return rate ; } return null ; } public Integer channelFrequency ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_CHANNEL ) != 0 ) { return channelFrequency ; } return null ; } public Integer channelFlags ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_CHANNEL ) != 0 ) { return channelFlags ; } return null ; } public Integer channel ( ) { Integer freq = channelFrequency ( ) ; if ( freq == null ) return null ; if ( ( channelFlags & IEEE80211_CHAN_2GHZ ) != 0 ) { if ( freq == 2484 ) return 14 ; if ( freq < 2484 ) return ( freq - 2407 ) / 5 ; else return 15 + ( ( freq - 2512 ) / 20 ) ; } if ( ( channelFlags & IEEE80211_CHAN_5GHZ ) != 0 ) { return ( freq - 5000 ) / 5 ; } if ( freq == 2484 ) return 14 ; if ( freq < 2484 ) return ( freq - 2407 ) / 5 ; if ( freq < 5000 ) return 15 + ( ( freq - 2512 ) / 20 ) ; return ( freq - 5000 ) / 5 ; } public Integer fhss ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_FHSS ) != 0 ) { return fhss ; } return null ; } public Integer dBmSignal ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_DBM_ANTSIGNAL ) != 0 ) { return dBmSignal ; } return null ; } public Integer dBmNoise ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_DBM_ANTNOISE ) != 0 ) { return dBmNoise ; } return null ; } public Integer lockQuality ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_LOCK_QUALITY ) != 0 ) { return lockQuality ; } return null ; } public Integer txAttenuation ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_TX_ATTENUATION ) != 0 ) { return txAttenuation ; } return null ; } public Integer dBTxAttenuation ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_DB_TX_ATTENUATION ) != 0 ) { return dBTxAttenuation ; } return null ; } public Integer dBmTxPower ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_DBM_TX_POWER ) != 0 ) { return dBmTxPower ; } return null ; } public Integer antenna ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_ANTENNA ) != 0 ) { return antenna ; } return null ; } public Integer dBSignal ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_DB_ANTSIGNAL ) != 0 ) { return dBSignal ; } return null ; } public Integer dBNoise ( ) { if ( ( presentFields & IEEE80211_RADIOTAP_DB_ANTNOISE ) != 0 ) { return dBNoise ; } return null ; } @ Override public String toString ( ) { String answer = "RadioTap" ; if ( tsft ( ) != null ) { answer += " TSFT" ; } if ( channelFrequency ( ) != null ) { answer += " Channel=" + channelFrequency ( ) + "Mhz" ; if ( ( channelFlags & IEEE80211_CHAN_TURBO ) != 0 ) answer += " Turbo" ; if ( ( channelFlags & IEEE80211_CHAN_CCK ) != 0 ) answer += " CCK" ; if ( ( channelFlags & IEEE80211_CHAN_OFDM ) != 0 ) answer += " OFDM" ; if ( ( channelFlags & IEEE80211_CHAN_2GHZ ) != 0 ) answer += " 2GHz" ; if ( ( channelFlags & IEEE80211_CHAN_5GHZ ) != 0 ) answer += " 5GHz" ; if ( ( channelFlags & IEEE80211_CHAN_PASSIVE ) != 0 ) answer += " Passive" ; if ( ( channelFlags & IEEE80211_CHAN_DYN ) != 0 ) answer += " CCK-OFDM" ; if ( ( channelFlags & IEEE80211_CHAN_GFSK ) != 0 ) answer += " GFSK" ; } if ( rate ( ) != null ) { answer += " rate=" + ( rate ( ) * 500 ) + "kb/s" ; } if ( dBSignal ( ) != null ) { answer += " signal=" + dBSignal ( ) + "dB" ; } if ( dBNoise ( ) != null ) { answer += " noise=" + dBNoise ( ) + "dB" ; } if ( dBmSignal ( ) != null ) { answer += " signal=" + dBmSignal ( ) + "dBm" ; } if ( dBmNoise ( ) != null ) { answer += " noise=" + dBmNoise ( ) + "dBm" ; } if ( lockQuality ( ) != null ) { answer += " quality=" + lockQuality ( ) ; } if ( antenna ( ) != null ) { answer += " antenna=" + antenna ( ) ; } if ( flags ( ) != null ) { if ( ( flags & IEEE80211_RADIOTAP_F_CFP ) != 0 ) { answer += " CFP" ; } if ( ( flags & IEEE80211_RADIOTAP_F_SHORTPRE ) != 0 ) { answer += " ShortPreamble" ; } if ( ( flags & IEEE80211_RADIOTAP_F_WEP ) != 0 ) { answer += " WEP" ; } if ( ( flags & IEEE80211_RADIOTAP_F_FRAG ) != 0 ) { answer += " Fragmentation" ; } if ( ( flags & IEEE80211_RADIOTAP_F_FCS ) != 0 ) { answer += " FCS" ; } } return answer ; } } 
=======
class ActionSet { private ResultAction resultAction ; private NoResultAction [ ] noResultActions = new NoResultAction [ 0 ] ; private boolean cancelNestedActions ; ResultAction getResultAction ( ) { return resultAction ; } void setResultAction ( ResultAction resultAction ) { this . resultAction = resultAction ; } void addNoResultAction ( NoResultAction action ) { NoResultAction [ ] actions = new NoResultAction [ noResultActions . length + 1 ] ; System . arraycopy ( noResultActions , 0 , actions , 0 , noResultActions . length ) ; actions [ noResultActions . length ] = action ; noResultActions = actions ; } NoResultAction [ ] getNoResultActions ( ) { return noResultActions ; } boolean getCancelNestedActions ( ) { return cancelNestedActions ; } void setCancelNestedActions ( boolean cancelNestedActions ) { this . cancelNestedActions = cancelNestedActions ; } ActionSet changeCurrentMode ( Mode mode ) { ActionSet actions = new ActionSet ( ) ; if ( this . resultAction != null ) actions . resultAction = this . resultAction . changeCurrentMode ( mode ) ; actions . noResultActions = new NoResultAction [ this . noResultActions . length ] ; for ( int i = 0 ; i < actions . noResultActions . length ; i ++ ) actions . noResultActions [ i ] = this . noResultActions [ i ] . changeCurrentMode ( mode ) ; return actions ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
