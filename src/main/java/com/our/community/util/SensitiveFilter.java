package com.our.community.util;

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

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //注解PostConstruct，表示当实例化该bean，在带哦用构造器时自动调用该方法，达到初始化树效果
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");//读取文件
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {//读取文件的每行数据
                //将敏感词添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败！" + e.getMessage());
        }
    }

    /**
     * 敏感词添加逻辑
     *
     * @param keyword
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            //判断节点下是否还有字节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束表示
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }

    }

    //    检索敏感词
    public String filter(String text) {
        //判断字符串是否为空
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //创建三个指针
        TrieNode tempNode = rootNode;//指针一，对应前缀树的根节点
        int begin = 0;//指针一，对应字符串检测的第一个字符
        int position = 0;//指针三，对应字符串检测除第一个字符外的其他字符

        StringBuilder sb = new StringBuilder();

        //指针三到达最后则表示该字符串检测完毕
        while (position < text.length()) {
            char c = text.charAt(position);//获取该指针对应字符

            //跳过特殊符号
            if (isSymbol(c)) {
                //若指针一属于根节点，将此符号计入结果，指针二下移
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或者中间，指针三都下移
                position++;
                continue;
            }
            //检测是否为敏感词
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //以begin开头的字符并不是敏感词，可以计入结果
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //指针一重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {//发现敏感词
                sb.append(REPLACEMENT);
                //进入下一位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            } else {
                //检查下一字符
                position++;
            }
        }
        //将最后字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //0x2E80~0x9FFF表示东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树
     */
    private class TrieNode {
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        private boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        private void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        private void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        private TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}
