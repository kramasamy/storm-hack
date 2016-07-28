package storm.starter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/**
 * This topology demonstrates Storm's stream groupings and multilang capabilities.
 */
public class WordCountTopology {

  private WordCountTopology() { }

  /**
   * A spout that emits a random word
   */
  static class WordSpout extends BaseRichSpout {
    private Random rnd;
    private SpoutOutputCollector collector;
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
      outputFieldsDeclarer.declare(new Fields("word"));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext,
                     SpoutOutputCollector spoutOutputCollector) {
      rnd = new Random(31);
      collector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
      String[] list = {"Jack", "Mary", "Jill", "McDonald"};
      Utils.sleep(10);
      int nextInt = rnd.nextInt(list.length);
      collector.emit(new Values(list[nextInt]));
    }
  }

  /**
   * A bolt that counts the words that it receives
   */
  static class ConsumerBolt extends BaseRichBolt {
    private OutputCollector collector;
    private Map<String, Integer> countMap;
    private int tupleCount;
    private String taskName;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
      collector = outputCollector;
      countMap = new HashMap<String, Integer>();
      tupleCount = 0;
      taskName = topologyContext.getThisComponentId() + "_" + topologyContext.getThisTaskId();
    }

    @Override
    public void execute(Tuple tuple) {
      String key = tuple.getString(0);

      tupleCount += 1;
      if (tupleCount % 200 == 0) {
        tupleCount = 0;
        System.out.println(taskName + ":" + Arrays.toString(countMap.entrySet().toArray()));
      }

      if (countMap.get(key) == null) {
        countMap.put(key, 1);
      } else {
        Integer val = countMap.get(key);
        countMap.put(key, ++val);
      }

      collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }
  }

  public static void main(String[] args) throws Exception {

    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("word", new WordSpout(), 2);
    builder.setBolt("count", new ConsumerBolt(), 3).fieldsGrouping("word", new Fields("word"));

    Config conf = new Config();
    conf.setDebug(true);

    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
    else {
      conf.setMaxTaskParallelism(3);

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("word-count", conf, builder.createTopology());

      Thread.sleep(10000);

      cluster.shutdown();
    }
  }
}
