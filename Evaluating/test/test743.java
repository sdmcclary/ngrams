abstract public class CopyOnWriteManager < E > implements Cloneable { private class Latch extends AbstractQueuedSynchronizer { Latch ( final boolean triggered ) { setState ( triggered ? 0 : 1 ) ; } public int tryAcquireShared ( final int acquires ) { return getState ( ) == 0 ? 1 : - 1 ; } public boolean tryReleaseShared ( final int releases ) { return compareAndSetState ( 1 , 0 ) ; } } private static final int MUTATE = 1 ; private static final int MUTATE_AFTER_FREEZE = 2 ; private static final int BULK_READ = 3 ; private static final int BULK_READ_AFTER_FREEZE = 4 ; private class COWEpoch extends EpochNode { private final Latch _activated ; final boolean mutationAllowed ; E value ; int initialSize ; private volatile E _frozenValue ; volatile boolean dirty ; final AtomicReference < COWEpoch > successorRef = new AtomicReference < COWEpoch > ( null ) ; Epoch . Ticket successorTicket ; boolean freezeRequested ; private COWEpoch ( final boolean mutationAllowed ) { this . _activated = new Latch ( false ) ; this . mutationAllowed = mutationAllowed ; } public COWEpoch ( final E value , final E frozenValue , final int initialSize ) { this . _activated = new Latch ( true ) ; this . mutationAllowed = true ; this . value = value ; this . initialSize = initialSize ; this . _frozenValue = frozenValue ; this . dirty = frozenValue == null ; } EpochNode attemptInitialArrive ( ) { return super . attemptArrive ( ) ; } @ Override public EpochNode attemptArrive ( ) { final EpochNode ticket = super . attemptArrive ( ) ; if ( ticket != null && ! dirty ) { dirty = true ; _frozenValue = null ; } return ticket ; } private void setFrozenValue ( final E v ) { if ( ! dirty ) { _frozenValue = v ; if ( dirty ) { _frozenValue = null ; } } } E getFrozenValue ( ) { final E v = _frozenValue ; return dirty ? null : v ; } protected void onClosed ( final int dataSum ) { assert ( dataSum == 0 || dirty ) ; final COWEpoch succ = successorRef . get ( ) ; if ( freezeRequested ) { succ . value = freezeAndClone ( value ) ; succ . setFrozenValue ( value ) ; } else { succ . value = value ; if ( dirty ) { succ . dirty = true ; } else { succ . setFrozenValue ( _frozenValue ) ; } } succ . initialSize = initialSize + dataSum ; _active = succ ; successorTicket . leave ( 0 ) ; succ . _activated . releaseShared ( 1 ) ; } public void awaitActivated ( ) { _activated . acquireShared ( 1 ) ; } public COWEpoch getOrCreateSuccessor ( final boolean preferredMutation ) { final COWEpoch existing = successorRef . get ( ) ; if ( existing != null ) { return existing ; } final COWEpoch repl = new COWEpoch ( preferredMutation ) ; if ( attemptInstallSuccessor ( repl ) ) { return repl ; } return successorRef . get ( ) ; } public boolean attemptInstallSuccessor ( final COWEpoch succ ) { final Epoch . Ticket t = succ . attemptInitialArrive ( ) ; if ( successorRef . compareAndSet ( null , succ ) ) { successorTicket = t ; beginClose ( ) ; return true ; } else { return false ; } } } private volatile COWEpoch _active ; public CopyOnWriteManager ( final E initialValue , final int initialSize ) { _active = new COWEpoch ( initialValue , null , initialSize ) ; } abstract protected E freezeAndClone ( final E value ) ; abstract protected E cloneFrozen ( E frozenValue ) ; public CopyOnWriteManager < E > clone ( ) { final CopyOnWriteManager < E > copy ; try { copy = ( CopyOnWriteManager < E > ) super . clone ( ) ; } catch ( final CloneNotSupportedException xx ) { throw new Error ( "unexpected" , xx ) ; } COWEpoch a = _active ; E f = a . getFrozenValue ( ) ; while ( f == null ) { a . freezeRequested = true ; final COWEpoch succ = a . getOrCreateSuccessor ( a . mutationAllowed ) ; succ . awaitActivated ( ) ; if ( a . value != succ . value ) { f = a . value ; } a = succ ; } copy . createNewEpoch ( f , a ) ; return copy ; } private void createNewEpoch ( E f , COWEpoch a ) { _active = new COWEpoch ( cloneFrozen ( f ) , f , a . initialSize ) ; } public E read ( ) { return _active . value ; } public Epoch . Ticket beginMutation ( ) { return begin ( true ) ; } public Epoch . Ticket beginQuiescent ( ) { return begin ( false ) ; } private Epoch . Ticket begin ( final boolean mutation ) { final COWEpoch active = _active ; if ( active . mutationAllowed == mutation ) { final Epoch . Ticket ticket = active . attemptArrive ( ) ; if ( ticket != null ) { return ticket ; } } return begin ( mutation , active ) ; } private Epoch . Ticket begin ( final boolean mutation , COWEpoch epoch ) { while ( true ) { COWEpoch succ = epoch . successorRef . get ( ) ; if ( succ == null ) { final COWEpoch newEpoch = new COWEpoch ( mutation ) ; final Epoch . Ticket newTicket = newEpoch . attemptArrive ( ) ; if ( epoch . attemptInstallSuccessor ( newEpoch ) ) { newEpoch . awaitActivated ( ) ; return newTicket ; } succ = epoch . successorRef . get ( ) ; } if ( succ . mutationAllowed == mutation ) { final Epoch . Ticket ticket = succ . attemptArrive ( ) ; if ( ticket != null ) { succ . awaitActivated ( ) ; return ticket ; } } epoch = succ ; } } public E mutable ( ) { return _active . value ; } public E frozen ( ) { COWEpoch a = _active ; E f = a . getFrozenValue ( ) ; while ( f == null ) { a . freezeRequested = true ; final COWEpoch succ = a . getOrCreateSuccessor ( a . mutationAllowed ) ; succ . awaitActivated ( ) ; if ( a . value != succ . value ) { f = a . value ; } a = succ ; } return f ; } public E availableFrozen ( ) { return _active . getFrozenValue ( ) ; } public boolean isEmpty ( ) { return size ( ) == 0 ; } public int size ( ) { final COWEpoch a = _active ; final Integer delta = a . attemptDataSum ( ) ; if ( delta != null ) { return a . initialSize + delta ; } final COWEpoch succ = a . getOrCreateSuccessor ( a . mutationAllowed ) ; succ . awaitActivated ( ) ; return succ . initialSize ; } } 