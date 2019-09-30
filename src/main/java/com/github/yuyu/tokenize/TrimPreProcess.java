package com.github.yuyu.tokenize;

import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;

/**
 * @author zhaoyuyu
 * @since 2019-09-26
 */
public final class TrimPreProcess implements TokenPreProcess {

  @Override
  public String preProcess(String s) {
    return StringUtils.trimToNull(s);
  }
}
