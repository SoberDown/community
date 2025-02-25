package com.lxc.community.util;

import lombok.Data;
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

    //初始化方法
    @PostConstruct//标记一个方法,表示该方法在类实例化之后立即执行;这个类在服务启动的时候就初始化,紧接着就执行这个方法
    public void init(){
        try(
                //字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");//获取编译后文件,在/target/classes下
                //把字节流转换成字符流,再传给缓冲流变为缓冲流,提高运行效率
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while ((keyword = reader.readLine()) != null){//每次读一行,当每次读取不为空时,则继续循环
                //添加前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("上传敏感词文件失败:" + e.getMessage());
        }
    }


    //敏感词添加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;//临时节点默认为根节点
        for (int i = 0;i<keyword.length();i++){
            char c = keyword.charAt(i);//过滤词的第i个字
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null){//判断是否有一样的子节点,没有则新建一个子节点
                //初始化子节点
                subNode = new TrieNode();
                //把子节点放到当前树下
                tempNode.addSubNode(c,subNode);
            }

            //指向子节点,进入下一循环
            tempNode = subNode;

            //查看最后一个字,设置结束表示
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }

    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针1 指着树的指针,初始指针根,会逐渐往子节点一层层向下
        TrieNode tempNode = rootNode;
        //指针2 指字符串首位置,只往前
        int begin = 0;
        //指针3 指字符串末位置
        int end = 0;
        //结果 使用变长字符串
        StringBuilder sb = new StringBuilder();

        while (end<text.length()){
            char c = text.charAt(end);

            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点,将此符号计入,指针2往后动一位
                if (tempNode==rootNode){
                    sb.append(c);
                    //指针2只有在是符号时才会往后动
                    begin++;
                }
                //指针3无论如何都要往后动
                end++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode==null){
                //以begin开头的字符串是不是敏感词
                sb.append(text.charAt(begin));
                //进入下一位置
                end = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                //发现敏感词,将begin到end字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一位置
                begin = ++end;//把现在end的下一位置,赋值给begin作为下次循环开始的地方
                //重新指向根节点
                tempNode = rootNode;
            } else {
                //检查下一个字符
                end++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x9FF ~ 0x2E80 为东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);//是符号返回fasle,取反后返回true
    }

    //前缀树
    private class TrieNode{//前缀树的某一个节点

        //关键词结束的表示
        private boolean isKeywordEnd = false;//是不是一个单词的结尾,是一个单词的结尾就是敏感词

        //当前节点的子节点,因为一个节点可以有多个孩子,所以用map集合
        //key(下级节点字符),value(下级节点)
        private Map<Character,TrieNode> subNode = new HashMap<>();

        //生成set/get 方法
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        private void addSubNode(Character c,TrieNode t){
            subNode.put(c,t);
        }
        //获取子节点
        private TrieNode getSubNode(Character c){
            return subNode.get(c);
        }

    }
}
