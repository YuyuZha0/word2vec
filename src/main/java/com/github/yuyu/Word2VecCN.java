package com.github.yuyu;

import com.github.yuyu.sentence.PatternSentenceIterator;
import com.github.yuyu.tokenize.AnsjTokenizerFactory;
import com.github.yuyu.tokenize.TrimPreProcess;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
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

    private final LinkedHashSet<Path> files = new LinkedHashSet<>();
    private int minWordFrequency = 5;
    private int iterations = 1;
    private int layerSize = 100;
    private int windowSize = 5;
    private TokenizerFactory tokenizerFactory = new AnsjTokenizerFactory(new TrimPreProcess());
    private Charset charset = StandardCharsets.UTF_8;

    private Pattern sentencePatten = Pattern.compile("[\u4e00-\u9fa50-9a-zA-Z\r\n]+");

    private static void ensurePositive(int i) {
      Preconditions.checkArgument(i > 0, "a positive number is required, but found: %s", i);
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
      return addFile(file.toPath());
    }

    public Word2VecCNBuilder addFile(@NonNull Path path) {
      Preconditions.checkArgument(
              !Files.isDirectory(path)
              && Files.isReadable(path), "invalid file path: %s",
              path
      );
      if (!files.add(path)) {
        log.warn("file [{}] already added!", path);
      } else {
        log.info("file [{}] added successfully!", path);
      }
      return this;
    }

    public Word2VecCNBuilder addFile(@NonNull String path) {
      return addFile(Paths.get(path));
    }

    public Word2VecCNBuilder addAllFiles(@NonNull Path path, @NonNull Predicate<? super Path> predicate) {
      Preconditions.checkArgument(
              Files.isDirectory(path)
              && Files.isReadable(path),
              "invalid directory: %s", path
      );
      try {
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .filter(predicate)
                .forEach(this::addFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return this;
    }

    public Word2VecCNBuilder addAllFiles(@NonNull String path, @NonNull Predicate<? super String> predicate) {
      return addAllFiles(Paths.get(path), (Path p) -> predicate.test(p.toString()));
    }

    public Word2VecCNBuilder addAllFiles(@NonNull File file, @NonNull Predicate<? super File> predicate) {
      return addAllFiles(file.toPath(), (Path p) -> predicate.test(new File(p.toString())));
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
