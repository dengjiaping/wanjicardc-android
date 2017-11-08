package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/12/3.
 */

public class ExchangeCardPageEntity {
	@SerializedName("pageNum")
	private int pageNum;
	@SerializedName("pageSize")
	private int pageSize;
	@SerializedName("pages")
	private int pages;
	@SerializedName("currentQuota")
	private String currentQuota;
	@SerializedName("residualQuota")
	private String residuaQuota;
	@SerializedName("result")
	private ExchangeResultEntity result;

	public ExchangeResultEntity getResult() {
		return result;
	}

	public void setResult(ExchangeResultEntity result) {
		this.result = result;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getCurrentQuota() {
		return currentQuota;
	}

	public void setCurrentQuota(String currentQuota) {
		this.currentQuota = currentQuota;
	}

	public String getResiduaQuota() {
		return residuaQuota;
	}

	public void setResiduaQuota(String residuaQuota) {
		this.residuaQuota = residuaQuota;
	}

	public class ExchangeResultEntity {
		@SerializedName("activityURL")
		private String url;
		@SerializedName("list")
		private List<ExchangeCardEntity> entities;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<ExchangeCardEntity> getEntities() {
			return entities;
		}

		public void setEntities(List<ExchangeCardEntity> entities) {
			this.entities = entities;
		}
	}

}
