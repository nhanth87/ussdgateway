
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ChargingCharacteristics;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "chargingCharacteristicsImpl")
public class ChargingCharacteristicsImpl extends OctetStringBase implements ChargingCharacteristics {

    public static final int _FLAG_NORMAL_CHARGING = 0x08;
    public static final int _FLAG_PREPAID_CHARGING = 0x04;
    public static final int _FLAG_FLAT_RATE_CHARGING_CHARGING = 0x02;
    public static final int _FLAG_CHARGING_BY_HOT_BILLING_CHARGING = 0x01;
    public ChargingCharacteristicsImpl() {
        super(2, 2, "ChargingCharacteristics");
    }

    public ChargingCharacteristicsImpl(byte[] data) {
        super(2, 2, "ChargingCharacteristics", data);
    }

    public ChargingCharacteristicsImpl(boolean isNormalCharging, boolean isPrepaidCharging, boolean isFlatRateChargingCharging,
            boolean isChargingByHotBillingCharging) {
        super(2, 2, "ChargingCharacteristics");

        this.setData(isNormalCharging, isPrepaidCharging, isFlatRateChargingCharging,
                isChargingByHotBillingCharging);
    }

    protected void setData(boolean isNormalCharging, boolean isPrepaidCharging, boolean isFlatRateChargingCharging,
            boolean isChargingByHotBillingCharging){
        this.data = new byte[2];

        if (isNormalCharging)
            this.data[0] |= _FLAG_NORMAL_CHARGING;
        if (isPrepaidCharging)
            this.data[0] |= _FLAG_PREPAID_CHARGING;
        if (isFlatRateChargingCharging)
            this.data[0] |= _FLAG_FLAT_RATE_CHARGING_CHARGING;
        if (isChargingByHotBillingCharging)
            this.data[0] |= _FLAG_CHARGING_BY_HOT_BILLING_CHARGING;
    }

    public byte[] getData() {
        return data;
    }

    private boolean isDataGoodFormed() {
        if (this.data != null && this.data.length == 2)
            return true;
        else
            return false;
    }

    @Override
    public boolean isNormalCharging() {
        if (isDataGoodFormed() && (this.data[0] & _FLAG_NORMAL_CHARGING) != 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean isPrepaidCharging() {
        if (isDataGoodFormed() && (this.data[0] & _FLAG_PREPAID_CHARGING) != 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean isFlatRateChargingCharging() {
        if (isDataGoodFormed() && (this.data[0] & _FLAG_FLAT_RATE_CHARGING_CHARGING) != 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean isChargingByHotBillingCharging() {
        if (isDataGoodFormed() && (this.data[0] & _FLAG_CHARGING_BY_HOT_BILLING_CHARGING) != 0)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        if (isDataGoodFormed()) {
            boolean normalCharging = isNormalCharging();
            boolean prepaidCharging = isPrepaidCharging();
            boolean flatRateChargingCharging = isFlatRateChargingCharging();
            boolean chargingByHotBillingCharging = isChargingByHotBillingCharging();

            StringBuilder sb = new StringBuilder();
            sb.append(_PrimitiveName);
            sb.append(" [Data= ");
            sb.append(this.printDataArr());

            if (normalCharging) {
                sb.append(", normalCharging");
            }
            if (prepaidCharging) {
                sb.append(", prepaidCharging");
            }
            if (flatRateChargingCharging) {
                sb.append(", flatRateChargingCharging");
            }
            if (chargingByHotBillingCharging) {
                sb.append(", chargingByHotBillingCharging");
            }

            sb.append("]");

            return sb.toString();
        } else {
            return super.toString();
        }
    }
}
