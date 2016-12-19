package org.acc.word2vec.demo;

import org.acc.word2vec.core.Word2VecUtils;
import org.acc.word2vec.util.TextUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public class FitDemo {

    public static void main(String[] args) {

        List<File> files = TextUtils.listFilesRecursively("D:\\tmp\\word2vec\\哲学\\汉译世界学术名著丛书（英国卷）1", file -> file.getName().endsWith(".txt"));
        Word2VecUtils
                .newWord2Vec()
                .addAllTextFile(files)
                .charset(Charset.forName("GB2312"))
                .saveAt("D:\\tmp\\word2vec\\result", true)
                .build();

    }
}
