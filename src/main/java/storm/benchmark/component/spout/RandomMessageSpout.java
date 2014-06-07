package storm.benchmark.component.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;
import java.util.Random;

public class RandomMessageSpout extends BaseRichSpout {

  private static final long serialVersionUID = -4100642374496292646L;
  public static final String FIELDS = "message";
  public static final String MESSAGE_SIZE = "message.size";
  public static final int DEFAULT_MESSAGE_SIZE = 100;

  private final int sizeInBytes;
  private long messageCount = 0;
  private SpoutOutputCollector collector;
  private String [] messages = null;
  private final boolean ackEnabled;
  private Random rand = null;

  public RandomMessageSpout(boolean ackEnabled) {
    this(DEFAULT_MESSAGE_SIZE, ackEnabled);
  }

  public RandomMessageSpout(int sizeInBytes, boolean ackEnabled) {
    this.sizeInBytes = sizeInBytes;
    this.ackEnabled = ackEnabled;
  }

  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    this.rand = new Random();
    this.collector = collector;
    final int differentMessages = 100;
    this.messages = new String[differentMessages];
    for(int i = 0; i < differentMessages; i++) {
      StringBuilder sb = new StringBuilder(sizeInBytes);
      for(int j = 0; j < sizeInBytes; j++) {
        sb.append(rand.nextInt(9));
      }
      messages[i] = sb.toString();
    }
  }


  @Override
  public void nextTuple() {
    final String message = messages[rand.nextInt(messages.length)];
    if(ackEnabled) {
      collector.emit(new Values(message), messageCount);
      messageCount++;
    } else {
      collector.emit(new Values(message));
    }
  }


  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields(FIELDS));
  }
}