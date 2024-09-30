
package es.caib.ripea.plugin.caib.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ModeTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ModeTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="attached"/>
 *     &lt;enumeration value="embedded"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ModeTypeEnum")
@XmlEnum
public enum ModeTypeEnum {

    @XmlEnumValue("attached")
    ATTACHED("attached"),
    @XmlEnumValue("embedded")
    EMBEDDED("embedded");
    private final String value;

    ModeTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ModeTypeEnum fromValue(String v) {
        for (ModeTypeEnum c: ModeTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
