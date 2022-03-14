package com.nowcoder.community.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct//初始化方法，在实例化前调用
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine())  != null){
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败!",e.getMessage());
        }
    }


    //敏感词添加到前缀树中
    private void addKeyWord(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0 ; i < keyword.length();i ++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            tempNode = subNode;

            if(i == keyword.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    //前缀树trie
    private class TrieNode{//关键词结束点
        private boolean isKeyWordEnd = false;

        //子节点 key 是下级字符，value是下级节点
        private Map<Character,TrieNode> subnodes = new HashMap();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subnodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subnodes.get(c);
        }
    }
    //判断是否为符号
    public boolean isSymbol(Character c){
        //0x2080-0x9FFF是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && c <0x2E80 || c > 0x9FFF;
    }

    //铭感词过滤算法
    public  String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1指向树
        TrieNode tempNode = rootNode;
        //指针2指向当前检测字符串首位置
        int begin = 0;
        //指针3指向当前检测字符串末尾位置
        int position = 0;
        // 结果
        StringBuilder res = new StringBuilder();
        while(position < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    res.append(c);
                    begin ++;
                }
                position ++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                res.append(text.charAt(begin));
                begin ++;
                position = begin;
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                res.append(REPLACEMENT);
                position ++;
                begin = position;
                tempNode = rootNode;
            }else {
                position ++;
            }
        }
        res.append(text.substring(begin));
        return res.toString();
    }
}
