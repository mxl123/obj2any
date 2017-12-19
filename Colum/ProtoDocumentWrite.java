package protoTool.Colum;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
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
public class ProtoDocumentWrite extends ProtoGoogleWrite {

    /**
     * 根据SolrDocument获取ByteString对象
     * @param document SolrDocument，NoNull
     * @return ByteString（protoBuf）
     * @throws IOException exception
     */
    public static ByteString getProtoStream(SolrDocument document) throws IOException {

        if (document == null) {
            return null;
        }
        /**
         * 解析SolrDocument
         */
        ProtoParseResult result = parseSolrDocument(document);

        return writeValue(result);
    }

    /**
     * 根据解析结果，创建buffer并写入数据，转化为ByteString（protoBuf）
     * @param result 解析结果
     * @return ByteString（protoBuf）
     * @throws IOException exception
     */
    private static ByteString writeValue(ProtoParseResult result) throws IOException {

        byte[] buffer;

        buffer = new byte[ProtoColumSize.getProtoStreamSize(result)];

        CodedOutputStream outputStream = CodedOutputStream.newInstance(buffer);

        writeValue(outputStream, result);

        outputStream.checkNoSpaceLeft();

        return ByteString.copyFrom(buffer);
    }

    /**
     * 根据解析结果，写入CodedOutputStream数据
     * @param outputStream CodedOutputStream
     * @param result 解析结果
     * @throws IOException exception
     */
    private static void writeValue(
            CodedOutputStream outputStream, ProtoParseResult result) throws IOException {

        for (ProtoParseResultField field : result.protoParseResultFields) {

            ProtoFieldType protoFieldType = field.protoFieldType;

            switch (protoFieldType) {
                case Proto_Bool: {
                    writeBool(outputStream, field);
                }
                break;
                case Proto_Enum: {
                    writeEnum(outputStream, field);
                }
                break;
                case Proto_Float: {
                    writeFloat(outputStream, field);
                }
                break;
                case Proto_Int32: {
                    writeInt32(outputStream, field);
                }
                break;
                case Proto_Double: {
                    writeDouble(outputStream, field);
                }
                break;
                case Proto_String: {
                    writeString(outputStream, field);
                }
                break;
                case Proto_Collect: {
                    ProtoParseResult protoParseResult = (ProtoParseResult) field.protoFieldValue;
                    /**
                     * TODO 类型判断，需要在知道SolrDocument属性形式之后修改
                     */
                    boolean isStringTypeCollect = false;
                    for (ProtoParseResultField tmpField : protoParseResult.protoParseResultFields) {
                        if (tmpField.protoFieldType.equals(ProtoFieldType.Proto_String)) {
                            isStringTypeCollect = true;
                            break;
                        }
                    }
                    /**
                     * 对于非字符串类型的collection的操作
                     * 字符串类型跳过，直接writeValue
                     */
                    if (!isStringTypeCollect) {
                        outputStream.writeUInt32NoTag(field.protoFieldNumber * 8 + 2);
                        outputStream.writeUInt32NoTag(-1);
                    }
                    writeValue(outputStream, protoParseResult);
                }
                break;
                case Proto_SolrDocument: {
                    ProtoParseResult tmpResult = (ProtoParseResult) field.protoFieldValue;
                    assert tmpResult != null;
                    outputStream.writeTag(field.protoFieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
                    outputStream.writeUInt32NoTag(ProtoColumSize.getProtoStreamSize(tmpResult));
                    writeValue(outputStream, tmpResult);
                }
                break;
                case Proto_Unknow: {

                }
                break;
            }
        }
    }

}
