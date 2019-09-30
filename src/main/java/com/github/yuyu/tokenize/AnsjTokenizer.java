package com.github.yuyu.tokenize;

import lombok.NonNull;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.StreamSupport;

/**
 * @author zhaoyuyu
 * @since 2019-09-25
 */
public final class AnsjTokenizer implements Tokenizer {

  private static final AtomicIntegerFieldUpdater<AnsjTokenizer> INDEX_UPDATER =
      AtomicIntegerFieldUpdater.newUpdater(AnsjTokenizer.class, "index");

  private final String[] a;
  private final TokenPreProcess tokenPreProcess;
  private volatile int index = 0;

  AnsjTokenizer(String toTokenize, @NonNull TokenPreProcess tokenPreProcess) {
    this.a = tokenizeToArray(toTokenize);
    this.tokenPreProcess = tokenPreProcess;
  }

  private static String[] tokenizeToArray(String toTokenize) {
    if (StringUtils.isEmpty(toTokenize)) return new String[0];
    return StreamSupport.stream(ToAnalysis.parse(toTokenize).spliterator(), false)
        .map(Term::getName)
        .filter(StringUtils::isNoneEmpty)
        .toArray(String[]::new);
  }

  @Override
  public boolean hasMoreTokens() {
    return index < a.length;
  }

  @Override
  public int countTokens() {
    return a.length;
  }

  @Override
  public String nextToken() {
    return tokenPreProcess.preProcess(a[INDEX_UPDATER.getAndIncrement(this)]);
  }

  @Override
  public List<String> getTokens() {
    return Arrays.asList(a);
  }

  @Override
  public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor) {
    throw new UnsupportedOperationException();
  }
}
