package protoTool.Parse;


public class ProtoParseResultField {
    // collect类型，在计算size的时候赋值为ProtoParseResult类，此时只有value, type, number有值
    /**
     * 字段类型
     */
    public ProtoFieldType protoFieldType;
    /**
     * 字段tag
     */
    public int protoFieldNumber;
    /**
     * 字段对应value
     */
    public Object protoFieldValue;
    /**
     * 字段key==>name
     */
    public Object protoFieldKey;
    /**
     * 计算大小和write时，是否需要附带tag参数
     */
    public boolean protoHasTag;
    /**
     * repeated字段，对应的collection大小，默认-1，计算size时变化，write时使用
     */
    public int protoCollectMemoizedSize = -1;
}
