package com.wjika.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Liu_Zhichao on 2016/9/2 11:03.
 * 校验
 */
public class VerifyUtils {

	private static int[] weight = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};//十七位数字本体码权重
	private static char[] validate = { '1','0','X','9','8','7','6','5','4','3','2'};//mod11,对应校验码字符值

	/**
	 * @return 18位身份证校验
	 */
	public static boolean verifyIdentityCard(String id18){
		if (id18 != null && 18 == id18.length()){
			String id17 = id18.substring(0, 17);
			char code = getValidateCode(id17);
			if (id18.equals(id17 + code)){
				return true;
			}
		}
		return false;
	}

	private static char getValidateCode(String id17){
		int sum=0;
		int mode;
		for(int i = 0; i < id17.length(); i++){
			sum = sum + Integer.parseInt(String.valueOf(id17.charAt(i))) * weight[i];
		}
		mode = sum % 11;
		return validate[mode];
	}

	/**
	 * 验证手机号码
	 * @param phoneNumber 手机号码
	 * @return boolean
	 */
	public static boolean checkPhoneNumber(String phoneNumber){
		Pattern pattern=Pattern.compile("^1[0-9]{10}$");
		Matcher matcher=pattern.matcher(phoneNumber);
		return matcher.matches();
	}

	/**
	 * 不建议使用，有很多正常银行卡不能通过
	 */
	@Deprecated
	public static boolean verifyBankNo(String bankNo) {
		int sumOdd = 0, sumEven = 0;
		String reverse = new StringBuffer(bankNo).reverse().toString();
		for (int i = 0; i < reverse.length(); i++) {
			int digit = Character.digit(reverse.charAt(i), 10);
			if (i % 2 == 0) {//this is for odd digits, they are 1-indexed in the algorithm
				sumOdd += digit;
			} else {//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
				sumEven += 2 * digit;
				if (digit >= 5) {
					sumEven -= 9;
				}
			}
		}
		int sum = sumOdd + sumEven;
		return sum > 0 && sum % 10 == 0;
	}
}
