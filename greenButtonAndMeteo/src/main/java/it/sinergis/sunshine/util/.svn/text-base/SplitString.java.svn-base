package it.sinergis.sunshine.util;

public class SplitString {
	public static void main(String args[]) {
		String s = "https://sunshine.hep.hr/DataCustodian/espi/1_1/resource/RetailCustomer/1/UsagePoint/3089672/MeterReading/3089672/IntervalBlock";
		System.out.println(getUsagePointId(s));
		System.out.println(getMeterReadingId(s));
		
	}
	
	public static Integer getUsagePointId(String identifier) {
		try {
			int index = identifier.indexOf("UsagePoint");
			if (index <= 0) {
				return -1;
			}
			String s2 = identifier.substring(index + "UsagePoint".length() + 1);
			index = s2.indexOf('/');
			if (index <= 0) {
				return -1;
			}
			return Integer.parseInt(s2.substring(0, index));
		}
		catch (Exception e) {
			return -1;
		}
	}
	
	public static Integer getMeterReadingId(String identifier) {
		try {
			int index = identifier.indexOf("MeterReading");
			if (index <= 0) {
				return -1;
			}
			String s2 = identifier.substring(index + "MeterReading".length() + 1);
			index = s2.indexOf('/');
			if (index <= 0) {
				return -1;
			}
			return Integer.parseInt(s2.substring(0, index));
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
