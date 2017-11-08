package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kkkkk on 2016/6/22.
 * 用户安全问题
 */
public class UserQuestionEntity {

	@SerializedName("questionName")
	private String questionName;

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}
}
