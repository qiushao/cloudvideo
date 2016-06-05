package com.morning.demo.onedot;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class VideoDescriptionObject {

	public static class OneDotVideo {
		public String title;
		public String img;
		public String url;

		@JSONCreator
		public OneDotVideo(@JSONField(name = "title") String title,
				@JSONField(name = "img") String img,
				@JSONField(name = "url") String url) {
			this.title = title;
			this.img = img;
			this.url = url;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("title = ").append(title).append("\n");
			sb.append("img = ").append(img).append("\n");
			sb.append("url = ").append(url).append("\n");
			return sb.toString();
		}
		
	}

	private Document doc;
	private List<OneDotVideo> viedos;
	private int total; 
	private String title; 
	private String description; 
	private String imgUrl; //海报地址
	private String lasted; //更新集数
	private String playTime; //上映时间
	private String area; //上映地区
	private String type; //影片类型
	private String company; //发行公司
	private String intro; //简介
	private String actors; //演员
	private String director; //导演
	private String writer; //编剧
	private String compere; //主持人
	private String channel; //电视台
	private String firstPlay; //首播时间
	
	public List<OneDotVideo> getViedos() {
		return viedos;
	}

	public void setViedos(List<OneDotVideo> viedos) {
		this.viedos = viedos;
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getIntro() {
		try { //避免没有简介时抛出数组溢出
			intro = intro.substring(5);
		} catch(Exception exception) { }
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	@JSONCreator
	public VideoDescriptionObject(
			@JSONField(name = "title") String title,
			@JSONField(name = "countvi") int total,
			@JSONField(name = "description") String description,
			@JSONField(name = "img") String img,
			@JSONField(name = "lasted") String lasted,
			@JSONField(name = "cates") List<OneDotVideo> viedos
			) {
		this.setTotal(total);
		this.setTitle(title);
		this.setDescription(description);
		this.setImgUrl(img);
		this.setLasted(lasted);
		this.viedos = viedos;
	}

	public List<OneDotVideo> getInfos() {
		return this.viedos;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getCompere() {
		return compere;
	}

	public void setCompere(String compere) {
		this.compere = compere;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getFirstPlay() {
		return firstPlay;
	}

	public void setFirstPlay(String firstPlay) {
		this.firstPlay = firstPlay;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public String getLasted() {
		return lasted;
	}

	public void setLasted(String lasted) {
		this.lasted = lasted;
	}

	public void parseHtmlDescription() {
		if (null == description || description.isEmpty()) {
			return;
		}
		doc = Jsoup.parseBodyFragment(description);
		doc.select(".show_all").remove();
		doc.select("#show_all_more_dot").remove();
		
		Element showAllMoreTag = doc.select("#show_all_more").first();
		if(null != doc.select("#show_all_more").first()) {
			String showAllMore = showAllMoreTag.text();
			doc.select("#show_all_more").remove();
			Element intro = doc.getElementsByClass("intro").first();
			intro.text(intro.text() + showAllMore);
		}
		
		if(null != doc.getElementsByClass("intro").first()) {
			this.intro = doc.getElementsByClass("intro").first().text();
		} else {
			this.intro = "";
		}
		actors = getElement("主演");
		area = getElement("地区");
		director = getElement("导演");
		type = getElement("类型");
		writer = getElement("编剧");
		company = getElement("发行公司");
		playTime = getElement("上映时间");
		compere = getElement("主持人");
		channel = getElement("电视台");
		firstPlay = getElement("首播");
	}
	
	private String getElement(String key) {
		Element element = doc.select("li:contains(" + key + ":)").first();
		if(null != element) {
			return element.text();
		} else {
			return null;
		}
	}
}
