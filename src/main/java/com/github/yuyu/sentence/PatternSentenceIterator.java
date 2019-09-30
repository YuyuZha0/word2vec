package com.github.yuyu.sentence;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019-09-26
 */
@Slf4j
public final class PatternSentenceIterator extends BaseSentenceIterator {

  private String currentMatch = null;
  private Matcher matcher = null;

  public PatternSentenceIterator(
      @NonNull Pattern pattern, @NonNull Charset charset, @NonNull Collection<File> files) {
    super();
    try {
      this.matcher = pattern.matcher(readFilesToString(files, charset));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    advance();
  }

  private static String readFilesToString(Collection<File> files, Charset charset)
      throws IOException {
    char[] chars = null;
    for (File file : files) {
      log.info("reading content in file[{}]...", file.getAbsolutePath());
      if (chars != null) {
        char[] chars1 = IOUtils.toCharArray(new FileInputStream(file), charset);
        chars = concatCharArray(chars, chars1);
      } else {
        chars = IOUtils.toCharArray(new FileInputStream(file), charset);
      }
    }
    assert chars != null;
    return new String(chars);
  }

  private static char[] concatCharArray(char[] chars1, char[] chars2) {
    char[] chars = new char[chars1.length + chars2.length];
    System.arraycopy(chars1, 0, chars, 0, chars1.length);
    System.arraycopy(chars2, 0, chars, chars1.length, chars2.length);
    return chars;
  }

  private void advance() {
    if (matcher.find()) {
      currentMatch = matcher.group();
    } else {
      currentMatch = null;
    }
  }

  @Override
  public String nextSentence() {
    if (currentMatch == null) throw new NoSuchElementException();
    String m = currentMatch;
    advance();
    return m;
  }

  @Override
  public boolean hasNext() {
    return currentMatch != null;
  }

  @Override
  public void reset() {
    matcher.reset();
    advance();
  }
}
