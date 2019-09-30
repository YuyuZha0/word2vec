package com.github.yuyu;

import com.github.yuyu.sentence.PatternSentenceIterator;
import com.github.yuyu.tokenize.AnsjTokenizerFactory;
import com.github.yuyu.tokenize.TrimPreProcess;
import com.google.common.base.Predicate;
import com.google.common.io.Files;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019-09-25
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Word2VecCN {

  private final int minWordFrequency;
  private final int iterations;
  private final int layerSize;
  private final long seed = ThreadLocalRandom.current().nextLong();
  private final int windowSize;

  private final TokenizerFactory tokenizerFactory;
  private final SentenceIterator sentenceIterator;

  public static Word2VecCNBuilder builder() {
    return new Word2VecCNBuilder();
  }

  public Word2Vec fit() {
    log.info("Building model....");
    Word2Vec vec =
        new Word2Vec.Builder()
            .minWordFrequency(minWordFrequency)
            .iterations(iterations)
            .layerSize(layerSize)
            .seed(seed)
            .windowSize(windowSize)
            .iterate(sentenceIterator)
            .tokenizerFactory(tokenizerFactory)
            .build();

    log.info("Fitting Word2Vec model....");
    vec.fit();
    return vec;
  }

  public static final class Word2VecCNBuilder {

    private final LinkedHashSet<File> files = new LinkedHashSet<>();
    private int minWordFrequency = 5;
    private int iterations = 1;
    private int layerSize = 100;
    private int windowSize = 5;
    private TokenizerFactory tokenizerFactory = new AnsjTokenizerFactory(new TrimPreProcess());
    private Charset charset = StandardCharsets.UTF_8;

    private Pattern sentencePatten = Pattern.compile("[\u4e00-\u9fa50-9a-zA-Z\r\n]+");

    private static void ensurePositive(int i) {
      if (i <= 0)
        throw new IllegalArgumentException("a positive number is required, but found:" + i);
    }

    public Word2VecCNBuilder minWordFrequency(int minWordFrequency) {
      ensurePositive(minWordFrequency);
      this.minWordFrequency = minWordFrequency;
      return this;
    }

    public Word2VecCNBuilder iterations(int iterations) {
      ensurePositive(iterations);
      this.iterations = iterations;
      return this;
    }

    public Word2VecCNBuilder layerSize(int layerSize) {
      ensurePositive(layerSize);
      this.layerSize = layerSize;
      return this;
    }

    public Word2VecCNBuilder windowSize(int windowSize) {
      ensurePositive(windowSize);
      this.windowSize = windowSize;
      return this;
    }

    public Word2VecCNBuilder tokenizerFactory(@NonNull TokenizerFactory tokenizerFactory) {
      this.tokenizerFactory = tokenizerFactory;
      return this;
    }

    private Word2VecCNBuilder addFile(@NonNull File file) {
      if (file.exists() && file.isFile() && file.canRead()) {
        if (!files.add(file)) {
          log.warn("file [{}] already added!", file.getAbsolutePath());
        } else {
          log.info("file [{}] added successfully!", file.getAbsolutePath());
        }
        return this;
      }
      throw new IllegalArgumentException(file + " is not a valid file!");
    }

    public Word2VecCNBuilder addFile(@NonNull String path) {
      return addFile(new File(path));
    }

    public Word2VecCNBuilder addAllFiles(@NonNull String root, @NonNull Predicate<File> predicate) {
      File dir = new File(root);
      if (dir.exists() && dir.isDirectory() && dir.canRead()) {
        Files.fileTreeTraverser()
            .breadthFirstTraversal(dir)
            .filter(predicate)
            .forEach(this::addFile);
      }
      throw new IllegalArgumentException(root + " is not a valid directory!");
    }

    public Word2VecCNBuilder charset(@NonNull Charset charset) {
      this.charset = charset;
      return this;
    }

    public Word2VecCNBuilder sentencePattern(@NonNull String pattern) {
      this.sentencePatten = Pattern.compile(pattern);
      return this;
    }

    public Word2VecCN build() {
      return new Word2VecCN(
          minWordFrequency,
          iterations,
          layerSize,
          windowSize,
          tokenizerFactory,
          new PatternSentenceIterator(sentencePatten, charset, files));
    }
  }
}
