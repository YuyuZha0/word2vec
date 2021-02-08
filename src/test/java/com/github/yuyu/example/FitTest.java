package com.github.yuyu.example;

import com.github.yuyu.Word2VecCN;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaoyuyu
 * @since 2019-09-27
 */
public class FitTest {

  @Test
  public void test() {
    Word2Vec word2Vec =
            Word2VecCN.builder()
                    .charset(StandardCharsets.UTF_8)
                    .addFile("/Users/fishzhao/IdeaProjects/word2vec/src/test/resources/32390.txt")
                    .build()
                    .fit();

    WordVectorSerializer.writeWord2VecModel(word2Vec, "/Users/fishzhao/IdeaProjects/word2vec/src/test/resources/word2vec.bin");

    System.out.println(word2Vec.wordsNearest("王熙凤", 10));
  }
}
