package com.morning.demo.onedot;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

class OneDotObject {
// 只在onedot包下面可视
	protected static class OneDotVideo {
		public String title;
		public String img;
		public String nextUrl;
		public String desc;
		public String actor;
		public String category;

		@JSONCreator
		public OneDotVideo(@JSONField(name = "title") String title,
				@JSONField(name = "img") String img,
				@JSONField(name = "url") String nextUrl) {
			this.title = title;
			this.img = img;
			this.nextUrl = nextUrl;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("title = ").append(title).append("\n");
			sb.append("img = ").append(img).append("\n");
			sb.append("nextUrl = ").append(nextUrl).append("\n");
			return sb.toString();
		}
		
	}

	private List<OneDotVideo> viedos;
	public String desc;
	public String actor;
	public String category;

	@JSONCreator
	public OneDotObject(
			@JSONField(name = "cates") List<OneDotVideo> viedos,
			@JSONField(name = "desc") String desc,
			@JSONField(name = "actor") String actor,
			@JSONField(name = "category") String category
			) {
		this.viedos = viedos;
		this.desc = desc;
		this.category = category;
		this.actor = actor;
	}

	public int getTotal() {
		return this.viedos.size();
	}

	public List<OneDotVideo> getInfos() {
		return this.viedos;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (OneDotVideo v : viedos) {
			sb.append(v.toString());
		}
		return sb.toString();
	}

}
