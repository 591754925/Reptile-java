package com.yhj.reptile.web.util;

import java.util.Date;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;

public class MyProcessor implements PageProcessor{

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	@Override
	public void process(Page page) {
		// 定义如何抽取页面信息，并保存下来
		if (!page.getUrl().regex("(https://blog\\.csdn\\.net/static_coder/article/details/\\d+)").match()) {
			// 从页面发现后续的url地址来抓取
			// 获取当前页面所有的列表的链接
			page.addTargetRequests(page.getHtml().xpath("//div[@id='article_list']").links()
					.regex("(https://blog\\.csdn\\.net/static_coder/article/details/\\d+)").all());
			//抓取分页数据 --https://blog.csdn.net/static_coder/article/list/2
			page.addTargetRequests(page.getHtml().xpath("//div[@id='papelist']").links()
					.regex("(https://blog\\.csdn\\.net/static_coder/article/list/\\d+)").all());
		}else {
			page.putField("title", page.getHtml().xpath("//h1/span[@class='link_title']/a/text()"));
			page.putField("sign", page.getHtml()
					.xpath("//div[@class='article_l']/span[@class='link_categories']/a/text()").all());
			page.putField("date", page.getHtml()
					.xpath("//div[@class='article_r']/span[@class='link_postdate']/text()"));
			page.putField("view", page.getHtml()
					.xpath("//div[@class='article_r']/span[@class='link_view']/text()")
					.toString().replaceAll("\\D+", ""));
			page.putField("comment", page.getHtml()
					.xpath("//div[@class='article_r']/span[@class='link_comments']/text()")
					.toString().replaceAll("\\D+", ""));
			page.putField("content", page.getHtml()
					.xpath("//div[@class='details']/div[@id='article_content']/html()"));
			page.putField("splitline", "-----------------分割线-------------------");
		}
	}

	@Override
	public Site getSite() {

		return site;
	}


	public static void main(String[] args) {
		long startTime = new Date().getTime();

		System.out.println("------------spider start >>>  -----------");

		Spider.create(new GithubRepoPageProcessor()).addUrl("http://blog.sina.com.cn/s/articlelist_1487828712_0_1.html")
		.run();

		long endTime = new Date().getTime();
		System.out.println("耗时：" + (endTime - startTime));
	}

}
