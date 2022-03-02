package com.nowcoder.community.community;

import com.nowcoder.community.community.dao.AlphaDao;
import com.nowcoder.community.community.service.AlphaService;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.SpringVersion;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		this.applicationContext = applicationContext;
	}

	@Test
	void Testapplicationcontest() {
		//System.out.print(applicationContext);
		AlphaDao alphadao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphadao.select());
		AlphaDao alphaDao = applicationContext.getBean("mybits",AlphaDao.class);
		System.out.println(alphaDao.select());
	}
	@Test
	void TestBeanManage(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}
	@Test
	void TestBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format((new Date())));
	}
	@Autowired
	@Qualifier("mybits")
	private AlphaDao alphaDao;
	@Autowired
	private SimpleDateFormat simpleDateFormat;
	@Autowired
	private AlphaService alphaService;
	@Test
	void TestDI(){
		System.out.println(alphaDao);
	}
	@Test
	public void getSpringVersion() {

		String version = SpringVersion.getVersion();

		String version1 = SpringBootVersion.getVersion();

		System.out.println(version);

		System.out.println(version1);

	}
}
