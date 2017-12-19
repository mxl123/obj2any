package protoTool.Colum;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import protoTool.Parse.ProtoParseResultField;

import java.io.IOException;

/**
 * ${PACKAGE_NAME}
 *
 * wfksgrpc-nodubbo
 *
 * Created by maxuliang on 2017/9/7.
 */
public class ProtoGoogleWrite {

    /**
     * 写入int32类型数据
     * @param output CodedOutputStream
     * @param protoParseResultField 字段
     * @throws IOException exception
     */
    protected static void writeInt32(
            CodedOutputStream output, ProtoParseResultField protoParseResultField) throws IOException{
        int tmpValue = (Integer) protoParseResultField.protoFieldValue;
        if (protoParseResultField.protoHasTag) {
            output.writeInt32(protoParseResultField.protoFieldNumber, tmpValue);
        } else {
            output.writeInt32NoTag(tmpValue);
        }
    }

    /**
     * 写入float类型数据
     * @param output CodedOutputStream
     * @param protoParseResultField 字段
     * @throws IOException exception
     */
    static void writeFloat(
            CodedOutputStream output, ProtoParseResultField protoParseResultField) throws IOException{
        float tmpValue = Float.parseFloat(protoParseResultField.protoFieldValue.toString());
        if (protoParseResultField.protoHasTag) {
            output.writeFloat(protoParseResultField.protoFieldNumber, tmpValue);
        } else {
            output.writeFloatNoTag(tmpValue);
        }

    }

    /**
     * 写入bool类型数据
     * @param output CodedOutputStream
     * @param protoParseResultField 字段
     * @throws IOException exception
     */
    static void writeBool(
            CodedOutputStream output, ProtoParseResultField protoParseResultField) throws IOException{
        boolean tmpValue = (Boolean) protoParseResultField.protoFieldValue;
        if (protoParseResultField.protoHasTag) {
            output.writeBool(protoParseResultField.protoFieldNumber, tmpValue);
        } else {
            output.writeBoolNoTag(tmpValue);
        }

    }

    /**
     * 写入Enum类型数据
     * @param output CodedOutputStream
     * @param protoParseResultField 字段
     * @throws IOException exception
     */
    static void writeEnum(
            CodedOutputStream output, ProtoParseResultField protoParseResultField) throws IOException{
        int tmpValue = (Integer) protoParseResultField.protoFieldValue;
        if (protoParseResultField.protoHasTag) {
            output.writeEnum(protoParseResultField.protoFieldNumber, tmpValue);
        } else {
            output.writeEnumNoTag(tmpValue);
        }

    }

    /**
     * 写入double类型数据
     * @param output CodedOutputStream
     * @param protoParseResultField 字段
     * @throws IOException exception
     */
    static void writeDouble(
            CodedOutputStream output, ProtoParseResultField protoParseResultField) throws IOException{
        double tmpValue = (Double) protoParseResultField.protoFieldValue;
        if (protoParseResultField.protoHasTag) {
            output.writeDouble(protoParseResultField.protoFieldNumber, tmpValue);
        } else {
            output.writeDoubleNoTag(tmpValue);
        }

    }

    /**
     * 写入string类型数据
     * @param output CodedOutputStream
     * @param protoParseResultField 字段
     * @throws IOException exception
     */
    protected static void writeString(
            CodedOutputStream output, ProtoParseResultField protoParseResultField) throws IOException {
        if (protoParseResultField.protoFieldValue instanceof String) {
            output.writeString(protoParseResultField.protoFieldNumber,
                    (String) protoParseResultField.protoFieldValue);
        } else {
            output.writeBytes(protoParseResultField.protoFieldNumber,
                    (ByteString) protoParseResultField.protoFieldValue);
        }
    }
}
