package com.github.yuyu.example;

import com.github.yuyu.Word2VecCN;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.nio.charset.Charset;

/**
 * @author zhaoyuyu
 * @since 2019-09-27
 */
public class FitExample {

  public static void main(String[] args) {
    Word2Vec word2Vec =
        Word2VecCN.builder()
            .charset(Charset.forName("GB2312"))
            .addFile("/Users/zhaoyuyu/Downloads/阅微草堂笔记.txt")
            .addFile("/Users/zhaoyuyu/Downloads/白话加强版阅微草堂笔记.txt")
            .build()
            .fit();

    WordVectorSerializer.writeWord2VecModel(word2Vec, "/Users/zhaoyuyu/Downloads/word2vec");

    System.out.println(word2Vec.wordsNearest("鬼", 10));
  }
}
