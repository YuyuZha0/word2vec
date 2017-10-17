package org.acc.word2vec.example;

import org.acc.word2vec.core.Word2VecUtils;

import java.nio.charset.Charset;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public class FitExample {

    public static void main(String[] args) {

        Word2VecUtils
                .newWord2Vec()
                .addAllTextFile("D:\\tmp\\word2vec\\哲学\\汉译世界学术名著丛书（英国卷）1", file -> file.getName().endsWith(".txt"))
                .charset(Charset.forName("GB2312"))
                .saveAt("D:\\tmp\\word2vec\\result", true)
                .build();

    }
}
