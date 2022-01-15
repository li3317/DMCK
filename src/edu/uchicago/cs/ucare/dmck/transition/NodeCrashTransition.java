package edu.uchicago.cs.ucare.dmck.transition;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.uchicago.cs.ucare.dmck.server.ModelCheckingServerAbstract;

@SuppressWarnings("serial")
public class NodeCrashTransition extends NodeOperationTransition {

  final static Logger LOG = LoggerFactory.getLogger(NodeCrashTransition.class);

  public static final String ACTION = "nodecrash";
  private static final short ACTION_HASH = (short) ACTION.hashCode();

  protected ModelCheckingServerAbstract dmck;

  public NodeCrashTransition(ModelCheckingServerAbstract dmck, int id) {
    this.dmck = dmck;
    this.id = id;
  }

  @Override
  public boolean apply() {
    LOG.info("Killing node " + id);
    if (dmck.killNode(id, vectorClock)) {
      dmck.numCurrentCrash++;
      for (Transition t : dmck.currentEnabledTransitions) {
        if (t instanceof AbstractNodeStartTransition) {
          ((AbstractNodeStartTransition) t).setPossibleVectorClock(id,
              dmck.getVectorClock(id, dmck.numNode));
        } else if (t instanceof NodeStartTransition) {
          if (((NodeStartTransition) t).id == id) {
            ((NodeStartTransition) t).setVectorClock(dmck.getVectorClock(id, dmck.numNode));
          }
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public long getTransitionId() {
    final long prime = 31;
    long hash = 1;
    hash = prime * hash + id;
    long tranId = ((long) ACTION_HASH) << 16;
    tranId = tranId | (0x0000FFFF & hash);
    return tranId;
  }

  public String toString() {
//    return "nodecrash id=" + id + " " + Arrays.deepToString(vectorClock);
    return "nodecrash id=" + id + " " + Arrays.deepToString(this.getVectorClock());
  }

  public int getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodeCrashTransition other = (NodeCrashTransition) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toStringForFutureExecution() {
    return "nodecrash id=" + id;
  }

  @Override
  public synchronized NodeCrashTransition clone() {
    return new NodeCrashTransition(this.dmck, this.id);
  }

//   protected int[][] vectorClock;
//
  @Override
  public int[][] getVectorClock() {
    return vectorClock;
  }
//
//  public void setVectorClock(int[][] vectorClock) {
//    this.vectorClock = new int[vectorClock.length][vectorClock.length];
//    for (int i = 0; i < vectorClock.length; ++i) {
//      for (int j = 0; j < vectorClock.length; ++j) {
//        this.vectorClock[i][j] = vectorClock[i][j];
//      }
//    }
//  }

}
