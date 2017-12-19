package protoTool.Parse;

/**
 * ${PACKAGE_NAME}
 *
 * wfksgrpc-nodubbo
 *
 * Created by maxuliang on 2017/9/7.
 */
public enum ProtoFieldType {
    Proto_Int32,
    Proto_String,
    Proto_Float,
    Proto_Double,
    Proto_Enum,
    Proto_Bool,
    Proto_Collect,
    Proto_SolrDocument,
    /**
     * 未知字段类型
     */
    Proto_Unknow
}