package storm.starter.tools;

import storm.starter.util.Constants;
import backtype.storm.tuple.Tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MockTupleHelpers {

  private MockTupleHelpers() {
  }

  public static Tuple mockTickTuple() {
    return mockTuple(Constants.SYSTEM_COMPONENT_ID, Constants.SYSTEM_TICK_STREAM_ID);
  }

  public static Tuple mockTuple(String componentId, String streamId) {
    Tuple tuple = mock(Tuple.class);
    when(tuple.getSourceComponent()).thenReturn(componentId);
    when(tuple.getSourceStreamId()).thenReturn(streamId);
    return tuple;
  }
}
