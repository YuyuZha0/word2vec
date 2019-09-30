package com.github.yuyu.tokenize;

import lombok.NonNull;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.InputStream;

/**
 * @author zhaoyuyu
 * @since 2019-09-25
 */
public final class AnsjTokenizerFactory implements TokenizerFactory {

  private final TokenPreProcess tokenPreProcess;

  public AnsjTokenizerFactory(@NonNull TokenPreProcess tokenPreProcess) {
    this.tokenPreProcess = tokenPreProcess;
  }

  @Override
  public Tokenizer create(String s) {
    return new AnsjTokenizer(s, tokenPreProcess);
  }

  @Override
  public Tokenizer create(InputStream inputStream) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TokenPreProcess getTokenPreProcessor() {
    return tokenPreProcess;
  }

  @Override
  public void setTokenPreProcessor(TokenPreProcess tokenPreProcess) {
    throw new UnsupportedOperationException();
  }
}
