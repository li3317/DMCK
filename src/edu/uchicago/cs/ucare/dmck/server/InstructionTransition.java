package edu.uchicago.cs.ucare.dmck.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.uchicago.cs.ucare.dmck.transition.NodeCrashTransition;
import edu.uchicago.cs.ucare.dmck.transition.NodeStartTransition;
import edu.uchicago.cs.ucare.dmck.transition.PacketSendTransition;
import edu.uchicago.cs.ucare.dmck.transition.SleepTransition;
import edu.uchicago.cs.ucare.dmck.transition.Transition;

abstract class InstructionTransition {

  abstract Transition getRealTransition(ModelCheckingServerAbstract dmck);

}


class PacketSendInstructionTransition extends InstructionTransition {

  protected final Logger LOG = LoggerFactory.getLogger(this.getClass());;

  long packetId;

  public PacketSendInstructionTransition(long packetId) {
    this.packetId = packetId;
  }

  @Override
  Transition getRealTransition(ModelCheckingServerAbstract dmck) {
    LOG.debug("to real transition from dmck/server");
    LOG.debug("num trans: " + dmck.currentEnabledTransitions.size());
    for (Object t : dmck.currentEnabledTransitions) {
      LOG.debug(String.valueOf(t.getClass()));
      if (t instanceof PacketSendTransition) {
        LOG.debug("packet send transition");
        PacketSendTransition p = (PacketSendTransition) t;
        if (p.getTransitionId() == packetId) {
          return p;
        }
      }
    }
    return null;
  }

}


class NodeCrashInstructionTransition extends InstructionTransition {

  int id;

  protected NodeCrashInstructionTransition(int id) {
    this.id = id;
  }

  @Override
  Transition getRealTransition(ModelCheckingServerAbstract dmck) {
    return new NodeCrashTransition(dmck, id);
  }

}


class NodeStartInstructionTransition extends InstructionTransition {

  int id;

  protected NodeStartInstructionTransition(int id) {
    this.id = id;
  }

  @Override
  Transition getRealTransition(ModelCheckingServerAbstract dmck) {
    return new NodeStartTransition(dmck, id);
  }

}


class SleepInstructionTransition extends InstructionTransition {

  long sleep;

  protected SleepInstructionTransition(long sleep) {
    this.sleep = sleep;
  }

  @Override
  Transition getRealTransition(ModelCheckingServerAbstract dmck) {
    return new SleepTransition(sleep);
  }

}


class ExitInstructionTransaction extends InstructionTransition {

  @SuppressWarnings("serial")
  @Override
  Transition getRealTransition(ModelCheckingServerAbstract dmck) {
    return new Transition() {

      @Override
      public long getTransitionId() {
        return 0;
      }

      @Override
      public boolean apply() {
        System.exit(0);
        return true;
      }

      @Override
      public int[][] getVectorClock() {
        throw new UnsupportedOperationException("Not implemented");
      }

      @Override
      public void setVectorClock(int[][] vectorClock) {

      }
    };
  }

}
