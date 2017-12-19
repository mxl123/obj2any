package protoTool.Colum;

import com.google.protobuf.CodedOutputStream;
import org.apache.solr.common.SolrDocument;
import protoTool.Parse.ProtoFieldType;
import protoTool.Parse.ProtoParseResult;
import protoTool.Parse.ProtoParseResultField;

import java.io.IOException;

import static protoTool.Parse.ProtoSolrDocumentParse.parseSolrDocument;

/**
 * ${PACKAGE_NAME}
 *
 * wfksgrpc-nodubbo
 *
 * Created by maxuliang on 2017/9/7.
 */
public class ProtoColumSize extends ProtoGoogleSize {

    /**
     * 根据SolrDocument获取对应protoStream的size
     * @param document SolrDocument类型对象
     * @return size
     * @throws IOException exception
     */
    public static int getProtoStreamSize(SolrDocument document) throws IOException {

        ProtoParseResult result = parseSolrDocument(document);

        return getProtoStreamSize(result);
    }

    /**
     * 获取collection value的size，其中parentField在计算size时，
     * 有些类型要赋值给protoCollectMemoizedSize，在write时使用
     * @param result Parse解析结果
     * @param parentField collection的key对应的Field
     * @return collection value size
     * @throws IOException exception
     */
    private static int getCollectStreamSize(ProtoParseResult result, ProtoParseResultField parentField) throws IOException {
        int size = 0;

        int needFeedSize = 1;
        /**
         * tag > 15 key占用空间为2
         */
        if (parentField.protoFieldNumber > 15) {
            needFeedSize = 2;
        }
        for (ProtoParseResultField tmpField : result.protoParseResultFields) {
            switch (tmpField.protoFieldType) {
                case Proto_Bool: {
                    int dataSize = 0;
                    dataSize = 1 * result.protoParseResultFields.size();
                    size += dataSize;
                    size += needFeedSize;
                    size += CodedOutputStream.computeInt32SizeNoTag(dataSize);
                    parentField.protoCollectMemoizedSize = dataSize;
                }
                break;
                case Proto_Enum: {
                    int dataSize = 0;
                    dataSize = getProtoStreamSize(result);
                    size += dataSize;
                    size += CodedOutputStream.computeUInt32SizeNoTag(dataSize);
                    parentField.protoCollectMemoizedSize = dataSize;
                }
                break;
                case Proto_Float: {
                    int dataSize = 0;
                    dataSize = 4 * result.protoParseResultFields.size();
                    size += dataSize;
                    size += needFeedSize;
                    size += CodedOutputStream.computeInt32SizeNoTag(dataSize);
                    parentField.protoCollectMemoizedSize = dataSize;
                }
                break;
                case Proto_Int32: {
                    int dataSize = 0;
                    dataSize = getProtoStreamSize(result);
                    size += dataSize;
                    size += needFeedSize;
                    size += CodedOutputStream.computeInt32SizeNoTag(dataSize);
                    parentField.protoCollectMemoizedSize = dataSize;
                }
                break;
                case Proto_Double: {
                    int dataSize = 0;
                    dataSize = 8 * result.protoParseResultFields.size();
                    size += dataSize;
                    size += needFeedSize;
                    size += CodedOutputStream.computeInt32SizeNoTag(dataSize);
                    parentField.protoCollectMemoizedSize = dataSize;
                }
                break;
                case Proto_String: {
                    int dataSize = 0;
                    dataSize = getProtoStreamSize(result);
                    size += dataSize;
                    size += needFeedSize * result.protoParseResultFields.size();
                }
                break;
                case Proto_Collect: {
                    // collect中不可能包含collect，只能包含message
                }
                break;
                case Proto_SolrDocument: {

                }
                break;
                case Proto_Unknow: {

                }
                break;
            }
            break;
        }
        return size;
    }

    /**
     * 根据parse结果，获取stream的size
     * @param result 解析结果
     * @return size
     * @throws IOException exception
     */
    @SuppressWarnings("uncheck")
    public static int getProtoStreamSize(ProtoParseResult result) throws IOException {
        int size = 0;

        for (ProtoParseResultField field : result.protoParseResultFields) {

            ProtoFieldType protoFieldType = field.protoFieldType;

            switch (protoFieldType) {
                case Proto_Bool: {
                    size += computeBoolSize(field);
                }
                break;
                case Proto_Enum: {
                    size += computeEnumSize(field);
                }
                break;
                case Proto_Float: {
                    size += computeFloatSize(field);
                }
                break;
                case Proto_Int32: {
                    size += computeIntSize(field);
                }
                break;
                case Proto_Double: {
                    size += computeDoubleSize(field);
                }
                break;
                case Proto_String: {
                    size += computeStringSize(field);
                }
                break;
                case Proto_Collect: {
                    ProtoParseResult protoParseResult = (ProtoParseResult) field.protoFieldValue;
                    /**
                     * collection对应的value size计算，有在循环外和循环内进行，因此这里不递归调用
                     * 在getCollectStreamSize中进行处理
                     */
                    size += getCollectStreamSize(protoParseResult, field);
                }
                break;
                case Proto_SolrDocument: {
                    ProtoParseResult tmpResult = (ProtoParseResult) field.protoFieldValue;
                    size += CodedOutputStream.computeTagSize(field.protoFieldNumber);
                    int tmpSize = getProtoStreamSize(tmpResult);
                    size += (CodedOutputStream.computeUInt32SizeNoTag(tmpSize) + tmpSize);
                }
                break;
                case Proto_Unknow: {

                }
                break;
            }
        }
        return size;
    }
}
