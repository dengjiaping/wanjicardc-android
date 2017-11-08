package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leo_Zhang on 2015/12/7.
 * 安全问题
 */
public class QuestionItemEntity extends Entity {

	@SerializedName("questionName")
	private String context;
	@SerializedName("Createtime")
	private String createtime;
	@SerializedName("Operator")
	private String operator;
	@SerializedName("id")
	private String id;
	@SerializedName("Question")
	private String question;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
