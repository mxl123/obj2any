package protoTool.Parse;

import org.apache.solr.common.SolrDocument;
import java.util.*;

import static javafx.scene.input.KeyCode.V;

/**
 * ${PACKAGE_NAME}
 *
 * wfksgrpc-nodubbo
 *
 * Created by maxuliang on 2017/9/7.
 */
public class ProtoSolrDocumentParse {

    /**
     * 解析数据对象入口，数据解析过程，只是将数据转换为ProtoParseResult对象，并包含filedNumber
     * 结果可交由 ProtoColumSize 和 ProtoDocumentWrite 处理
     * TODO 添加fieldTypes字段，用于匹配类型，需要在知道SolrDocument属性形式之后修改
     * @param document solr查询结果
     * @return 解析后返回对象内容包括：fieldNumber，fieldType，fieldValue，hasTag
     */
    public static ProtoParseResult parseSolrDocument(SolrDocument document) {

        ProtoParseResult protoParseResult = new ProtoParseResult();

        if (document == null) {
            return null;
        }
        int fieldNumber = 1;

        Map<String, Object> mapValue = document.getFieldValueMap();

        Collection<ProtoParseResultField> protoParseResultFieldCollection = new ArrayList<>();

        for (String keyString : mapValue.keySet()) {

            Object object = document.getFieldValue(keyString);

            ProtoParseResultField protoParseResultField = new ProtoParseResultField();
            protoParseResultField.protoFieldKey = keyString;
            protoParseResultField.protoFieldValue = object;
            protoParseResultField.protoFieldNumber = fieldNumber;
            protoParseResultField.protoHasTag = true;

            /**
             TODO 类型判断，需要根据SolrDocument中field的类型判断，这种判断在没有value的时候无法判断类型
             TODO 嵌套message类型判断需要验证 需要在知道SolrDocument属性形式之后修改
             */
            if (object instanceof Integer) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Int32;
            } else if (object instanceof String) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_String;
            } else if (object instanceof Float) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Float;
            } else if (object instanceof Double) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Double;
            } else if (object instanceof Boolean) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Bool;
            } else if (object.getClass().isEnum()) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Enum;
            } else if ( (object instanceof Collection) ||
                      (object instanceof Iterable) ||
                      (object instanceof Object[]) ) {
                /**
                 * TODO 测试通过，待优化
                 */
                Collection<Object> collection = document.getFieldValues(keyString);

                boolean isDocType = false;

                SolrDocument tmpDoc = new SolrDocument();

                for (Object value : collection) {
                    Class cls = value.getClass();

                    Class inerCls = cls.getDeclaringClass();

                    if (inerCls != null) {
                        /**
                         * public SolrDocument(){_fields = new LinkedHashMap<>();}
                         *
                         * 当SolrDocument的Value是SolrDocument类型时，会把value的值存放在ArrayList中
                         * 作为value
                         * document.addField("field", document1);
                         *
                         * 如果下面条件成立，表示collection是SolrDocument类型
                         */

                        if (cls.getDeclaringClass().equals(LinkedHashMap.class) ||
                                cls.equals(LinkedHashMap.class)) {
                            ArrayList arrayList = (ArrayList) collection;
                            for (Object anArrayList : arrayList) {
                                Map.Entry entry = (Map.Entry) anArrayList;
                                tmpDoc.addField((String) entry.getKey(), entry.getValue());
                            }
                            isDocType = true;
                        }
                    }
                    break;
                }
                if (isDocType) {
                    protoParseResultField.protoFieldType = ProtoFieldType.Proto_SolrDocument;
                    protoParseResultField.protoFieldValue = parseSolrDocument(tmpDoc);
                } else  {
                    protoParseResultField.protoFieldType = ProtoFieldType.Proto_Collect;
                    protoParseResultField.protoFieldValue = computeRepeatedParse(fieldNumber, keyString, document);
                }

            }
            /**
             * 这种类型判断目前无效，SolrDocument作为value放入另外一个SolrDocument中，会被转为ArrayList
             *
             * 暂时保留，后续修改
             */
            else if (object.getClass().getName().equals("SolrDocument")) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_SolrDocument;

                protoParseResultField.protoFieldValue = parseSolrDocument( (SolrDocument) object);
            } else {
                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Unknow;
            }
            protoParseResultFieldCollection.add(protoParseResultField);
            fieldNumber++;
        }
        protoParseResult.protoParseResultFields = protoParseResultFieldCollection;
        return protoParseResult;
    }

    /**
     * 解析collection类型数据
     * @param parentFieldNumber 该参数用于记录collection对应key的tag，因为collection类型对应的repeated类型，
     *                          在计算size和写入stream的时候，tag不变，需要用到key对应的tag
     * @param keyValue collection对应的key值
     * @param document SolrDocument对象，用于获取key对应的collection，不能为空
     * @return 解析结果
     */
    private static ProtoParseResult computeRepeatedParse(final int parentFieldNumber, final String keyValue, SolrDocument document) {
        return computeRepeatedParse(parentFieldNumber, keyValue, document, null);
    }

    /**
     * 解析collection类型数据
     * @param parentFieldNumber 该参数用于记录collection对应key的tag，因为collection类型对应的repeated类型，
     *                          在计算size和写入stream的时候，tag不变，需要用到key对应的tag
     * @param objectValue collection数据
     * @return 解析结果
     */
    private static ProtoParseResult computeRepeatedParse(final int parentFieldNumber, final Object objectValue) {
        return computeRepeatedParse(parentFieldNumber, null, null, objectValue);
    }

    /**
     * 解析collection类型数据，不能出现 objectValue == null && document == null
     * @param parentFieldNumber 该参数用于记录collection对应key的tag，因为collection类型对应的repeated类型，
     *                          在计算size和写入stream的时候，tag不变，需要用到key对应的tag
     * @param keyValue collection对应的key值
     * @param document SolrDocument对象，用于获取key对应的collection，可为空
     * @param objectValue collection数据
     * @return 解析结果
     */
    private static ProtoParseResult computeRepeatedParse(
            final int parentFieldNumber, final String keyValue, SolrDocument document, final Object objectValue) {
        if (objectValue == null && document == null) {
            System.err.println("can not send both of document and objectValue by null");
            return null;
        }
        ProtoParseResult protoParseResult = new ProtoParseResult();

        Collection<ProtoParseResultField> protoParseResultFieldCollection = new ArrayList<>();

        int tmpFieldNumber = 1;

        Collection<Object> collection;
        if (document != null) {
            collection = document.getFieldValues(keyValue);
        } else {
            collection = getCollectionWithObject(objectValue);
        }


        // value是collection时，collection中可能存放的其他message，也就是其他SolrDocument，也可能是java数据类型
        for (Object value : collection) {

//            System.out.println(value.getClass().getName());


            ProtoParseResultField protoParseResultField = new ProtoParseResultField();
            /**
             * collection内部使用的tag，是collection对应的key的tag，不能递增，不同于嵌套message
             */
            protoParseResultField.protoFieldNumber = parentFieldNumber;
            protoParseResultField.protoFieldValue = value;
            /**
             * repeated除了message，string，其他的都用NoTag方法
             */
            protoParseResultField.protoHasTag = false;

            /**
             * 这种情况是嵌套其他message
             */
            if (value instanceof SolrDocument) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_SolrDocument;
                protoParseResultField.protoFieldValue = parseSolrDocument((SolrDocument) value);
            }
            // 下面的情况 repeated的是java数据类型，
            else if (value instanceof Integer) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Int32;
            } else if (value instanceof String) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_String;
            } else if (value instanceof Float) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Float;
            } else if (value instanceof Double) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Double;
            } else if (value instanceof Boolean) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Bool;
            } else if (value.getClass().isEnum()) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Enum;
            } else if ( (value instanceof Collection) ||
                    (value instanceof Iterable) ||
                    (value instanceof Object[]) ) {

                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Collect;
                protoParseResultField.protoFieldValue = computeRepeatedParse(tmpFieldNumber, value);
            } else {
                protoParseResultField.protoFieldType = ProtoFieldType.Proto_Unknow;
            }
            protoParseResultFieldCollection.add(protoParseResultField);
            tmpFieldNumber++;
        }
        protoParseResult.protoParseResultFields = protoParseResultFieldCollection;
        return protoParseResult;
    }

    /**
     * Object类型转Collection
     * @param object Object对象
     * @return Collection结果
     */
    @SuppressWarnings("unchecked")
    private static Collection<Object> getCollectionWithObject(Object object) {
        if (object == null) {
            return null;
        }
        if( object instanceof Collection ) {
            return (Collection<Object>)object;
        }
        ArrayList<Object> arr = new ArrayList<>(1);
        arr.add( object );
        return arr;
    }

}
