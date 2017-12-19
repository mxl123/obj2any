package protoTool.Colum;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import protoTool.Parse.ProtoParseResultField;

/**
 * ${PACKAGE_NAME}
 *
 * wfksgrpc-nodubbo
 *
 * Created by maxuliang on 2017/9/7.
 */
public class ProtoGoogleSize {

    /**
     * 获取枚举类型size
     * @param field 字段
     * @return Enum类型size
     */
    protected static int computeEnumSize(ProtoParseResultField field) {
        int tmpValue = (Integer) field.protoFieldValue;
        if (field.protoHasTag) {
            return CodedOutputStream.computeEnumSize(field.protoFieldNumber, tmpValue);
        } else {
            return CodedOutputStream.computeEnumSizeNoTag(tmpValue);
        }
    }

    /**
     * 获取double类型size
     * @param field 字段
     * @return double类型size
     */
    protected static int computeDoubleSize(ProtoParseResultField field) {
        double tmpValue = (Double) field.protoFieldValue;
        if (field.protoHasTag) {
            return CodedOutputStream.computeDoubleSize(field.protoFieldNumber, tmpValue);
        } else {
            return CodedOutputStream.computeDoubleSizeNoTag(tmpValue);
        }
    }

    /**
     * 获取Bool类型size
     * @param field 字段
     * @return Bool类型字段
     */
    protected static int computeBoolSize(ProtoParseResultField field) {
        boolean tmpValue = (Boolean) field.protoFieldValue;
        if (field.protoHasTag) {
            return CodedOutputStream.computeBoolSize(field.protoFieldNumber, tmpValue);
        } else {
            return CodedOutputStream.computeBoolSizeNoTag(tmpValue);
        }
    }

    /**
     * 获取Int类型size
     * @param field 字段
     * @return Int类型size
     */
    protected static int computeIntSize(ProtoParseResultField field) {
        int tmpValue = (Integer) field.protoFieldValue;
        if (field.protoHasTag) {
            return CodedOutputStream.computeInt32Size(field.protoFieldNumber, tmpValue);
        } else {
            return CodedOutputStream.computeInt32SizeNoTag(tmpValue);
        }
    }

    /**
     * 获取Float类型size
     * @param field 字段
     * @return Float类型size
     */
    protected static int computeFloatSize(ProtoParseResultField field) {
        float tmpValue = Float.parseFloat(field.protoFieldValue.toString());
        if (field.protoHasTag) {
            return CodedOutputStream.computeFloatSize(field.protoFieldNumber, tmpValue);
        } else {
            return CodedOutputStream.computeFloatSizeNoTag(tmpValue);
        }
    }

    /**
     * 获取字符串size
     * @param field 字段
     * @return 字符串size
     */
    protected static int computeStringSize(ProtoParseResultField field) {
        if (field.protoHasTag) {
            if (field.protoFieldValue instanceof String) {
                return CodedOutputStream.computeStringSize(field.protoFieldNumber, (String) field.protoFieldValue);
            } else {
                return CodedOutputStream.computeBytesSize(field.protoFieldNumber, (ByteString) field.protoFieldValue);
            }
        }
        return computeStringSizeNoTag(field.protoFieldValue);
    }

    /**
     * 获取字符串size（without tag）
     * @param value 字段值
     * @return 字符串size
     */
    private static int computeStringSizeNoTag(final Object value) {
        if (value instanceof String) {
            return CodedOutputStream.computeStringSizeNoTag((String) value);
        } else {
            return CodedOutputStream.computeBytesSizeNoTag((ByteString) value);
        }
    }

}
