package org.acc.word2vec.text;

import com.google.common.base.Strings;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public final class ChineseTokenPreProcess implements TokenPreProcess {
    @Override
    public String preProcess(String token) {
        if (Strings.isNullOrEmpty(token))
            return null;
        return token.replaceAll("[^\u4e00-\u9fa5\\w]+", " ");
    }
}
